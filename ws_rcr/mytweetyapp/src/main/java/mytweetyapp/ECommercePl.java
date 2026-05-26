package mytweetyapp;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.reasoner.SatReasoner;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import java.io.IOException;

/**
 * TP 2 : Logique propositionnelle — traitement d'une commande e-commerce.
 * Chaque etape du workflow est une proposition ; les regles metier sont des implications.
 *
 * @see ECommercePlKb
 */
public class ECommercePl {

    public record PlContext(PlParser parser, PlBeliefSet kb, SatReasoner reasoner) {
    }

    public static PlBeliefSet buildKnowledgeBase(PlParser parser) throws ParserException, IOException {
        return buildKnowledgeBase(parser, ECommercePlKb.Scenario.SUCCESS);
    }

    public static PlBeliefSet buildKnowledgeBase(PlParser parser, ECommercePlKb.Scenario scenario)
            throws ParserException, IOException {
        PlBeliefSet kb = new PlBeliefSet();
        for (String rule : ECommercePlKb.RULES) {
            kb.add((PlFormula) parser.parseFormula(rule));
        }
        for (String fact : ECommercePlKb.factsFor(scenario)) {
            kb.add((PlFormula) parser.parseFormula(fact));
        }
        return kb;
    }

    public static PlContext buildContext() throws ParserException, IOException {
        return buildContext(ECommercePlKb.Scenario.SUCCESS);
    }

    public static PlContext buildContext(ECommercePlKb.Scenario scenario) throws ParserException, IOException {
        PlParser parser = new PlParser();
        PlBeliefSet kb = buildKnowledgeBase(parser, scenario);
        return new PlContext(parser, kb, new SatReasoner());
    }

    public static void main(String[] args) throws ParserException, IOException {
        System.out.println("=== TP 2: Logique propositionnelle — E-commerce ===\n");

        runScenario(ECommercePlKb.Scenario.SUCCESS);
        runScenario(ECommercePlKb.Scenario.FRAUD_BLOCKED);
        runScenario(ECommercePlKb.Scenario.OUT_OF_STOCK);

        System.out.println("\n--- Ask (GUI) ---");
        System.out.println("Ex: Is order shipped? | Is payment received? | Is refund issued?");
        System.out.println("Scenario actif dans l'interface : SUCCESS (commande acceptee).");
    }

    private static void runScenario(ECommercePlKb.Scenario scenario) throws ParserException, IOException {
        ECommercePlKb.printKnowledge(scenario);
        PlContext ctx = buildContext(scenario);
        PlParser parser = ctx.parser();
        SatReasoner reasoner = ctx.reasoner();

        System.out.println("\n--- Base Tweety ---");
        System.out.println(ctx.kb());

        System.out.println("\n--- Requetes (entailment PL) ---");
        String[] queries = switch (scenario) {
            case SUCCESS -> ECommercePlKb.DEMO_QUERIES_SUCCESS;
            case FRAUD_BLOCKED -> ECommercePlKb.DEMO_QUERIES_FRAUD;
            case OUT_OF_STOCK -> ECommercePlKb.DEMO_QUERIES_STOCK;
        };
        for (String label : queries) {
            String formula = label.replaceAll("\\s*\\?.*", "").trim();
            PlFormula q = (PlFormula) parser.parseFormula(formula);
            boolean ok = reasoner.query(ctx.kb(), q);
            System.out.println("  " + label + "  →  " + ok);
        }
        System.out.println();
    }
}
