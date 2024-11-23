package juniper.monotone.interaction;

import java.util.ArrayList;
import java.util.List;

import juniper.monotone.Monotone;
import juniper.monotone.util.MapUtil;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface RegionMask {
    public boolean contains(BlockPos pos);

    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vec3d cameraPos, Vec3d color);

    public static ActionResult checkBreakMask(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
        return checkMask(pos, InteractionType.BREAK);
    }

    public static ActionResult checkPlaceMask(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        return checkMask(hitResult.getBlockPos().offset(hitResult.getSide()), InteractionType.PLACE);
    }

    public static ActionResult checkMask(BlockPos pos, InteractionType type) {
        if (!Monotone.CONFIG.interactionMaskEnabled.getOrDefault(type, false)) {
            return ActionResult.PASS;
        }
        MapUtil.ensureKey2(Monotone.CONFIG.interactionMask, type, ArrayList::new);
        List<RegionMask> rms = Monotone.CONFIG.interactionMask.get(type);
        for (RegionMask rm : rms) {
            if (rm.contains(pos)) {
                return ActionResult.PASS;
            }
        }
        return ActionResult.FAIL;
    }
}
