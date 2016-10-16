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
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * 
 * @author Abdelraouf Hecham (INRIA) <hecham.abdelraouf@gmail.com>
 */
public class PreferenceTest {
	@Test
	public void testPreference() {
		DefeasibleKB kb = new DefeasibleKB();
		
		kb.addRule("[r1] p(X) :- q(X).");
		kb.addRule("[DEFTr1] p(X) :- q(X).");
		kb.addRule("[DEFTr2] p(X) :- s(X).");
		
		kb.addPreference("[laaa] [DEFTr1] > [DEFTr2].");
		kb.addPreference("[DEFTr2] > [DEFTr1].");
		
		Iterator<Preference> it = kb.preferenceSet.iterator();
		while(it.hasNext()) {
			System.out.println(it.next());
		}
		assertTrue("Failure - Defeasible rule set must not be empty.", kb.defeasibleRuleSet.iterator().hasNext());
	}
}
