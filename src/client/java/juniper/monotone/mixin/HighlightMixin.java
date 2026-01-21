package juniper.monotone.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import juniper.monotone.Monotone;
import juniper.monotone.command.VisibilitySetting;
import juniper.monotone.mixinInterface.FeedingInterface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AnimalEntity;

@Mixin(MinecraftClient.class)
public abstract class HighlightMixin {
    @Inject(method = "hasOutline", at = @At("HEAD"), cancellable = true)
    private void hasOutline(Entity entity, CallbackInfoReturnable<Boolean> info) {
        if ((Object) entity instanceof FeedingInterface aea && Monotone.CONFIG.highlight.getOrDefault(VisibilitySetting.CAN_FEED, false) && !((AnimalEntity) entity).isBaby()
                && entity.getEntityWorld().getTime() - aea.getLastFed() >= AnimalEntityAccessor.getBREEDING_COOLDOWN()) {
            info.setReturnValue(true);
        }
        if (Monotone.CONFIG.highlightEntity.getOrDefault(entity.getType().getUntranslatedName(), false)) {
            info.setReturnValue(true);
        }
    }
}
