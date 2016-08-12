package fr.lirmm.graphik.DEFT.core;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
		this.kb1 = new DefeasibleKB("./src/test/resources/kowalski.dlgp");
		
		// Populating second kb explicitly
		this.kb2 = new DefeasibleKB();
		
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
	
	@After
	public void tearDown() {
		this.kb1 = null;
		this.kb2 = null;
	}
	
	@Ignore
	@Test
	public void testParseDefeasibleAtom() throws AtomSetException {
		DefeasibleKB kb = new DefeasibleKB();
		
		kb.addAtom("p(a).");
		kb.addAtom("[DEFT] p(a).");
		kb.addAtom("[DEFT] p(b).");
		/*for(Atom atom : kb.strictAtomSet) {
			System.out.println(atom);
		}
		System.out.println("-----");
		for(Atom atom : kb.defeasibleAtomSet) {
			System.out.println(atom);
		}*/
		assertFalse("Failure - Defeasible atom set must not be empty.", kb.defeasibleAtomSet.isEmpty());
	}
	
	@Test
	public void testParseDefeasibleRule() {
		DefeasibleKB kb = new DefeasibleKB();
		
		kb.addRule("p(X) :- q(X).");
		kb.addRule("[DEFT] p(X) :- q(X).");
		kb.addRule("[DEFT] p(X) :- s(X).");
		
		assertTrue("Failure - Defeasible rule set must not be empty.", kb.defeasibleRuleSet.iterator().hasNext());
	}
	
	
	@Test
	public void testDefeasibleKBFileVSExplicitInstantiationStrictRules() {
		// Testing Strict rules (must contain the same information)
		for(Rule rule : kb1.strictRuleSet) {
			if(!kb2.strictRuleSet.contains(rule)) {
				fail("Failure - The set of strict rules is not the same.");
				break;
			}
		}
	}
	
	@Test
	public void testDefeasibleKBFileVSExplicitInstantiationStrictAtoms() throws AtomSetException {
		// Testing Strict atoms (must contain the same information)
		for(Atom atom : kb1.strictAtomSet) {
			if(!kb2.strictAtomSet.contains(atom)) {
				fail("Failure - The set of strict atom is not the same.");
				break;
			}
		}
	}
	
	@Test
	public void testDefeasibleKBFileVSExplicitInstantiationDefeasibleRules() {
		// Testing Defeasible rules (must contain the same information)
		for(Rule rule : kb1.defeasibleRuleSet) {
			if(!kb2.defeasibleRuleSet.contains(rule)) {
				System.out.println(rule);
				fail("Failure - The set of defeasible rules is not the same.");
				break;
			}
		}
	}
	
	@Test
	public void testDefeasibleKBFileVSExplicitInstantiationDefeasibleAtoms() throws AtomSetException {
		// Testing Defeasible atoms (must contain the same information)
		for(Atom atom : kb1.defeasibleAtomSet) {
			System.out.println("defeasible: " + atom);
			if(!kb2.defeasibleAtomSet.contains(atom)) {
				fail("Failure - The set of defeasible atoms is not the same.");
				break;
			}
		}
	}
	
	@Test
	public void testDefeasibleKBFileVSExplicitInstantiationFacts() throws AtomSetException {
		// Testing the set of facts (must contain the same information)
		for(Atom atom : kb1.facts) {
			if(!kb2.facts.contains(atom)) {
				fail("Failure - The set of facts is not the same.");
				break;
			}
		}
	}
	
	@Test
	public void testDefeasibleKBFileInstantiationEntailementTest1() throws FileNotFoundException, AtomSetException {
		
	}
	
	/*
	public void testDefeasibleKBEntailementInExplicitInstantiation() {
		DefeasibleKB kb = new DefeasibleKB();
	}
	
	public void testDefeasibleKBRuleOrderInFileInstantiation() throws FileNotFoundException, AtomSetException {
		DefeasibleKB kb = new DefeasibleKB("");
	}
	
	public void testDefeasibleKBRuleOrderInExplicitInstantiation() {
		DefeasibleKB kb = new DefeasibleKB();
	}*/
	
	//TODO: test argumentation framework and specially in case of inconsitancy.
}
