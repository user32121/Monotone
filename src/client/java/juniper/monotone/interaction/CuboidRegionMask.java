package juniper.monotone.interaction;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class CuboidRegionMask implements RegionMask {
    public final BlockPos from, to;

    public CuboidRegionMask(BlockPos from, BlockPos to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "CuboidRegionMask [from=" + from + ", to=" + to + "]";
    }

    @Override
    public boolean contains(BlockPos pos) {
        return Box.enclosing(from, to).contains(pos.toCenterPos());
    }
}
