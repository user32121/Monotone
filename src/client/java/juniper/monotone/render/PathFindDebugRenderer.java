package juniper.monotone.render;

import java.util.ArrayList;
import java.util.List;

import juniper.monotone.pathfinding.PathFind;
import juniper.monotone.pathfinding.PathFind.Tile;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.debug.DebugRenderer.Renderer;
import net.minecraft.util.Pair;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.gizmo.GizmoDrawing;

public class PathFindDebugRenderer implements Renderer {
    private List<PathFind> paths = new ArrayList<>();

    public void addPath(PathFind path) {
        paths.add(path);
    }

    @Override
    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum,
            float tickProgress) {
        if (!PathFind.getShowPath()) {
            return;
        }
        for (int i = 0; i < paths.size(); ++i) {
            PathFind path = paths.get(i);
            if (path.path == null) {
                continue;
            }
            Vec3d prev = null;
            for (Pair<Vec3i, Tile> cur : path.path) {
                Vec3d pos = Vec3d.ofCenter(cur.getLeft());
                if (prev != null) {
                    GizmoDrawing.line(prev, pos, ColorHelper.getArgb(255, 0, 255, 0));
                }
                prev = pos;
            }
            if (path.path.isEmpty()) {
                paths.remove(i);
                --i;
            }
        }
    }
}
