package juniper.monotone.pathfinding;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.common.collect.Lists;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import juniper.monotone.Monotone;
import juniper.monotone.pathfinding.steps.DiagonalSprintStep;
import juniper.monotone.pathfinding.steps.FallStep;
import juniper.monotone.pathfinding.steps.JumpStep;
import juniper.monotone.pathfinding.steps.ParkourStep;
import juniper.monotone.pathfinding.steps.SprintStep;
import juniper.monotone.pathfinding.steps.Step;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

/**
 * Runs pathfinding in a separate threads
 */
public class PathFind extends Thread {
    public static final RequiredArgumentBuilder<FabricClientCommandSource, ?> INTERVAL_ARG = ClientCommandManager.argument("seconds", IntegerArgumentType.integer(1));
    public static final RequiredArgumentBuilder<FabricClientCommandSource, ?> RADIUS_ARG = ClientCommandManager.argument("chunks", FloatArgumentType.floatArg(0));
    public static final RequiredArgumentBuilder<FabricClientCommandSource, ?> ANGLE_ARG = ClientCommandManager.argument("degrees", FloatArgumentType.floatArg(0));
    public static final RequiredArgumentBuilder<FabricClientCommandSource, ?> ENABLED_ARG = ClientCommandManager.argument("enabled", BoolArgumentType.bool());

    private static final int HEURISTIC_ESTIMATED_COST = 10;
    private static final List<Step> STEPS = new ArrayList<>();
    static {
        STEPS.add(new SprintStep(new Vec3i(-1, 0, 0)));
        STEPS.add(new SprintStep(new Vec3i(1, 0, 0)));
        STEPS.add(new SprintStep(new Vec3i(0, 0, -1)));
        STEPS.add(new SprintStep(new Vec3i(0, 0, 1)));

        STEPS.add(new DiagonalSprintStep(new Vec3i(-1, 0, -1)));
        STEPS.add(new DiagonalSprintStep(new Vec3i(-1, 0, 1)));
        STEPS.add(new DiagonalSprintStep(new Vec3i(1, 0, -1)));
        STEPS.add(new DiagonalSprintStep(new Vec3i(1, 0, 1)));

        STEPS.add(new DiagonalSprintStep(new Vec3i(-2, 0, -1)));
        STEPS.add(new DiagonalSprintStep(new Vec3i(-1, 0, -2)));
        STEPS.add(new DiagonalSprintStep(new Vec3i(-2, 0, 1)));
        STEPS.add(new DiagonalSprintStep(new Vec3i(-1, 0, 2)));
        STEPS.add(new DiagonalSprintStep(new Vec3i(2, 0, -1)));
        STEPS.add(new DiagonalSprintStep(new Vec3i(1, 0, -2)));
        STEPS.add(new DiagonalSprintStep(new Vec3i(2, 0, 1)));
        STEPS.add(new DiagonalSprintStep(new Vec3i(1, 0, 2)));

        STEPS.add(new JumpStep(new Vec3i(-1, 1, 0)));
        STEPS.add(new JumpStep(new Vec3i(1, 1, 0)));
        STEPS.add(new JumpStep(new Vec3i(0, 1, -1)));
        STEPS.add(new JumpStep(new Vec3i(0, 1, 1)));

        for (int i = 0; i < 3; ++i) {
            STEPS.add(new FallStep(new Vec3i(-1, -i, 0)));
            STEPS.add(new FallStep(new Vec3i(1, -i, 0)));
            STEPS.add(new FallStep(new Vec3i(0, -i, -1)));
            STEPS.add(new FallStep(new Vec3i(0, -i, 1)));
        }

        //TODO distance 5 jumps (4 block gap)
        //currently it seems incapable of jumping those gaps for some reason
        for (BlockPos offset : BlockPos.iterate(-4, -1, -4, 4, 1, 4)) {
            if (!offset.isWithinDistance(Vec3i.ZERO, 1) && offset.isWithinDistance(Vec3i.ZERO, 4.001)) {
                STEPS.add(new ParkourStep(new BlockPos(offset)));
            }
        }
    }

    public static class Tile {
        public enum TILE_TYPE {
            UNKNOWN, OUT_OF_BOUNDS, EMPTY, FLOOR, OBSTACLE,
        }

        public TILE_TYPE type = TILE_TYPE.UNKNOWN;
        public int cost = Integer.MAX_VALUE;
        public Vec3i travelFrom = null;
        public Step travelUsing = null;

        public Tile(TILE_TYPE type) {
            this.type = type;
        }
    }

    public static int setNotifyInterval(CommandContext<FabricClientCommandSource> ctx) {
        Monotone.CONFIG.pathfindNotifyIntervalSeconds = IntegerArgumentType.getInteger(ctx, INTERVAL_ARG.getName());
        ctx.getSource().sendFeedback(Text.literal(String.format("Set pathfinding notify interval to %s seconds", Monotone.CONFIG.pathfindNotifyIntervalSeconds)));
        return 1;
    }

    public static int getNotifyInterval(CommandContext<FabricClientCommandSource> ctx) {
        ctx.getSource().sendFeedback(Text.literal(String.format("Pathfinding notify interval is set to %s seconds", Monotone.CONFIG.pathfindNotifyIntervalSeconds)));
        return 1;
    }

    public static int setSearchRadius(CommandContext<FabricClientCommandSource> ctx) {
        Monotone.CONFIG.pathfindSearchRadiusChunks = FloatArgumentType.getFloat(ctx, RADIUS_ARG.getName());
        ctx.getSource().sendFeedback(Text.literal(String.format("Set pathfinding maximum search radius to %s chunks", Monotone.CONFIG.pathfindSearchRadiusChunks)));
        return 1;
    }

