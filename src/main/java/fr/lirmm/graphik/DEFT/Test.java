package fr.lirmm.graphik.DEFT;

import static org.junit.Assert.assertEquals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.lirmm.graphik.DEFT.core.DefeasibleKB;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplicationException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.util.stream.IteratorException;

public  class Test {

		public static void main(String[] args) throws HomomorphismException, HomomorphismFactoryException, RuleApplicationException, AtomSetException, ChaseException, IteratorException {
				DefeasibleKB kb = new DefeasibleKB();

				kb.addAtom("q(a).");
				
				
				//Atom atom = kb.getAtomsSatisfiyingAtomicQuery().iterator().next();
				System.out.println(StaticHomomorphism
				.instance().execute(DlgpParser.parseQuery("? :- q(a)."), kb.facts).hasNext());
			
		}
}
