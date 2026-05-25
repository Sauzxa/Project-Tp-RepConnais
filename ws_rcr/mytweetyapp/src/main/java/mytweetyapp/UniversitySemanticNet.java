package mytweetyapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TP 4: Semantic Networks (University Domain)
 */
public class UniversitySemanticNet {

    static class Node {
        String name;
        List<Edge> outgoingRelations = new ArrayList<>();

        Node(String name) {
            this.name = name;
        }

        void addRelation(String type, Node target) {
            outgoingRelations.add(new Edge(type, target));
        }
    }

    static class Edge {
        String relationType;
        Node target;

        Edge(String relationType, Node target) {
            this.relationType = relationType;
            this.target = target;
        }
    }

    private final Map<String, Node> network = new HashMap<>();

    Node getOrCreateNode(String name) {
        network.putIfAbsent(name, new Node(name));
        return network.get(name);
    }

    boolean checkHeritage(Node start, String relationToFind, Node targetToFind) {
        for (Edge edge : start.outgoingRelations) {
            if (edge.relationType.equals(relationToFind) && edge.target.equals(targetToFind)) {
                return true;
            }
        }
        for (Edge edge : start.outgoingRelations) {
            if (edge.relationType.equals("is-a") && checkHeritage(edge.target, relationToFind, targetToFind)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println("=== TP 4: Réseaux Sémantiques (University Management) ===");

        UniversitySemanticNet semanticNet = new UniversitySemanticNet();

        Node person = semanticNet.getOrCreateNode("Person");
        Node professor = semanticNet.getOrCreateNode("Professor");
        Node aiProfessor = semanticNet.getOrCreateNode("AI_Professor");
        Node university = semanticNet.getOrCreateNode("University");
        Node drSmith = semanticNet.getOrCreateNode("Dr_Smith");
        Node machineLearning = semanticNet.getOrCreateNode("Machine_Learning");

        professor.addRelation("is-a", person);
        aiProfessor.addRelation("is-a", professor);
        drSmith.addRelation("is-a", aiProfessor);
        person.addRelation("works-in", university);
        aiProfessor.addRelation("teaches", machineLearning);

        System.out.println("Réseau Sémantique Universitaire construit avec succès.");
        System.out.println("\n--- Exploitation & Héritage de Propriétés ---");

        boolean teachesMl = semanticNet.checkHeritage(drSmith, "teaches", machineLearning);
        System.out.println("Q1: Est-ce que Dr_Smith enseigne Machine_Learning ? -> " + teachesMl);

        boolean worksInUniversity = semanticNet.checkHeritage(drSmith, "works-in", university);
        System.out.println("Q2: Est-ce que Dr_Smith travaille dans une université ? -> " + worksInUniversity);

        boolean isUniversity = semanticNet.checkHeritage(drSmith, "is-a", university);
        System.out.println("Q3: Est-ce que Dr_Smith est une université ? -> " + isUniversity);
    }
}
