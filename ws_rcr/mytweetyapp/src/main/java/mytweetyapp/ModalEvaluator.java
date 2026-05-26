package mytweetyapp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Evaluateur modal (semantique de Kripke) — tramway autonome.
 * w₀ = monde actuel ; R(w₀) = {w₁…w₄} ; w₅ = monde interdit (affichage seulement).
 */
public final class ModalEvaluator {

    public record WorldSnapshot(String id, String caption, Map<String, Boolean> atoms, boolean accessible) {
    }

    private static final List<String> ATOM_ORDER = List.of(
            "AtStation", "Moving", "DoorOpen", "PowerFailure", "Emergency");

    private final Map<String, Boolean> w0 = new LinkedHashMap<>();
    private final List<Map<String, Boolean>> successors = new ArrayList<>();
    private final List<WorldSnapshot> successorMeta = new ArrayList<>();
    private final Map<String, Boolean> wBlocked = new LinkedHashMap<>();

    public ModalEvaluator() {
        set(w0, "AtStation", true);
        set(w0, "Moving", false);
        set(w0, "DoorOpen", false);
        set(w0, "PowerFailure", false);
        set(w0, "Emergency", false);

        addSuccessor("w1", "w₁ — Depart possible", world(
                true, true, false, false, false));
        addSuccessor("w2", "w₂ — Panne electrique", world(
                true, false, false, true, false));
        addSuccessor("w3", "w₃ — Portes ouvertes (embarquement)", world(
                true, false, true, false, false));
        addSuccessor("w4", "w₄ — Mode urgence", world(
                true, true, false, false, true));

        set(wBlocked, "AtStation", true);
        set(wBlocked, "Moving", true);
        set(wBlocked, "DoorOpen", true);
        set(wBlocked, "PowerFailure", false);
        set(wBlocked, "Emergency", false);
    }

    private static Map<String, Boolean> world(
            boolean atStation, boolean moving, boolean doorOpen,
            boolean powerFailure, boolean emergency) {
        Map<String, Boolean> w = new LinkedHashMap<>();
        set(w, "AtStation", atStation);
        set(w, "Moving", moving);
        set(w, "DoorOpen", doorOpen);
        set(w, "PowerFailure", powerFailure);
        set(w, "Emergency", emergency);
        return w;
    }

    private void addSuccessor(String id, String caption, Map<String, Boolean> world) {
        successors.add(world);
        successorMeta.add(new WorldSnapshot(id, caption, copy(world), true));
    }

    private static void set(Map<String, Boolean> world, String atom, boolean value) {
        world.put(atom, value);
    }

    public boolean entails(String tweetyFormula) {
        return evalAtWorld(normalize(tweetyFormula), w0);
    }

    private boolean evalAtWorld(String formula, Map<String, Boolean> world) {
        formula = formula.trim();
        if (formula.startsWith("[](") && formula.endsWith(")")) {
            String inner = formula.substring(3, formula.length() - 1);
            for (Map<String, Boolean> succ : successors) {
                if (!evalAtWorld(inner, succ)) {
                    return false;
                }
            }
            return !successors.isEmpty();
        }
        if (formula.startsWith("<>(") && formula.endsWith(")")) {
            String inner = formula.substring(3, formula.length() - 1);
            for (Map<String, Boolean> succ : successors) {
                if (evalAtWorld(inner, succ)) {
                    return true;
                }
            }
            return false;
        }
        if (formula.startsWith("!(")) {
            return !evalAtWorld(formula.substring(2, formula.length() - 1), world);
        }
        if (formula.startsWith("!")) {
            return !evalAtWorld(formula.substring(1), world);
        }
        int imp = indexOfTopLevel(formula, "=>");
        if (imp >= 0) {
            String left = formula.substring(0, imp);
            String right = formula.substring(imp + 2);
            return !evalAtWorld(left, world) || evalAtWorld(right, world);
        }
        int and = indexOfTopLevel(formula, "&&");
        if (and >= 0) {
            return evalAtWorld(formula.substring(0, and), world)
                    && evalAtWorld(formula.substring(and + 2), world);
        }
        int or = indexOfTopLevel(formula, "||");
        if (or >= 0) {
            return evalAtWorld(formula.substring(0, or), world)
                    || evalAtWorld(formula.substring(or + 2), world);
        }
        return truthValue(formula.trim(), world);
    }

    private static int indexOfTopLevel(String formula, String op) {
        int depth = 0;
        for (int i = 0; i <= formula.length() - op.length(); i++) {
            char c = formula.charAt(i);
            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
            } else if (depth == 0 && formula.startsWith(op, i)) {
                return i;
            }
        }
        return -1;
    }

    private static boolean truthValue(String atom, Map<String, Boolean> world) {
        return Boolean.TRUE.equals(world.get(atom));
    }

    private static String normalize(String formula) {
        return formula.replaceAll("\\s+", "");
    }

    public List<WorldSnapshot> snapshots() {
        List<WorldSnapshot> list = new ArrayList<>();
        list.add(new WorldSnapshot("w0", "w₀ — Monde actuel", copy(w0), true));
        list.addAll(successorMeta);
        list.add(new WorldSnapshot("w5", "w₅ — Interdit (□¬◇ danger)", copy(wBlocked), false));
        return list;
    }

    public List<WorldSnapshot> accessibleSuccessors() {
        return List.copyOf(successorMeta);
    }

    public static List<String> formatAtomLines(Map<String, Boolean> atoms) {
        List<String> lines = new ArrayList<>();
        for (String atom : ATOM_ORDER) {
            boolean v = Boolean.TRUE.equals(atoms.get(atom));
            lines.add((v ? "✓ " : "✗ ") + atom);
        }
        return lines;
    }

    public String accessibilitySummary() {
        return "R(w₀) = {w₁, w₂, w₃, w₄}  |  w₅ bloque par □¬◇(DoorOpen ∧ Moving)";
    }

    private static Map<String, Boolean> copy(Map<String, Boolean> source) {
        return new LinkedHashMap<>(source);
    }

    public void printFrame() {
        System.out.println("--- Modele de Kripke ---");
        System.out.println("  w0 : " + w0);
        for (WorldSnapshot s : successorMeta) {
            System.out.println("  " + s.id() + " : " + s.atoms());
        }
        System.out.println("  w5 (non accessible) : " + wBlocked);
        System.out.println("  " + accessibilitySummary());
    }
}
