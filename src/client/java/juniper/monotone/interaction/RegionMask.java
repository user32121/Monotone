package juniper.monotone.interaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import juniper.monotone.Monotone;
import juniper.monotone.mixinInterface.ReadOnlyWorldInterface;
import juniper.monotone.util.MapUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface RegionMask extends Iterable<Pair<BlockPos, BlockState>> {
    /**
     * Check if the position and state would be accepted by the mask
     * @param state the state, or null if state is not relevant (e.g. BREAK mask)
     * @return true if the state is valid at the position by the mask
     */
    public boolean contains(BlockPos pos, BlockState state);

    public Pair<BlockPos, BlockPos> getBounds();

    /**
     * Called when deserializing to ensure nonserialized fields are initialized
     */
    public void init() throws IOException;

    public static ActionResult checkBreakMask(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
        return checkMask(pos, InteractionType.BREAK, null);
    }

    public static ActionResult checkPlaceMask(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getStackInHand(hand);
        ItemUsageContext iuc = new ItemUsageContext(world, player, hand, stack, hitResult);
        ReadOnlyWorldInterface rowi = (ReadOnlyWorldInterface) world;
        rowi.enable();
        stack.useOnBlock(iuc);
        rowi.disable();
        List<Pair<BlockPos, BlockState>> changes = rowi.getBlockChanges();
        for (Pair<BlockPos, BlockState> change : changes) {
            ActionResult ar = checkMask(change.getLeft(), InteractionType.PLACE, change.getRight());
            if (!ar.equals(ActionResult.PASS)) {
                rowi.clearBlockChanges();
                return ar;
            }
        }
        rowi.clearBlockChanges();
        return ActionResult.PASS;
    }

    public static ActionResult checkMask(BlockPos pos, InteractionType type, BlockState state) {
        if (!Monotone.CONFIG.interactionMaskEnabled.getOrDefault(type, false)) {
            return ActionResult.PASS;
        }
        MapUtil.ensureKey2(Monotone.CONFIG.interactionMask, type, ArrayList::new);
        List<RegionMask> rms = Monotone.CONFIG.interactionMask.get(type);
        for (RegionMask rm : rms) {
            if (rm.contains(pos, state)) {
                return ActionResult.PASS;
            }
        }
        return ActionResult.FAIL;
    }
}
