package fr.lirmm.graphik.DEFT.core;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import fr.lirmm.graphik.DEFT.gad.CompactDerivation;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplicationException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.util.stream.IteratorException;

public class GADTest {
	@Test
	public void testCompactDerivation() throws IteratorException, HomomorphismException, ChaseException, AtomSetException, HomomorphismFactoryException, RuleApplicationException {
		DefeasibleKB kb = new DefeasibleKB();
		
		kb.addAtom("p(a).");
		kb.addAtom("d(a).");
		kb.addAtom("v(a).");
		
		kb.addRule("[r1] s(X) :- p(X).");
		kb.addRule("[r1] t(X) :- d(X).");
		kb.addRule("[r1] t(X) :- v(X).");
		kb.addRule("[r1] q(X) :- s(X), t(X).");
		
		kb.addRule("[DEFTr1] q(X) :- p(X).");
		
		kb.saturate();
		
		Atom atom = kb.getAtomsSatisfiyingAtomicQuery("? :- q(a).").iterator().next();
		List<CompactDerivation> derivations = kb.gad.getCompactDerivationFor(atom);
		
		for(CompactDerivation deriv : derivations) {
			//System.out.println(deriv.toString());
		}
		
		assertTrue("Failure - Defeasible rule set must not be empty.", true);
	}
	
	@Test
	public void testCompactDerivationEntailment() throws IteratorException, HomomorphismException, ChaseException, AtomSetException, HomomorphismFactoryException, RuleApplicationException {
		DefeasibleKB kb = new DefeasibleKB();
		
		kb.addAtom("p(a).");
		kb.addAtom("d(a).");
		kb.addAtom("v(a).");
		
		kb.addRule("[r1] s(X) :- p(X).");
		
		kb.addRule("[DEFTr1] q(X) :- p(X).");
		
		kb.addNegativeConstraint("! :- s(X), q(X).");
		kb.saturate();
		
		Atom atom = kb.getAtomsSatisfiyingAtomicQuery("? :- q(a).").iterator().next();
		List<CompactDerivation> derivations = kb.gad.getCompactDerivationFor(atom);
		
		for(CompactDerivation deriv : derivations) {
			//System.out.println(deriv.toString());
		}
		
		//System.out.println("entailment sattus: " + kb.af.updateAtomEntailmentStatus(atom));
		
		assertTrue("Failure - Defeasible rule set must not be empty.", true);
	}
}
