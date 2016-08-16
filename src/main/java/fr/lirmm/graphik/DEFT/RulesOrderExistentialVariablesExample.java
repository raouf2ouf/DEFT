package fr.lirmm.graphik.DEFT;

import java.io.IOException;

import fr.lirmm.graphik.DEFT.core.DefeasibleKB;
import fr.lirmm.graphik.DEFT.dialectical_tree.GeneralizedSpecificityPreference;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplicationException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismFactoryException;

/**
 * When Existential variables are used, the order in witch the rules are applied affects the resulting KB.
 * These differences -if not handled- can affect the entailment of a query.
 * DEFT can deal with this using the Graph Of Atom Dependency (GAD).
 * 
 * @author Abdelraouf Hecham
 *
 */
public class RulesOrderExistentialVariablesExample {
	public static void main( String[] args ) throws AtomSetException, ChaseException, HomomorphismException, 
    IOException, HomomorphismFactoryException, RuleApplicationException {
		DefeasibleKB kb1 = new DefeasibleKB();
		
		kb1.addRule("s(X,Y) :- p(X).");
		kb1.addRule("[DEFT] s(X,Y), t(Y) :- q(X).");
		kb1.addRule("[DEFT] u(X) :- r(X), q(X).");
		kb1.addNegativeConstraint("! :- u(X), s(X,Y).");
		kb1.addAtom("p(a).");
		kb1.addAtom("q(a).");
		kb1.addAtom("[DEFT] r(a).");
		
		DefeasibleKB kb2 = new DefeasibleKB();
		
		kb2.addRule("[DEFT] s(X,Y), t(Y) :- q(X).");
		kb2.addRule("s(X,Y) :- p(X).");
		kb2.addRule("[DEFT] u(X) :- r(X), q(X).");
		kb2.addNegativeConstraint("! :- u(X), s(X,Y).");
		kb2.addAtom("p(a).");
		kb2.addAtom("q(a).");
		kb2.addAtom("[DEFT] r(a).");
		
		kb1.saturate();
		kb2.saturate();
		
		// Not the same atoms in KB1 and KB2
		System.out.println("---------------- Saturated Atoms of KB1 ----------------");
        System.out.print(kb1.toString());
        System.out.println("--------------------------------------------------------");
        System.out.println("Not the same atoms in KB1 and KB2: s(a,EE)");
        System.out.println("---------------- Saturated Atoms of KB2 ----------------");
        System.out.print(kb2.toString());
        System.out.println("--------------------------------------------------------\n");
        
        // the entailment of q(a) should be the same in both KBs and not be affected by Existential Variables.
        // since our query is an atomic fully grounded query, there can only be one atom matching it.
        Atom atom1 = kb1.getAtomsSatisfiyingAtomicQuery("?(X) :- q(a).").iterator().next();
		int entailment1 = kb1.EntailmentStatus(atom1);
		
		Atom atom2 = kb2.getAtomsSatisfiyingAtomicQuery("?(X) :- q(a).").iterator().next();
		int entailment2 = kb1.EntailmentStatus(atom2);
		
		System.out.println("The entailment of q(a) should be the same in both KBs: ");
        switch(entailment1) {
	        case DefeasibleKB.NOT_ENTAILED: System.out.println(atom1 + " in KB1 is NOT entailed!"); break;
	        case DefeasibleKB.STRICTLY_ENTAILED: System.out.println(atom1 + " in KB1 is Strictly entailed!"); break;
	        case DefeasibleKB.DEFEASIBLY_ENTAILED: System.out.println(atom1 + " in KB1 is Defeasibly entailed!"); break;
        }
        
        switch(entailment2) {
	        case DefeasibleKB.NOT_ENTAILED: System.out.println(atom2 + " in KB2 is NOT entailed!"); break;
	        case DefeasibleKB.STRICTLY_ENTAILED: System.out.println(atom2 + " in KB2 is Strictly entailed!"); break;
	        case DefeasibleKB.DEFEASIBLY_ENTAILED: System.out.println(atom2 + " in KB2 is Defeasibly entailed!"); break;
        }
    }
}
