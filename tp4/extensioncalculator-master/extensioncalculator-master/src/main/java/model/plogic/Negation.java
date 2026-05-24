package model.plogic;

import java.util.ArrayList;

/**
 * Represents a negation as in propositional logic. It negates a single formula and also is a formula itself.
 */

public class Negation extends Formula {
    private Formula formula;

    public Negation(Formula formula) {
        this.formula = formula;
    }

    @Override
    public ArrayList<Formula> getElements() {
        ArrayList<Formula> list = new ArrayList<>();
        list.add(formula);
        return list;
    }

    @Override
    public Boolean isValid() {
        return (formula != null && formula.isValid());
    }

    @Override
    public Formula toCNF() {
        if (formula.getClass() == Negation.class) {
            return formula.getElements().get(0).toCNF();
        } else if (formula.getClass() == Disjunction.class) {
            ArrayList<Formula> elements = formula.getElements();
            return new Conjunction(new Negation(elements.get(0)),new Negation(elements.get(1))).toCNF();
        } else if (formula.getClass() == Conjunction.class) {
            ArrayList<Formula> elements = formula.getElements();
            return new Disjunction(new Negation(elements.get(0)),new Negation(elements.get(1))).toCNF();
        } else {
            return this;
        }
    }

    @Override
    public String toString() {
        return "~(" + formula + ")";
    }

    @Override
    public Integer getValue() {
        return formula.getValue() * -1;
    }
}
