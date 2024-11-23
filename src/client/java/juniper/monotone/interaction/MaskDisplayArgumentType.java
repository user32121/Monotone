package juniper.monotone.interaction;

import net.minecraft.command.argument.EnumArgumentType;

public class MaskDisplayArgumentType extends EnumArgumentType<MaskDisplayType> {
    public MaskDisplayArgumentType() {
        super(MaskDisplayType.CODEC, MaskDisplayType::values);
    }
}
