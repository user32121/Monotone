package juniper.monotone.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import juniper.monotone.command.RotationPlane;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Mixin(Entity.class)
public abstract class RotationLimitMixin {
    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void changeLookDirection(double cursorDeltaX, double cursorDeltaY, CallbackInfo info) {
        Entity self = (Entity) (Object) this;
        if (self != MinecraftClient.getInstance().player) {
            return;
        }
        info.cancel();
        float deltaPitch = (float) cursorDeltaY * 0.15f;
        float deltaYaw = (float) cursorDeltaX * 0.15f;
        self.setPitch(RotationPlane.limitIfSet(RotationPlane.PITCH, self.getPitch() + deltaPitch));
        self.setYaw(RotationPlane.limitIfSet(RotationPlane.YAW, self.getYaw() + deltaYaw));
        self.setPitch(MathHelper.clamp(self.getPitch(), -90.0f, 90.0f));
        self.lastPitch = RotationPlane.limitIfSet(RotationPlane.PITCH, self.lastPitch + deltaPitch);
        self.lastYaw = RotationPlane.limitIfSet(RotationPlane.YAW, self.lastYaw + deltaYaw);
        self.lastPitch = MathHelper.clamp(self.lastPitch, -90.0f, 90.0f);
        if (getVehicle() != null) {
            getVehicle().onPassengerLookAround(self);
        }
    }

    @Accessor
    public abstract Entity getVehicle();
}
