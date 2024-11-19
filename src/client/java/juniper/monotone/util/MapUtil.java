package juniper.monotone.util;

import java.util.Map;
import java.util.function.Supplier;

public class MapUtil {
    public static <K, V> void ensureKey(Map<K, V> map, K k, V defaultValue) {
        ensureKeySupplier(map, k, () -> defaultValue);
    }

    public static <K, V> void ensureKeySupplier(Map<K, V> map, K k, Supplier<V> defaultValue) {
        if (!map.containsKey(k)) {
            map.put(k, defaultValue.get());
        }
    }
}
