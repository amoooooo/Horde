package coffee.amo.horde.mode;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.scores.PlayerTeam;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HordeManager {
    private static final ResourceLocation BAR_RL = new ResourceLocation("horde:wave");
    private List<HordePlayer> PLAYERS = new ArrayList<>();
    private int wave = 0;
    private int maxWave = 5;
    private boolean isActive = false;
    private List<LootContainer> chests = new ArrayList<>();
    private List<SpawnPosition> zombieSpawns = new ArrayList<>();
    private List<SpawnPosition> playerSpawns = new ArrayList<>();
    private CustomBossEvent bossBar;
    private int zombieMaxHealth = 10;
    private ServerLevel level;
    private PlayerTeam aliveTeam;
    private PlayerTeam zombieTeam;

    public HordeManager() {

    }

    public void addLivingSpawn(SpawnPosition pos){
        playerSpawns.add(pos);
    }

    public void addZombieSpawn(SpawnPosition pos){
        zombieSpawns.add(pos);
    }

    public void removeLivingSpawn(BlockPos pos){
        playerSpawns.removeIf(p -> p.getPos().equals(pos));
    }

    public void removeZombieSpawn(BlockPos pos){
        zombieSpawns.removeIf(p -> p.getPos().equals(pos));
    }

    public void addLootContainer(LootContainer container){
        chests.add(container);
    }

    public void removeLootContainer(BlockPos pos){
        chests.removeIf(c -> c.getPos().equals(pos));
    }

    public void init() {
        wave = 0;
        maxWave = 5;
        isActive = false;
        chests.clear();
        zombieSpawns.clear();
        playerSpawns.clear();
        bossBar = null;
        zombieMaxHealth = 10;
        aliveTeam = null;
        zombieTeam = null;
        initBossBar();
        initTeams();
    }

    public void setLevel(ServerLevel level){
        this.level = level;
    }

    public ServerLevel getLevel(){
        return this.level;
    }

    public void start() {
        isActive = true;
        wave++;
        chests.forEach(c -> c.generate(level));
        PLAYERS.forEach(player -> {
            if(level.getPlayerByUUID(player.getUUID()) == null) return;
            getLevel().getServer().getCustomBossEvents().get(BAR_RL).addPlayer((ServerPlayer) level.getPlayerByUUID(player.getUUID()));
            if(wave == 1){
                player.reset();
            }
            BlockPos pos = null;
            if(player.isZombie()){
                pos = zombieSpawns.get((int) (Math.random() * zombieSpawns.size())).getRandomPos();
                level.getPlayerByUUID(player.getUUID()).setHealth(zombieMaxHealth);
                level.getScoreboard().addPlayerToTeam(level.getPlayerByUUID(player.getUUID()).getScoreboardName(), zombieTeam);
            } else if(wave == 1) {
                pos = playerSpawns.get((int) (Math.random() * playerSpawns.size())).getRandomPos();
                level.getScoreboard().addPlayerToTeam(level.getPlayerByUUID(player.getUUID()).getScoreboardName(), aliveTeam);
            }
            if(pos != null){
                if(level.getPlayerByUUID(player.getUUID()) == null) return;
                level.getPlayerByUUID(player.getUUID()).teleportTo(pos.getX(), pos.getY(), pos.getZ());
            }
        });
    }

    public void stop(){
        isActive = false;
        PLAYERS.forEach(player -> {
            if(level.getPlayerByUUID(player.getUUID()) == null) return;
            if(level.getScoreboard().getPlayersTeam(level.getPlayerByUUID(player.getUUID()).getScoreboardName()) == aliveTeam)
                level.getScoreboard().removePlayerFromTeam(level.getPlayerByUUID(player.getUUID()).getScoreboardName(), aliveTeam);
            else if (level.getScoreboard().getPlayersTeam(level.getPlayerByUUID(player.getUUID()).getScoreboardName()) == zombieTeam)
                level.getScoreboard().removePlayerFromTeam(level.getPlayerByUUID(player.getUUID()).getScoreboardName(), zombieTeam);
        });
        PLAYERS.clear();
        wave = 0;
    }

    public PlayerTeam getAliveTeam(){
        return this.aliveTeam;
    }

    public PlayerTeam getZombieTeam(){
        return this.zombieTeam;
    }

    public void teleportDeadPlayer(UUID player) {
        ServerPlayer serverPlayer = (ServerPlayer) level.getPlayerByUUID(player);
        List<HordePlayer> alivePlayers = PLAYERS.stream().filter(HordePlayer::isAlive).toList();
        if (serverPlayer != null && !alivePlayers.isEmpty()) {
            HordePlayer randomPlayer = alivePlayers.get((int) (Math.random() * alivePlayers.size()));
            ServerPlayer randomServerPlayer = (ServerPlayer) level.getPlayerByUUID(randomPlayer.getUUID());
            serverPlayer.teleportTo(randomServerPlayer.getX(), randomServerPlayer.getY(), randomServerPlayer.getZ());
        }
    }

    public void tick(){
        if(!isActive) return;
        if(bossBar == null) initBossBar();
        if(aliveTeam == null || zombieTeam == null){
            initTeams();
        }
        bossBar.setProgress((float) PLAYERS.stream().filter(HordePlayer::isZombie).count() / PLAYERS.size());
        bossBar.setName(Component.nullToEmpty("Wave " + wave));
        if(PLAYERS.stream().noneMatch(HordePlayer::isAlive)){
            bossBar.setName(Component.nullToEmpty("Wave " + wave + " - Failed"));
            bossBar.setProgress(0);
            wave = 0;
        }
        if(PLAYERS.stream().noneMatch(HordePlayer::isZombie) && wave >= maxWave){
            bossBar.setName(Component.nullToEmpty("Wave " + wave + " - Completed"));
            bossBar.setProgress(0);
        }
        if(PLAYERS.stream().noneMatch(HordePlayer::isZombie) && PLAYERS.stream().noneMatch(HordePlayer::isAlive) && wave < maxWave){
            start();
        }
        if(wave == 0){
            reset();
        }
    }

    public void initBossBar(){
        level.getServer().getCustomBossEvents().create(BAR_RL, Component.nullToEmpty("Wave"));
        bossBar = level.getServer().getCustomBossEvents().get(BAR_RL);
        bossBar.setMax(1);
        bossBar.setProgress(0);
        bossBar.setVisible(true);
        bossBar.setColor(BossEvent.BossBarColor.GREEN);
    }

    public void initTeams(){
        level.getScoreboard().addPlayerTeam("alivePlayers");
        level.getScoreboard().addPlayerTeam("zombiePlayers");
        aliveTeam = level.getScoreboard().getPlayerTeam("alivePlayers");
        zombieTeam = level.getScoreboard().getPlayerTeam("zombiePlayers");
    }

    public void addPlayer(UUID player) {
        if (getPlayer(player) == null) {
            PLAYERS.add(new HordePlayer(player));
        }
    }

    public void removePlayer(UUID player) {
        PLAYERS.removeIf(p -> p.getUUID().equals(player));
    }

    public HordePlayer getPlayer(UUID player) {
        for (HordePlayer p : PLAYERS) {
            if (p.getUUID().equals(player)) {
                return p;
            }
        }
        return null;
    }

    public List<HordePlayer> getPlayers() {
        return PLAYERS;
    }

    public void reset() {
        PLAYERS.forEach(HordePlayer::reset);
        wave = 0;
        isActive = false;
    }

    public void clear() {
        PLAYERS.clear();
    }

    public void setZombie(UUID player, boolean isZombie) {
        HordePlayer p = getPlayer(player);
        if (p != null) {
            p.setZombie(isZombie);
        }
    }

    public void setDied(UUID player, boolean hasDied) {
        HordePlayer p = getPlayer(player);
        if (p != null) {
            p.setDied(hasDied);
        }
    }

    public boolean isZombie(UUID player) {
        HordePlayer p = getPlayer(player);
        if (p != null) {
            return p.isZombie();
        }
        return false;
    }

    public boolean hasDied(UUID player) {
        HordePlayer p = getPlayer(player);
        if (p != null) {
            return p.hasDied();
        }
        return false;
    }

    public boolean isAlive(UUID player) {
        HordePlayer p = getPlayer(player);
        if (p != null) {
            return !p.hasDied() && !p.isZombie();
        }
        return false;
    }

    public boolean isDead(UUID player) {
        HordePlayer p = getPlayer(player);
        if (p != null) {
            return p.hasDied() && !p.isZombie();
        }
        return false;
    }

    public boolean isZombified(UUID player) {
        HordePlayer p = getPlayer(player);
        if (p != null) {
            return !p.hasDied() && p.isZombie();
        }
        return false;
    }

    public List<Component> listLivingSpawn() {
        List<Component> components = new ArrayList<>();
        for(SpawnPosition spawn : playerSpawns){
            components.add(Component.literal(spawn.getPos().toShortString()).withStyle(s -> s.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp @p " + spawn.getPos().toShortString()))));
        }
        return components;
    }

    public List<Component> listZombieSpawn() {
        List<Component> components = new ArrayList<>();
        for(SpawnPosition spawn : zombieSpawns){
            components.add(Component.literal(spawn.getPos().toShortString()).withStyle(s -> s.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp @p " + spawn.getPos().toShortString()))));
        }
        return components;
    }

    public List<Component> listLootContainers(){
        List<Component> components = new ArrayList<>();
        for(LootContainer container : chests){
            components.add(Component.literal(container.getPos().toShortString()).withStyle(s -> s.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp @p " + container.getPos().toShortString()))));
        }
        return components;
    }
}
