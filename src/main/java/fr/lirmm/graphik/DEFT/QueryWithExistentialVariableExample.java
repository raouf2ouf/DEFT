package fr.lirmm.graphik.DEFT;

import java.io.IOException;
import java.util.Iterator;

import fr.lirmm.graphik.DEFT.core.DefeasibleKB;
import fr.lirmm.graphik.DEFT.dialectical_tree.GeneralizedSpecificityPreference;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplicationException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismFactoryException;

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
        System.out.println( "Hello World!" );
        DefeasibleKB kb = new DefeasibleKB("./src/main/resources/kowalski.dlgp");
        
        kb.saturate();
        
        System.out.println("---------------- Saturated Atoms ----------------");
        System.out.println(kb.toString());
        
        // set preference function
        kb.setPreferenceFunction(new GeneralizedSpecificityPreference());
        
        // Our query might not be a fully ground atomic query, it might contain existential variables!
        int entailment = DefeasibleKB.NOT_ENTAILED;
        
        Iterator<Atom> it = kb.getAtomsSatisfiyingAtomicQuery("?(X) :- nofly(tweety).").iterator();
        Atom atom = null;
        
        while(it.hasNext()) {
        	Atom a = it.next();
        	int local_entailment = kb.EntailmentStatus(a);
	        
        	// 
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
        
        switch(entailment) {
        case DefeasibleKB.NOT_ENTAILED: System.out.println(atom + "is NOT entailed!"); break;
        case DefeasibleKB.STRICTLY_ENTAILED: System.out.println(atom + "is Strictly entailed!"); break;
        case DefeasibleKB.DEFEASIBLY_ENTAILED: System.out.println(atom + "is Defeasibly entailed!"); break;
        }
        System.out.println( "Bye World!" );
    }
}
