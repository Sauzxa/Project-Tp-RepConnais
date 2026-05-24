package model.plogic;

import org.sat4j.core.*;

public class DimacsConverter {

    private static Vec<VecInt> combine(Vec<VecInt> a, Vec<VecInt> b) {
        for (int i = 0; i < b.size(); i++) {
            a.push(b.get(i));
        }
        return a;
    }

    private static VecInt combine(VecInt a, VecInt b) {
        for (int i = 0; i < b.size(); i++) {
            a.push(b.get(i));
        }
        return a;
    }

    public static Vec<VecInt> convert(Formula f) {
        Formula cnfFormula = f.toCNF();

        if (cnfFormula.getClass() == Conjunction.class) {
            Formula clause1 = cnfFormula.getElements().get(0);
            Formula clause2 = cnfFormula.getElements().get(1);

            return combine(convert(clause1), convert(clause2));

        } else if (cnfFormula.getClass() == Disjunction.class) {

            Vec<VecInt> vector = new Vec<>();
            vector.push(convertDisjunction(cnfFormula));
            return vector;

        } else if (cnfFormula.getClass() == Negation.class || cnfFormula.getClass() == Atom.class) {
            VecInt intVector = new VecInt();
            intVector.push(cnfFormula.getValue());

            Vec<VecInt> vector = new Vec<>();
            vector.push(intVector);

            return vector;

        } else {
            return null;
        }
    }

    private static VecInt convertDisjunction(Formula f) {
        VecInt part1 = new VecInt();
        VecInt part2 = new VecInt();
        Formula clause1 = f.getElements().get(0);
        Formula clause2 = f.getElements().get(1);

        if (clause1.getClass() == Disjunction.class) {
            part1 = convertDisjunction(clause1);
        } else {
            part1.push(clause1.getValue());
        }

        if (clause2.getClass() == Disjunction.class) {
            part2 = convertDisjunction(clause1);
        } else {
            part2.push(clause2.getValue());
        }

        return combine(part1,part2);

    }
}
