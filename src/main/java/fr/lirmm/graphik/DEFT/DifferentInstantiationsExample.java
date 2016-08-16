package fr.lirmm.graphik.DEFT;

import java.io.FileNotFoundException;
import java.io.StringReader;

import fr.lirmm.graphik.DEFT.core.DefeasibleKB;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;

/**
 * A defeasible knowledge base can be instantiated in different ways:
 * 1. Using a DLGP File by providing the path of the file.
 * 2. Using a String containing atoms, rules and negative constraints.
 * 3. Explicitly for each atom, rule, negative constraint.
 * 4. A combination of some or all previous methods.
 */
public class DifferentInstantiationsExample {
	
	public static void main( String[] args ) throws FileNotFoundException, AtomSetException, ChaseException {
		// 1. Instantiating a KB from a DLGP file:
		DefeasibleKB kbFile = new DefeasibleKB("./src/main/resources/kowalski.dlgp");
		
		// 2. Instantiating a KB using a String containing all the information:
		String str = "%----------------- Rules --------------------" + "\n" +
				"bird(X) :- penguin(X)." + "\n" +
				"[DEFT] fly(X) :- bird(X)." + "\n" +
				"nofly(X) :- penguin(X)." + "\n" +
				"%----------- Negative Constraints -----------" + "\n" +
				"! :- nofly(X), fly(X)." + "\n" +
				"%----------------- Facts --------------------" + "\n" +
				"[DEFT] penguin(tweety).";
		
		DefeasibleKB kbString = new DefeasibleKB(new StringReader(str));
		
		// 3. Explicitly for each atom, rule, negative constraint:
		DefeasibleKB kbExplicit = new DefeasibleKB();

		kbExplicit.addRule("bird(X) :- penguin(X).");
		kbExplicit.addRule("[DEFT] fly(X) :- bird(X).");
		kbExplicit.addRule("nofly(X) :- penguin(X).");
		kbExplicit.addNegativeConstraint("! :- nofly(X), fly(X).");
		kbExplicit.addAtom("[DEFT] penguin(tweety).");
		
		// 4. A combination of different methods:
		String str2 = "bird(X) :- penguin(X)." + "\n" +
				"[DEFT] fly(X) :- bird(X).";
		
		DefeasibleKB kbCombined = new DefeasibleKB(new StringReader(str2));
		
		kbCombined.addRule("nofly(X) :- penguin(X).");
		kbCombined.addNegativeConstraint("! :- nofly(X), fly(X).");
		kbCombined.addAtom("[DEFT] penguin(tweety).");
		
		// Staturating the Knowledge bases
		kbFile.saturate();
		kbString.saturate();
		kbExplicit.saturate();
		kbCombined.saturate();
		
		// Displaying the saturated KBs
		System.out.println("------ KB from file ------");
		System.out.print(kbFile.toString());
		System.out.println("--------------------------\n");
		
		System.out.println("------ KB from string ------");
		System.out.print(kbString.toString());
		System.out.println("--------------------------\n");
		
		System.out.println("------ KB explicitly ------");
		System.out.print(kbExplicit.toString());
		System.out.println("--------------------------\n");
		
		System.out.println("------ KB combined ------");
		System.out.print(kbCombined.toString());
		System.out.println("--------------------------");
	}
}
