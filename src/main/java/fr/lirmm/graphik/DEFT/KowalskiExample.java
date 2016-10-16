package fr.lirmm.graphik.DEFT;

import java.io.IOException;
import java.util.Iterator;

import fr.lirmm.graphik.DEFT.core.DefeasibleKB;
import fr.lirmm.graphik.DEFT.dialectical_tree.argument_preference.GeneralizedSpecificityArgumentPreference;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplicationException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismFactoryException;


/**
 * A simple example of a defeasible knowledge base about the penguin
 */

public class KowalskiExample {
	public static void main( String[] args ) throws AtomSetException, ChaseException, HomomorphismException, 
    IOException, HomomorphismFactoryException, RuleApplicationException {
        
        DefeasibleKB kb = new DefeasibleKB();
        kb.addRule("bird(X) :- penguin(X).");
		kb.addRule("[DEFT] fly(X) :- bird(X).");
		kb.addRule("nofly(X) :- penguin(X).");
		kb.addNegativeConstraint("! :- nofly(X), fly(X).");
		kb.addAtom("[DEFT] penguin(kowalski).");
        
        kb.saturate();
        
        System.out.println("---------------- Saturated Atoms ----------------");
        System.out.print(kb.toString());
        System.out.println("-------------------------------------------------\n");
        // set preference function, by default it's `GeneralizedSpecificityPreference`
        kb.setPreferenceFunction(new GeneralizedSpecificityArgumentPreference());
        
        // Can Kowalski fly?
        // since our query is an atomic fully grounded query, there can only be one atom matching it.
        Atom atom = kb.getAtomsSatisfiyingAtomicQuery("? :- fly(kowalski).").iterator().next();
        
    	int entailment = kb.EntailmentStatus(atom);
        
        switch(entailment) {
	        case DefeasibleKB.NOT_ENTAILED: System.out.println(atom + " is NOT entailed!"); break;
	        case DefeasibleKB.STRICTLY_ENTAILED: System.out.println(atom + " is Strictly entailed!"); break;
	        case DefeasibleKB.DEFEASIBLY_ENTAILED: System.out.println(atom + " is Defeasibly entailed!"); break;
        }
        
        // Can Kowalski fly?
        // since our query is an atomic fully grounded query, there can only be one atom matching it.
        Atom atom2 = kb.getAtomsSatisfiyingAtomicQuery("? :- nofly(kowalski).").iterator().next();
        
    	int entailment2 = kb.EntailmentStatus(atom2);
        
        switch(entailment2) {
	        case DefeasibleKB.NOT_ENTAILED: System.out.println(atom2 + " is NOT entailed!"); break;
	        case DefeasibleKB.STRICTLY_ENTAILED: System.out.println(atom2 + " is Strictly entailed!"); break;
	        case DefeasibleKB.DEFEASIBLY_ENTAILED: System.out.println(atom2 + " is Defeasibly entailed!"); break;
        }
    }
}
