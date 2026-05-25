package mytweetyapp;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class desclogic {
	
	    public static void main(String[] args) {
	        // Create an instance of the OWL API manager
	        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

	        try {
	            File exemplFile = findOntologyFile();
	            //charger le .owl
	            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(exemplFile);

	           //T-BOX
	            System.out.println("concepts:");
	            for (OWLClass owlClass : ontology.getClassesInSignature()) {
	                System.out.println(owlClass.getIRI().getShortForm());
	            }

	            // roles
	            System.out.println("\nRoles:");
	            for (OWLObjectProperty objectProperty : ontology.getObjectPropertiesInSignature()) {
	                System.out.println(objectProperty.getIRI().getShortForm());
	            }

	            // A-BOX
	            System.out.println("\nIndividuals:");
	            for (OWLNamedIndividual individual : ontology.getIndividualsInSignature()) {
	                System.out.println(individual.getIRI().getShortForm());
	            }
	        } catch (OWLOntologyCreationException e) {
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

			throw new IllegalStateException("Cannot find desclogic.owl from current directory");
		}

	}


