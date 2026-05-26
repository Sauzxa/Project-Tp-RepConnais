package mytweetyapp;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * Resolves Ask-panel input (NL question + Subject / Relation / Object) into a single triple.
 * Filled Subject/Relation/Object fields take priority over the NL box.
 */
public final class QueryInputResolver {

    private static final Set<String> RELATION_TOKENS = Set.of(
            "type", "is-a", "a", "stops-at", "stopsat", "on", "connects", "connected", "connected-to",
            "electric", "order-shipped", "ordershipped", "shipped",
            "customer-notified", "customernotified", "notified",
            "payment-received", "paymentreceived", "item-in-stock", "iteminstock",
            "autorenew", "subscriber", "card-expired", "cardexpired",
            "works-in", "worksin", "performs", "teaches", "locomotion", "voler", "exception",
            "necessary", "necessarily", "possible", "possibly", "box", "diamond", "[]", "<>");

    public record ResolvedQuery(String subject, String relation, String object) {
    }

    private QueryInputResolver() {
    }

    public static Optional<ResolvedQuery> resolve(
            String nl, String subject, String relation, String object, String exampleKey) {
        Optional<NaturalLanguageQueryParser.ParsedQuery> parsed =
                NaturalLanguageQueryParser.resolveInputs(nl, subject, relation, object, exampleKey);
        if (parsed.isPresent()) {
            return Optional.of(toResolved(parsed.get()));
        }
        String sub = subject == null ? "" : subject.trim();
        String rel = relation == null ? "" : relation.trim();
        String obj = object == null ? "" : object.trim();
        if (!sub.isBlank() || !rel.isBlank() || !obj.isBlank()) {
            return Optional.of(new ResolvedQuery(sub, rel, obj));
        }
        return Optional.empty();
    }

    public static Optional<ResolvedQuery> resolve(String nl, String subject, String relation, String object) {
        return resolve(nl, subject, relation, object, null);
    }

    private static ResolvedQuery toResolved(NaturalLanguageQueryParser.ParsedQuery p) {
        return new ResolvedQuery(
                p.subject(),
                p.relation() == null ? "" : p.relation(),
                p.object() == null ? "" : p.object());
    }

    /** True only for DL class names (PascalCase), not relations or arbitrary words. */
    public static boolean looksLikeDlConcept(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        String t = token.trim();
        String lower = t.toLowerCase(Locale.ROOT);
        if (RELATION_TOKENS.contains(lower)) {
            return false;
        }
        return switch (lower) {
            case "tram", "bus", "station", "vehicle", "electricvehicle", "dieselvehicle",
                    "publictransport", "transportentity" -> true;
            default -> t.matches("[A-Z][a-zA-Z0-9]*");
        };
    }

    public static String capitalizeConcept(String raw) {
        if (raw == null || raw.isEmpty()) {
            return raw;
        }
        String lower = raw.toLowerCase(Locale.ROOT);
        return switch (lower) {
            case "electricvehicle" -> "ElectricVehicle";
            case "dieselvehicle" -> "DieselVehicle";
            case "publictransport" -> "PublicTransport";
            case "transportentity" -> "TransportEntity";
            default -> raw.substring(0, 1).toUpperCase(Locale.ROOT) + raw.substring(1);
        };
    }
}
