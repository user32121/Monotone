package juniper.monotone.interaction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

public class PathArgumentType implements ArgumentType<String> {
    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String query = builder.getRemaining();
        try (Stream<Path> files = Files.list(Path.of("schematics"))) {
            files.forEach(path -> {
                String filename = path.getFileName().toString();
                if (filename.contains(query)) {
                    builder.suggest(filename);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.buildFuture();
    }
}
