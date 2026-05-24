package mytweetyapp;

/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
//package org.tweetyproject.logics.ml.examples;

import java.io.IOException;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.ml.reasoner.SimpleMlReasoner;
import org.tweetyproject.logics.ml.syntax.MlBeliefSet;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.ml.parser.MlParser;
import java.io.File;
/**
 * Some examples for testing ModalParser and NaiveModalReasoner. Shows how to
 * construct a modal logic knowledge base programmatically and how to query it
 * using the naive reasoner.
 * 
 * @author Anna Gessler
 */
public class MlExample {

	public static void main(String[] args) throws ParserException, IOException {

		// Parse simple BeliefBase from file
		MlParser parser = new MlParser();
		/*MlBeliefSet b1 = parser.parseBeliefBaseFromFile("../../tp3/Example.mlogic");
		FolFormula f1 = (FolFormula) parser.parseFormula("<>(A&&B)");
		System.out.println("Parsed belief base:" + b1 + "\nSignature of belief base:" + b1.getMinimalSignature());
		System.out.println("Parsed formula:" + f1);*/

		// Parse simple BeliefBase from string
		parser = new MlParser();
		MlBeliefSet b2 = parser.parseBeliefBase("Animal = {penguin,eagle} \n type(Flies(Animal)) \n (Flies(eagle))");
		FolFormula f2 = (FolFormula) parser.parseFormula("(Flies(penguin)) || (!(Flies(penguin)))");
		System.out.println("Parsed belief base:" + b2);
		System.out.println("Parsed formula:" + f2);
		System.out.println("2nd test \n");
		// Parse simple BeliefBase from string
				parser = new MlParser();
				MlBeliefSet b3 = parser.parseBeliefBase("Animal = {penguin, eagle, worm}\r\n"
						+ "Plant = {cactus}\r\n"
						+ "\r\n"
						+ "type(Flies(Animal))\r\n"
						+ "type(HasWings(Animal))\r\n"
						+ "type(Eats(Animal,Animal))\r\n"
						+ "type(Stings(Plant))\r\n"
						+ "\r\n"
						+ "<>(Flies(eagle))\r\n"
						+ "!([](<>(!Stings(cactus))))\r\n"
						+ "Eats(eagle,worm)\r\n"
						+ "!(<>(Eats(worm,penguin)))\r\n"
						+ "[](forall X:(([](Flies(X))) || (<>(!HasWings(X)))))");
				FolFormula f3 = (FolFormula) parser.parseFormula("<>(A&&B)");
				System.out.println("Parsed belief base:" + b3);
				System.out.println("Parsed formula:" + f3);

		// Parse more complex BeliefBase from file
		/*parser = new MlParser();
		MlBeliefSet b3 = parser.parseBeliefBaseFromFile("src/main/resources/examplebeliefbase.mlogic");
		System.out.println("Parsed belief base:" + b3 + "\nSignature of belief base:" + b3.getMinimalSignature());

		// Reasoner examples
		SimpleMlReasoner reasoner = new SimpleMlReasoner();
		System.out.println("Answer to query: " + reasoner.query(b1, f1));
		System.out.println("Answer to query: " + reasoner.query(b2, f2));*/
	}

}