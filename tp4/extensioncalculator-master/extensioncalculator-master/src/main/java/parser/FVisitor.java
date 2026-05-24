package parser;

import model.plogic.*;

public class FVisitor extends FormulaBaseVisitor<Formula> {

    @Override
    public Formula visitParens(FormulaParser.ParensContext context) {
        return this.visit(context.f);
    }

    @Override
    public Formula visitAtom(FormulaParser.AtomContext context) {
        return new Atom(context.getText());
    }

    @Override
    public Formula visitConjunction(FormulaParser.ConjunctionContext context) {
        return new Conjunction(this.visit(context.l),this.visit(context.r));
    }

    @Override
    public Formula visitDisjunction(FormulaParser.DisjunctionContext context) {
        return new Disjunction(this.visit(context.l),this.visit(context.r));
    }

    @Override
    public Formula visitNegation(FormulaParser.NegationContext context) {
        return new Negation(this.visit(context.f));
    }
}