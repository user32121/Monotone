package juniper.monotone.interaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import juniper.monotone.Monotone;
import juniper.monotone.util.MapUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface RegionMask extends Iterable<Pair<BlockPos, BlockState>> {
    public boolean contains(BlockPos pos);

    public Pair<BlockPos, BlockPos> getBounds();

    /**
     * Called when deserializing to ensure nonserialized fields are initialized
     */
    public void init() throws IOException;

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
