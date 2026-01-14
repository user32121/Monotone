package juniper.monotone.render;

import org.apache.commons.lang3.NotImplementedException;

import juniper.monotone.Monotone;
import juniper.monotone.command.LocatorMode;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.debug.DebugRenderer.Renderer;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.gizmo.GizmoDrawing;

public class LocatorDebugRenderer implements Renderer {
    @Override
    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum,
            float tickProgress) {
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
                GizmoDrawing.box(box, DrawStyle.filled(ColorHelper.getArgb(127, 0, 255, 0)));
                break;
            }
            case XZ: {
                box = box.expand(0, 1000, 0);
                GizmoDrawing.box(box, DrawStyle.filled(ColorHelper.getArgb(127, 0, 255, 0)));
                break;
            }
            default: {
                throw new NotImplementedException(Monotone.CONFIG.locatorMode.toString());
            }
        }
    }
}
