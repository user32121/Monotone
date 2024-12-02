package juniper.monotone.mixinInterface;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

public interface ReadOnlyWorldInterface {
    public void enable();

    public void disable();

    public List<Pair<BlockPos, BlockState>> getBlockChanges();

    public void clearBlockChanges();
}
