package juniper.monotone.init;

import java.util.function.Function;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import juniper.monotone.Monotone;
import juniper.monotone.command.InteractionMask;
import juniper.monotone.command.RotationPlane;
import juniper.monotone.command.TaskQueue;
import juniper.monotone.command.VisibilitySetting;
import juniper.monotone.pathfinding.PathFind;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class MonotoneCommand {
    public static void init() {
        makeCommand("visibility", node -> node
                .then(VisibilitySetting.SETTING_ARG.executes(VisibilitySetting::getHighlight).then(VisibilitySetting.VALUE_ARG.executes(VisibilitySetting::setHighlight))));
        makeCommand("limit",
                node -> node.then(RotationPlane.PLANE_ARG.executes(RotationPlane::getLimit).then(ClientCommandManager.literal("clear").executes(RotationPlane::clearLimit))
                        .then(RotationPlane.MIN_ARG
                                .then(RotationPlane.MAX_ARG.executes(RotationPlane::setLimit)
                                        .then(RotationPlane.DEVIANCE_ARG.executes(
                                                RotationPlane::setLimitWithDeviance)
                                                .then(RotationPlane.SOFTCAP_ARG
                                                        .executes(RotationPlane::setLimitWithDevianceSoftcap)))))));
        makeCommand("tasks",
                node -> node.then(ClientCommandManager.literal("start").executes(TaskQueue::startTasks)).then(ClientCommandManager.literal("stop").executes(TaskQueue::stopTasks))
                        .then(ClientCommandManager.literal("skip").executes(TaskQueue::skipTask)).then(ClientCommandManager.literal("clear").executes(TaskQueue::clearTasks))
                        .then(ClientCommandManager.literal("view").executes(TaskQueue::viewTasks)).then(TaskQueue.makeInfoCommand()).then(TaskQueue.makeAddCommand()));
        makeCommand("pathfinding",
                node -> node.then(ClientCommandManager.literal("notify_interval").executes(PathFind::getNotifyInterval).then(PathFind.INTERVAL_ARG.executes(PathFind::setNotifyInterval)))
                        .then(ClientCommandManager.literal("search_radius").executes(PathFind::getSearchRadius).then(PathFind.RADIUS_ARG.executes(PathFind::setSearchRadius)))
                        .then(ClientCommandManager.literal("search_angle").executes(PathFind::getSearchAngle).then(PathFind.ANGLE_ARG.executes(PathFind::setSearchAngle)))
                        .then(ClientCommandManager.literal("show_path").executes(PathFind::getShowPath).then(PathFind.ENABLED_ARG.executes(PathFind::setShowPath))));
        makeCommand("mask",
                node -> node.then(InteractionMask.INTERACTION_ARG
                        .then(ClientCommandManager.literal("add")
                                .then(ClientCommandManager.literal("cuboid").then(InteractionMask.FROM_ARG.then(InteractionMask.TO_ARG.executes(InteractionMask::addCuboid))))
                                .then(ClientCommandManager.literal("schematic").then(InteractionMask.PATH_ARG.executes(InteractionMask::addSchematic))))
                        .then(ClientCommandManager.literal("remove").then(InteractionMask.INDEX_ARG.executes(InteractionMask::remove)))
                        .then(ClientCommandManager.literal("list").executes(InteractionMask::list))
                                        .then(ClientCommandManager.literal("display")
                                                        .executes(InteractionMask::getDisplay)
                                                        .then(InteractionMask.DISPLAY_ARG
                                                                        .executes(InteractionMask::setDisplay)))
                        .then(ClientCommandManager.literal("clear").executes(InteractionMask::clear))
                        .then(ClientCommandManager.literal("enabled").executes(InteractionMask::getEnabled).then(InteractionMask.ENABLED_ARG.executes(InteractionMask::setEnabled)))));
    }

    private static void makeCommand(String command, Function<LiteralArgumentBuilder<FabricClientCommandSource>, LiteralArgumentBuilder<FabricClientCommandSource>> buildCommand) {
        ClientCommandRegistrationCallback.EVENT
                .register((dispatcher, registry) -> dispatcher.register(buildCommand.apply(ClientCommandManager.literal(Monotone.MODID + ":" + command))));
    }
}
