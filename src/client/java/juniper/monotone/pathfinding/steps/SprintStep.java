package juniper.monotone.pathfinding.steps;

import juniper.monotone.pathfinding.GridView;
import juniper.monotone.pathfinding.PathFind.Tile.TILE_TYPE;
import net.minecraft.util.math.Vec3i;

public class SprintStep implements SmoothStep {
    public Vec3i offset;

    public SprintStep(Vec3i offset) {
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
    public Vec3i getOffset() {
        return offset;
    }
}
