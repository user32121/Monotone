package juniper.monotone.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import juniper.monotone.mixinInterface.DebugRendererInterface;
import juniper.monotone.render.InteractionMaskDebugRenderer;
import juniper.monotone.render.LocatorDebugRenderer;
import juniper.monotone.render.PathFindDebugRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.world.debug.DebugDataStore;

@Mixin(DebugRenderer.class)
public class DebugRendererMixin implements DebugRendererInterface {
    public final PathFindDebugRenderer pathFindDebugRenderer = new PathFindDebugRenderer();
    public final InteractionMaskDebugRenderer interactionMaskDebugRenderer = new InteractionMaskDebugRenderer();
    public final LocatorDebugRenderer locatorDebugRenderer = new LocatorDebugRenderer();

    @Override
    public PathFindDebugRenderer getPathFindDebugRenderer() {
        return pathFindDebugRenderer;
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void render(
            MatrixStack matrices, Frustum frustum, VertexConsumerProvider.Immediate vertexConsumers, double cameraX,
            double cameraY, double cameraZ, boolean lateDebug, CallbackInfo info) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        DebugDataStore store = minecraftClient.getNetworkHandler().getDebugDataStore();
        pathFindDebugRenderer.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ, store, frustum);
        interactionMaskDebugRenderer.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ, store, frustum);
        locatorDebugRenderer.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ, store, frustum);
    }
}
