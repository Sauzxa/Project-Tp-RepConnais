package mytweetyapp;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.fol.reasoner.FolReasoner;
import org.tweetyproject.logics.fol.reasoner.SimpleFolReasoner;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.rdl.parser.RdlParser;
import org.tweetyproject.logics.rdl.reasoner.SimpleDefaultReasoner;
import org.tweetyproject.logics.rdl.syntax.DefaultTheory;

import java.io.IOException;

public class SubscriptionDefL {

    public static DefaultTheory buildTheory(RdlParser parser) throws ParserException, IOException {
        FolReasoner.setDefaultReasoner(new SimpleFolReasoner());

        String kbString =
                "Person = {alice, bob} \n" +
                "type(Subscriber(Person)) \n" +
                "type(AutoRenew(Person)) \n" +
                "type(CardExpired(Person)) \n" +
                "Subscriber(alice) \n" +
                "Subscriber(bob) \n" +
                "CardExpired(bob) \n" +
                "!AutoRenew(bob) \n" +
                "Subscriber(X) :: AutoRenew(X) / AutoRenew(X)";

        return parser.parseBeliefBase(kbString);
    }

    public static void main(String[] args) throws ParserException, IOException {
        System.out.println("=== TP 3: Logique des Défauts (Subscription Auto-Renewal) ===");

        RdlParser parser = new RdlParser();
        DefaultTheory theory = buildTheory(parser);
        System.out.println("Base de Connaissances (Théorie des Défauts) chargée.");

        SimpleDefaultReasoner reasoner = new SimpleDefaultReasoner();

        System.out.println("\n--- Exécution & Preuves ---");
        
        // Query Alice
        FolFormula aliceRenew = (FolFormula) parser.parseFormula("AutoRenew(alice)");
        System.out.println("Q1: Est-ce que Alice va auto-renouveler par défaut ? -> " + 
                            reasoner.query(theory, aliceRenew));

        // Query Bob
        FolFormula bobRenew = (FolFormula) parser.parseFormula("AutoRenew(bob)");
        System.out.println("Q2: Est-ce que Bob va auto-renouveler par défaut ? -> " + 
                            reasoner.query(theory, bobRenew));
                            
        FolFormula bobNotRenew = (FolFormula) parser.parseFormula("!AutoRenew(bob)");
        System.out.println("Q3: A-t-on prouvé que Bob NE DOIT PAS auto-renouveler ? -> " + 
                            reasoner.query(theory, bobNotRenew));

        System.out.println("\n--- Questions interactives (GUI) ---");
        System.out.println("Dans TpRunnerFx: Is alice autorenew? | Is bob card-expired?");
    }
}