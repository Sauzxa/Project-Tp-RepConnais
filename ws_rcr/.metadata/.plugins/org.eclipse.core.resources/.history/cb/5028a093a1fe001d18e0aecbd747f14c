package mytweetyapp;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import java.io.File;

public class desclogic {
	
	
	

	    public static void main(String[] args) {
	        // Path to the .owl file
	        String FILE = "D:/desclogic5.owl";

	        // Create an instance of the OWL API manager
	        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

	        try {
	            File exemplFile=new File(FILE);
	            //charger le .owl
	            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(exemplFile);

	           //T-BOX
	            System.out.println("concepts:");
	            for (OWLClass owlClass : ontology.getClassesInSignature()) {
	                System.out.println(owlClass.getIRI());
	            }

	            // roles
	            System.out.println("\nRoles:");
	            for (OWLObjectProperty objectProperty : ontology.getObjectPropertiesInSignature()) {
	                System.out.println(objectProperty.getIRI());
	            }

	            // A-BOX
	            System.out.println("\nIndividuals:");
	            for (OWLNamedIndividual individual : ontology.getIndividualsInSignature()) {
	                System.out.println(individual.getIRI());
	            }
	        } catch (OWLOntologyCreationException e) {
	            e.printStackTrace();
	        }

	    }

	}


