package coffee.amo.horde.mode;

import net.minecraft.core.BlockPos;

public class SpawnPosition {
    private BlockPos pos;
    private int radius;

    public SpawnPosition(BlockPos pos, int radius) {
        this.pos = pos;
        this.radius = radius;
    }

    public BlockPos getRandomPos(){
        return new BlockPos(pos.getX() + (int) (Math.random() * radius * 2) - radius, pos.getY(), pos.getZ() + (int) (Math.random() * radius * 2) - radius);
    }

    public BlockPos getPos(){
        return pos;
    }
}
