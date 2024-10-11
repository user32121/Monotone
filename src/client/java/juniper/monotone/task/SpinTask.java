package juniper.monotone.task;

import java.util.List;

import com.google.common.base.MoreObjects;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import juniper.monotone.mixin.MouseInputAccessor;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class SpinTask implements Task {
    private float rotation;
    private float targetYaw;

    public SpinTask(float rotation) {
        this.rotation = rotation;
    }

    @Override
    public void prepare(MinecraftClient client) {
        targetYaw = client.player.getYaw() + rotation;
    }

    @Override
    public boolean tick(MinecraftClient client) {
        double move = MathHelper.sign(targetYaw - client.player.getYaw());
        if (move != MathHelper.sign(rotation)) {
            return true;
        }
        MouseInputAccessor mia = (MouseInputAccessor) (Object) client.mouse;
        mia.setCursorDeltaX(mia.getCursorDeltaX() + move * 30);
        //finish when rotation changes sign
        return false;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("degrees", rotation).toString();
    }

    public static class SpinTaskFactory implements TaskFactory<SpinTask> {
        public static final RequiredArgumentBuilder<FabricClientCommandSource, Float> DEGREES_ARG = ClientCommandManager.argument("degrees", FloatArgumentType.floatArg());

        @Override
        public String getTaskName() {
            return "spin";
        }

        @Override
        public Text getDescription() {
            return Text.literal("Spin a certain amount of degrees");
        }

        @Override
        public List<RequiredArgumentBuilder<FabricClientCommandSource, ?>> getArgs() {
            return List.of(DEGREES_ARG);
        }

        @Override
        public SpinTask makeTask(CommandContext<FabricClientCommandSource> ctx) {
            float rotation = FloatArgumentType.getFloat(ctx, DEGREES_ARG.getName());
            return new SpinTask(rotation);
        }

    }
}
