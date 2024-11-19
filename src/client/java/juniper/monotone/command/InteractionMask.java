package juniper.monotone.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import juniper.monotone.interaction.CuboidRegionMask;
import juniper.monotone.interaction.InteractionArgumentType;
import juniper.monotone.interaction.InteractionType;
import juniper.monotone.interaction.RegionMask;
import juniper.monotone.util.MapUtil;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class InteractionMask {
    public static final RequiredArgumentBuilder<FabricClientCommandSource, InteractionType> INTERACTION_ARG = ClientCommandManager
            .argument("interaction", new InteractionArgumentType());
    public static final RequiredArgumentBuilder<FabricClientCommandSource, PosArgument> FROM_ARG = ClientCommandManager
            .argument("from", new BlockPosArgumentType());
    public static final RequiredArgumentBuilder<FabricClientCommandSource, PosArgument> TO_ARG = ClientCommandManager
            .argument("to", new BlockPosArgumentType());

    public static final Map<InteractionType, List<RegionMask>> mask = new HashMap<>();

    public static int addCuboid(CommandContext<FabricClientCommandSource> ctx) {
        FabricClientCommandSource fccs = ctx.getSource();
        ServerCommandSource scs = new ServerCommandSource(CommandOutput.DUMMY, fccs.getPosition(), fccs.getRotation(), null, 0, "client_command_source_wrapper",
                Text.literal("Client Command Source Wrapper"), null, fccs.getEntity());
        InteractionType interaction = ctx.getArgument(INTERACTION_ARG.getName(), InteractionType.class);
        MapUtil.ensureKey2(mask, interaction, ArrayList::new);
        BlockPos from = ctx.getArgument(FROM_ARG.getName(), PosArgument.class).toAbsoluteBlockPos(scs);
        BlockPos to = ctx.getArgument(TO_ARG.getName(), PosArgument.class).toAbsoluteBlockPos(scs);
        CuboidRegionMask crm = new CuboidRegionMask(from, to);
        mask.get(interaction).add(crm);
        fccs.sendFeedback(Text.literal(String.format("Added region %s to %s mask", crm, interaction)));
        return 1;
    }

    public static int list(CommandContext<FabricClientCommandSource> ctx) {
        InteractionType interaction = ctx.getArgument(INTERACTION_ARG.getName(), InteractionType.class);
        MapUtil.ensureKey2(mask, interaction, ArrayList::new);
        List<RegionMask> regions = mask.get(interaction);
        ctx.getSource().sendFeedback(Text.literal(String.format("%s mask regions:", interaction)));
        for (int i = 0; i < regions.size(); ++i) {
            ctx.getSource().sendFeedback(Text.literal(String.format("%s: %s", i, regions.get(i))));
        }
        return regions.size();
    }
}
