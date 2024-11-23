package juniper.monotone.interaction;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringIdentifiable;

public enum MaskDisplayType implements StringIdentifiable {
    NONE("none"), UNMATCHING("unmatching"), ALL("all");

    public static final Codec<MaskDisplayType> CODEC;
    static {
        CODEC = StringIdentifiable.createCodec(MaskDisplayType::values);
    }
    private final String id;

    private MaskDisplayType(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return id;
    }

}