package fr.lirmm.graphik.DEFT;

import java.io.IOException;
import java.util.Iterator;

import fr.lirmm.graphik.DEFT.core.DefeasibleKB;
import fr.lirmm.graphik.DEFT.dialectical_tree.argument_preference.GeneralizedSpecificityArgumentPreference;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplicationException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.util.stream.CloseableIterator;

/**
 * The Kowalski examples uses atomic ground query, but DEFT can deal with atomic queries that contain existential variables (not fully ground queries) 
 * This examples shoes how to deal with queries that contain existential variables.
 * 
 * @author Abdelraouf Hecham
 *
 */
public class QueryWithExistentialVariableExample {
	public static void main( String[] args ) throws AtomSetException, ChaseException, HomomorphismException, 
    IOException, HomomorphismFactoryException, RuleApplicationException {
        
		DefeasibleKB kb = new DefeasibleKB();
		
		kb.addRule("s(X,Y) :- p(X).");
		kb.addRule("[DEFT] s(X,Y), t(Y) :- q(X).");
		kb.addRule("[DEFT] u(X) :- r(X), q(X).");
		kb.addNegativeConstraint("! :- u(X), s(X,Y).");
		kb.addAtom("p(a).");
		kb.addAtom("q(a).");
		kb.addAtom("[DEFT] r(a).");
		
        kb.saturate();
        
        System.out.println("---------------- Saturated Atoms ----------------");
        System.out.print(kb.toString());
        System.out.println("-------------------------------------------------\n");
        
        // set preference function
        kb.setPreferenceFunction(new GeneralizedSpecificityArgumentPreference());
        
        // The query might not be a fully ground atomic query, it might contain existential variables!
        // e.g. "?(X) :- s(a, X)."
        int entailment = DefeasibleKB.NOT_ENTAILED;
        
        // Find all the atoms that can be mapped to the query, and for each atom test its entailment
        // The final answer for the query is the strongest entailment of the mapped atoms. 
        CloseableIterator<Atom> it = kb.getAtomsSatisfiyingAtomicQuery("?(X) :- s(a,X).").iterator();
        Atom atom = null;
        
        while(it.hasNext()) {
        	Atom a = it.next();
        	int local_entailment = kb.EntailmentStatus(a);
	        
        	System.out.println(a + " is " + printEntailment(local_entailment));
        	
        	// Test if the entailment of this atom is stronger that the pervious atoms.
        	if(local_entailment == DefeasibleKB.STRICTLY_ENTAILED) {
        		entailment = DefeasibleKB.STRICTLY_ENTAILED;
        		atom = a;
        	} else if(local_entailment == DefeasibleKB.DEFEASIBLY_ENTAILED && entailment != DefeasibleKB.STRICTLY_ENTAILED) {
        		entailment = DefeasibleKB.DEFEASIBLY_ENTAILED;
        		atom = a;
        	} else if(entailment == DefeasibleKB.NOT_ENTAILED) {
        		atom = a;
        	}
        }
        
        System.out.println("Thus, The query s(a,X) is : " + printEntailment(entailment) );
    }
	
	public static String printEntailment(int entailment) {
		switch(entailment) {
        case DefeasibleKB.NOT_ENTAILED: return (" NOT entailed!");
        case DefeasibleKB.STRICTLY_ENTAILED: return(" Strictly entailed!");
        case DefeasibleKB.DEFEASIBLY_ENTAILED: return(" Defeasibly entailed!");
		}
		return "";
	}
}
