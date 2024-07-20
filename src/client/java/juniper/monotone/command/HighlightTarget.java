package juniper.monotone.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

public enum HighlightTarget {
    CAN_FEED;

    public static class HighlightTargetArgument implements ArgumentType<HighlightTarget> {
        private static final DynamicCommandExceptionType EXCEPTION_TYPE = new DynamicCommandExceptionType(target -> Text.literal("Unknown target: " + target));

        @Override
        public HighlightTarget parse(StringReader reader) throws CommandSyntaxException {
            String s = reader.readUnquotedString();
            try {
                return Enum.valueOf(HighlightTarget.class, s.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw EXCEPTION_TYPE.createWithContext(reader, s);
            }
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            return CommandSource.suggestMatching(Arrays.stream(values()).map(Object::toString).map(String::toLowerCase), builder);
        }
    }

    public static final Map<HighlightTarget, Boolean> HIGHLIGHT = new HashMap<>();
    public static final RequiredArgumentBuilder<FabricClientCommandSource, HighlightTarget> TARGET_ARG = ClientCommandManager.argument("target", new HighlightTargetArgument());
    public static final RequiredArgumentBuilder<FabricClientCommandSource, Boolean> VALUE_ARG = ClientCommandManager.argument("value", BoolArgumentType.bool());

    public static int getHighlight(CommandContext<FabricClientCommandSource> ctx) {
        HighlightTarget target = ctx.getArgument(TARGET_ARG.getName(), HighlightTarget.class);
        boolean b = HIGHLIGHT.getOrDefault(target, false);
        ctx.getSource().sendFeedback(Text.literal(String.format("Highlight for %s is %s", target, b)));
        return 1;
    }

    public static int setHighlight(CommandContext<FabricClientCommandSource> ctx) {
        HighlightTarget target = ctx.getArgument(TARGET_ARG.getName(), HighlightTarget.class);
        boolean b = BoolArgumentType.getBool(ctx, VALUE_ARG.getName());
        HIGHLIGHT.put(target, b);
        ctx.getSource().sendFeedback(Text.literal(String.format("Highlight for %s set to %s", target, b)));
        return 1;
    }
}
