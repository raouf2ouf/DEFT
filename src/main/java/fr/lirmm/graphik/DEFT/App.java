package fr.lirmm.graphik.DEFT;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import fr.lirmm.graphik.DEFT.core.KB;
import fr.lirmm.graphik.DEFT.dialectical_tree.Argument;
import fr.lirmm.graphik.DEFT.dialectical_tree.ArgumentationFramework;
import fr.lirmm.graphik.DEFT.gad.Derivation;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplicationException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismFactoryException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws AtomSetException, ChaseException, HomomorphismException, 
    IOException, HomomorphismFactoryException, RuleApplicationException {
        System.out.println( "Hello World!" );
        KB kb = new KB("./src/main/java/fly.dlgp");
        
        kb.saturate();
        
        System.out.println("---------------- Saturated Atoms ----------------");
        for(Atom atom : kb.facts) {
        	System.out.println(atom);
        }
        
        
        //printAnswers(kb.query("?(X) :- s(X,Y)."));
        
        Iterator<Atom> it = kb.getAtomsSatisfiyingAtomicQuery("?(X) :- nofly(tweety).").iterator();
        if(!it.hasNext()) {
        	System.out.println("This query has no corresponding Atom!");
        } else {
        	Atom a = it.next();
        	
        	int entailment = kb.EntailmentStatus(a);
	        switch(entailment) {
	        case 0: System.out.println(a + "is NOT entailed!"); break;
	        case 1: System.out.println(a + "is Strictly entailed!"); break;
	        case 2: System.out.println(a + "is Defeasibly entailed!"); break;
	        }
        	
        }
        System.out.println( "Bye World!" );
        
        
    }
    
	public static void printAnswers(Iterator<Substitution> results) throws IOException {
		if (results.hasNext()) {
			while (results.hasNext()) {
				Substitution s = results.next(); 
				System.out.println(s);
				System.out.println("\n");
			}
		} else {
			System.out.println("No answer\n");
		}
	}
}
