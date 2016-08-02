package fr.lirmm.graphik.DEFT.core;


import java.io.FileNotFoundException;

import junit.framework.TestCase;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;

/**
 * 
 * @author Abdelraouf Hecham (INRIA) <hecham.abdelraouf@gmail.com>
 */
public class DefeasibleKBTest extends TestCase {
	
	public void testDefeasibleKBPoplationViaFileVSExplicitInstantiation() throws FileNotFoundException, AtomSetException, ChaseException {
		// Populating first kb via a DLGP File
		KB kb1 = new KB("./src/test/resources/kowalski.dlgp");
		
		// Populating second kb explicitly
		KB kb2 = new KB();
		//----------------- Rules --------------------
		kb2.addRule("bird(X) :- penguin(X).");
		kb2.addRule("[DEFT] fly(X) :- bird(X).");
		kb2.addRule("nofly(X) :- penguin(X).");
		kb2.addRule("[DEFT] nofly(X) :- penguin(X).");
		//----------- Negative Constraints -----------
		kb2.addNegativeConstraint("! :- nofly(X), fly(X).");
		//----------------- Facts --------------------
		kb2.addAtom("[DEFT] penguin(kowalski).");
		kb2.addAtom("bird(tweety).");
		
		// Testing if kb1 and kb2 contain the same information
		kb1.saturate();
		kb2.saturate();
		// 1. Testing Strict rules
		for(Rule rule : kb1.strictRuleSet) {
			if(!kb2.strictRuleSet.contains(rule)) {
				// TODO Assert false;
				break;
			}
		}
		// 2. Testing Strict atoms
		for(Atom atom : kb1.strictAtomSet) {
			if(!kb2.strictAtomSet.contains(atom)) {
				// TODO Assert false;
				break;
			}
		}
		// 3. Testing Defeasible rules
		for(Rule rule : kb1.defeasibleRuleSet) {
			if(!kb2.defeasibleRuleSet.contains(rule)) {
				// TODO Assert false;
				break;
			}
		}
		// 4. Testing Defeasible atoms
		for(Atom atom : kb1.defeasibleAtomSet) {
			if(!kb2.defeasibleAtomSet.contains(atom)) {
				// TODO Assert false;
				break;
			}
		}
		// 5. Testing the set of facts
		for(Atom atom : kb1.facts) {
			if(!kb2.facts.contains(atom)) {
				// TODO Assert false;
				break;
			}
		}
	}
	
	public void testDefeasibleKBEntailementInFileInstantiation() throws FileNotFoundException, AtomSetException {
		KB kb = new KB("./src/test/resources/entailement.dlgp");
	}
	
	public void testDefeasibleKBEntailementInExplicitInstantiation() {
		KB kb = new KB();
	}
	
	public void testDefeasibleKBRuleOrderInFileInstantiation() throws FileNotFoundException, AtomSetException {
		KB kb = new KB("");
	}
	
	public void testDefeasibleKBRuleOrderInExplicitInstantiation() {
		KB kb = new KB();
	}
	
	//TODO: test argumentation framework and specially in case of inconsitancy.
}
