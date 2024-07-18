package juniper.monotone.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

public enum HighlightTarget {
    CAN_FEED;

    public static class RaycastTargetArgument implements ArgumentType<HighlightTarget> {
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

    public static final Map<HighlightTarget, Boolean> highlight = new HashMap<>();
}
