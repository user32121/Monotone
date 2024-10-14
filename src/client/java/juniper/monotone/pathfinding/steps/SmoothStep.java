package juniper.monotone.pathfinding.steps;

import org.jetbrains.annotations.Nullable;

import net.minecraft.util.math.Vec3i;

public interface SmoothStep extends Step {
    public Vec3i getOffset();

    @Override
    public default int getCost(@Nullable Step prevStep) {
        int smoothFactor = 0;
        if (prevStep instanceof SmoothStep ss) {
            smoothFactor = (int) (2 * getSmoothness(ss));
        }
        return (int) (10 * Math.sqrt(getOffset().getSquaredDistance(0, 0, 0))) - smoothFactor;
    }

    public default double getSmoothness(SmoothStep other) {
        //dot product: the closer the angles, the higher the smoothness
        Vec3i offset1 = getOffset();
        Vec3i offset2 = other.getOffset();
        int dot = offset1.getX() * offset2.getX() + offset1.getZ() * offset2.getZ();
        double dotNorm = dot / Math.sqrt(offset1.getSquaredDistance(0, 0, 0)) / Math.sqrt(offset2.getSquaredDistance(0, 0, 0));
        return dotNorm;
    }
}
