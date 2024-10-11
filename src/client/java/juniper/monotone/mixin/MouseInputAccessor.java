package juniper.monotone.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.Mouse;

@Mixin(Mouse.class)
public interface MouseInputAccessor {
    @Accessor
    public double getCursorDeltaX();

    @Accessor
    public double getCursorDeltaY();

    @Accessor
    public void setCursorDeltaX(double value);

    @Accessor
    public void setCursorDeltaY(double value);
}
