package mytweetyapp;

import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses simple natural-language questions into subject / relation / object.
 * Examples: "Is tramA on northPark?", "Does Dr_House work-in Hospital?"
 */
public final class NaturalLanguageQueryParser {

    private static final Pattern IS_ON = Pattern.compile(
            "(?i)(?:is|est-ce que)\\s+([\\w.]+)\\s+(?:on|à|at|sur)\\s+([\\w.]+)\\??");
    private static final Pattern DOES_REL = Pattern.compile(
            "(?i)(?:does|est-ce que)\\s+([\\w.]+)\\s+([\\w-]+)\\s+([\\w.]+)\\??");
    private static final Pattern IS_A = Pattern.compile(
            "(?i)(?:is|est-ce que)\\s+([\\w.]+)\\s+(?:a|un|une)\\s+([\\w.]+)\\??");
    private static final Pattern DOES_VOLER = Pattern.compile(
            "(?i)(?:does|est-ce que)\\s+([\\w.]+)\\s+voler\\??");
    private static final Pattern MODAL_NECESSARY = Pattern.compile(
            "(?i)(?:is|est-ce que)\\s+(!?[\\w]+)\\s+(?:necessarily|necessairement|toujours)\\??");
    private static final Pattern MODAL_POSSIBLE = Pattern.compile(
            "(?i)(?:is|est-ce que)\\s+(!?[\\w]+)\\s+(?:possible|peut(?:\\s+arriver)?)\\??");
    private static final Pattern IS_CONNECTED = Pattern.compile(
            "(?i)(?:is|est-ce que)\\s+([\\w.]+)\\s+connected(?:\\s+to)?\\s+([\\w.]+)\\??");
    /** DL: Tram(tramA), ElectricVehicle(tramA)? */
    private static final Pattern DL_CLASS = Pattern.compile(
            "(?i)^(?:DL:\\s*)?([A-Z][\\w]*)\\s*\\(\\s*([\\w.]+)\\s*\\)\\s*\\??$");
    /** DL: stopsAt(tramA, northPark), connects(northPark, centralStation) */
    private static final Pattern DL_ROLE = Pattern.compile(
            "(?i)^(?:DL:\\s*)?(stopsAt|connects)\\s*\\(\\s*([\\w.]+)\\s*,\\s*([\\w.]+)\\s*\\)\\s*\\??$");
    private static final Pattern IS_PRED = Pattern.compile(
            "(?i)(?:is|est-ce que)\\s+([\\w.]+)\\s+([\\w-]+)\\??");
    private static final Pattern IS_UNARY = Pattern.compile(
            "(?i)(?:is|est-ce que)\\s+([\\w.]+)\\s+(\\w+)\\??");
    private static final Pattern ECOMMERCE_IS = Pattern.compile(
            "(?i)(?:is|est-ce que)\\s+(order|customer)\\s+([\\w-]+)\\??");

    private static final java.util.Set<String> RESERVED_RELATIONS = java.util.Set.of(
            "type", "is-a", "a", "stops-at", "stopsat", "on", "connects", "connected", "connected-to",
            "electric", "necessary", "possibly", "possible", "necessarily", "box", "diamond",
            "order-shipped", "ordershipped", "customer-notified", "customernotified",
            "payment-received", "paymentreceived", "item-in-stock", "iteminstock",
            "autorenew", "subscriber", "card-expired", "cardexpired",
            "works-in", "worksin", "performs", "teaches", "locomotion", "voler", "exception");

    private NaturalLanguageQueryParser() {
    }

    public record ParsedQuery(String subject, String relation, String object) {
    }

    public static Optional<ParsedQuery> parse(String text) {
        return parse(text, null);
    }

