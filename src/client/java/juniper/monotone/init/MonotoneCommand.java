package juniper.monotone.init;

import java.util.function.Function;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import juniper.monotone.Monotone;
import juniper.monotone.command.HighlightTarget;
import juniper.monotone.command.HighlightTarget.RaycastTargetArgument;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class MonotoneCommand {
    public static void init() {
        makeCommand("highlight", node -> node.then(ClientCommandManager.argument("target", new RaycastTargetArgument()).executes(context -> {
            HighlightTarget target = context.getArgument("target", HighlightTarget.class);
            boolean b = HighlightTarget.highlight.getOrDefault(target, false);
            context.getSource().sendFeedback(Text.literal(String.format("Highlight for %s is %s", target, b)));
            return 1;
        }).then(ClientCommandManager.argument("value", BoolArgumentType.bool()).executes(context -> {
            HighlightTarget target = context.getArgument("target", HighlightTarget.class);
            boolean b = BoolArgumentType.getBool(context, "value");
            HighlightTarget.highlight.put(target, b);
            context.getSource().sendFeedback(Text.literal(String.format("Highlight for %s set to %s", target, b)));
            return 1;
        }))));
    }

    private static void makeCommand(String command, Function<LiteralArgumentBuilder<FabricClientCommandSource>, LiteralArgumentBuilder<FabricClientCommandSource>> buildCommand) {
        ClientCommandRegistrationCallback.EVENT
                .register((dispatcher, registry) -> dispatcher.register(buildCommand.apply(ClientCommandManager.literal(Monotone.MODID + ":" + command))));
    }
}
