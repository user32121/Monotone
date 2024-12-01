package juniper.monotone.interaction;

import java.util.Iterator;

import com.google.common.collect.Iterators;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

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

    @Override
    public void renderBounds(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vec3d cameraPos, Vec3d color) {
        DebugRenderer.drawBox(matrices, vertexConsumers, from, to, (float) color.x, (float) color.y,
                (float) color.z, 0.5f);
    }

    @Override
    public Iterator<Pair<BlockPos, BlockState>> iterator() {
        return Iterators.transform(BlockPos.iterate(from, to).iterator(), bp -> new Pair<>(bp, null));
    }
}
