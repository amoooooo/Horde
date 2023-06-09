package coffee.amo.horde.mode;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;

public class LootContainer {
    private BlockPos pos;
    private ResourceLocation lootTable;

    public LootContainer(BlockPos pos, ResourceLocation lootTable) {
        this.pos = pos;
        this.lootTable = lootTable;
    }

    public void generate(ServerLevel level) {
        if(level.getBlockEntity(pos) instanceof ChestBlockEntity cbe){
            cbe.clearContent();
        }
        level.setBlock(pos, Blocks.CHEST.defaultBlockState(), 3);
        ChestBlockEntity chest = (ChestBlockEntity) level.getBlockEntity(pos);
        if(chest == null) return;
        chest.setLootTable(lootTable, 42L);
        chest.unpackLootTable(null);
    }

    public BlockPos getPos() {
        return pos;
    }
}
