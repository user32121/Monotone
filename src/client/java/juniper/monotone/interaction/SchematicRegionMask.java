package juniper.monotone.interaction;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Iterators;

import juniper.monotone.mixin.StructureTemplateAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.registry.Registries;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplate.StructureBlockInfo;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class SchematicRegionMask implements RegionMask {
    public final String path;
    public final BlockPos base;
    public transient List<StructureBlockInfo> sbis;
    public transient BlockPos from, to;

    public SchematicRegionMask(String path, BlockPos basePos) throws IOException {
        this.path = path;
        this.base = basePos;
        init();
    }

    @Override
    public Iterator<Pair<BlockPos, BlockState>> iterator() {
        return Iterators.transform(sbis.iterator(), sbi -> new Pair<>(sbi.pos().add(base), sbi.state()));
    }

    @Override
    public boolean contains(BlockPos pos) {
        //TODO failing to allow placement
        for (StructureBlockInfo sbi : sbis) {
            if (pos.equals(sbi.pos())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void renderBounds(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vec3d cameraPos, Vec3d color) {
        //TODO return box and let caller call drawBox
        DebugRenderer.drawBox(matrices, vertexConsumers, from, to, (float) color.x, (float) color.y,
                (float) color.z, 0.5f);
    }

    @Override
    public void init() throws IOException {
        StructureTemplate st = new StructureTemplate();
        Path path2 = Paths.get("schematics", path);
        NbtCompound nbt = NbtIo.readCompressed(path2, NbtSizeTracker.ofUnlimitedBytes());
        if (nbt == null) {
            throw new FileNotFoundException(String.format("Could not read file at %s", path2));
        }
        st.readNbt(Registries.BLOCK, nbt);
        sbis = ((StructureTemplateAccessor) (Object) st).getBlockInfoLists().get(0).getAll();
        BlockBox bb = st.calculateBoundingBox(new StructurePlacementData(), base);
        from = new BlockPos(bb.getMinX(), bb.getMinY(), bb.getMinZ());
        to = new BlockPos(bb.getMaxX(), bb.getMaxY(), bb.getMaxZ());
    }
}
