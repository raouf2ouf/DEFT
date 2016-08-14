package fr.lirmm.graphik.DEFT.core;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.util.Iterator;

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
		// Populating first kb via a DLGP File
		//this.kb1 = new DefeasibleKB("./src/test/resources/entailment.dlgp");
		
		this.kb1 = new DefeasibleKB("./src/test/resources/t1.dlgp");
		
		// Populating second kb explicitly
		this.kb2 = new DefeasibleKB();
		
		//----------------- Test1 --------------------
		// Strict attack (proper defeat). q1(a) should be NOT_ENTAILED
		kb2.addRule("[DEFT] q1(X) :- p1(X).");
		kb2.addRule("n1(X) :- neg1(X).");
		kb2.addNegativeConstraint("! :- n1(X), q1(X).");
		kb2.addAtom("p1(a).");
		kb2.addAtom("neg1(a).");
		/*
		//----------------- Test2 --------------------
		// Defeasible attack (Blocking defeat). q2(a) should be NOT_ENTAILED
		kb2.addRule("[DEFT] q2(X) :- p2(X).");
		kb2.addRule("[DEFT] n2(X) :- neg2(X).");
		kb2.addNegativeConstraint("! :- n2(X), q2(x).");
		kb2.addAtom("p2(a).");
		kb2.addAtom("neg2(a).");
		
		//----------------- Test3 --------------------
		// Defeasible attack (proper defeat). q3(a) should be NOT_ENTAILED
		kb2.addRule("[DEFT] q3(X) :- p3(X).");
		kb2.addRule("[DEFT] n3(X) :- neg3(X),p3(X).");
		kb2.addNegativeConstraint("! :- n3(X), q3(x).");
		kb2.addAtom("p3(a).");
		kb2.addAtom("neg3(a).");
		
		//----------------- Test4 --------------------
		// Defeasible attack on Strict atom. q4(a) should be STRICTLY_ENTAILED
		kb2.addRule("q4(X) :- p4(X).");
		kb2.addRule("[DEFT] n4(X) :- neg4(X),p4(X).");
		kb2.addNegativeConstraint("! :- n4(X), q4(x).");
		kb2.addAtom("p4(a).");
		kb2.addAtom("neg4(a).");
		
		//----------------- Test5 --------------------
		// Defeasible attack (proper defeat) but defended by Strict attack. q5(a) should be DEFEASIBLY_ENTAILED
		kb2.addRule("[DEFT] q5(X) :- p5(X).");
		kb2.addRule("[DEFT] n5(X) :- neg5(X),p5(X).");
		kb2.addRule("d5(X) :- def5(X).");
		kb2.addNegativeConstraint("! :- n5(X), q5(x).");
		kb2.addNegativeConstraint("! :- n5(X), d5(x).");
		kb2.addAtom("p5(a).");
		kb2.addAtom("neg5(a).");
		kb2.addAtom("def5(a).");
		
		//----------------- Test6 --------------------
		// Defeasible attack (proper defeat) but defended by Defeasible attack (proper defeat). q6(a) should be DEFEASIBLY_ENTAILED
		kb2.addRule("[DEFT] q6(X) :- p6(X).");
		kb2.addRule("[DEFT] n6(X) :- neg6(X),p6(X).");
		kb2.addRule("d6(X) :- def6(X),n6(X).");
		kb2.addNegativeConstraint("! :- n6(X), q6(x).");
		kb2.addNegativeConstraint("! :- n6(X), d6(x).");
		kb2.addAtom("p6(a).");
		kb2.addAtom("neg6(a).");
		kb2.addAtom("def6(a).");
		
		//----------------- Test7 --------------------
		// Defeasible attack (proper defeat) but defended by Defeasible attack (blocking defeat). q7(a) should be DEFEASIBLY_ENTAILED
		kb2.addRule("[DEFT] q7(X) :- p7(X).");
		kb2.addRule("[DEFT] n7(X) :- neg7(X),p7(X).");
		kb2.addRule("[DEFT] d7(X) :- def7(X).");
		kb2.addNegativeConstraint("! :- n7(X), q7(x).");
		kb2.addNegativeConstraint("! :- n7(X), d7(x).");
		kb2.addAtom("p7(a).");
		kb2.addAtom("neg7(a).");
		kb2.addAtom("def7(a).");
		
		//----------------- Test8 --------------------
		// Defeasible attack (blocking defeat) but defended by Defeasible attack (proper defeat). q8(a) should be DEFEASIBLY_ENTAILED
		kb2.addRule("[DEFT] q8(X) :- p8(X).");
		kb2.addRule("[DEFT] n8(X) :- neg8(X).");
		kb2.addRule("[DEFT] d8(X) :- def8(X),n8(X).");
		kb2.addNegativeConstraint("! :- n8(X), q8(x).");
		kb2.addNegativeConstraint("! :- n8(X), d8(x).");
		kb2.addAtom("p8(a).");
		kb2.addAtom("neg8(a).");
		kb2.addAtom("def8(a).");
		
		//----------------- Test9 --------------------
		// Defeasible attack (blocking defeat) but defended by Defeasible attack (blocking defeat). q9(a) should be NOT_ENTAILED
		kb2.addRule("[DEFT] q9(X) :- p9(X).");
		kb2.addRule("[DEFT] n9(X) :- neg9(X).");
		kb2.addRule("[DEFT] d9(X) :- def9(X).");
		kb2.addNegativeConstraint("! :- n9(X), q9(x).");
		kb2.addNegativeConstraint("! :- n9(X), d9(x).");
		kb2.addAtom("p9(a).");
		kb2.addAtom("neg9(a).");
		kb2.addAtom("def9(a).");
		
		//----------------- Test10 --------------------
		// Defeasible attack (proper defeat) but defended by Defeasible attack (proper defeat) but itself
		// attacked by Defeasible attack (proper defeat). q10(a) should be NOT_ENTAILED
		kb2.addRule("[DEFT] q10(X) :- p10(X).");
		kb2.addRule("[DEFT] n10(X) :- neg10(X), p10(X).");
		kb2.addRule("[DEFT] d10(X) :- def10(X), n10(X).");
		kb2.addRule("[DEFT] nn10(X) :- att10(X), def10(X).");
		kb2.addNegativeConstraint("! :- n10(X), q10(x).");
		kb2.addNegativeConstraint("! :- n10(X), d10(x).");
		kb2.addNegativeConstraint("! :- d10(X), nn10(x).");
		kb2.addAtom("p10(a).");
		kb2.addAtom("neg10(a).");
		kb2.addAtom("def10(a).");
		kb2.addAtom("att10(a).");
		
		//----------------- Test11 --------------------
		// Defeasible attack (proper defeat) but defended by Defeasible attack (proper defeat) but 
		// it is still attacked by Defeasible attack (proper defeat). q11(a) should be NOT_ENTAILED
		kb2.addRule("[DEFT] q11(X) :- p11(X).");
		kb2.addRule("[DEFT] n11(X) :- neg11(X), p11(X).");
		kb2.addRule("[DEFT] d11(X) :- def11(X), n11(X).");
		kb2.addRule("[DEFT] nn11(X) :- att11(X), def11(X).");
		kb2.addNegativeConstraint("! :- n11(X), q11(x).");
		kb2.addNegativeConstraint("! :- n11(X), d11(x).");
		kb2.addNegativeConstraint("! :- d11(X), nn11(x).");
		kb2.addAtom("p11(a).");
		kb2.addAtom("neg11(a).");
		kb2.addAtom("def11(a).");
		kb2.addAtom("att11(a).");
		
		//----------------- Test12 --------------------
		// Defeasible attack (proper defeat) but defended by Defeasible attack (proper defeat) but
		// it is still attacked by Defeasible attack (proper defeat) but defended by Defeasible attack (proper defeat). 
		// q12(a) should be DEFEASIBLY_ENTAILED
		kb2.addRule("[DEFT] q12(X) :- p12(X).");
		kb2.addRule("[DEFT] n12(X) :- neg12(X), p12(X).");
		kb2.addRule("[DEFT] d12(X) :- def12(X), n12(X).");
		kb2.addRule("[DEFT] m12(X) :- meg12(X), p12(X).");
		kb2.addRule("[DEFT] c12(X) :- ctt12(X), m12(X).");
		kb2.addNegativeConstraint("! :- n12(X), q12(x).");
		kb2.addNegativeConstraint("! :- n12(X), d12(x).");
		kb2.addNegativeConstraint("! :- m12(X), q12(x).");
		kb2.addNegativeConstraint("! :- m12(X), c12(x).");
		kb2.addAtom("p12(a).");
		kb2.addAtom("neg12(a).");
		kb2.addAtom("def12(a).");
		kb2.addAtom("meg12(a).");
		kb2.addAtom("ctt12(a).");*/
		
		
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
	
	/*
	@Test
	public void testDefeasibleKBFileInstantiationEntailementTest1() throws HomomorphismException, AtomSetException, HomomorphismFactoryException, RuleApplicationException, ChaseException {
		for(Atom a : kb1.facts) {
			System.out.println(a);
		}
		
		Atom atom = kb1.getAtomsSatisfiyingAtomicQuery("?(X) :- q1(a).").iterator().next();
		Argument arg = kb1.af.getArgumentsFor(atom).iterator().next();
		System.out.println("Argument: " + arg);
		Iterator<Argument> it = kb1.af.getAttackersFor(arg).iterator();
		if(!it.hasNext()) System.out.println("No Attackers for " + arg);
		
		while(it.hasNext()) {
			System.out.println("Attackers of " + atom + ": " + it.next());
		}
		
		System.out.println(kb1.negativeConstraintSet.iterator().next());
		
		int entailment = kb1.EntailmentStatus(atom);
		assertEquals("File: " + atom + " must Not be entailed.", DefeasibleKB.NOT_ENTAILED, entailment);
	}*/
	
	@Test
	public void testDefeasibleKBExplicitInstantiationEntailementTest1() throws HomomorphismException, HomomorphismFactoryException, RuleApplicationException, AtomSetException, ChaseException {
		DefeasibleKB kb = new DefeasibleKB();
		//----------------- Test1 --------------------
		// Strict attack (proper defeat). q1(a) should be NOT_ENTAILED
		kb.addRule("[DEFT] q(X) :- p(X).");
		kb.addRule("n(X) :- neg(X).");
		kb.addNegativeConstraint("! :- q(X), n(X).");
		kb.addAtom("p(a).");
		kb.addAtom("neg(a).");
		
		kb.saturateWithNegativeConstraint();
		
		for(Atom a : kb.facts) {
			System.out.println(a);
		}
		Atom atom = kb.getAtomsSatisfiyingAtomicQuery("?(X) :- q(a).").iterator().next();
		Argument arg = kb.af.getArgumentsFor(atom).iterator().next();
		System.out.println("Argument: " + arg);
		Iterator<Argument> it = kb.af.getAttackersFor(arg).iterator();
		if(!it.hasNext()) System.out.println("No attackers for " + arg);
		
		while(it.hasNext()) {
			System.out.println("Attackers of " + atom + ": " + it.next());
		}
		
		
		int entailment = kb.EntailmentStatus(atom);
		assertEquals("Explicit: " + atom + " must Not be entailed.", DefeasibleKB.NOT_ENTAILED, entailment);
	}
	
	// negative constraint atoms order
	
	/*
	public void testDefeasibleKBRuleOrderInFileInstantiation() throws FileNotFoundException, AtomSetException {
		DefeasibleKB kb = new DefeasibleKB("");
	}
	
	public void testDefeasibleKBRuleOrderInExplicitInstantiation() {
		DefeasibleKB kb = new DefeasibleKB();
	}*/
	
	//TODO: test argumentation framework and specially in case of inconsitancy.
}
