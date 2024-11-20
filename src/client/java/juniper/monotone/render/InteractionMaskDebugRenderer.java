package juniper.monotone.render;

import juniper.monotone.Monotone;
import juniper.monotone.interaction.CuboidRegionMask;
import juniper.monotone.interaction.InteractionType;
import juniper.monotone.interaction.RegionMask;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.debug.DebugRenderer.Renderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class InteractionMaskDebugRenderer implements Renderer {
    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        Vec3d col = Vec3d.ZERO;
        for (InteractionType it : Monotone.CONFIG.interactionMask.keySet()) {
            if (it.equals(InteractionType.BREAK)) {
                col = new Vec3d(1, 0, 0);
            } else if (it.equals(InteractionType.PLACE)) {
                col = new Vec3d(0, 1, 0);
            } else {
                col = Vec3d.ZERO;
            }
            for (RegionMask rm : Monotone.CONFIG.interactionMask.get(it)) {
                if (rm instanceof CuboidRegionMask crm) {
                    DebugRenderer.drawBox(matrices, vertexConsumers, crm.from, crm.to, (float) col.x, (float) col.y, (float) col.z, 0.5f);
                }
            }
        }
    }
}
