package mytweetyapp;

import java.io.IOException;
import java.util.Locale;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.RelationalFormula;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.FolSignature;
import org.tweetyproject.logics.ml.parser.MlParser;
import org.tweetyproject.logics.ml.syntax.MlBeliefSet;

/**
 * TP 3 : Logique modale — Securite tramway (ville intelligente).
 * Syntaxe □/◇ ; evaluation par modele de Kripke ({@link ModalEvaluator}).
 */
public class SmartCityModal {

    private static final ModalEvaluator EVALUATOR = new ModalEvaluator();

    public static ModalEvaluator evaluator() {
        return EVALUATOR;
    }

    public static MlBeliefSet buildKb(MlParser parser) throws ParserException, IOException {
        FolSignature sig = new FolSignature();
        for (String prop : new String[]{"Moving", "DoorOpen", "AtStation", "PowerFailure", "Emergency"}) {
            sig.add(new Predicate(prop, 0));
        }
        parser.setSignature(sig);
        MlBeliefSet kb = new MlBeliefSet();
        for (String ax : ModalLogicKb.TWEETY_AXIOMS) {
            kb.add((RelationalFormula) parser.parseFormula(ax));
        }
        for (String fact : ModalLogicKb.TWEETY_FACTS) {
            kb.add((RelationalFormula) parser.parseFormula(fact));
        }
        return kb;
    }

    public static QueryAnswer ask(String subject, String relation, String object) {
        String formula = buildQueryFormula(subject, relation, object);
        if (formula == null) {
            return new QueryAnswer(false, "?",
                    "Relation: necessary / possible + proposition, ou formule: [](!Moving), <>(Moving)");
        }
        try {
            validateFormula(formula);
            boolean ok = EVALUATOR.entails(formula);
            String msg = ok ? "YES — ⊢ " + formula + "  (modele Kripke w0)" : "NO — ⊬ " + formula;
            return new QueryAnswer(ok, formula, msg);
        } catch (Exception e) {
            return new QueryAnswer(false, formula, "Erreur: " + e.getMessage());
        }
    }

    private static void validateFormula(String formula) throws ParserException, IOException {
        MlParser parser = new MlParser();
        buildKb(parser);
        parser.parseFormula(formula);
    }

    public static String buildQueryFormula(String subject, String relation, String object) {
        String sub = subject == null ? "" : subject.trim();
        String rel = relation == null ? "" : relation.trim().toLowerCase(Locale.ROOT);
        String obj = object == null ? "" : object.trim();

        if (sub.startsWith("[") || sub.startsWith("<") || sub.contains("[]") || sub.contains("<>")) {
            return sub.replaceAll("\\s+", "");
        }
        if (obj.startsWith("[") || obj.startsWith("<")) {
            return obj.replaceAll("\\s+", "");
        }

        if ("necessary".equals(rel) || "necessarily".equals(rel) || "box".equals(rel) || "[]".equals(rel)) {
            if (sub.isBlank()) {
                return null;
            }
            return ModalLogicKb.toTweetyNecessarily(sub);
        }
        if ("possible".equals(rel) || "possibly".equals(rel) || "diamond".equals(rel) || "<>".equals(rel)) {
            if (sub.isBlank()) {
                return null;
            }
            return ModalLogicKb.toTweetyPossibly(sub);
        }
        if (rel.isBlank() && !sub.isBlank() && obj.isBlank()) {
            return sub.replaceAll("\\s+", "");
        }
        return null;
    }

    public static void main(String[] args) throws ParserException, IOException {
        System.out.println("=== TP 3: Logique modale — Securite tramway (Smart City) ===\n");

        ModalLogicKb.printModalKnowledge();
        System.out.println();

        EVALUATOR.printFrame();
        System.out.println();

        MlParser parser = new MlParser();
        System.out.println("--- Base Tweety (serialisation) ---");
        System.out.println(buildKb(parser));
        System.out.println("\n--- Evaluation modale (w0 |= ?) ---");

        runQuery("[](!Moving)", "Q1: □(¬Moving) — necessairement a l'arret ?");
        runQuery("<>(Moving)", "Q2: ◇(Moving) — demarrage possible ?");
        runQuery("<>(DoorOpen)", "Q3: ◇(DoorOpen) — embarquement possible ?");
        runQuery("<>(Emergency)", "Q4: ◇(Emergency) — urgence possible ?");
        runQuery("<>(DoorOpen&&Moving)", "Q5: ◇(DoorOpen ∧ Moving) — danger possible ?");
        runQuery("<>(PowerFailure)", "Q6: ◇(PowerFailure) — panne possible ?");
        runQuery("[](DoorOpen=>!Moving)", "Q7: □(DoorOpen → ¬Moving) — regle de securite ?");

        System.out.println("\n--- Interpretation ---");
        System.out.println("w0 : tram a l'arret en station, portes fermees.");
        System.out.println("w1 : depart possible (◇Moving).");
        System.out.println("w2 : panne electrique (◇PowerFailure).");
        System.out.println("w3 : portes ouvertes a l'arret (◇DoorOpen).");
        System.out.println("w4 : mode urgence (◇Emergency).");
        System.out.println("w5 : etat interdit (DoorOpen ∧ Moving) — hors de R(w0).");

        System.out.println("\n--- GUI : Ask ---");
        System.out.println("Ex: necessary + !Moving | possible + Moving | <>(PowerFailure)");
    }

    private static void runQuery(String formula, String label) {
        boolean ok = EVALUATOR.entails(formula);
        System.out.println(label);
        System.out.println("  " + formula + "  →  " + ok);
    }
}
