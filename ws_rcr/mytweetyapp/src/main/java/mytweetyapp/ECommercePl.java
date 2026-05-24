package mytweetyapp;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.reasoner.SatReasoner;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import java.io.IOException;

public class ECommercePl {
    public static void main(String[] args) throws ParserException, IOException {
        System.out.println("=== TP 2: Propositional Logic (E-commerce Order Processing) ===");

        // 1. Build the Knowledge Base
        PlParser parser = new PlParser();
        PlBeliefSet kb = new PlBeliefSet();

        /*
         * RULES of the E-commerce platform:
         * 1. If Payment is received AND Item is in stock, then the Order is Shipped
         * 2. If the Order is Shipped, the Customer is Notified (Email/SMS sent)
         */
        kb.add((PlFormula) parser.parseFormula("(PaymentReceived && ItemInStock) => OrderShipped"));
        kb.add((PlFormula) parser.parseFormula("OrderShipped => CustomerNotified"));

        /*
         * FACTS (What actually happened in reality for this specific order):
         * - Payment was processed successfully.
         * - The Warehouse confirms the item is in stock.
         */
        kb.add((PlFormula) parser.parseFormula("PaymentReceived")); 
        kb.add((PlFormula) parser.parseFormula("ItemInStock"));     
        
        System.out.println("Knowledge Base Created: ");
        System.out.println(kb.toString());

        // 2. Reasoning / Exploitation
        SatReasoner reasoner = new SatReasoner();
        
        System.out.println("\n--- Queries & Exploitation ---");
        
        // Query 1: Can we infer that the order is shipped?
        PlFormula query1 = (PlFormula) parser.parseFormula("OrderShipped");
        System.out.println("Q1: Will the order be shipped? -> " + reasoner.query(kb, query1));

        // Query 2: Did the system infer that we should notify the customer?
        PlFormula query2 = (PlFormula) parser.parseFormula("CustomerNotified");
        System.out.println("Q2: Has the customer been notified? -> " + reasoner.query(kb, query2));
        
        // Query 3: Is it possible the payment failed?
        PlFormula query3 = (PlFormula) parser.parseFormula("!PaymentReceived");
        System.out.println("Q3: Is the payment missing? -> " + reasoner.query(kb, query3));
    }
}