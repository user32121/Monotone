package juniper.monotone.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
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
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

public enum RotationPlane {
    PITCH, YAW;

    public static class RotationPlaneArgument implements ArgumentType<RotationPlane> {
        private static final DynamicCommandExceptionType EXCEPTION_TYPE = new DynamicCommandExceptionType(target -> Text.literal("Unknown rotation: " + target));

        @Override
        public RotationPlane parse(StringReader reader) throws CommandSyntaxException {
            String s = reader.readUnquotedString();
            try {
                return Enum.valueOf(RotationPlane.class, s.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw EXCEPTION_TYPE.createWithContext(reader, s);
            }
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            return CommandSource.suggestMatching(Arrays.stream(values()).map(Object::toString).map(String::toLowerCase), builder);
        }
    }

    public static final Map<RotationPlane, Pair<Float, Float>> LIMITS = new HashMap<>();
    public static final RequiredArgumentBuilder<FabricClientCommandSource, RotationPlane> PLANE_ARG = ClientCommandManager.argument("plane", new RotationPlaneArgument());
    public static final RequiredArgumentBuilder<FabricClientCommandSource, Float> MIN_ARG = ClientCommandManager.argument("min", FloatArgumentType.floatArg());
    public static final RequiredArgumentBuilder<FabricClientCommandSource, Float> MAX_ARG = ClientCommandManager.argument("max", FloatArgumentType.floatArg());
    public static final RequiredArgumentBuilder<FabricClientCommandSource, Float> DEVIANCE_ARG = ClientCommandManager.argument("deviance", FloatArgumentType.floatArg());

    public static int getLimit(CommandContext<FabricClientCommandSource> ctx) {
        RotationPlane plane = ctx.getArgument(PLANE_ARG.getName(), RotationPlane.class);
        Pair<Float, Float> limit = RotationPlane.LIMITS.get(plane);
        if (limit == null) {
            ctx.getSource().sendFeedback(Text.literal(String.format("No limit for %s set", plane)));
        } else {
            ctx.getSource().sendFeedback(Text.literal(String.format("Limit for %s is [%s, %s]", plane, limit.getLeft(), limit.getRight())));
        }
        return 1;
    }

    public static int clearLimit(CommandContext<FabricClientCommandSource> ctx) {
        RotationPlane plane = ctx.getArgument(PLANE_ARG.getName(), RotationPlane.class);
        RotationPlane.LIMITS.remove(plane);
        ctx.getSource().sendFeedback(Text.literal(String.format("Removed limit for %s", plane)));
        return 1;
    }

    public static int setDeviance(CommandContext<FabricClientCommandSource> ctx, RotationPlane plane, float min, float max, float deviance) {
        Random rng = Random.create();
        min += (rng.nextFloat() * 2 - 1) * deviance;
        max += (rng.nextFloat() * 2 - 1) * deviance;
        RotationPlane.LIMITS.put(plane, new Pair<Float, Float>(min, max));
        ctx.getSource().sendFeedback(Text.literal(String.format("Limit for %s set to [%s, %s]", plane, min, max)));
        return 1;
    }

    public static int setLimit(CommandContext<FabricClientCommandSource> ctx) {
        RotationPlane plane = ctx.getArgument(PLANE_ARG.getName(), RotationPlane.class);
        float min = FloatArgumentType.getFloat(ctx, MIN_ARG.getName());
        float max = FloatArgumentType.getFloat(ctx, MAX_ARG.getName());
        return setDeviance(ctx, plane, min, max, 0);
    }

    public static int setLimitWithDeviance(CommandContext<FabricClientCommandSource> ctx) {
        RotationPlane plane = ctx.getArgument(PLANE_ARG.getName(), RotationPlane.class);
        float min = FloatArgumentType.getFloat(ctx, MIN_ARG.getName());
        float max = FloatArgumentType.getFloat(ctx, MAX_ARG.getName());
        float deviance = FloatArgumentType.getFloat(ctx, DEVIANCE_ARG.getName());
        return setDeviance(ctx, plane, min, max, deviance);
    }

    public static float limitIfSet(RotationPlane plane, float value) {
        Pair<Float, Float> limit = LIMITS.get(plane);
        if (limit == null) {
            return value;
        }
        return MathHelper.clamp(value, limit.getLeft(), limit.getRight());
    }
}