    public static int getSearchRadius(CommandContext<FabricClientCommandSource> ctx) {
        ctx.getSource().sendFeedback(Text.literal(String.format("Pathfinding maximum search radius is set to %s chunks", Monotone.CONFIG.pathfindSearchRadiusChunks)));
        return 1;
    }

    public static float getSearchRadius() {
        return Monotone.CONFIG.pathfindSearchRadiusChunks;
    }

    public static int setSearchAngle(CommandContext<FabricClientCommandSource> ctx) {
        Monotone.CONFIG.pathfindSearchAngleDegrees = FloatArgumentType.getFloat(ctx, ANGLE_ARG.getName());
        ctx.getSource().sendFeedback(Text.literal(String.format("Set pathfinding search angle to %s degrees", Monotone.CONFIG.pathfindSearchAngleDegrees)));
        return 1;
    }

    public static int getSearchAngle(CommandContext<FabricClientCommandSource> ctx) {
        ctx.getSource().sendFeedback(Text.literal(String.format("Pathfinding search angle is set to %s degrees", Monotone.CONFIG.pathfindSearchAngleDegrees)));
        return 1;
    }

    public static float getSearchAngle() {
        return Monotone.CONFIG.pathfindSearchAngleDegrees;
    }

    public static int setShowPath(CommandContext<FabricClientCommandSource> ctx) {
        Monotone.CONFIG.pathfindShowPath = BoolArgumentType.getBool(ctx, ENABLED_ARG.getName());
        ctx.getSource().sendFeedback(Text.literal(String.format("Set show path to %s", Monotone.CONFIG.pathfindShowPath)));
        return 1;
    }

    public static int getShowPath(CommandContext<FabricClientCommandSource> ctx) {
        ctx.getSource().sendFeedback(Text.literal(String.format("Show path is set to %s", Monotone.CONFIG.pathfindShowPath)));
        return 1;
    }

    public static boolean getShowPath() {
        return Monotone.CONFIG.pathfindShowPath;
    }

    public List<Pair<Vec3i, Tile>> path;
    public BlockingQueue<Text> feedback = new LinkedBlockingQueue<>();

    private Vec3i start;
    private Vec3i target;
    private GridView grid;
    private boolean fuzzy;

    public PathFind(Vec3i start, Vec3i target, GridView grid, boolean fuzzy) {
        this.start = start;
        this.target = target;
        this.grid = grid;
        this.fuzzy = fuzzy;
    }

    @Override
    public void run() {
        try {
            long blocksExplored = 0;
            long lastNotify = System.currentTimeMillis();
            Queue<Vec3i> toProcess = new PriorityQueue<>((v1, v2) -> {
                int cost1;
                int cost2;
                try {
                    cost1 = grid.getTile(v1).cost;
                    cost2 = grid.getTile(v2).cost;
                } catch (InterruptedException e) {
                    return 0;
                }
                int heuristic1 = HEURISTIC_ESTIMATED_COST * v1.getManhattanDistance(target);
                int heuristic2 = HEURISTIC_ESTIMATED_COST * v2.getManhattanDistance(target);
                return (cost1 + heuristic1) - (cost2 + heuristic2);
            });

            toProcess.add(start);
            grid.getTile(start).cost = 0;
            grid.getTile(start).travelFrom = start;
            while (toProcess.size() > 0 && grid.getTile(target).travelFrom == null) {
                long now = System.currentTimeMillis();
                if (now - lastNotify >= Monotone.CONFIG.pathfindNotifyIntervalSeconds * 1000) {
                    feedback.add(Text.literal(String.format("pathfinding (%s blocks explored) ...", blocksExplored)));
                    lastNotify = now;
                }
                Vec3i pos = toProcess.remove();
                ++blocksExplored;
                for (Step step : STEPS) {
                    Vec3i newPos = step.getNewPos(grid, pos);
                    if (newPos == null) {
                        continue;
                    }
                    int newCost = grid.getTile(pos).cost + step.getCost(grid.getTile(pos).travelUsing);
                    if (newCost < grid.getTile(newPos).cost) {
                        Tile t = grid.getTile(newPos);
                        t.cost = newCost;
                        t.travelFrom = pos;
                        t.travelUsing = step;
                        toProcess.add(newPos);
                    }
                }
            }

            //move target if fuzzy
            if (fuzzy && grid.getTile(target).travelFrom == null) {
                target = grid.getClosestReachablePos(target);
            }

            //extract path
            Vec3i pos = target;
            path = new LinkedList<>();
            while (!pos.equals(start)) {
                Tile t = grid.getTile(pos);
                path.add(new Pair<>(pos, t));
                pos = t.travelFrom;
                if (pos == null) {
                    path = null;
                    feedback.add(Text.literal(String.format("Unable to reach %s (%s) (%s blocks explored)", target, grid.getTile(target).type, blocksExplored)).formatted(Formatting.RED));
                    return;
                }
            }
            path = Lists.reverse(path);

            feedback.add(Text.literal(String.format("Found a path of length %s to %s (%s blocks explored)", path.size(), target, blocksExplored)));
        } catch (InterruptedException e) {
            feedback.add(Text.literal(String.format("An exception occurred: %s", e)).formatted(Formatting.RED));
        } catch (Exception e) {
            feedback.add(Text.literal(String.format("An exception occurred: %s", e)).formatted(Formatting.RED));
            throw e;
        } finally {
            //free up memory cause this thing gets really large
            grid.clear();
        }
    }
}
