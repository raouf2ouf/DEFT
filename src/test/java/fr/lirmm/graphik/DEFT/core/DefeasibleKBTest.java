package fr.lirmm.graphik.DEFT.core;


import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;

/**
 * 
 * @author Abdelraouf Hecham (INRIA) <hecham.abdelraouf@gmail.com>
 */
public class DefeasibleKBTest {
	private DefeasibleKB kb1;
	private DefeasibleKB kb2;
	
	@Before
	public void setUp() throws FileNotFoundException, AtomSetException, ChaseException {
		// Populating first kb via a DLGP File
		DefeasibleKB kb1 = new DefeasibleKB("./src/test/resources/kowalski.dlgp");
		
		// Populating second kb explicitly
		DefeasibleKB kb2 = new DefeasibleKB();
		
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
	}
	
	@Test
	public void testDefeasibleKBFileVSExplicitInstantiationStrictRules() {
		
		// 1. Testing Strict rules (must be the same)
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
		DefeasibleKB kb = new DefeasibleKB("./src/test/resources/entailement.dlgp");
	}
	
	public void testDefeasibleKBEntailementInExplicitInstantiation() {
		DefeasibleKB kb = new DefeasibleKB();
	}
	
	public void testDefeasibleKBRuleOrderInFileInstantiation() throws FileNotFoundException, AtomSetException {
		DefeasibleKB kb = new DefeasibleKB("");
	}
	
	public void testDefeasibleKBRuleOrderInExplicitInstantiation() {
		DefeasibleKB kb = new DefeasibleKB();
	}
	
	//TODO: test argumentation framework and specially in case of inconsitancy.
}
