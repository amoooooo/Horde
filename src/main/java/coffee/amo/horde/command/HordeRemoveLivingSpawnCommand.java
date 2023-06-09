package coffee.amo.horde.command;

import coffee.amo.horde.Horde;
import coffee.amo.horde.mode.SpawnPosition;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;

public class HordeRemoveLivingSpawnCommand implements Command<CommandSourceStack> {
    private static final HordeRemoveLivingSpawnCommand CMD = new HordeRemoveLivingSpawnCommand();
    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        HordeCommand.feedback(context.getSource(), "removelivingspawn", "success", HordeCommand.CommandFeedbackType.INFO);
        try {
            return execute(context);
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }    }

    public static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "position");
        Horde.getHordeManager().removeLivingSpawn(pos);
        return 1;
    }

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher){
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("removelivingspawn").executes(CMD);
        builder.then(Commands.argument("position", BlockPosArgument.blockPos()).executes(HordeRemoveLivingSpawnCommand::execute));
        return builder;
    }
}
