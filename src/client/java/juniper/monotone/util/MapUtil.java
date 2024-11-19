package juniper.monotone.util;

import java.util.Map;
import java.util.function.Supplier;

public class MapUtil {
    public static <K, V> void ensureKey(Map<K, V> map, K k, V defaultValue) {
        ensureKey2(map, k, () -> defaultValue);
    }

    public static <K, V> void ensureKey2(Map<K, V> map, K k, Supplier<V> defaultValue) {
        if (!map.containsKey(k)) {
            map.put(k, defaultValue.get());
        }
    }
}
