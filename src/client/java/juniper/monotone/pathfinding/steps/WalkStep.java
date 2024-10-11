package juniper.monotone.pathfinding.steps;

import juniper.monotone.pathfinding.GridView;
import juniper.monotone.pathfinding.PathFind.Tile.TILE_TYPE;
import juniper.monotone.task.InputManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3i;

public class WalkStep implements Step {
    private Vec3i offset;

    public WalkStep(Vec3i offset) {
        this.offset = offset;
    }

    @Override
    public Vec3i getNewPos(GridView grid, Vec3i oldPos) throws InterruptedException {
        Vec3i newPos = oldPos.add(offset);
        if (!grid.hasTileType(newPos, TILE_TYPE.EMPTY) || !grid.hasTileType(newPos.up(), TILE_TYPE.EMPTY) || !grid.hasTileType(newPos.down(), TILE_TYPE.FLOOR)) {
            return null;
        }
        return newPos;
    }

    @Override
    public int getCost() {
        return 10;
    }

    @Override
    public boolean tick(MinecraftClient client, Vec3i destination) {
        //TODO turning
        InputManager.forward = true;
        if (client.player.getBlockPos().equals(destination)) {
            InputManager.forward = false;
            return true;
        }
        return false;
    }
}
