package mytweetyapp;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.reasoner.SatReasoner;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import java.io.IOException;

public class ECommercePl {

    public static PlBeliefSet buildKnowledgeBase(PlParser parser) throws ParserException, IOException {
        PlBeliefSet kb = new PlBeliefSet();
        kb.add((PlFormula) parser.parseFormula("(PaymentReceived && ItemInStock) => OrderShipped"));
        kb.add((PlFormula) parser.parseFormula("OrderShipped => CustomerNotified"));
        kb.add((PlFormula) parser.parseFormula("PaymentReceived"));
        kb.add((PlFormula) parser.parseFormula("ItemInStock"));
        return kb;
    }

    public static void main(String[] args) throws ParserException, IOException {
        System.out.println("=== TP 2: Propositional Logic (E-commerce Order Processing) ===");

        PlParser parser = new PlParser();
        PlBeliefSet kb = buildKnowledgeBase(parser);

        System.out.println("Knowledge Base Created: ");
        System.out.println(kb.toString());

        SatReasoner reasoner = new SatReasoner();
        
        System.out.println("\n--- Queries & Exploitation ---");
        
        // Query 1: Will the order be shipped?
        PlFormula query1 = (PlFormula) parser.parseFormula("OrderShipped");
        System.out.println("Q1: Will the order be shipped? -> " + reasoner.query(kb, query1));

        // Query 2: Has the customer been notified?
        PlFormula query2 = (PlFormula) parser.parseFormula("CustomerNotified");
        System.out.println("Q2: Has the customer been notified? -> " + reasoner.query(kb, query2));
        
        // Query 3: Is the payment missing?
        PlFormula query3 = (PlFormula) parser.parseFormula("!PaymentReceived");
        System.out.println("Q3: Is the payment missing? -> " + reasoner.query(kb, query3));
    }
}