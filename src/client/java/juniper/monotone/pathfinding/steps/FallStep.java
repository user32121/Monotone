package juniper.monotone.pathfinding.steps;

import juniper.monotone.pathfinding.GridView;
import juniper.monotone.pathfinding.PathFind.Tile.TILE_TYPE;
import juniper.monotone.task.InputManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class FallStep implements SmoothStep {
    public Vec3i offset;

    public FallStep(Vec3i offset) {
        this.offset = offset;
    }

    @Override
    public Vec3i getNewPos(GridView grid, Vec3i oldPos) throws InterruptedException {
        Vec3i newPos = oldPos.add(offset);
        if (!grid.hasTileType(newPos.down(), TILE_TYPE.FLOOR)) {
            return null;
        }
        for (BlockPos p : BlockPos.iterate(new BlockPos(newPos), new BlockPos(newPos).withY(oldPos.getY() + 1))) {
            if (!grid.hasTileType(p, TILE_TYPE.EMPTY)) {
                return null;
            }
        }
        return newPos;
    }

    @Override
    public boolean tick(MinecraftClient client, Vec3i destination) {
        if (client.player.getBlockPos().equals(destination)) {
            return true;
        } else if (client.player.getBlockPos().withY(destination.getY()).equals(destination)) {
            InputManager.forward = false;
            return false;
        }
        return SmoothStep.super.tick(client, destination);
    }

    @Override
    public Vec3i getOffset() {
        return offset;
    }
}
