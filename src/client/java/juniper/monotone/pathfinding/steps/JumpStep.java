package juniper.monotone.pathfinding.steps;

import juniper.monotone.pathfinding.GridView;
import juniper.monotone.pathfinding.PathFind.Tile.TILE_TYPE;
import juniper.monotone.task.InputManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3i;

public class JumpStep implements SmoothStep {
    public Vec3i offset;

    public JumpStep(Vec3i offset) {
        this.offset = offset;
    }

    @Override
    public Vec3i getNewPos(GridView grid, Vec3i oldPos) throws InterruptedException {
        Vec3i newPos = oldPos.add(offset);
        if (!grid.hasTileType(oldPos.up().up(), TILE_TYPE.EMPTY) || !grid.hasTileType(newPos, TILE_TYPE.EMPTY) || !grid.hasTileType(newPos.up(), TILE_TYPE.EMPTY)
                || !grid.hasTileType(newPos.down(), TILE_TYPE.FLOOR)) {
            return null;
        }
        return newPos;
    }

    @Override
    public boolean tick(MinecraftClient client, Vec3i destination) {
        InputManager.jump = true;
        boolean ret = SmoothStep.super.tick(client, destination);
        if (ret) {
            InputManager.jump = false;
        }
        return ret;
    }

    @Override
    public Vec3i getOffset() {
        return offset;
    }
}
