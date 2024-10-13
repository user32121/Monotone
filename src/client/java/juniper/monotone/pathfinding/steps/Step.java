package juniper.monotone.pathfinding.steps;

import org.jetbrains.annotations.Nullable;

import juniper.monotone.pathfinding.GridView;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3i;

public interface Step {
    /**
     * @return the new position if this step was taken, or null if this step cannot be taken
     */
    public Vec3i getNewPos(GridView grid, Vec3i oldPos) throws InterruptedException;

    /**
     * @param prevStep the previous step taken, to allow for factoring in smoothness of path
     * @return the cost of this step
     */
    public int getCost(@Nullable Step prevStep);

    /**
     * @return true if finished
     */
    public boolean tick(MinecraftClient client, Vec3i destination);
}
