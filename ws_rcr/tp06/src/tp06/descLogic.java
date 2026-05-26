package tp06;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

/**
 * Module tp06 — charge la meme ontologie Smart City que {@code mytweetyapp.desclogic}.
 * Preferez: {@code mvn -pl mytweetyapp exec:java -Dexec.mainClass=mytweetyapp.desclogic}
 */
public class descLogic {

    public static void main(String[] args) {
        System.out.println("=== tp06: OWL Smart City (voir aussi mytweetyapp.desclogic) ===\n");
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        try {
            File owl = findOntologyFile();
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(owl);
            System.out.println("Ontologie chargee: " + owl.getAbsolutePath());
            System.out.println("Classes: " + ontology.getClassesInSignature().size());
            System.out.println("Individus: " + ontology.getIndividualsInSignature().size());

            OWLReasonerFactory factory = new ReasonerFactory();
            OWLReasoner reasoner = factory.createReasoner(ontology);
            try {
                System.out.println("Coherente: " + reasoner.isConsistent());
            } finally {
                reasoner.dispose();
            }
            System.out.println("\nPour la demo complete: mvn -pl mytweetyapp exec:java -Dexec.mainClass=mytweetyapp.desclogic");
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
    }

    private static File findOntologyFile() {
        Path[] candidates = {
                Paths.get("desclogic.owl"),
                Paths.get("../desclogic.owl"),
                Paths.get("../../desclogic.owl"),
                Paths.get("../../../desclogic.owl")
        };
        for (Path candidate : candidates) {
            if (Files.isRegularFile(candidate)) {
                return candidate.toFile();
            }
        }
        throw new IllegalStateException("Cannot find desclogic.owl from " + Paths.get("").toAbsolutePath());
    }
}
