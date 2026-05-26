package mytweetyapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * TP 4: Réseaux Sémantiques (Semantic Networks)
 * Modélisation d'un hôpital et de l'héritage des propriétés.
 */
public class HospitalSemanticNet {

    // Internal representation of a Semantic Network Node
    static class Node {
        String name;
        List<Edge> outgoingRelations = new ArrayList<>();

        public Node(String name) {
            this.name = name;
        }

        public void addRelation(String type, Node target) {
            outgoingRelations.add(new Edge(type, target));
        }
    }

    // Internal representation of an Edge (Relation)
    static class Edge {
        String relationType; // e.g., "is-a", "works-in", "treats"
        Node target;

        public Edge(String relationType, Node target) {
            this.relationType = relationType;
            this.target = target;
        }
    }

    // The Semantic Network Graph
    private Map<String, Node> network = new HashMap<>();

    public Node getOrCreateNode(String name) {
        network.putIfAbsent(name, new Node(name));
        return network.get(name);
    }

    // 1. Heritage Algorithm (Exploitation of the Semantic Network)
    // This answers queries like: "Does X have property Y?" by traversing up the "is-a" taxonomy.
    public boolean checkHeritage(Node start, String relationToFind, Node targetToFind) {
        // Direct relation check
        for (Edge edge : start.outgoingRelations) {
            if (edge.relationType.equals(relationToFind) && edge.target.equals(targetToFind)) {
                return true;
            }
        }

        // Semantic Inheritance (Recursive check up the "is-a" hierarchy)
        for (Edge edge : start.outgoingRelations) {
            if (edge.relationType.equals("is-a")) {
                if (checkHeritage(edge.target, relationToFind, targetToFind)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static HospitalSemanticNet buildNetwork() {
        HospitalSemanticNet semanticNet = new HospitalSemanticNet();

        Node professional = semanticNet.getOrCreateNode("Healthcare_Professional");
        Node doctor = semanticNet.getOrCreateNode("Doctor");
        Node surgeon = semanticNet.getOrCreateNode("Surgeon");
        Node hospital = semanticNet.getOrCreateNode("Hospital");
        Node drHouse = semanticNet.getOrCreateNode("Dr_House");

        surgeon.addRelation("is-a", doctor);
        doctor.addRelation("is-a", professional);
        drHouse.addRelation("is-a", surgeon);
        professional.addRelation("works-in", hospital);
        surgeon.addRelation("performs", semanticNet.getOrCreateNode("Surgery"));
        return semanticNet;
    }

    public boolean ask(String subjectName, String relation, String objectName) {
        Node start = resolveNode(subjectName);
        Node target = resolveNode(objectName);
        if (start == null || target == null) {
            return false;
        }
        String rel = relation == null ? "" : relation.trim().toLowerCase(Locale.ROOT);
        if ("a".equals(rel)) {
            rel = "is-a";
        }
        return checkHeritage(start, rel, target);
    }

    Node resolveNode(String name) {
        return SemanticNetLookup.resolve(network, name);
    }

    List<String> knownNodes() {
        return List.copyOf(network.keySet());
    }

    public static void main(String[] args) {
        System.out.println("=== TP 4: Réseaux Sémantiques (Hospital Management) ===");

        HospitalSemanticNet semanticNet = buildNetwork();
        Node hospital = semanticNet.getOrCreateNode("Hospital");
        Node drHouse = semanticNet.getOrCreateNode("Dr_House");

        System.out.println("Réseau Sémantique Hospitalier construit avec succès.");

        // 3. Exploitation (Reasoning via Inheritance)
        System.out.println("\n--- Exploitation & Héritage de Propriétés ---");

        // Query 1: Direct Property
        boolean performsSurgery = semanticNet.checkHeritage(drHouse, "performs", semanticNet.getOrCreateNode("Surgery"));
        System.out.println("Q1: Est-ce que Dr. House pratique des chirurgies ? (Héritage depuis Surgeon) -> " + performsSurgery);

        // Query 2: Deep Inheritance
        boolean worksInHospital = semanticNet.checkHeritage(drHouse, "works-in", hospital);
        System.out.println("Q2: Est-ce que Dr. House travaille dans un hôpital ? (Héritage depuis Healthcare_Professional) -> " + worksInHospital);

        // Query 3: False path
        boolean isHospital = semanticNet.checkHeritage(drHouse, "is-a", hospital);
        System.out.println("Q3: Est-ce que Dr. House EST un hôpital ? -> " + isHospital);
    }
}