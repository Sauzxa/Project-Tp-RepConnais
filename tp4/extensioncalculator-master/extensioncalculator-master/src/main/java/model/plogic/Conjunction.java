package model.plogic;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Represents a conjunction as in propositional logic. Connects two propositional formulas and is also a formula itself.
 */

public class Conjunction extends Formula {
    private Formula clause1;
    private Formula clause2;

    public Conjunction(Formula clause1, Formula clause2) {
        this.clause1 = clause1;
        this.clause2 = clause2;
    }

    public Conjunction(HashSet<Formula> hset) {
        ArrayList<Formula> set = new ArrayList<>(hset);

        if (set.size() == 2) {
            this.clause1 = set.get(0);
            this.clause2 = set.get(1);
        } else if (set.size() > 2) {
            this.clause1 = set.get(0);
            set.remove(0);
            this.clause2 = new Conjunction(new HashSet<>(set));
        } else if (set.size() == 1) {
            this.clause1 = set.get(0);
            this.clause2 = set.get(0);
        } else {
            try {
                throw new Exception("HashSet is empty");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        return new Conjunction(clause1.toCNF(),clause2.toCNF());
    }

    @Override
    public String toString() {
        return "(" + clause1 + " & " + clause2 + ")";
    }

    @Override
    public Integer getValue() {
        return null;
    }
}
