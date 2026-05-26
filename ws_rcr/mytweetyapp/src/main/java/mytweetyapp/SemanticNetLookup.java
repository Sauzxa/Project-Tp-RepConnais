package mytweetyapp;

import java.util.Locale;
import java.util.Map;

/** Case-insensitive node lookup for semantic networks. */
public final class SemanticNetLookup {

    private SemanticNetLookup() {
    }

    public static <T> T resolve(Map<String, T> graph, String name) {
        if (name == null || name.isBlank() || graph == null) {
            return null;
        }
        String trimmed = name.trim();
        if (graph.containsKey(trimmed)) {
            return graph.get(trimmed);
        }
        String underscored = trimmed.replace(' ', '_');
        if (graph.containsKey(underscored)) {
            return graph.get(underscored);
        }
        for (Map.Entry<String, T> entry : graph.entrySet()) {
            String key = entry.getKey();
            if (key.equalsIgnoreCase(trimmed)
                    || key.equalsIgnoreCase(underscored)
                    || key.replace('_', ' ').equalsIgnoreCase(trimmed)) {
                return entry.getValue();
            }
        }
        return null;
    }
}
