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
    public static void main( String[] args ) throws AtomSetException, ChaseException, HomomorphismException, IOException, HomomorphismFactoryException, RuleApplicationException
    {
        System.out.println( "Hello World!" );
        KB kb = new KB("./src/main/java/fly.dlgp");
        
        kb.saturate();
        
        System.out.println("---------------- Saturated Atoms ----------------");
        for(Atom atom : kb.facts) {
        	System.out.println(atom);
        }
        
        
        printAnswers(kb.query("?(X) :- s(X,Y)."));
        
        AtomSet atoms = kb.getAtomsSatisfiyingAtomicQuery("?(X) :- nofly(tweety).");
        
        Atom a = null;
        Argument uArg = null;
        for(Atom atom : atoms) {
	        LinkedList<Derivation> derivations = kb.getDerivationsFor(atom);
	        
	        for(Derivation d : derivations) {
	        	System.out.println(d.toString());
	        	uArg = new Argument(d, atom);
	        	a = atom;
	        }
        }
        
       
        System.out.println("---------------- Attackers for u(x) ----------------");
        ArgumentationFramework af = kb.af;
        LinkedList<Argument> attackers = af.getAttackersFor(uArg);
        
        System.out.println("There is " + attackers.size() + " attackers for " + uArg.conclusion.toString());
        for(Argument arg : attackers) {
        	
        	System.out.println(arg);
        }
        
        System.out.println("---------------- Entailment for u(a) ----------------");
        int entailment = kb.EntailmentStatus(a);
        System.out.println(a + "is " + entailment + " entailed!");
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
