package juniper.monotone.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import juniper.monotone.task.InputManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

@Mixin(KeyBinding.class)
public class KeyInputMixin {
    @Inject(method = "isPressed", at = @At("HEAD"), cancellable = true)
    private void isPressed(CallbackInfoReturnable<Boolean> info) {
        KeyBinding self = (KeyBinding) (Object) this;
        if (InputManager.forward && self == MinecraftClient.getInstance().options.forwardKey) {
            info.setReturnValue(true);
        } else if (InputManager.back && self == MinecraftClient.getInstance().options.backKey) {
            info.setReturnValue(true);
        } else if (InputManager.left && self == MinecraftClient.getInstance().options.leftKey) {
            info.setReturnValue(true);
        } else if (InputManager.right && self == MinecraftClient.getInstance().options.rightKey) {
            info.setReturnValue(true);
        }
    }
}