    public static Optional<ParsedQuery> parse(String text, String exampleKey) {
        if (text == null || text.isBlank()) {
            return Optional.empty();
        }
        String trimmed = text.trim().replaceFirst("(?i)^DL:\\s*", "");

        Optional<ParsedQuery> domain = parseDomainPhrases(trimmed, exampleKey);
        if (domain.isPresent()) {
            return domain;
        }

        Matcher dlClass = DL_CLASS.matcher(trimmed);
        if (dlClass.matches()) {
            return Optional.of(new ParsedQuery(dlClass.group(2), "is-a", dlClass.group(1)));
        }

        Matcher dlRole = DL_ROLE.matcher(trimmed);
        if (dlRole.matches()) {
            String role = dlRole.group(1).equalsIgnoreCase("stopsAt") ? "stops-at" : "connects";
            return Optional.of(new ParsedQuery(dlRole.group(2), role, dlRole.group(3)));
        }

        Matcher onMatcher = IS_ON.matcher(trimmed);
        if (onMatcher.matches()) {
            return Optional.of(new ParsedQuery(
                    onMatcher.group(1), "stops-at", onMatcher.group(2)));
        }

        Matcher relMatcher = DOES_REL.matcher(trimmed);
        if (relMatcher.matches()) {
            return Optional.of(new ParsedQuery(
                    relMatcher.group(1),
                    normalizeRelation(relMatcher.group(2)),
                    relMatcher.group(3)));
        }

        Matcher isAMatcher = IS_A.matcher(trimmed);
        if (isAMatcher.matches()) {
            return Optional.of(new ParsedQuery(
                    isAMatcher.group(1), "is-a", isAMatcher.group(2)));
        }

        Matcher volerMatcher = DOES_VOLER.matcher(trimmed);
        if (volerMatcher.matches()) {
            return Optional.of(new ParsedQuery(volerMatcher.group(1), "voler", "Voler"));
        }

        Matcher necMatcher = MODAL_NECESSARY.matcher(trimmed);
        if (necMatcher.matches()) {
            return Optional.of(new ParsedQuery(necMatcher.group(1), "necessary", null));
        }

        Matcher possMatcher = MODAL_POSSIBLE.matcher(trimmed);
        if (possMatcher.matches()) {
            return Optional.of(new ParsedQuery(possMatcher.group(1), "possible", null));
        }

        Matcher connectedMatcher = IS_CONNECTED.matcher(trimmed);
        if (connectedMatcher.matches()) {
            String rel = isDlExample(exampleKey) ? "connects" : "connected";
            return Optional.of(new ParsedQuery(
                    connectedMatcher.group(1), rel, connectedMatcher.group(2)));
        }

        Matcher ecomMatcher = ECOMMERCE_IS.matcher(trimmed);
        if (ecomMatcher.matches()) {
            String entity = ecomMatcher.group(1).toLowerCase(Locale.ROOT);
            String prop = normalizeRelation(ecomMatcher.group(2));
            String rel = switch (prop) {
                case "shipped" -> "order-shipped";
                case "notified" -> "customer-notified";
                default -> prop;
            };
            return Optional.of(new ParsedQuery(entity, rel, ""));
        }

        Matcher predMatcher = IS_PRED.matcher(trimmed);
        if (predMatcher.matches()) {
            String subject = predMatcher.group(1);
            String rel = normalizeRelation(predMatcher.group(2));
            if (QueryInputResolver.looksLikeDlConcept(rel)) {
                return Optional.of(new ParsedQuery(subject, "is-a", QueryInputResolver.capitalizeConcept(rel)));
            }
            if (!rel.equals(subject.toLowerCase(Locale.ROOT))) {
                return Optional.of(new ParsedQuery(subject, rel, null));
            }
        }

        Matcher unaryMatcher = IS_UNARY.matcher(trimmed);
        if (unaryMatcher.matches()) {
            String subject = unaryMatcher.group(1);
            String rel = normalizeRelation(unaryMatcher.group(2));
            if (QueryInputResolver.looksLikeDlConcept(rel)) {
                return Optional.of(new ParsedQuery(subject, "is-a", QueryInputResolver.capitalizeConcept(rel)));
            }
            return Optional.of(new ParsedQuery(subject, rel, null));
        }

        return Optional.empty();
    }

    private static Optional<ParsedQuery> parseDomainPhrases(String trimmed, String exampleKey) {
        Matcher subAuto = Pattern.compile("(?i)(?:is|est-ce que)\\s+([\\w.]+)\\s+autorenew\\??").matcher(trimmed);
        if (subAuto.matches()) {
            return Optional.of(new ParsedQuery(subAuto.group(1), "autorenew", ""));
        }
        Matcher subCard = Pattern.compile("(?i)(?:is|est-ce que)\\s+([\\w.]+)\\s+card[- ]?expired\\??").matcher(trimmed);
        if (subCard.matches()) {
            return Optional.of(new ParsedQuery(subCard.group(1), "card-expired", ""));
        }
        Matcher subSub = Pattern.compile("(?i)(?:is|est-ce que)\\s+([\\w.]+)\\s+a?\\s*subscriber\\??").matcher(trimmed);
        if (subSub.matches()) {
            return Optional.of(new ParsedQuery(subSub.group(1), "subscriber", ""));
        }
        if (isDlExample(exampleKey)) {
            Matcher dlUnary = Pattern.compile("(?i)(?:is|est-ce que)\\s+([\\w.]+)\\s+([A-Z][\\w]*)\\??").matcher(trimmed);
            if (dlUnary.matches()) {
                return Optional.of(new ParsedQuery(
                        dlUnary.group(1), "is-a", QueryInputResolver.capitalizeConcept(dlUnary.group(2))));
            }
        }
        return Optional.empty();
    }

