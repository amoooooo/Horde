package coffee.amo.horde.mode;

import coffee.amo.horde.Horde;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

import java.util.UUID;

public class HordePlayer {
    private UUID uuid;
    private boolean hasDied = false;
    private boolean isZombie = false;

    public HordePlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public boolean hasDied() {
        return this.hasDied;
    }

    public void setDied(boolean hasDied) {
        this.hasDied = hasDied;
    }

    public boolean isZombie() {
        return this.isZombie;
    }

    public void setZombie(boolean isZombie) {
        this.isZombie = isZombie;
    }

    public void reset() {
        this.hasDied = false;
        this.isZombie = false;
    }

    public boolean isAlive() {
        return !this.hasDied && !this.isZombie;
    }

    public void handleLivingDeath() {
        setZombie(true);
        ServerLevel level = Horde.getHordeManager().getLevel();
        level.getScoreboard().addPlayerToTeam(level.getPlayerByUUID(uuid).getScoreboardName(), Horde.getHordeManager().getZombieTeam());
        setDied(true);
        ServerPlayer player = (ServerPlayer) Horde.getHordeManager().getLevel().getPlayerByUUID(this.uuid);
        if (player != null) {
            player.setGameMode(GameType.SPECTATOR);
            Horde.getHordeManager().teleportDeadPlayer(this.uuid);
        }
    }

    public void handleZombieDeath() {
        setDied(true);
        ServerPlayer player = (ServerPlayer) Horde.getHordeManager().getLevel().getPlayerByUUID(this.uuid);
        if (player != null) {
            player.setGameMode(GameType.SPECTATOR);
            Horde.getHordeManager().teleportDeadPlayer(this.uuid);
        }
    }
}
