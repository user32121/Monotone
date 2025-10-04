package juniper.monotone.render;

import org.apache.commons.lang3.NotImplementedException;

import juniper.monotone.Monotone;
import juniper.monotone.command.LocatorMode;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.debug.DebugRenderer.Renderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class LocatorDebugRenderer implements Renderer {
    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        if (Monotone.CONFIG.locatorMode.equals(LocatorMode.NONE)) {
            return;
        }
        double dist = Monotone.CONFIG.locatorTarget.getSquaredDistanceFromCenter(cameraX, cameraY, cameraZ);
        Vec3d inRangeTarget = Monotone.CONFIG.locatorTarget.toCenterPos();
        if (dist > Monotone.CONFIG.locatorRange * Monotone.CONFIG.locatorRange) {
            Vec3d delta = Monotone.CONFIG.locatorTarget.toCenterPos().subtract(cameraX, cameraY, cameraZ);
            delta = delta.normalize().multiply(Monotone.CONFIG.locatorRange);
            inRangeTarget = delta.add(cameraX, cameraY, cameraZ);
        }
        Box box = new Box(inRangeTarget, inRangeTarget).expand(0.5).offset(-cameraX, -cameraY, -cameraZ);

        switch (Monotone.CONFIG.locatorMode) {
            case NONE: {
                return;
            }
            case XYZ: {
                DebugRenderer.drawBox(matrices, vertexConsumers, box, 0, 1, 0, 0.5f);
                break;
            }
            case XZ: {
                box = box.expand(0, 1000, 0);
                DebugRenderer.drawBox(matrices, vertexConsumers, box, 0, 1, 0, 0.5f);
                break;
            }
            default: {
                throw new NotImplementedException(Monotone.CONFIG.locatorMode.toString());
            }
        }
    }
}
