package model.plogic;

import java.util.ArrayList;

/**
 * Represents a disjunction as in propositional logic. Connects two propositional formulas and is also a formula itself.
 */

public class Disjunction extends Formula {
    private Formula clause1;
    private Formula clause2;

    public Disjunction(Formula clause1, Formula clause2) {
        this.clause1 = clause1;
        this.clause2 = clause2;
    }

    @Override
    public ArrayList<Formula> getElements() {
        ArrayList<Formula> list = new ArrayList<Formula>();
        list.add(clause1);
        list.add(clause2);
        return list;
    }

    @Override
    public Boolean isValid() {
        if (clause1 != null && clause2 != null) {
            return clause1.isValid() && clause2.isValid();
        } else {
            return false;
        }
    }

    @Override
    public Formula toCNF() {
        Formula c1 = clause1.toCNF();
        Formula c2 = clause2.toCNF();

        if (c1.getClass() == Conjunction.class) {
            Formula p1 = c1.getElements().get(0);
            Formula p2 = c1.getElements().get(1);
            return new Conjunction(new Disjunction(p1,c2),new Disjunction(p2,c2));
        } else if (c2.getClass() == Conjunction.class) {
            Formula p1 = c2.getElements().get(0);
            Formula p2 = c2.getElements().get(1);
            return new Conjunction(new Disjunction(c1,p1), new Disjunction(c1,p2));
        } else {
            return new Disjunction(c1,c2);
        }
    }

    @Override
    public String toString() {
        return "(" + clause1 + " | " + clause2 + ")";
    }

    @Override
    public Integer getValue() {
        return null;
    }
}
