package coffee.amo.horde;

import coffee.amo.horde.command.HordeCommand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Horde.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HordeServerForgeEvents {

    @SubscribeEvent
    public static void onServerStartingEvent(ServerStartedEvent event) {
        Horde.getHordeManager().setLevel(event.getServer().overworld());
        Horde.getHordeManager().init();
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Horde.getHordeManager().addPlayer(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        Horde.getHordeManager().removePlayer(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event){
        HordeCommand.registerSubCommands(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingHurtEvent event){
        if(event.getEntity() instanceof Player player){
            if(event.getEntity().getHealth() <= 0){
                event.getEntity().setHealth(1);
                if(!Horde.getHordeManager().isZombie(player.getUUID())){
                    Horde.getHordeManager().getPlayer(player.getUUID()).handleLivingDeath();
                } else {
                    Horde.getHordeManager().getPlayer(player.getUUID()).handleZombieDeath();
                }
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        Horde.getHordeManager().tick();
    }
}
