package model.dlogic;

import model.plogic.Formula;
import utility.Utility;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * A default theory consists of a finite set of axioms and a finite set of defaults.
 */

public class DefaultTheory {
    private ObservableList<Formula> axioms = Utility.mainController.getAxiomList();
    private ObservableList<Default> defaults = Utility.mainController.getDefaultList();

    public DefaultTheory() {
        Utility.theory = this;
    }

    public void add(Formula f) {
        axioms.add(f);
    }

    public void remove(Formula f) {
        axioms.remove(f);
    }

    public void add(Default d) {
        defaults.add(d);
    }

    public void remove(Default d) {
        defaults.remove(d);
    }

    public Extension buildProcessTree() {
        long startTime = System.nanoTime();
        Extension top = new Extension(new HashSet<>(new ArrayList<>(defaults)), new HashSet<>(new ArrayList<>(axioms)), new HashSet<>(), new ArrayList<>());
        top.buildProcessTree();
        long endTime = System.nanoTime();
        long totalTime = (endTime - startTime) / 1000;
        System.out.println("Process tree built in " + totalTime + " mircoseconds");
        return top;
    }
}
