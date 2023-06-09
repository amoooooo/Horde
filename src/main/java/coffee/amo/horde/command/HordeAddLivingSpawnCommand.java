package coffee.amo.horde.command;

import coffee.amo.horde.Horde;
import coffee.amo.horde.mode.LootContainer;
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
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class HordeAddLivingSpawnCommand implements Command<CommandSourceStack> {
    private static final HordeAddLivingSpawnCommand CMD = new HordeAddLivingSpawnCommand();

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        HordeCommand.feedback(context.getSource(), "addlivingspawn", "success", HordeCommand.CommandFeedbackType.INFO);
        try {
            return execute(context);
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    public static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "position");
        int radius = IntegerArgumentType.getInteger(context, "radius");
        Horde.getHordeManager().addLivingSpawn(new SpawnPosition(pos, radius));
        return 1;
    }

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("addlivingspawn").executes(CMD);
        builder.then(Commands.argument("position", BlockPosArgument.blockPos())
                .then(Commands.argument("radius", IntegerArgumentType.integer())
                        .executes(HordeAddLivingSpawnCommand::execute)));
        return builder;
    }
}
