package juniper.monotone.interaction;

import java.util.ArrayList;
import java.util.List;

import juniper.monotone.Monotone;
import juniper.monotone.util.MapUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface RegionMask {
    public boolean contains(BlockPos pos);

    public static ActionResult checkBreakMask(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
        if (!Monotone.CONFIG.interactionMaskEnabled.getOrDefault(InteractionType.BREAK, false)) {
            return ActionResult.PASS;
        }
        MapUtil.ensureKey2(Monotone.CONFIG.interactionMask, InteractionType.BREAK, ArrayList::new);
        List<RegionMask> rms = Monotone.CONFIG.interactionMask.get(InteractionType.BREAK);
        for (RegionMask rm : rms) {
            if (rm.contains(pos)) {
                return ActionResult.PASS;
            }
        }
        return ActionResult.FAIL;
    }
}
