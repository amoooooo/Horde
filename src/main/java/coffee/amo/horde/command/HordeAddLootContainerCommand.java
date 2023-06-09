package coffee.amo.horde.command;

import coffee.amo.horde.Horde;
import coffee.amo.horde.mode.LootContainer;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
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

public class HordeAddLootContainerCommand implements Command<CommandSourceStack> {
    private static final HordeAddLootContainerCommand CMD = new HordeAddLootContainerCommand();
    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        HordeCommand.feedback(context.getSource(), "addloot", "success", HordeCommand.CommandFeedbackType.INFO);
        try {
            return execute(context);
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    public static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "position");
        ResourceLocation lootTable = ResourceLocationArgument.getId(context, "lootTable");
        Horde.getHordeManager().addLootContainer(new LootContainer(pos, lootTable));
        return 1;
    }

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher){
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("addloot").executes(CMD);
        builder.then(Commands.argument("lootTable", ResourceLocationArgument.id()).then(Commands.argument("position", BlockPosArgument.blockPos()).executes(HordeAddLootContainerCommand::execute)));
        return builder;
    }
}
