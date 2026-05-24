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
    public static void main(String[] args) throws ParserException, IOException {
        System.out.println("=== TP 3: Logique des Défauts (Subscription Auto-Renewal) ===");

        // Setup the underlying First Order Logic Reasoner for Default Logic
        FolReasoner.setDefaultReasoner(new SimpleFolReasoner());

        /* 
         * Real-world scenario: Auto-renewing software subscriptions.
         * 
         * RULES:
         * 1. If someone is a Subscriber, by DEFAULT they will AutoRenew, 
         *    UNLESS we know their Card is Expired.
         * 
         * SCENARIO:
         * - Alice is a Subscriber. 
         * - Bob is a Subscriber.
         * - Bob's Card is Expired.
         */

        String kbString = 
                "Person = {alice, bob} \n" +
                "type(Subscriber(Person)) \n" +
                "type(AutoRenew(Person)) \n" +
                "type(CardExpired(Person)) \n" +
                
                // Facts
                "Subscriber(alice) \n" + 
                "Subscriber(bob) \n" +
                "CardExpired(bob) \n" +

                // Default Rule: Subscriber(X) : AutoRenew(X) / AutoRenew(X)
                // Meaning: If X is a Subscriber, and it is consistent to assume they will AutoRenew 
                //          (i.e., we don't have proof they can't, like a card expiry rule), then they AutoRenew.
                // We add a strict FOL rule: CardExpired(X) => !AutoRenew(X)
                // (In this RDL parser, strict rules can be added as regular FOL formulas)
                "!AutoRenew(bob) \n" + // Strict consequence of card expiry for bob
                
                "Subscriber(X) :: AutoRenew(X) / AutoRenew(X)"; 

        // 1. Build the Default Theory
        RdlParser parser = new RdlParser();
        DefaultTheory theory = parser.parseBeliefBase(kbString);
        System.out.println("Base de Connaissances (Théorie des Défauts) chargée.");
        
        // 2. Exploit the Knowledge Base
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
    }
}