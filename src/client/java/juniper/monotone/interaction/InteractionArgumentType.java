package juniper.monotone.interaction;

import net.minecraft.command.argument.EnumArgumentType;

public class InteractionArgumentType extends EnumArgumentType<InteractionType> {
    public InteractionArgumentType() {
        super(InteractionType.CODEC, InteractionType::values);
    }
}
