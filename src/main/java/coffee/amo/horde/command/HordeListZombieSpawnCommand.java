package coffee.amo.horde.command;

import coffee.amo.horde.Horde;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class HordeListZombieSpawnCommand implements Command<CommandSourceStack> {
    private static final HordeListZombieSpawnCommand CMD = new HordeListZombieSpawnCommand();
    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        HordeCommand.feedback(context.getSource(), "listzombiespawn", "success", HordeCommand.CommandFeedbackType.INFO);
        try {
            return execute(context);
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }    }

    public static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Horde.getHordeManager().listZombieSpawn().forEach(comp -> {
            context.getSource().sendSuccess(comp, false);
        });
        return 1;
    }

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher){
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("listzombiespawn").executes(CMD);
        builder.executes(HordeListZombieSpawnCommand::execute);
        return builder;
    }
}
