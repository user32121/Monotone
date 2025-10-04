package juniper.monotone.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import juniper.monotone.mixinInterface.DebugRendererInterface;
import juniper.monotone.render.InteractionMaskDebugRenderer;
import juniper.monotone.render.PathFindDebugRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(DebugRenderer.class)
public class DebugRendererMixin implements DebugRendererInterface {
    public final PathFindDebugRenderer pathFindDebugRenderer = new PathFindDebugRenderer();
    public final InteractionMaskDebugRenderer interactionMaskDebugRenderer = new InteractionMaskDebugRenderer();

    @Override
    public PathFindDebugRenderer getPathFindDebugRenderer() {
        return pathFindDebugRenderer;
    }

    @Inject(method = "reset", at = @At("TAIL"))
    public void reset(CallbackInfo info) {
        pathFindDebugRenderer.clear();
        interactionMaskDebugRenderer.clear();
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void render(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo info) {
        pathFindDebugRenderer.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
        interactionMaskDebugRenderer.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
    }
}
