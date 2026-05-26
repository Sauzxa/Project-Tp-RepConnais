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
    private static final Pattern IS_CONNECTED = Pattern.compile(
            "(?i)(?:is|est-ce que)\\s+([\\w.]+)\\s+connected\\s+([\\w.]+)\\??");
    private static final Pattern IS_PRED = Pattern.compile(
            "(?i)(?:is|est-ce que)\\s+([\\w.]+)\\s+([\\w-]+)\\??");
    private static final Pattern IS_UNARY = Pattern.compile(
            "(?i)(?:is|est-ce que)\\s+([\\w.]+)\\s+(\\w+)\\??");

    private NaturalLanguageQueryParser() {
    }

    public record ParsedQuery(String subject, String relation, String object) {
    }

    public static Optional<ParsedQuery> parse(String text) {
        if (text == null || text.isBlank()) {
            return Optional.empty();
        }
        String trimmed = text.trim();

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

        Matcher connectedMatcher = IS_CONNECTED.matcher(trimmed);
        if (connectedMatcher.matches()) {
            return Optional.of(new ParsedQuery(
                    connectedMatcher.group(1), "connected", connectedMatcher.group(2)));
        }

        Matcher predMatcher = IS_PRED.matcher(trimmed);
        if (predMatcher.matches()) {
            String subject = predMatcher.group(1);
            String rel = normalizeRelation(predMatcher.group(2));
            if (!rel.equals(subject.toLowerCase(Locale.ROOT))) {
                return Optional.of(new ParsedQuery(subject, rel, null));
            }
        }

        Matcher unaryMatcher = IS_UNARY.matcher(trimmed);
        if (unaryMatcher.matches()) {
            return Optional.of(new ParsedQuery(
                    unaryMatcher.group(1),
                    normalizeRelation(unaryMatcher.group(2)),
                    null));
        }

        return Optional.empty();
    }

    private static String normalizeRelation(String raw) {
        return raw.trim().toLowerCase(Locale.ROOT).replace(' ', '-');
    }
}
