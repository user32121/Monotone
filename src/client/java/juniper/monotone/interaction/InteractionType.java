package juniper.monotone.interaction;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringIdentifiable;

public enum InteractionType implements StringIdentifiable {
    PLACE("place"), BREAK("break");

    public static final Codec<InteractionType> CODEC;
    static {
        CODEC = StringIdentifiable.createCodec(InteractionType::values);
    }
    private final String id;

    private InteractionType(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return id;
    }

}
