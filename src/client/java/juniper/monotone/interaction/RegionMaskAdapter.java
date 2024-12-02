package juniper.monotone.interaction;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class RegionMaskAdapter extends TypeAdapter<RegionMask> {
    Gson gson = new Gson();

    @Override
    public void write(JsonWriter out, RegionMask value) throws IOException {
        gson.toJson(value);
        out.beginObject().name("type").value(value.getClass().getName()).name("data").jsonValue(gson.toJson(value)).endObject();
    }

    @Override
    public RegionMask read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        in.beginObject();
        Class<? extends RegionMask> clazz = null;
        RegionMask ret = null;
        while (in.peek() != JsonToken.END_OBJECT) {
            String name = in.nextName();
            if (name.equals("type")) {
                try {
                    clazz = Class.forName(in.nextString()).asSubclass(RegionMask.class);
                } catch (ClassNotFoundException | ClassCastException e) {
                    throw new JsonIOException(e);
                }
            } else if (name.equals("data")) {
                ret = gson.fromJson(in, clazz);
                ret.init();
            } else {
                in.skipValue();
            }
        }
        if (clazz == null || ret == null) {
            throw new JsonIOException(String.format("Missing information for constructing RegionMask subtype (type=%s, data=%s)", clazz, ret));
        }
        in.endObject();
        return ret;
    }
}
