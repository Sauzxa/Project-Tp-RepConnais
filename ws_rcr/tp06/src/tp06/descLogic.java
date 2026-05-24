package tp06;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class descLogic {
	
	
	

	    public static void main(String[] args) {
			// Create an instance of the OWL API manager (use fully-qualified names to avoid import accessibility issues)
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

	        try {
	            File exemplFile = findOntologyFile();
						//charger le .owl
						org.semanticweb.owlapi.model.OWLOntology ontology = manager.loadOntologyFromOntologyDocument(exemplFile);

	           //T-BOX
	            System.out.println("concepts:");
						for (org.semanticweb.owlapi.model.OWLClass owlClass : ontology.getClassesInSignature()) {
								System.out.println(owlClass.getIRI());
	            }

	            // roles
	            System.out.println("\nRoles:");
				for (org.semanticweb.owlapi.model.OWLObjectProperty objectProperty : ontology.getObjectPropertiesInSignature()) {
					System.out.println(objectProperty.getIRI());
	            }

	            // A-BOX
	            System.out.println("\nIndividuals:");
				for (org.semanticweb.owlapi.model.OWLNamedIndividual individual : ontology.getIndividualsInSignature()) {
					System.out.println(individual.getIRI());
	            }
	        } catch (org.semanticweb.owlapi.model.OWLOntologyCreationException e) {
	            e.printStackTrace();
	        }

	    }

		private static File findOntologyFile() {
			Path[] candidates = {
					Paths.get("desclogic.owl"),
					Paths.get("../desclogic.owl"),
					Paths.get("../../desclogic.owl")
			};

			for (Path candidate : candidates) {
				if (Files.isRegularFile(candidate)) {
					return candidate.toFile();
				}
			}

			throw new IllegalStateException("Cannot find desclogic.owl from " + Paths.get("").toAbsolutePath());
		}

	}

