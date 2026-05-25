package mytweetyapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public static void main(String[] args) {
        System.out.println("=== TP 4: Réseau sémantique avec exceptions (Oiseaux) ===");

        BirdExceptionSemanticNet net = new BirdExceptionSemanticNet();

        Node locomotion = net.node("Locomotion");
        Node voler = net.node("Voler");
        Node oiseaux = net.node("Oiseaux");
        Node moineaux = net.node("Moineaux");
        Node autruche = net.node("Autruche");

        // Relation générale (typique): les oiseaux volent.
        oiseaux.addRelation("locomotion", voler);
        voler.addRelation("is-a", locomotion);

        // Taxonomie.
        moineaux.addRelation("is-a", oiseaux);
        autruche.addRelation("is-a", oiseaux);

        // Exception explicite: autruche ne vole pas (blocage de l'heritage).
        autruche.addRelation("exception", voler);

        boolean moineauVole = net.inherited(moineaux, "locomotion", voler);
        boolean autrucheHeriteVol = net.inherited(autruche, "locomotion", voler);
        boolean autrucheException = net.inherited(autruche, "exception", voler);

        System.out.println("Q1: Est-ce qu'un moineau vole (heritage) ? -> " + moineauVole);
        System.out.println("Q2: Est-ce qu'une autruche herite 'vole' ? -> " + autrucheHeriteVol);
        System.out.println("Q3: Exception active pour autruche->voler ? -> " + autrucheException);
        System.out.println("Conclusion: l'exception bloque l'inference par defaut pour l'autruche.");
    }
}
