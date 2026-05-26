package mytweetyapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * TP 4: Semantic Networks with default relation + exception blocking.
 */
public class BirdExceptionSemanticNet {

    static class Node {
        String name;
        List<Edge> outgoingRelations = new ArrayList<>();

        Node(String name) {
            this.name = name;
        }

        void addRelation(String relation, Node target) {
            outgoingRelations.add(new Edge(relation, target));
        }
    }

    static class Edge {
        String relation;
        Node target;

        Edge(String relation, Node target) {
            this.relation = relation;
            this.target = target;
        }
    }

    private final Map<String, Node> graph = new HashMap<>();

    Node node(String name) {
        graph.putIfAbsent(name, new Node(name));
        return graph.get(name);
    }

    boolean inherited(Node start, String relation, Node target) {
        if (!relation.equals("exception")) {
            for (Edge edge : start.outgoingRelations) {
                if (edge.relation.equals("exception") && edge.target.equals(target)) {
                    return false;
                }
            }
        }
        for (Edge edge : start.outgoingRelations) {
            if (edge.relation.equals(relation) && edge.target.equals(target)) {
                return true;
            }
        }
        for (Edge edge : start.outgoingRelations) {
            if (edge.relation.equals("is-a") && inherited(edge.target, relation, target)) {
                return true;
            }
        }
        return false;
    }

    public static BirdExceptionSemanticNet buildNetwork() {
        BirdExceptionSemanticNet net = new BirdExceptionSemanticNet();

        Node locomotion = net.node("Locomotion");
        Node voler = net.node("Voler");
        Node oiseaux = net.node("Oiseaux");
        Node moineaux = net.node("Moineaux");
        Node autruche = net.node("Autruche");

        oiseaux.addRelation("locomotion", voler);
        voler.addRelation("is-a", locomotion);
        moineaux.addRelation("is-a", oiseaux);
        autruche.addRelation("is-a", oiseaux);
        autruche.addRelation("exception", voler);
        return net;
    }

    public boolean ask(String subjectName, String relation, String objectName) {
        Node start = resolveNode(subjectName);
        if (start == null) {
            return false;
        }
        String rel = normalizeRelation(relation);
        String objName = objectName == null ? "" : objectName.trim();
        if (objName.isBlank() && ("voler".equals(rel) || "vole".equals(rel) || "fly".equals(rel) || "locomotion".equals(rel))) {
            objName = "Voler";
            rel = "locomotion";
        }
        if ("voler".equals(rel) || "vole".equals(rel) || "fly".equals(rel)) {
            rel = "locomotion";
            if (objName.isBlank()) {
                objName = "Voler";
            }
        }
        if ("a".equals(rel)) {
            rel = "is-a";
        }
        Node target = resolveNode(objName);
        if (target == null) {
            return false;
        }
        return inherited(start, rel, target);
    }

    Node resolveNode(String name) {
        return SemanticNetLookup.resolve(graph, name);
    }

    List<String> knownNodes() {
        return List.copyOf(graph.keySet());
    }

    private static String normalizeRelation(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.trim().toLowerCase(Locale.ROOT).replace(' ', '-');
    }

    public static void main(String[] args) {
        System.out.println("=== TP 4: Réseau sémantique avec exceptions (Oiseaux) ===");

        BirdExceptionSemanticNet net = buildNetwork();
        Node voler = net.node("Voler");
        Node moineaux = net.node("Moineaux");
        Node autruche = net.node("Autruche");

        boolean moineauVole = net.inherited(moineaux, "locomotion", voler);
        boolean autrucheHeriteVol = net.inherited(autruche, "locomotion", voler);
        boolean autrucheException = net.inherited(autruche, "exception", voler);

        System.out.println("Q1: Est-ce qu'un moineau vole (heritage) ? -> " + moineauVole);
        System.out.println("Q2: Est-ce qu'une autruche herite 'vole' ? -> " + autrucheHeriteVol);
        System.out.println("Q3: Exception active pour autruche->voler ? -> " + autrucheException);
        System.out.println("Conclusion: l'exception bloque l'inference par defaut pour l'autruche.");
    }
}
