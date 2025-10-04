package juniper.monotone.command;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import juniper.monotone.Monotone;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.command.argument.Vec2ArgumentType;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public enum LocatorMode {
    NONE, XYZ, XZ;

    public static final RequiredArgumentBuilder<FabricClientCommandSource, PosArgument> XYZPOS_ARG = ClientCommandManager.argument("xyz", new BlockPosArgumentType());
    public static final RequiredArgumentBuilder<FabricClientCommandSource, PosArgument> XZPOS_ARG = ClientCommandManager.argument("xz", new Vec2ArgumentType(true));
    public static final RequiredArgumentBuilder<FabricClientCommandSource, Float> RANGE_ARG = ClientCommandManager.argument("blocks", FloatArgumentType.floatArg(0));

    public static int getTarget(CommandContext<FabricClientCommandSource> ctx) {
        ctx.getSource().sendFeedback(Text.literal(String.format("Target is %s (%s)", Monotone.CONFIG.locatorTarget, Monotone.CONFIG.locatorMode)));
        return 1;
    }

    public static int locateXYZ(CommandContext<FabricClientCommandSource> ctx) {
        FabricClientCommandSource fccs = ctx.getSource();
        ServerCommandSource scs = new ServerCommandSource(CommandOutput.DUMMY, fccs.getPosition(), fccs.getRotation(), null, 0, "client_command_source_wrapper",
                Text.literal("Client Command Source Wrapper"), null, fccs.getEntity());
        BlockPos pos = ctx.getArgument(XYZPOS_ARG.getName(), PosArgument.class).toAbsoluteBlockPos(scs);
        Monotone.CONFIG.locatorTarget = pos;
        Monotone.CONFIG.locatorMode = XYZ;
        ctx.getSource().sendFeedback(Text.literal(String.format("Tracking %s (xyz)", pos)));
        return 1;
    }

    public static int locateXZ(CommandContext<FabricClientCommandSource> ctx) {
        FabricClientCommandSource fccs = ctx.getSource();
        ServerCommandSource scs = new ServerCommandSource(CommandOutput.DUMMY, fccs.getPosition(), fccs.getRotation(), null, 0, "client_command_source_wrapper",
                Text.literal("Client Command Source Wrapper"), null, fccs.getEntity());
        BlockPos pos = ctx.getArgument(XZPOS_ARG.getName(), PosArgument.class).toAbsoluteBlockPos(scs);
        Monotone.CONFIG.locatorTarget = pos;
        Monotone.CONFIG.locatorMode = XZ;
        ctx.getSource().sendFeedback(Text.literal(String.format("Tracking %s (xz)", pos)));
        return 1;
    }

    public static int clear(CommandContext<FabricClientCommandSource> ctx) {
        Monotone.CONFIG.locatorMode = NONE;
        ctx.getSource().sendFeedback(Text.literal("Cleared tracking"));
        return 1;
    }

    public static int getRange(CommandContext<FabricClientCommandSource> ctx) {
        ctx.getSource().sendFeedback(Text.literal(String.format("Range is %s", Monotone.CONFIG.locatorRange)));
        return 1;
    }

    public static int setRange(CommandContext<FabricClientCommandSource> ctx) {
        Monotone.CONFIG.locatorRange = FloatArgumentType.getFloat(ctx, RANGE_ARG.getName());
        ctx.getSource().sendFeedback(Text.literal(String.format("Set range to %s", Monotone.CONFIG.locatorRange)));
        return 1;
    }
}
