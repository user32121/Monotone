package juniper.monotone.pathfinding.steps;

import juniper.monotone.pathfinding.GridView;
import juniper.monotone.pathfinding.PathFind.Tile.TILE_TYPE;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class DiagonalSprintStep extends SprintStep {
    public DiagonalSprintStep(Vec3i offset) {
        super(offset);
    }

    @Override
    public Vec3i getNewPos(GridView grid, Vec3i oldPos) throws InterruptedException {
        Vec3i newPos = oldPos.add(offset);
        for (BlockPos p : BlockPos.iterate(new BlockPos(oldPos), new BlockPos(newPos))) {
            if (!grid.hasTileType(p, TILE_TYPE.EMPTY) || !grid.hasTileType(p.up(), TILE_TYPE.EMPTY) || !grid.hasTileType(p.down(), TILE_TYPE.FLOOR)) {
                return null;
            }
        }
        return newPos;
    }
}
