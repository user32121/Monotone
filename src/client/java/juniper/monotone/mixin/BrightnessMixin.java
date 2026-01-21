package juniper.monotone.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import juniper.monotone.Monotone;
import juniper.monotone.command.VisibilitySetting;
import net.minecraft.client.render.LightmapTextureManager;

@Mixin(LightmapTextureManager.class)
public class BrightnessMixin {
    @ModifyVariable(method = "update", at = @At("STORE"), ordinal = 6)
    private float getNightVisionStrength(float value) {
        if (Monotone.CONFIG.highlight.getOrDefault(VisibilitySetting.BRIGHT_VISION, false)) {
            return 1;
        } else {
            return value;
        }
    }
}
