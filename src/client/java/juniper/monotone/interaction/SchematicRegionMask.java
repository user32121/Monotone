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

public class SchematicRegionMask implements RegionMask {
    // TODO schematic transforms
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
    public boolean contains(BlockPos pos, BlockState state) {
        for (StructureBlockInfo sbi : sbis) {
            if (sbi.pos().add(base).equals(pos) && (state == null || sbi.state().equals(state))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Pair<BlockPos, BlockPos> getBounds() {
        return new Pair<>(from, to);
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

    @Override
    public String toString() {
        return "SchematicRegionMask [path=" + path + ", base=" + base + "]";
    }
}
