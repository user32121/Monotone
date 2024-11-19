package juniper.monotone.interaction;

import net.minecraft.util.math.BlockPos;

public class CuboidRegionMask implements RegionMask {
    private BlockPos from, to;

    public CuboidRegionMask(BlockPos from, BlockPos to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "CuboidRegionMask [from=" + from + ", to=" + to + "]";
    }
}
