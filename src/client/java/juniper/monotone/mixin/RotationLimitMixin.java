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
        self.prevPitch = RotationPlane.limitIfSet(RotationPlane.PITCH, self.prevPitch + deltaPitch);
        self.prevYaw = RotationPlane.limitIfSet(RotationPlane.YAW, self.prevYaw + deltaYaw);
        self.prevPitch = MathHelper.clamp(self.prevPitch, -90.0f, 90.0f);
        if (getVehicle() != null) {
            getVehicle().onPassengerLookAround(self);
        }
    }

    @Accessor
    public abstract Entity getVehicle();
}
