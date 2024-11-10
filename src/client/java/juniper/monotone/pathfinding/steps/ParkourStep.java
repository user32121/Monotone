package juniper.monotone.pathfinding.steps;

import java.util.Iterator;

import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Iterables;

import juniper.monotone.Monotone;
import juniper.monotone.mixin.MouseInputAccessor;
import juniper.monotone.pathfinding.GridView;
import juniper.monotone.pathfinding.PathFind.Tile.TILE_TYPE;
import juniper.monotone.task.InputManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;

public class ParkourStep implements Step {
    public Vec3i offset;

    private enum STATE {
        PREPARING, JUMPING, JUMPED
    }

    private STATE state = STATE.PREPARING;

    public ParkourStep(Vec3i offset) {
        this.offset = offset;
    }

    @Override
    public Vec3i getNewPos(GridView grid, Vec3i oldPos) throws InterruptedException {
        Vec3i newPos = oldPos.add(offset);
        if (!grid.hasTileType(newPos.down(), TILE_TYPE.FLOOR)) {
            return null;
        }
        for (BlockPos p : BlockPos.iterate(new BlockPos(oldPos), new BlockPos(newPos))) {
            if ((!p.equals(oldPos.down()) && !p.equals(newPos.down()) && !grid.hasTileType(p, TILE_TYPE.EMPTY)) || !grid.hasTileType(p.up(), TILE_TYPE.EMPTY)
                    || !grid.hasTileType(p.up(2), TILE_TYPE.EMPTY)) {
                return null;
            }
        }
        return newPos;
    }

    @Override
    public int getCost(@Nullable Step prevStep) {
        return (int) (Math.sqrt(offset.getSquaredDistance(Vec3i.ZERO)) * 20);
    }

    @Override
    public boolean tick(MinecraftClient client, Vec3i destination) {
        Vec3d destinationDelta = client.player.getPos().subtract(Vec3d.ofBottomCenter(destination));
        float destinationDeltaAngle = MathHelper.subtractAngles((float) Math.toDegrees(Math.atan2(destinationDelta.z, destinationDelta.x)) + 90, client.player.getYaw());
        switch (state) {
            case PREPARING: {
                InputManager.reset();
                InputManager.sneak = true;

                Box bb = client.player.getBoundingBox().offset(0, -0.001, 0);
                Iterator<VoxelShape> collisions = client.world.getCollisions(client.player, bb).iterator();
                if (!collisions.hasNext()) {
                    break;
                }
                Vec3d blockDelta = client.player.getPos().subtract(collisions.next().getBoundingBox().getCenter()).withAxis(Axis.Y, 0);
                if (Math.abs(destinationDeltaAngle) >= 1) {
                    MouseInputAccessor mia = (MouseInputAccessor) (Object) client.mouse;
                    mia.setCursorDeltaX(mia.getCursorDeltaX() - destinationDeltaAngle * 2);
                }
                if (!blockDelta.isInRange(Vec3d.ZERO, 0.1)) {
                    float blockDeltaAngle = MathHelper.subtractAngles((float) Math.toDegrees(Math.atan2(blockDelta.z, blockDelta.x)) + 90, client.player.getYaw());
                    InputManager.forward = MathHelper.angleBetween(blockDeltaAngle, 0) <= 60;
                    InputManager.left = MathHelper.angleBetween(blockDeltaAngle, 90) <= 60;
                    InputManager.back = MathHelper.angleBetween(blockDeltaAngle, 180) <= 60;
                    InputManager.right = MathHelper.angleBetween(blockDeltaAngle, 270) <= 60;
                }
                if (Math.abs(destinationDeltaAngle) < 1 && blockDelta.isInRange(Vec3d.ZERO, 0.1)) {
                    InputManager.reset();
                    state = STATE.JUMPING;
                }
                break;
            }
            case JUMPING: {
                InputManager.sprint = !destinationDelta.isInRange(Vec3d.ZERO, 2.5);
                InputManager.forward = true;
                state = STATE.JUMPING;

                if (destinationDelta.isInRange(Vec3d.ZERO, 4)) {
                    Box bb = client.player.getBoundingBox().offset(0, -0.001, 0);
                    if (!Iterables.isEmpty(client.world.getCollisions(client.player, bb))
                            && Iterables.isEmpty(client.world.getCollisions(client.player, bb.offset(client.player.getVelocity().multiply(3))))) {
                        Monotone.LOGGER.info("jump from {}", client.player.getBlockPos());
                        InputManager.jump = true;
                        state = STATE.JUMPED;
                    }
                } else {
                    //jump at last moment if jump is long
                    Box bb = client.player.getBoundingBox().offset(0, -0.001, 0);
                    if (!Iterables.isEmpty(client.world.getCollisions(client.player, bb))
                            && Iterables.isEmpty(client.world.getCollisions(client.player, bb.offset(client.player.getVelocity())))) {
                        Monotone.LOGGER.info("late jump from {}", client.player.getBlockPos());
                        InputManager.jump = true;
                        state = STATE.JUMPED;
                    }
                }
                break;
            }
            case JUMPED: {
                if (client.player.getBlockPos().withY(destination.getY()).equals(destination) || client.player.isOnGround()) {
                    InputManager.reset();
                    InputManager.sneak = true;
                    InputManager.back = true;
                }
                if (client.player.getBlockPos().equals(destination) || client.player.isOnGround()) {
                    InputManager.reset();
                    state = STATE.PREPARING;
                    return true;
                }
                break;
            }
            default:
                throw new NotImplementedException();
        }
        return false;
    }
}
