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
import net.minecraft.client.render.debug.DebugRenderer;
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
    public void render(Frustum frustum, double cameraX, double cameraY, double cameraZ, float tickProgress,
            CallbackInfo info) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        DebugDataStore store = minecraftClient.getNetworkHandler().getDebugDataStore();
        pathFindDebugRenderer.render(cameraX, cameraY, cameraZ, store, frustum, tickProgress);
        interactionMaskDebugRenderer.render(cameraX, cameraY, cameraZ, store, frustum, tickProgress);
        locatorDebugRenderer.render(cameraX, cameraY, cameraZ, store, frustum, tickProgress);
    }
}
