package coffee.amo.horde.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.Arrays;

public class HordeCommand {
    protected static final SimpleCommandExceptionType NO_PERMISSION = new SimpleCommandExceptionType(Component.literal("You do not have permission to use this command!"));

    public static void registerSubCommands(CommandDispatcher<CommandSourceStack> dispatcher){
        LiteralArgumentBuilder<CommandSourceStack> cmd = Commands.literal("horde")
                .then(HordeListLootContainerCommand.register(dispatcher))
                .then(HordeListLivingSpawnCommand.register(dispatcher))
                .then(HordeListZombieSpawnCommand.register(dispatcher))
                .then(HordeAddZombieSpawnCommand.register(dispatcher))
                .then(HordeAddLootContainerCommand.register(dispatcher))
                .then(HordeAddLivingSpawnCommand.register(dispatcher))
                .then(HordeRemoveZombieSpawnCommand.register(dispatcher))
                .then(HordeRemoveLootContainerCommand.register(dispatcher))
                .then(HordeRemoveLivingSpawnCommand.register(dispatcher))
                .then(HordeStartCommand.register(dispatcher))
                .then(HordeStopCommand.register(dispatcher))
                .then(HordeResetCommand.register(dispatcher))
                .then(HordeMakeZombieCommand.register(dispatcher))
                .requires(source -> source.hasPermission(2));

        dispatcher.register(cmd);
    }

    protected static Component getCommandPrefix(String cmdName){
        return Component.literal(ChatFormatting.GOLD + "< HORDE > " + cmdName + " >" + ChatFormatting.RESET + " ");
    }

    protected enum CommandFeedbackType {
        INFO(ChatFormatting.GRAY),
        SUCCESS(ChatFormatting.GREEN),
        ERROR(ChatFormatting.RED),
        WARNING(ChatFormatting.YELLOW);
        private final ChatFormatting color;

        CommandFeedbackType(ChatFormatting color){
            this.color = color;
        }

        public ChatFormatting getColor(){
            return this.color;
        }
    }

    protected static void feedback(CommandSourceStack source, String commandName, String langKey, CommandFeedbackType type, Component... args) {
        source.sendSuccess(((MutableComponent)getCommandPrefix(commandName)).append(Component.literal(Arrays.toString(args)).setStyle(Style.EMPTY.applyFormat(type.getColor()))), true);
    }

    protected static void error(CommandSourceStack source, String commandName, String langKey, Component... args) {
        source.sendFailure(((MutableComponent)getCommandPrefix(commandName)).append(Component.literal(Arrays.toString(args)).setStyle(Style.EMPTY.applyFormat(CommandFeedbackType.ERROR.getColor()))));
    }
}
