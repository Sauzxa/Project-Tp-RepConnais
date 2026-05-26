package mytweetyapp;

/**
 * Base de connaissances TP3 en logique modale (notation K).
 */
public final class ModalLogicKb {

    private ModalLogicKb() {
    }

    public static final String DOMAIN =
            "Tramway autonome — contraintes modales (mondes = etats du systeme)";

    public static final String[] MODAL_AXIOMS = {
            "□(DoorOpen → ¬Moving)",
            "□¬◇(DoorOpen ∧ Moving)",
            "◇Moving",
            "◇PowerFailure",
            "◇DoorOpen",
            "◇Emergency"
    };

    public static final String[] WORLDS_DESCRIPTION = {
            "w₀ — actuel : a l'arret en station, portes fermees",
            "w₁ — accessible : depart (Moving)",
            "w₂ — accessible : panne (PowerFailure)",
            "w₃ — accessible : embarquement (DoorOpen, arret)",
            "w₄ — accessible : urgence (Emergency, Moving)",
            "w₅ — NON accessible : DoorOpen ∧ Moving (interdit)"
    };

    public static final String[] FACTS = {
            "AtStation",
            "¬Moving",
            "¬DoorOpen",
            "¬Emergency"
    };

    public static final String[] DEMO_QUERIES = {
            "□(¬Moving) ?",
            "◇(Moving) ?",
            "◇(DoorOpen) ?",
            "◇(Emergency) ?",
            "◇(DoorOpen ∧ Moving) ?",
            "◇(PowerFailure) ?"
    };

    public static final String[] TWEETY_AXIOMS = {
            "[](DoorOpen => !Moving)",
            "!(<>(DoorOpen && Moving))",
            "<>(Moving)",
            "<>(PowerFailure)",
            "<>(DoorOpen)",
            "<>(Emergency)"
    };

    public static final String[] TWEETY_FACTS = {
            "AtStation",
            "!Moving",
            "!DoorOpen",
            "!Emergency"
    };

    public static void printModalKnowledge() {
        System.out.println("=== Logique modale (notation □ / ◇) ===");
        System.out.println(DOMAIN);
        System.out.println("Syntaxe Tweety : [] = □, <> = ◇\n");

        System.out.println("--- Axiomes modaux ---");
        for (int i = 0; i < MODAL_AXIOMS.length; i++) {
            System.out.println("  " + MODAL_AXIOMS[i]);
            System.out.println("      → " + TWEETY_AXIOMS[i]);
        }

        System.out.println("\n--- Mondes (modele de Kripke) ---");
        for (String w : WORLDS_DESCRIPTION) {
            System.out.println("  " + w);
        }

        System.out.println("\n--- Faits (w₀) ---");
        for (int i = 0; i < FACTS.length; i++) {
            System.out.println("  " + FACTS[i] + "  →  " + TWEETY_FACTS[i]);
        }

        System.out.println("\n--- Requetes ---");
        for (String q : DEMO_QUERIES) {
            System.out.println("  " + q);
        }
    }

    public static String toTweetyNecessarily(String proposition) {
        return "[](" + proposition.trim() + ")";
    }

    public static String toTweetyPossibly(String proposition) {
        return "<>(" + proposition.trim() + ")";
    }
}
