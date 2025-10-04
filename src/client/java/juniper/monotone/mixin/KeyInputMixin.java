package juniper.monotone.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import juniper.monotone.task.InputManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;

@Mixin(KeyBinding.class)
public class KeyInputMixin {
    @Inject(method = "isPressed", at = @At("HEAD"), cancellable = true)
    private void isPressed(CallbackInfoReturnable<Boolean> info) {
        KeyBinding self = (KeyBinding) (Object) this;
        GameOptions options = MinecraftClient.getInstance().options;
        if (InputManager.forward && self == options.forwardKey) {
            info.setReturnValue(true);
        } else if (InputManager.back && self == options.backKey) {
            info.setReturnValue(true);
        } else if (InputManager.left && self == options.leftKey) {
            info.setReturnValue(true);
        } else if (InputManager.right && self == options.rightKey) {
            info.setReturnValue(true);
        } else if (InputManager.jump && self == options.jumpKey) {
            info.setReturnValue(true);
        } else if (InputManager.sprint && self == options.sprintKey) {
            info.setReturnValue(true);
        } else if (InputManager.sneak && self == options.sneakKey) {
            info.setReturnValue(true);
        }
    }
}
