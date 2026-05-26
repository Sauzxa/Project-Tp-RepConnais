package mytweetyapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.Sort;
import org.tweetyproject.logics.fol.parser.FolParser;
import org.tweetyproject.logics.fol.reasoner.FolReasoner;
import org.tweetyproject.logics.fol.reasoner.SimpleFolReasoner;
import org.tweetyproject.logics.fol.syntax.FolBeliefSet;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.FolSignature;

public class SmartCityFol {

    public record SmartCityContext(FolParser parser, FolBeliefSet kb, FolReasoner reasoner) {
    }

    public static SmartCityContext buildContext() throws ParserException, IOException {
        FolSignature sig = new FolSignature(true);
        
        // Sorts (Types of objects)
        Sort sortVehicle = new Sort("Vehicle");
        Sort sortStation = new Sort("Station");
        sig.add(sortVehicle, sortStation); 
        
        // Constants (Specific real-world instances)
        Constant tramA = new Constant("tramA", sortVehicle);
        Constant bus14 = new Constant("bus14", sortVehicle);
        Constant centralStation = new Constant("centralStation", sortStation);
        Constant northPark = new Constant("northPark", sortStation);
        sig.add(tramA, bus14, centralStation, northPark);

        // Predicates (Relations and Properties)
        List<Sort> vehicleStation = new ArrayList<Sort>();
        vehicleStation.add(sortVehicle);
        vehicleStation.add(sortStation);
        Predicate stopsAt = new Predicate("StopsAt", vehicleStation);
        
        List<Sort> vehicleOnly = new ArrayList<Sort>();
        vehicleOnly.add(sortVehicle);
        Predicate electric = new Predicate("Electric", vehicleOnly);
        
        List<Sort> stationStation = new ArrayList<Sort>();
        stationStation.add(sortStation);
        stationStation.add(sortStation);
        Predicate connected = new Predicate("Connected", stationStation);
        
        sig.add(stopsAt, electric, connected); 
        
        // 2. Build the Knowledge Base
        FolParser parser = new FolParser();
        parser.setSignature(sig);
        FolBeliefSet kb = new FolBeliefSet();
        
        // Facts (What we know about the network)
        kb.add((FolFormula)parser.parseFormula("Electric(tramA)"));
        kb.add((FolFormula)parser.parseFormula("!Electric(bus14)")); 
        kb.add((FolFormula)parser.parseFormula("StopsAt(tramA, northPark)"));
        kb.add((FolFormula)parser.parseFormula("StopsAt(bus14, centralStation)"));
        kb.add((FolFormula)parser.parseFormula("Connected(northPark, centralStation)"));
        
        // Rules (If station X is connected to Y, then Y is connected to X by symmetry)
        kb.add((FolFormula)parser.parseFormula("forall X:(forall Y:(Connected(X,Y) => Connected(Y,X)))"));
        
        FolReasoner.setDefaultReasoner(new SimpleFolReasoner());
        FolReasoner prover = FolReasoner.getDefaultReasoner();
        return new SmartCityContext(parser, kb, prover);
    }

    public static void main(String[] args) throws ParserException, IOException {
        System.out.println("=== TP 1: First-Order Logic (Smart City Public Transport) ===");

        SmartCityContext ctx = buildContext();
        FolParser parser = ctx.parser();
        FolBeliefSet kb = ctx.kb();
        FolReasoner prover = ctx.reasoner();

        System.out.println("Knowledge Base successfully constructed!");
        System.out.println("\n--- Queries & Exploitation ---");
        
        // Query 1: Simple fact checking
        System.out.println("Q1: Is tramA an Electric vehicle? -> " + 
            prover.query(kb, (FolFormula)parser.parseFormula("Electric(tramA)")));
            
        // Query 2: Inference checking (Testing the symmetry rule we created)
        System.out.println("Q2: Is centralStation connected to northPark? -> " + 
            prover.query(kb, (FolFormula)parser.parseFormula("Connected(centralStation, northPark)")));
            
        // Query 3: Complex existential query
        System.out.println("Q3: Is there an electric vehicle that stops at northPark? -> " + 
            prover.query(kb, (FolFormula)parser.parseFormula("exists V:(Electric(V) && StopsAt(V, northPark))")));
    }
}