package juniper.monotone.init;

import java.util.function.Function;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import juniper.monotone.Monotone;
import juniper.monotone.command.HighlightTarget;
import juniper.monotone.command.RotationPlane;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class MonotoneCommand {
    public static void init() {
        makeCommand("highlight",
                node -> node.then(HighlightTarget.TARGET_ARG.executes(HighlightTarget::getHighlight).then(HighlightTarget.VALUE_ARG.executes(HighlightTarget::setHighlight))));
        makeCommand("limit",
                node -> node.then(RotationPlane.PLANE_ARG.executes(RotationPlane::getLimit).then(ClientCommandManager.literal("clear").executes(RotationPlane::clearLimit))
                        .then(RotationPlane.MIN_ARG
                                .then(RotationPlane.MAX_ARG.executes(RotationPlane::setLimit).then(RotationPlane.DEVIANCE_ARG.executes(RotationPlane::setLimitWithDeviance))))));
    }

    private static void makeCommand(String command, Function<LiteralArgumentBuilder<FabricClientCommandSource>, LiteralArgumentBuilder<FabricClientCommandSource>> buildCommand) {
        ClientCommandRegistrationCallback.EVENT
                .register((dispatcher, registry) -> dispatcher.register(buildCommand.apply(ClientCommandManager.literal(Monotone.MODID + ":" + command))));
    }
}
