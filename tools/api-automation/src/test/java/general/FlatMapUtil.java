

package general;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class FlatMapUtil {
    public static String[] patterns = new String[0];

    private FlatMapUtil() {
        throw new AssertionError("No instances for you!");
    }

    public static String transformJson(String jsonString) {
        return jsonString != null && !jsonString.isEmpty() && jsonString.charAt(0) == '[' ? "{\"data\":" + jsonString + "}" : jsonString;
    }

    public static Map<String, Object> flatten(Map<String, Object> map) {
        return (Map)map.entrySet().stream().flatMap(FlatMapUtil::flatten).collect(LinkedHashMap::new, (m, e) -> {
            m.put("/" + (String)e.getKey(), e.getValue());
        }, HashMap::putAll);
    }

    private static Stream<Entry<String, Object>> flatten(Entry<String, Object> entry) {
        String[] var1 = patterns;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            String pattern = var1[var3];
            if (((String)entry.getKey()).toString().matches(pattern)) {
                return Stream.empty();
            }
        }

        if (entry == null) {
            return Stream.empty();
        } else if (entry.getValue() instanceof Map) {
            Map<?, ?> properties = (Map)entry.getValue();
            return properties.entrySet().stream().flatMap((e) -> {
                return flatten((Entry)(new SimpleEntry((String)entry.getKey() + "/" + e.getKey(), e.getValue())));
            });
        } else if (entry.getValue() instanceof List) {
            List<?> list = (List)entry.getValue();
            return IntStream.range(0, list.size()).mapToObj((i) -> {
                return new SimpleEntry((String)entry.getKey() + "/" + i, list.get(i));
            }).flatMap(FlatMapUtil::flatten);
        } else {
            return Stream.of(entry);
        }
    }
}
