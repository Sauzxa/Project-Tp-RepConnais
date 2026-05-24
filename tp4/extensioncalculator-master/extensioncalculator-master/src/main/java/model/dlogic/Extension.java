package model.dlogic;

import model.plogic.Formula;
import model.plogic.Negation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Extension {

    private HashSet<Default> remainingDefaults;
    private HashSet<Formula> inSetBase;
    private HashSet<Formula> outSetBase;
    private HashSet<Extension> children = new HashSet<>();
    private ArrayList<Default> process = new ArrayList<>();

    public Extension(HashSet<Default> remainingDefaults, HashSet<Formula> inSetBase, HashSet<Formula> outSetBase, ArrayList<Default> process) {
        this.remainingDefaults = remainingDefaults;
        this.inSetBase = inSetBase;
        this.outSetBase = outSetBase;
        this.process = process;
    }

    private void applyDefault(Default d) {
        assert(d.isApplicable(inSetBase));

        HashSet<Default> newDefaults = (HashSet<Default>) remainingDefaults.clone();
        newDefaults.remove(d);

        HashSet<Formula> newInSetBase = (HashSet<Formula>)inSetBase.clone();
        Formula consequence = d.getConsequence();
        if (!consequence.isInSet(newInSetBase)) {
            newInSetBase.add(consequence);
        }

        HashSet<Formula> newOutSetBase = (HashSet<Formula>)outSetBase.clone();

        for(Formula f : d.getJustification()) {
            Formula neg;
            if (f.getClass() == Negation.class) {
                neg = f.getElements().get(0);
            } else {
                neg = new Negation(f);
            }

            if (!neg.isInSet(newOutSetBase)) {
                newOutSetBase.add(neg);
            }
        }

        ArrayList<Default> newProcess = (ArrayList<Default>)process.clone();
        newProcess.add(d);

        Extension newExtension = new Extension(newDefaults,newInSetBase,newOutSetBase,newProcess);
        children.add(newExtension);
    }

    public void applyAllDefaults() {
        Set<Default> inconsistentDefaults = new HashSet<>();
        for(Default d : remainingDefaults) {
            if(!d.isConsistent(inSetBase)) {
                inconsistentDefaults.add(d);
            }
        }

        remainingDefaults.removeAll(inconsistentDefaults);

        for(Default d : remainingDefaults) {
            if(d.isApplicable(inSetBase)) {
                applyDefault(d);
            }
        }
    }

    protected void buildProcessTree() {
        applyAllDefaults();

        for (Extension c : children) {
            c.buildProcessTree();
        }
    }

    public boolean isClosed() {
        for(Default d : remainingDefaults) {
            if (d.isApplicable(inSetBase)) {
                return false;
            }
        }
        return true;
    }

    public boolean isSuccessful() {
        if (!Formula.isConsistent(inSetBase)) {
            return false;
        }

        if (inSetBase.isEmpty() || outSetBase.isEmpty()) {
            return true;
        }

        for (Formula f : outSetBase) {
            if (f.isInSet(inSetBase)) {
                return false;
            }
        }

        return true;

    }

    @Override
    public String toString() {
        if (!process.isEmpty()) {
            if (Formula.isConsistent(inSetBase)) {
                return process.get(process.size()-1).toString() + "\n" + inSetBase.toString();
            } else {
                return process.get(process.size()-1).toString() + "\n" + "⊥";
            }

        } else {
            if (Formula.isConsistent(inSetBase)) {
                return inSetBase.toString();
            } else {
                return "⊥";
            }
        }
    }

    public HashSet<Extension> getChildren() {
        return children;
    }

    public String getDetailsString() {
        StringBuilder s = new StringBuilder();
        s.append("Process: " + process + "\n");
        if (isClosed()) {
            s.append("Closed: yes\n");
        } else {
            s.append("Closed: no\n");
        }

        if (isSuccessful()) {
            s.append("Successful: yes\n");
        } else {
            s.append("Successful: no\n");
        }

        if (Formula.isConsistent(inSetBase)) {
            s.append("In-set: " + inSetBase + "\n");
        } else {
            s.append("In-set: ⊥\n");
        }

        s.append("Out-set: " + outSetBase + "\n");
        return s.toString();
    }
}