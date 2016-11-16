package fr.lirmm.graphik.DEFT.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplicationException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.util.stream.IteratorException;

public class PreferenceTest {
	@Test
	public void testPreferenceParsing() throws IteratorException {
		DefeasibleKB kb = new DefeasibleKB();
		
		kb.addRule("[r1] p(X) :- q(X).");
		kb.addRule("[DEFTr1] p(X) :- q(X).");
		kb.addRule("[DEFTr2] p(X) :- s(X).");
		
		kb.addPreference("[laaa] [DEFTr1] > [DEFTr2].");
		kb.addPreference("[DEFTr2] > [DEFTr1].");
		
		assertTrue("Failure - Defeasible rule set must not be empty.", kb.preferenceSet.iterator().hasNext());
	}
	
	
	@Test
	public void testPreferenceBasedEntailmentCase01() throws IteratorException, ChaseException, AtomSetException, HomomorphismException, HomomorphismFactoryException, RuleApplicationException {
		// Blocking attack resolved with Preferences
		DefeasibleKB kb = new DefeasibleKB();
		
		kb.addAtom("p(a).");
		
		kb.addRule("[DEFTr1] q(X) :- p(X).");
		kb.addRule("[DEFTr2] s(X) :- p(X).");
		
		kb.addNegativeConstraint("! :- q(X), s(X).");
		
		kb.addPreference("[DEFTr1] > [DEFTr2].");
		//kb.addPreference("[DEFTr2] > [DEFTr1].");
		
		kb.saturate();
		
		Atom atom = kb.getAtomsSatisfiyingAtomicQuery("? :- q(a).").iterator().next();
		
		int entailment = kb.EntailmentStatus(atom);
		
		assertEquals(atom + " must be defeasibly entailed.", DefeasibleKB.DEFEASIBLY_ENTAILED, entailment);
	}
	
	@Test
	public void testPreferenceBasedEntailmentCase02() throws IteratorException, ChaseException, AtomSetException, HomomorphismException, HomomorphismFactoryException, RuleApplicationException {
		// Blocking Preferences
		DefeasibleKB kb = new DefeasibleKB();
		
		kb.addAtom("p(a).");
		
		kb.addRule("[DEFTr1] q(X) :- p(X).");
		kb.addRule("[DEFTr2] s(X) :- p(X).");
		
		kb.addNegativeConstraint("! :- q(X), s(X).");
		
		kb.addPreference("[DEFTr1] > [DEFTr2].");
		kb.addPreference("[DEFTr2] > [DEFTr1].");
		
		kb.saturate();
		
		Atom atom = kb.getAtomsSatisfiyingAtomicQuery("? :- q(a).").iterator().next();
		
		int entailment = kb.EntailmentStatus(atom);
		
		assertEquals(atom + " must NOT be entailed (Blocking).", DefeasibleKB.NOT_ENTAILED, entailment);
	}
	
	@Test
	public void testPreferenceBasedEntailmentCase03() throws IteratorException, ChaseException, AtomSetException, HomomorphismException, HomomorphismFactoryException, RuleApplicationException {
		// Preferences along side a strict Entailment
		DefeasibleKB kb = new DefeasibleKB();
		
		kb.addAtom("p(a).");
		
		kb.addRule("[r1] q(X) :- p(X).");
		kb.addRule("[DEFTr2] s(X) :- p(X).");
		
		kb.addNegativeConstraint("! :- q(X), s(X).");
		
		kb.addPreference("[r1] > [DEFTr2].");
		//kb.addPreference("[DEFTr2] > [DEFTr1].");
		
		kb.saturate();
		
		Atom atom = kb.getAtomsSatisfiyingAtomicQuery("? :- q(a).").iterator().next();
		
		int entailment = kb.EntailmentStatus(atom);
		
		assertEquals(atom + " must be Strictly entailed.", DefeasibleKB.STRICTLY_ENTAILED, entailment);
	}
	
	@Test
	public void testPreferenceBasedEntailmentCase04() throws IteratorException, ChaseException, AtomSetException, HomomorphismException, HomomorphismFactoryException, RuleApplicationException {
		// Preferences against a strict Entailment
		DefeasibleKB kb = new DefeasibleKB();
		
		kb.addAtom("p(a).");
		
		kb.addRule("[DEFTr1] q(X) :- p(X).");
		kb.addRule("[r2] s(X) :- p(X).");
		
		kb.addNegativeConstraint("! :- q(X), s(X).");
		
		kb.addPreference("[DEFTr1] > [r2].");
		
		kb.saturate();
		
		Atom atom = kb.getAtomsSatisfiyingAtomicQuery("? :- q(a).").iterator().next();
		
		int entailment = kb.EntailmentStatus(atom);
		
		assertEquals(atom + " must Not be entailed.", DefeasibleKB.NOT_ENTAILED, entailment);
	}
}
