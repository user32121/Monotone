package juniper.monotone.render;

import java.util.function.Predicate;

import juniper.monotone.Monotone;
import juniper.monotone.interaction.InteractionType;
import juniper.monotone.interaction.MaskDisplayType;
import juniper.monotone.interaction.RegionMask;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.debug.DebugRenderer.Renderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.debug.DebugDataStore;

public class InteractionMaskDebugRenderer implements Renderer {
    @Override
    public void render(
            MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY,
            double cameraZ, DebugDataStore store, Frustum frustum) {
        MinecraftClient client = MinecraftClient.getInstance();
        World world = client.world;
        for (InteractionType it : Monotone.CONFIG.interactionMask.keySet()) {
            MaskDisplayType mdt = Monotone.CONFIG.interactionMaskDisplay.getOrDefault(it, MaskDisplayType.UNMATCHING);
            Vec3d col;
            Predicate<Pair<BlockPos, BlockState>> blockRenderPredicate;
            if (it.equals(InteractionType.BREAK)) {
                col = new Vec3d(1, 0, 0);
                blockRenderPredicate = pair -> !world.getBlockState(pair.getLeft()).isAir();
            } else if (it.equals(InteractionType.PLACE)) {
                col = new Vec3d(0, 1, 0);
                blockRenderPredicate = pair -> {
                    BlockState state = world.getBlockState(pair.getLeft());
                    return pair.getRight() == null ? state.isAir() : state != pair.getRight();
                };
            } else {
                continue;
            }
            for (RegionMask rm : Monotone.CONFIG.interactionMask.get(it)) {
                if (mdt.equals(MaskDisplayType.BOUNDS)) {
                    Pair<BlockPos, BlockPos> bounds = rm.getBounds();
                    DebugRenderer.drawBox(matrices, vertexConsumers, bounds.getLeft(), bounds.getRight(),
                            (float) col.x, (float) col.y, (float) col.z, 0.5f);
                } else if (mdt.equals(MaskDisplayType.UNMATCHING)) {
                    for (Pair<BlockPos, BlockState> pair : rm) {
                        if (blockRenderPredicate.test(pair)) {
                            DebugRenderer.drawBox(matrices, vertexConsumers,
                                    new Box(pair.getLeft()).offset(-cameraX, -cameraY, -cameraZ), (float) col.x,
                                    (float) col.y, (float) col.z, 0.5f);
                        }
                    }
                }
            }
        }
    }
}
