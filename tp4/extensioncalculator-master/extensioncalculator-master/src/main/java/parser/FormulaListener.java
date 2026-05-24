// Generated from /Users/marco/ai/bscproj/extensioncalculator/src/main/java/parser/Formula.g4 by ANTLR 4.7
package parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link FormulaParser}.
 */
public interface FormulaListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by the {@code parens}
	 * labeled alternative in {@link FormulaParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterParens(FormulaParser.ParensContext ctx);
	/**
	 * Exit a parse tree produced by the {@code parens}
	 * labeled alternative in {@link FormulaParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitParens(FormulaParser.ParensContext ctx);
	/**
	 * Enter a parse tree produced by the {@code negation}
	 * labeled alternative in {@link FormulaParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterNegation(FormulaParser.NegationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code negation}
	 * labeled alternative in {@link FormulaParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitNegation(FormulaParser.NegationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code conjunction}
	 * labeled alternative in {@link FormulaParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterConjunction(FormulaParser.ConjunctionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code conjunction}
	 * labeled alternative in {@link FormulaParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitConjunction(FormulaParser.ConjunctionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code disjunction}
	 * labeled alternative in {@link FormulaParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterDisjunction(FormulaParser.DisjunctionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code disjunction}
	 * labeled alternative in {@link FormulaParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitDisjunction(FormulaParser.DisjunctionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code atom}
	 * labeled alternative in {@link FormulaParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterAtom(FormulaParser.AtomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code atom}
	 * labeled alternative in {@link FormulaParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitAtom(FormulaParser.AtomContext ctx);
}