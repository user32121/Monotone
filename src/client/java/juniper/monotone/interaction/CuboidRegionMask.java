package juniper.monotone.interaction;

import java.util.Iterator;

import com.google.common.collect.Iterators;

import net.minecraft.block.BlockState;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class CuboidRegionMask implements RegionMask {
    public final BlockPos from, to;

    public CuboidRegionMask(BlockPos from, BlockPos to) {
        this.from = from;
        this.to = to;
        init();
    }

    @Override
    public String toString() {
        return "CuboidRegionMask [from=" + from + ", to=" + to + "]";
    }

    @Override
    public boolean contains(BlockPos pos, BlockState state) {
        return Box.enclosing(from, to).contains(pos.toCenterPos());
    }

    @Override
    public Pair<BlockPos, BlockPos> getBounds() {
        return new Pair<>(from, to);
    }

    @Override
    public Iterator<Pair<BlockPos, BlockState>> iterator() {
        return Iterators.transform(BlockPos.iterate(from, to).iterator(), bp -> new Pair<>(bp, null));
    }

    @Override
    public void init() {
    }
}
