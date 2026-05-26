package mytweetyapp;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.fol.reasoner.FolReasoner;
import org.tweetyproject.logics.fol.reasoner.SimpleFolReasoner;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.reasoner.SatReasoner;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.rdl.parser.RdlParser;
import org.tweetyproject.logics.rdl.reasoner.SimpleDefaultReasoner;
import org.tweetyproject.logics.rdl.syntax.DefaultTheory;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ExampleQueries {

    private ExampleQueries() {
    }

    public static List<String> relationsFor(Class<?> clazz) {
        return RELATIONS.getOrDefault(clazz.getSimpleName(), List.of());
    }

    public static boolean supportsInteractiveQuery(Class<?> clazz) {
        return RELATIONS.containsKey(clazz.getSimpleName());
    }

    private static final Map<String, List<String>> RELATIONS = Map.ofEntries(
            Map.entry("SmartCityFol", List.of("stops-at", "electric", "connected")),
            Map.entry("ECommercePl", List.of(
                    "order-confirmed", "order-shipped", "customer-notified",
                    "payment-received", "item-in-stock", "fraud-check-passed",
                    "shipping-address-valid", "refund-issued")),
            Map.entry("SubscriptionDefL", List.of("autorenew", "subscriber", "card-expired")),
            Map.entry("HospitalSemanticNet", List.of("works-in", "performs", "is-a")),
            Map.entry("UniversitySemanticNet", List.of("works-in", "teaches", "is-a")),
            Map.entry("BirdExceptionSemanticNet", List.of("locomotion", "voler", "is-a", "exception")),
            Map.entry("desclogic", List.of("type", "stops-at", "connects")),
            Map.entry("SmartCityModal", List.of("necessary", "possible"))
    );

    public static QueryAnswer ask(Class<?> clazz, String subject, String relation, String object)
            throws ParserException, IOException {
        String rel = relation == null ? "" : relation.trim().toLowerCase(Locale.ROOT);
        String sub = subject == null ? "" : subject.trim();
        String obj = object == null ? "" : object.trim();

        return switch (clazz.getSimpleName()) {
            case "SmartCityFol" -> querySmartCity(sub, rel, obj);
            case "ECommercePl" -> queryECommerce(sub, rel);
            case "SubscriptionDefL" -> querySubscription(sub, rel);
            case "HospitalSemanticNet" -> queryHospital(sub, rel, obj);
            case "UniversitySemanticNet" -> queryUniversity(sub, rel, obj);
            case "BirdExceptionSemanticNet" -> queryBird(sub, rel, obj);
            case "desclogic" -> desclogic.ask(sub, rel, obj);
            case "SmartCityModal" -> SmartCityModal.ask(sub, rel, obj);
            default -> new QueryAnswer(false, "?", "Interactive queries are not supported for this example.");
        };
    }

    private static QueryAnswer querySmartCity(String subject, String relation, String object)
            throws ParserException, IOException {
        if ("connects".equals(relation)) {
            relation = "connected";
        }
        var ctx = SmartCityFol.buildContext();
        String formula = switch (relation) {
            case "stops-at", "on", "stopsat" -> {
                if (object.isBlank()) {
                    yield null;
                }
                yield "StopsAt(" + subject + ", " + object + ")";
            }
            case "electric" -> "Electric(" + subject + ")";
            case "connected" -> {
                if (object.isBlank()) {
                    yield null;
                }
                yield "Connected(" + subject + ", " + object + ")";
            }
            default -> null;
        };
        if (formula == null) {
            return new QueryAnswer(false, "?", "Unknown relation or missing object. Use: stops-at, electric, connected.");
        }
        FolFormula q = (FolFormula) ctx.parser().parseFormula(formula);
        boolean ok = ctx.reasoner().query(ctx.kb(), q);
        return new QueryAnswer(ok, formula, explain(ok, formula));
    }

    private static QueryAnswer queryECommerce(String subject, String relation) throws ParserException, IOException {
        PlParser parser = new PlParser();
        PlBeliefSet kb = ECommercePl.buildKnowledgeBase(parser);
        String atom = ECommercePlKb.atomForRelation(relation);
        if (atom == null) {
            return new QueryAnswer(false, "?",
                    "Relations: order-confirmed, order-shipped, customer-notified, payment-received, "
                            + "item-in-stock, fraud-check-passed, refund-issued.");
        }
        if (!subject.isBlank() && !subject.equalsIgnoreCase("order") && !subject.equalsIgnoreCase("customer")) {
            return new QueryAnswer(false, atom,
                    "Requetes PL sur les propositions (Subject ignore). Ex: Is order shipped?");
        }
        PlFormula q = (PlFormula) parser.parseFormula(atom);
        SatReasoner reasoner = new SatReasoner();
        boolean ok = reasoner.query(kb, q);
        String ctx = " (scenario GUI: commande acceptee — " + ECommercePlKb.scenarioLabel(ECommercePlKb.Scenario.SUCCESS) + ")";
        return new QueryAnswer(ok, atom, explain(ok, atom) + ctx);
    }

    private static QueryAnswer querySubscription(String subject, String relation) throws ParserException, IOException {
        RdlParser parser = new RdlParser();
        DefaultTheory theory = SubscriptionDefL.buildTheory(parser);
        SimpleDefaultReasoner reasoner = new SimpleDefaultReasoner();
        String formula = switch (relation) {
            case "autorenew" -> "AutoRenew(" + subject + ")";
            case "subscriber" -> "Subscriber(" + subject + ")";
            case "card-expired", "cardexpired" -> "CardExpired(" + subject + ")";
            default -> null;
        };
        if (formula == null || subject.isBlank()) {
            return new QueryAnswer(false, "?", "Provide a person (alice, bob) and: autorenew, subscriber, card-expired.");
        }
        FolFormula q = (FolFormula) parser.parseFormula(formula);
        boolean ok = reasoner.query(theory, q);
        return new QueryAnswer(ok, formula, explain(ok, formula));
    }

    private static QueryAnswer queryHospital(String subject, String relation, String object) {
        return querySemanticNet(HospitalSemanticNet.buildNetwork(), subject, relation, object);
    }

    private static QueryAnswer queryUniversity(String subject, String relation, String object) {
        return querySemanticNet(UniversitySemanticNet.buildNetwork(), subject, relation, object);
    }

    private static QueryAnswer queryBird(String subject, String relation, String object) {
        BirdExceptionSemanticNet net = BirdExceptionSemanticNet.buildNetwork();
        String rel = relation;
        String obj = object;
        if (obj.isBlank() && ("voler".equals(rel) || "vole".equals(rel) || "locomotion".equals(rel) || "fly".equals(rel))) {
            obj = "Voler";
        }
        if (subject.isBlank() || rel.isBlank()) {
            return semanticNetHelp(false, subject, rel, obj,
                    "Provide subject + relation. For voler/locomotion, object defaults to Voler.",
                    net.knownNodes());
        }
        if (net.resolveNode(subject) == null) {
            return semanticNetHelp(false, subject, rel, obj,
                    "Unknown subject (case-insensitive).", net.knownNodes());
        }
        if (!obj.isBlank() && net.resolveNode(obj) == null) {
            return semanticNetHelp(false, subject, rel, obj,
                    "Unknown object (case-insensitive).", net.knownNodes());
        }
        boolean ok = net.ask(subject, rel, obj);
        String formula = formatSemanticFormula(subject, rel, obj.isBlank() ? "Voler" : obj);
        return new QueryAnswer(ok, formula, explain(ok, formula) + " (heritage + exceptions)");
    }

    private static QueryAnswer querySemanticNet(
            Object net,
            String subject,
            String relation,
            String object) {
        if (subject.isBlank() || relation.isBlank() || object.isBlank()) {
            List<String> nodes = List.of();
            if (net instanceof HospitalSemanticNet h) {
                nodes = h.knownNodes();
            } else if (net instanceof UniversitySemanticNet u) {
                nodes = u.knownNodes();
            }
            return semanticNetHelp(false, subject, relation, object,
                    "Provide subject, relation (e.g. works-in), and object (e.g. Hospital).", nodes);
        }
        boolean ok;
        String formula = formatSemanticFormula(subject, relation, object);
        if (net instanceof HospitalSemanticNet h) {
            if (h.resolveNode(subject) == null || h.resolveNode(object) == null) {
                return semanticNetHelp(false, subject, relation, object,
                        "Unknown node name (case-insensitive).", h.knownNodes());
            }
            ok = h.ask(subject, relation, object);
        } else if (net instanceof UniversitySemanticNet u) {
            if (u.resolveNode(subject) == null || u.resolveNode(object) == null) {
                return semanticNetHelp(false, subject, relation, object,
                        "Unknown node name (case-insensitive).", u.knownNodes());
            }
            ok = u.ask(subject, relation, object);
        } else {
            return new QueryAnswer(false, formula, "Unsupported semantic network.");
        }
        return new QueryAnswer(ok, formula, explain(ok, formula) + " (inheritance via is-a)");
    }

    private static String formatSemanticFormula(String subject, String relation, String object) {
        return subject + " --" + relation + "--> " + object;
    }

    private static QueryAnswer semanticNetHelp(
            boolean entailed,
            String subject,
            String relation,
            String object,
            String message,
            List<String> nodes) {
        String formula = formatSemanticFormula(
                subject == null ? "" : subject,
                relation == null ? "" : relation,
                object == null ? "" : object);
        String nodesHint = nodes.isEmpty() ? "" : " Known nodes: " + String.join(", ", nodes) + ".";
        return new QueryAnswer(entailed, formula, message + nodesHint);
    }

    private static String explain(boolean ok, String formula) {
        return ok ? "YES — entailed by the knowledge base: " + formula
                : "NO — not entailed: " + formula;
    }

    public static Map<String, String> exampleHints() {
        Map<String, String> hints = new LinkedHashMap<>();
        hints.put("SmartCityFol", "Is tramA on northPark? | Is tramA electric? | Is northPark connected centralStation?");
        hints.put("ECommercePl",
                "Is order shipped? | Is order confirmed? | Is payment received? | Is refund issued?");
        hints.put("SubscriptionDefL", "Is alice autorenew? | Is bob card-expired?");
        hints.put("HospitalSemanticNet", "Does Dr_House work-in Hospital? | Does Dr_House performs Surgery?");
        hints.put("BirdExceptionSemanticNet",
                "Does Moineaux voler? | Is Autruche a Oiseaux? | Does Moineaux locomotion Voler? | Does Autruche exception Voler?");
        hints.put("desclogic",
                "Tram(tramA) | Is tramA a Tram? | Is tramA on northPark? | Is northPark connected centralStation?");
        hints.put("SmartCityModal",
                "necessary + !Moving | possible + Moving | formula: <>(PowerFailure)");
        return hints;
    }
}
