package juniper.monotone.pathfinding.steps;

import org.jetbrains.annotations.Nullable;

import juniper.monotone.mixin.MouseInputAccessor;
import juniper.monotone.task.InputManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

public interface SmoothStep extends Step {
    public Vec3i getOffset();

    @Override
    public default int getCost(@Nullable Step prevStep) {
        int smoothFactor = 0;
        if (prevStep instanceof SmoothStep ss) {
            smoothFactor = (int) (2 * getSmoothness(ss));
        }
        return (int) (10 * Math.sqrt(getOffset().getSquaredDistance(Vec3i.ZERO))) - smoothFactor;
    }

    public default double getSmoothness(SmoothStep other) {
        //dot product: the closer the angles, the higher the smoothness
        Vec3i offset1 = getOffset();
        Vec3i offset2 = other.getOffset();
        int dot = offset1.getX() * offset2.getX() + offset1.getZ() * offset2.getZ();
        double dotNorm = dot / Math.sqrt(offset1.getSquaredDistance(Vec3i.ZERO)) / Math.sqrt(offset2.getSquaredDistance(Vec3i.ZERO));
        return dotNorm;
    }

    @Override
    public default boolean tick(MinecraftClient client, Vec3i destination) {
        float deltaAngle = MathHelper.subtractAngles((float) Math.toDegrees(Math.atan2(destination.getZ() + 0.5 - client.player.getZ(), destination.getX() + 0.5 - client.player.getX())) - 90,
                client.player.getYaw());
        MouseInputAccessor mia = (MouseInputAccessor) (Object) client.mouse;
        mia.setCursorDeltaX(mia.getCursorDeltaX() - deltaAngle * 2);

        InputManager.forward = MathHelper.angleBetween(deltaAngle, 0) <= 60;
        InputManager.left = MathHelper.angleBetween(deltaAngle, 90) <= 60;
        InputManager.back = MathHelper.angleBetween(deltaAngle, 180) <= 60;
        InputManager.right = MathHelper.angleBetween(deltaAngle, 270) <= 60;
        InputManager.sprint = true;

        if (client.player.getBlockPos().equals(destination)) {
            return true;
        }
        return false;
    }
}
