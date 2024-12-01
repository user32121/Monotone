package juniper.monotone.interaction;

import java.io.IOException;
import java.util.Iterator;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class SchematicRegionMask implements RegionMask {
    public SchematicRegionMask(String path) throws IOException {
        // TODO Auto-generated method stub
        NbtIo.read(null);
        // StructureTemplate.nbt
    }

    @Override
    public Iterator<Pair<BlockPos, BlockState>> iterator() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'iterator'");
    }

    @Override
    public boolean contains(BlockPos pos) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'contains'");
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vec3d cameraPos, Vec3d color) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'render'");
    }

}
