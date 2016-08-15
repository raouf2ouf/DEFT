package fr.lirmm.graphik.DEFT.core;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import fr.lirmm.graphik.DEFT.dialectical_tree.Argument;
import fr.lirmm.graphik.DEFT.dialectical_tree.Defeater;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplicationException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismFactoryException;

/**
 * 
 * @author Abdelraouf Hecham (INRIA) <hecham.abdelraouf@gmail.com>
 */
public class DefeasibleKBTest {
	private DefeasibleKB kb1;
	private DefeasibleKB kb2;
	
	@Before
	public void setUp() throws FileNotFoundException, AtomSetException, ChaseException {
		// Populating first kb via a `mocked` DLGP File
		String str = "[DEFT] q(X) :- p(X). \n" +
		"n(X) :- neg(X). \n" +
		"! :- n(X), q(X). \n" +
		"p(a). \n" +
		"neg(a).";
		this.kb1 = new DefeasibleKB(new StringReader(str));
		
		
		// Populating second kb explicitly
		this.kb2 = new DefeasibleKB();
		
		kb2.addRule("[DEFT] q(X) :- p(X).");
		kb2.addRule("n(X) :- neg(X).");
		kb2.addNegativeConstraint("! :- n(X), q(X).");
		kb2.addAtom("p(a).");
		kb2.addAtom("neg(a).");
		
		// Saturate both KB
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
	public void testFileVSExplicitInstantiationStrictRules() {
		// Testing Strict rules (must contain the same information)
		for(Rule rule : kb1.strictRuleSet) {
			if(!kb2.strictRuleSet.contains(rule)) {
				fail("Failure - The set of strict rules is not the same.");
				break;
			}
		}
	}
	
	@Test
	public void testFileVSExplicitInstantiationStrictAtoms() throws AtomSetException {
		// Testing Strict atoms (must contain the same information)
		for(Atom atom : kb1.strictAtomSet) {
			if(!kb2.strictAtomSet.contains(atom)) {
				fail("Failure - The set of strict atom is not the same.");
				break;
			}
		}
	}
	
	@Test
	public void testFileVSExplicitInstantiationDefeasibleRules() {
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
	public void testFileVSExplicitInstantiationDefeasibleAtoms() throws AtomSetException {
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
	public void testFileVSExplicitInstantiationFacts() throws AtomSetException {
		// Testing the set of facts (must contain the same information)
		for(Atom atom : kb1.facts) {
			if(!kb2.facts.contains(atom)) {
				fail("Failure - The set of facts is not the same.");
				break;
			}
		}
	}
	
	@Test
	public void testFileInstantiationEntailement() throws HomomorphismException, AtomSetException, HomomorphismFactoryException, RuleApplicationException, ChaseException, FileNotFoundException {
		//----------------- Test1 --------------------
		// Strict attack (proper defeat). q(a) should be NOT_ENTAILED
		String str = "[DEFT] q(X) :- p(X)." + " \n" +
					"n(X) :- neg(X)." + " \n" +
					"! :- n(X), q(X)." + " \n" +
					"p(a)." + " \n" +
					"neg(a).";
		DefeasibleKB kb = new DefeasibleKB(new StringReader(str));
		
		kb.saturate();
		
		Atom atom = kb.getAtomsSatisfiyingAtomicQuery("?(X) :- q(a).").iterator().next();
		Argument arg = kb.af.getArgumentsFor(atom).iterator().next();
		
		System.out.println("Argument: " + arg);
		Iterator<Defeater> it = kb.af.getDefeatersFor(arg).iterator();
		if(!it.hasNext()) System.out.println("No defeaters for " + arg);
		
		while(it.hasNext()) {
			System.out.println("Defeaters of " + atom + ": " + it.next());
		}
		
		int entailment = kb.EntailmentStatus(atom);
		assertEquals("File: " + atom + " must Not be entailed.", DefeasibleKB.NOT_ENTAILED, entailment);
	}
	
	@Test
	public void testExplicitInstantiationEntailement() throws HomomorphismException, HomomorphismFactoryException, RuleApplicationException, AtomSetException, ChaseException {
		DefeasibleKB kb = new DefeasibleKB();
		//----------------- Test1 --------------------
		// Strict attack (proper defeat). q(a) should be NOT_ENTAILED
		kb.addRule("[DEFT] q(X) :- p(X).");
		kb.addRule("n(X) :- neg(X).");
		kb.addNegativeConstraint("! :- q(X), n(X).");
		kb.addAtom("p(a).");
		kb.addAtom("neg(a).");
		
		kb.saturate();
		
		Atom atom = kb.getAtomsSatisfiyingAtomicQuery("?(X) :- q(a).").iterator().next();
		Argument arg = kb.af.getArgumentsFor(atom).iterator().next();
		
		int entailment = kb.EntailmentStatus(atom);
		assertEquals("Explicit: " + atom + " must Not be entailed.", DefeasibleKB.NOT_ENTAILED, entailment);
	}
	
	@Test
	public void testEntailementCase1() throws HomomorphismException, HomomorphismFactoryException, RuleApplicationException, AtomSetException, ChaseException {
		DefeasibleKB kb = new DefeasibleKB();
		//----------------- Test1 --------------------
		// Defeasible attack (Blocking defeat). q(a) should be NOT_ENTAILED
		kb.addRule("[DEFT] q(X) :- p(X).");
		kb.addRule("[DEFT] n(X) :- neg(X).");
		kb.addNegativeConstraint("! :- n(X), q(X).");
		kb.addAtom("p(a).");
		kb.addAtom("neg(a).");
		
		kb.saturate();
		
		Atom atom = kb.getAtomsSatisfiyingAtomicQuery("?(X) :- q(a).").iterator().next();
		Argument arg = kb.af.getArgumentsFor(atom).iterator().next();
		
		int entailment = kb.EntailmentStatus(atom);
		assertEquals("Explicit: " + atom + " must Not be entailed.", DefeasibleKB.NOT_ENTAILED, entailment);
	}
	
	@Test
	public void testEntailementCase2() throws HomomorphismException, HomomorphismFactoryException, RuleApplicationException, AtomSetException, ChaseException {
		DefeasibleKB kb = new DefeasibleKB();
		//----------------- Test2 --------------------
		// Defeasible attack (proper defeat). q(a) should be NOT_ENTAILED
		kb.addRule("[DEFT] q(X) :- p(X).");
		kb.addRule("[DEFT] n(X) :- neg(X),p(X).");
		kb.addNegativeConstraint("! :- n(X), q(X).");
		kb.addAtom("p(a).");
		kb.addAtom("neg(a).");
		
		kb.saturate();
		
		Atom atom = kb.getAtomsSatisfiyingAtomicQuery("?(X) :- q(a).").iterator().next();
		Argument arg = kb.af.getArgumentsFor(atom).iterator().next();
		
		int entailment = kb.EntailmentStatus(atom);
		assertEquals("Explicit: " + atom + " must Not be entailed.", DefeasibleKB.NOT_ENTAILED, entailment);
	}
	
	@Test
	public void testEntailementCase3() throws HomomorphismException, HomomorphismFactoryException, RuleApplicationException, AtomSetException, ChaseException {
		DefeasibleKB kb = new DefeasibleKB();
		//----------------- Test3 --------------------
		// Defeasible attack on Strict atom. q(a) should be STRICTLY_ENTAILED
		kb.addRule("q(X) :- p(X).");
		kb.addRule("[DEFT] n(X) :- neg(X),p(X).");
		kb.addNegativeConstraint("! :- n(X), q(X).");
		kb.addAtom("p(a).");
		kb.addAtom("neg(a).");
				
		kb.saturate();
		
		Atom atom = kb.getAtomsSatisfiyingAtomicQuery("?(X) :- q(a).").iterator().next();
		Argument arg = kb.af.getArgumentsFor(atom).iterator().next();
		
		int entailment = kb.EntailmentStatus(atom);
		assertEquals("Explicit: " + atom + " must be strictly entailed.", DefeasibleKB.STRICTLY_ENTAILED, entailment);
	}
	
	@Test
	public void testEntailementCase4() throws HomomorphismException, HomomorphismFactoryException, RuleApplicationException, AtomSetException, ChaseException {
		DefeasibleKB kb = new DefeasibleKB();
		//----------------- Test4 --------------------
		// Defeasible attack (proper defeat) but defended by Strict attack. q(a) should be DEFEASIBLY_ENTAILED
		kb.addRule("[DEFT] q(X) :- p(X).");
		kb.addRule("[DEFT] n(X) :- neg(X),p(X).");
		kb.addRule("d(X) :- def(X).");
		kb.addNegativeConstraint("! :- n(X), q(X).");
		kb.addNegativeConstraint("! :- n(X), d(X).");
		kb.addAtom("p(a).");
		kb.addAtom("neg(a).");
		kb.addAtom("def(a).");
				
		kb.saturate();
		
		Atom atom = kb.getAtomsSatisfiyingAtomicQuery("?(X) :- q(a).").iterator().next();
		Argument arg = kb.af.getArgumentsFor(atom).iterator().next();
		
		int entailment = kb.EntailmentStatus(atom);
		assertEquals("Explicit: " + atom + " must be defeasibly entailed.", DefeasibleKB.DEFEASIBLY_ENTAILED, entailment);
	}
	
	@Test
	public void testEntailementCase5() throws HomomorphismException, HomomorphismFactoryException, RuleApplicationException, AtomSetException, ChaseException {
		DefeasibleKB kb = new DefeasibleKB();
		//----------------- Test5 --------------------
		// Defeasible attack (proper defeat) but defended by Defeasible attack (proper defeat). q(a) should be DEFEASIBLY_ENTAILED
		kb.addRule("[DEFT] q(X) :- p(X).");
		kb.addRule("[DEFT] n(X) :- neg(X),p(X).");
		kb.addRule("d(X) :- def(X),n(X).");
		kb.addNegativeConstraint("! :- n(X), q(X).");
		kb.addNegativeConstraint("! :- n(X), d(X).");
		kb.addAtom("p(a).");
		kb.addAtom("neg(a).");
		kb.addAtom("def(a).");
				
		kb.saturate();
		
		System.out.println(kb.toString());
		
		Atom atom = kb.getAtomsSatisfiyingAtomicQuery("?(X) :- q(a).").iterator().next();
		Argument arg = kb.af.getArgumentsFor(atom).iterator().next();
		
		int entailment = kb.EntailmentStatus(atom);
		assertEquals("Explicit: " + atom + " must be defeasibly entailed.", DefeasibleKB.DEFEASIBLY_ENTAILED, entailment);
	}
	
	@Test
	public void testEntailementCase6() throws HomomorphismException, HomomorphismFactoryException, RuleApplicationException, AtomSetException, ChaseException {
		DefeasibleKB kb = new DefeasibleKB();
		//----------------- Test6 --------------------
		// Defeasible attack (proper defeat) but defended by Defeasible attack (blocking defeat). q(a) should be DEFEASIBLY_ENTAILED
		kb.addRule("[DEFT] q(X) :- p(X).");
		kb.addRule("[DEFT] n(X) :- neg(X),p(X).");
		kb.addRule("[DEFT] d(X) :- def(X).");
		kb.addNegativeConstraint("! :- n(X), q(X).");
		kb.addNegativeConstraint("! :- n(X), d(X).");
		kb.addAtom("p(a).");
		kb.addAtom("neg(a).");
		kb.addAtom("def(a).");
				
		kb.saturate();
		
		Atom atom = kb.getAtomsSatisfiyingAtomicQuery("?(X) :- q(a).").iterator().next();
		Argument arg = kb.af.getArgumentsFor(atom).iterator().next();
		
		int entailment = kb.EntailmentStatus(atom);
		assertEquals("Explicit: " + atom + " must be defeasibly entailed.", DefeasibleKB.DEFEASIBLY_ENTAILED, entailment);
	}
	
	@Test
	public void testEntailementCase7() throws HomomorphismException, HomomorphismFactoryException, RuleApplicationException, AtomSetException, ChaseException {
		DefeasibleKB kb = new DefeasibleKB();
		//----------------- Test7 --------------------
		// Defeasible attack (blocking defeat) but defended by Defeasible attack (proper defeat). q(a) should be DEFEASIBLY_ENTAILED
		kb.addRule("[DEFT] q(X) :- p(X).");
		kb.addRule("[DEFT] n(X) :- neg(X).");
		kb.addRule("[DEFT] d(X) :- def(X),n(X).");
		kb.addNegativeConstraint("! :- n(X), q(X).");
		kb.addNegativeConstraint("! :- n(X), d(X).");
		kb.addAtom("p(a).");
		kb.addAtom("neg(a).");
		kb.addAtom("def(a).");
		
		kb.saturate();
		
		Atom atom = kb.getAtomsSatisfiyingAtomicQuery("?(X) :- q(a).").iterator().next();
		Argument arg = kb.af.getArgumentsFor(atom).iterator().next();
		
		int entailment = kb.EntailmentStatus(atom);
		assertEquals("Explicit: " + atom + " must be defeasibly entailed.", DefeasibleKB.DEFEASIBLY_ENTAILED, entailment);
	}
	
	@Test
	public void testEntailementCase8() throws HomomorphismException, HomomorphismFactoryException, RuleApplicationException, AtomSetException, ChaseException {
		DefeasibleKB kb = new DefeasibleKB();
		//----------------- Test8 --------------------
		// Defeasible attack (blocking defeat) but defended by Defeasible attack (blocking defeat). q(a) should be NOT_ENTAILED
		kb.addRule("[DEFT] q(X) :- p(X).");
		kb.addRule("[DEFT] n(X) :- neg(X).");
		kb.addRule("[DEFT] d(X) :- def(X).");
		kb.addNegativeConstraint("! :- n(X), q(X).");
		kb.addNegativeConstraint("! :- n(X), d(X).");
		kb.addAtom("p(a).");
		kb.addAtom("neg(a).");
		kb.addAtom("def(a).");
		
		kb.saturate();
		
		Atom atom = kb.getAtomsSatisfiyingAtomicQuery("?(X) :- q(a).").iterator().next();
		Argument arg = kb.af.getArgumentsFor(atom).iterator().next();
		
		int entailment = kb.EntailmentStatus(atom);
		assertEquals("Explicit: " + atom + " must Not be entailed.", DefeasibleKB.NOT_ENTAILED, entailment);
	}
	
	@Test
	public void testEntailementCase9() throws HomomorphismException, HomomorphismFactoryException, RuleApplicationException, AtomSetException, ChaseException {
		DefeasibleKB kb = new DefeasibleKB();
		//----------------- Test9 --------------------
		// Defeasible attack (proper defeat) but defended by Defeasible attack (proper defeat) but itself
		// attacked by Defeasible attack (proper defeat). q(a) should be NOT_ENTAILED
		kb.addRule("[DEFT] q(X) :- p(X).");
		kb.addRule("[DEFT] n(X) :- neg(X), p(X).");
		kb.addRule("[DEFT] d(X) :- def(X), n(X).");
		kb.addRule("[DEFT] nn(X) :- att(X), def(X).");
		kb.addNegativeConstraint("! :- n(X), q(X).");
		kb.addNegativeConstraint("! :- n(X), d(X).");
		kb.addNegativeConstraint("! :- d(X), nn(X).");
		kb.addAtom("p(a).");
		kb.addAtom("neg(a).");
		kb.addAtom("def(a).");
		kb.addAtom("att(a).");
		
		kb.saturate();
		
		Atom atom = kb.getAtomsSatisfiyingAtomicQuery("?(X) :- q(a).").iterator().next();
		Argument arg = kb.af.getArgumentsFor(atom).iterator().next();
		
		int entailment = kb.EntailmentStatus(atom);
		assertEquals("Explicit: " + atom + " must Not be entailed.", DefeasibleKB.NOT_ENTAILED, entailment);
	}
	
	@Test
	public void testEntailementCase10() throws HomomorphismException, HomomorphismFactoryException, RuleApplicationException, AtomSetException, ChaseException {
		DefeasibleKB kb = new DefeasibleKB();
		//----------------- Test10 --------------------
		// Defeasible attack (proper defeat) but defended by Defeasible attack (proper defeat) but 
		// it is still attacked by Defeasible attack (proper defeat). q(a) should be NOT_ENTAILED
		kb.addRule("[DEFT] q(X) :- p(X).");
		kb.addRule("[DEFT] n(X) :- neg(X), p(X).");
		kb.addRule("[DEFT] d(X) :- def(X), n(X).");
		kb.addRule("[DEFT] nn(X) :- att(X), def(X).");
		kb.addNegativeConstraint("! :- n(X), q(X).");
		kb.addNegativeConstraint("! :- n(X), d(X).");
		kb.addNegativeConstraint("! :- d(X), nn(X).");
		kb.addAtom("p(a).");
		kb.addAtom("neg(a).");
		kb.addAtom("def(a).");
		kb.addAtom("att(a).");
		
		kb.saturate();
		
		Atom atom = kb.getAtomsSatisfiyingAtomicQuery("?(X) :- q(a).").iterator().next();
		Argument arg = kb.af.getArgumentsFor(atom).iterator().next();
		
		int entailment = kb.EntailmentStatus(atom);
		assertEquals("Explicit: " + atom + " must Not be entailed.", DefeasibleKB.NOT_ENTAILED, entailment);
	}
	
	@Test
	public void testEntailementCase11() throws HomomorphismException, HomomorphismFactoryException, RuleApplicationException, AtomSetException, ChaseException {
		DefeasibleKB kb = new DefeasibleKB();
		//----------------- Test11 --------------------
		// Defeasible attack (proper defeat) but defended by Defeasible attack (proper defeat) but
		// it is still attacked by Defeasible attack (proper defeat) but defended by Defeasible attack (proper defeat). 
		// q(a) should be DEFEASIBLY_ENTAILED
		kb.addRule("[DEFT] q(X) :- p(X).");
		kb.addRule("[DEFT] n(X) :- neg(X), p(X).");
		kb.addRule("[DEFT] d(X) :- def(X), n(X).");
		kb.addRule("[DEFT] m(X) :- meg(X), p(X).");
		kb.addRule("[DEFT] c(X) :- ctt(X), m(X).");
		kb.addNegativeConstraint("! :- n(X), q(X).");
		kb.addNegativeConstraint("! :- n(X), d(X).");
		kb.addNegativeConstraint("! :- m(X), q(X).");
		kb.addNegativeConstraint("! :- m(X), c(X).");
		kb.addAtom("p(a).");
		kb.addAtom("neg(a).");
		kb.addAtom("def(a).");
		kb.addAtom("meg(a).");
		kb.addAtom("ctt(a).");
		
		kb.saturate();
		
		Atom atom = kb.getAtomsSatisfiyingAtomicQuery("?(X) :- q(a).").iterator().next();
		Argument arg = kb.af.getArgumentsFor(atom).iterator().next();
		
		int entailment = kb.EntailmentStatus(atom);
		assertEquals("Explicit: " + atom + " must be defeasibly entailed.", DefeasibleKB.DEFEASIBLY_ENTAILED, entailment);
	}
	
	
	@Test
	public void testNegativeConstraintAtomsOrder() throws HomomorphismException, HomomorphismFactoryException, RuleApplicationException, AtomSetException, ChaseException {
		DefeasibleKB kb1 = new DefeasibleKB();
		DefeasibleKB kb2 = new DefeasibleKB();
		
		// kb1 and kb2 contain the same information, except the negative constraint atoms are in different order.
		kb1.addRule("[DEFT] q(X) :- p(X).");
		kb1.addRule("n(X) :- neg(X).");
		kb1.addAtom("p(a).");
		kb1.addAtom("neg(a).");
		
		kb2.addRule("[DEFT] q(X) :- p(X).");
		kb2.addRule("n(X) :- neg(X).");
		kb2.addAtom("p(a).");
		kb2.addAtom("neg(a).");
		
		kb1.addNegativeConstraint("! :- q(X), n(X).");
		kb2.addNegativeConstraint("! :- n(X), q(X).");
		
		kb1.saturate();
		kb2.saturate();
		
		Atom atom1 = kb1.getAtomsSatisfiyingAtomicQuery("?(X) :- q(a).").iterator().next();
		Argument arg = kb1.af.getArgumentsFor(atom1).iterator().next();
		
		Atom atom2 = kb2.getAtomsSatisfiyingAtomicQuery("?(X) :- q(a).").iterator().next();
		Argument arg2 = kb2.af.getArgumentsFor(atom2).iterator().next();
		
		LinkedList<Argument> attackers1 = kb1.af.getAttackersFor(arg);
		LinkedList<Argument> attackers2 = kb2.af.getAttackersFor(arg2);
		
		Iterator<Argument> it1 = attackers1.iterator();
		
		// Testing if both give the same attackers
		boolean found = true;
		while(it1.hasNext() && found) {
			Argument att1 = it1.next();
			found = false;
			for(Argument att2 : attackers2) {
				if(att1.toString().equals(att2.toString())) {
					found = true;
					
					break;
				}
			}
		}
		assertTrue("The order of the atoms in NegativeConstraint affects the attackers!", found);
	}

	
	
	// TODO test preference functions
	// TODO separate tests given class AF, Preference, etc.
	// TODO test existential variables
	// TODO test when nc maps to many atoms in the support.
	
	
	@Test
	public void testRuleOrderInFileInstantiation() throws FileNotFoundException, AtomSetException {
		DefeasibleKB kb = new DefeasibleKB();
		
		
	}
	
	/*
	public void testRuleOrderInExplicitInstantiation() {
		DefeasibleKB kb = new DefeasibleKB();
	}*/
	
	//TODO: test argumentation framework and specially in case of inconsitancy.
}