    /**
     * Parse DL / NL from the three Ask fields when the NL box is empty
     * (e.g. subject = {@code Tram(tramA)} or relation = {@code Tram} with object empty).
     */
    public static Optional<ParsedQuery> parseFields(String subject, String relation, String object) {
        String sub = subject == null ? "" : subject.trim();
        String rel = relation == null ? "" : relation.trim();
        String obj = object == null ? "" : object.trim();

        if (!sub.isBlank()) {
            Optional<ParsedQuery> fromSubject = parse(sub);
            if (fromSubject.isPresent()) {
                return fromSubject;
            }
        }

        if (!sub.isBlank() && !rel.isBlank()) {
            String relNorm = rel.toLowerCase(Locale.ROOT);
            if ("type".equals(relNorm) || "is-a".equals(relNorm) || "a".equals(relNorm)) {
                if (!obj.isBlank()) {
                    return Optional.of(new ParsedQuery(sub, "is-a", obj));
                }
            }
            if (QueryInputResolver.looksLikeDlConcept(rel) && obj.isBlank()) {
                return Optional.of(new ParsedQuery(sub, "is-a", QueryInputResolver.capitalizeConcept(rel)));
            }
            if (("stops-at".equals(relNorm) || "stopsat".equals(relNorm) || "connects".equals(relNorm)
                    || "connected".equals(relNorm) || "connected-to".equals(relNorm)) && !obj.isBlank()) {
                String r = relNorm.startsWith("stop") ? "stops-at" : relNorm;
                return Optional.of(new ParsedQuery(sub, r, obj));
            }
        }

        if (!sub.isBlank() && rel.isBlank() && !obj.isBlank()) {
            if (QueryInputResolver.looksLikeDlConcept(sub)) {
                return Optional.of(new ParsedQuery(obj, "is-a", QueryInputResolver.capitalizeConcept(sub)));
            }
        }

        return Optional.empty();
    }

    /**
     * Prefer filled Subject/Relation/Object fields; use NL only when fields are incomplete.
     */
    public static Optional<ParsedQuery> resolveInputs(
            String nl, String subject, String relation, String object, String exampleKey) {
        Optional<ParsedQuery> fromFields = parseFields(subject, relation, object);
        if (fieldsLookComplete(subject, relation, object)) {
            return fromFields;
        }
        Optional<ParsedQuery> fromNl = parse(nl, exampleKey);
        if (fromNl.isPresent()) {
            return fromNl;
        }
        return fromFields;
    }

    public static boolean fieldsLookComplete(String subject, String relation, String object) {
        String sub = subject == null ? "" : subject.trim();
        String rel = relation == null ? "" : relation.trim().toLowerCase(Locale.ROOT);
        String obj = object == null ? "" : object.trim();
        if (rel.isBlank()) {
            return false;
        }
        return switch (rel) {
            case "type", "is-a", "a" -> !sub.isBlank() && !obj.isBlank();
            case "stops-at", "stopsat", "on", "connects", "connected", "connected-to" ->
                    !sub.isBlank() && !obj.isBlank();
            case "necessary", "possible" -> !sub.isBlank();
            case "order-shipped", "customer-notified", "payment-received", "item-in-stock",
                    "autorenew", "subscriber", "card-expired", "cardexpired" -> true;
            case "electric" -> !sub.isBlank();
            case "works-in", "performs", "teaches", "locomotion", "voler", "exception" ->
                    !sub.isBlank() && !obj.isBlank();
            default -> !sub.isBlank();
        };
    }

    private static boolean isDlExample(String exampleKey) {
        return exampleKey != null && "desclogic".equals(exampleKey);
    }

    private static String normalizeRelation(String raw) {
        return raw.trim().toLowerCase(Locale.ROOT).replace(' ', '-');
    }
}
