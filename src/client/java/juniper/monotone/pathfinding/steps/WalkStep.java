package juniper.monotone.pathfinding.steps;

import org.jetbrains.annotations.Nullable;

import juniper.monotone.mixin.MouseInputAccessor;
import juniper.monotone.pathfinding.GridView;
import juniper.monotone.pathfinding.PathFind.Tile.TILE_TYPE;
import juniper.monotone.task.InputManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

public class WalkStep implements Step {
    protected Vec3i offset;

    public WalkStep(Vec3i offset) {
        this.offset = offset;
    }

    @Override
    public Vec3i getNewPos(GridView grid, Vec3i oldPos) throws InterruptedException {
        Vec3i newPos = oldPos.add(offset);
        if (!grid.hasTileType(newPos, TILE_TYPE.EMPTY) || !grid.hasTileType(newPos.up(), TILE_TYPE.EMPTY)
                || !grid.hasTileType(newPos.down(), TILE_TYPE.FLOOR)) {
            return null;
        }
        return newPos;
    }

    @Override
    public int getCost(@Nullable Step prevStep) {
        int smoothness = 0;
        if (prevStep != null && prevStep instanceof WalkStep ws) {
            //dot product, the closer the angles, the higher the smoothness
            int dot = offset.getX() * ws.offset.getX() + offset.getZ() * ws.offset.getZ();
            double dotNorm = dot / Math.sqrt(offset.getSquaredDistance(0, 0, 0)) / Math.sqrt(ws.offset.getSquaredDistance(0, 0, 0));
            smoothness = (int) (2 * dotNorm);
        }
        return (int) (10 * Math.sqrt(offset.getSquaredDistance(0, 0, 0))) - smoothness;
    }

    @Override
    public boolean tick(MinecraftClient client, Vec3i destination) {
        float deltaAngle = MathHelper.subtractAngles((float) Math.toDegrees(Math.atan2(destination.getZ() + 0.5 - client.player.getZ(), destination.getX() + 0.5 - client.player.getX())) - 90,
                client.player.getYaw());
        MouseInputAccessor mia = (MouseInputAccessor) (Object) client.mouse;
        mia.setCursorDeltaX(mia.getCursorDeltaX() - deltaAngle * 2);

        InputManager.forward = true;
        if (client.player.getBlockPos().equals(destination)) {
            InputManager.forward = false;
            return true;
        }
        return false;
    }
}
