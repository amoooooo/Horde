package coffee.amo.horde.command;

import coffee.amo.horde.Horde;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;

public class HordeMakeZombieCommand implements Command<CommandSourceStack> {
    private static final HordeMakeZombieCommand CMD = new HordeMakeZombieCommand();
    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        HordeCommand.feedback(context.getSource(), "makezombie", "success", HordeCommand.CommandFeedbackType.INFO);
        try {
            return execute(context);
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }    }

    public static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        GameProfile profile = GameProfileArgument.getGameProfiles(context, "position").iterator().next();
        Horde.getHordeManager().getPlayer(profile.getId()).setZombie(true);
        return 1;
    }

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher){
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("makezombie").executes(CMD);
        builder.then(Commands.argument("position", GameProfileArgument.gameProfile()).executes(HordeMakeZombieCommand::execute));
        return builder;
    }
}
