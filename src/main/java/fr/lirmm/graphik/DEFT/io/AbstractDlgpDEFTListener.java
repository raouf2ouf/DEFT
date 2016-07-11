package fr.lirmm.graphik.DEFT.io;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.DEFT.core.DefeasibleAtom;
import fr.lirmm.graphik.DEFT.core.DefeasibleRule;
import fr.lirmm.graphik.DEFT.core.StrictAtom;
import fr.lirmm.graphik.DEFT.core.StrictRule;
import fr.lirmm.graphik.dlgp2.parser.ParserListener;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Constant;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultNegativeConstraint;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.factory.ConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.factory.RuleFactory;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;


/**
 * Extends AbstractDlgpDeftListener to take into account the DEFT label to detect atom and rule type.
 * @author Abdelraouf Hecham (INRIA) <hecham.abdelraouf@gmail.com>
 * 
 */
abstract class AbstractDlgpDEFTListener implements ParserListener {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AbstractDlgpDEFTListener.class);

	private List<Term> answerVars;
	private LinkedListAtomSet atomSet = null;
	private LinkedListAtomSet atomSet2 = null;
	private DefaultAtom atom;
	private String label;
	private boolean isDefeasible;
	
	
	protected abstract void createAtomSet(InMemoryAtomSet atom);

	protected abstract void createQuery(ConjunctiveQuery query);

	protected abstract void createRule(Rule basicRule);

	protected abstract void createNegConstraint(
			DefaultNegativeConstraint negativeConstraint);

	//@Override
	public void startsObject(OBJECT_TYPE objectType, String name) {
		this.label = (name == null) ? "" : name;
		// Before Creating this object, check if it is defeasible
		this.isDefeasible = this.isDefeasible(this.label);
		
		atomSet = new LinkedListAtomSet();
		atomSet2 = null;

		if (OBJECT_TYPE.QUERY.equals(objectType)) {
			this.answerVars = new LinkedList<Term>();
		}


	}

	//@Override
	public void createsAtom(Object predicate, Object[] terms) {
		List<Term> list = new LinkedList<Term>();
		for (Object t : terms) {
			list.add(createTerm(t));
		}

		atom = new DefaultAtom(createPredicate(predicate, terms.length),
				list);
		
		atom = (this.isDefeasible) ? new DefeasibleAtom(atom) : new StrictAtom(atom);
		
		this.atomSet.add(atom);

	}

	//@Override
	public void createsEquality(Object term1, Object term2) {
		atom = new DefaultAtom(Predicate.EQUALITY, createTerm(term1),
				createTerm(term2));
		
		atom = (this.isDefeasible) ? new DefeasibleAtom(atom) : new StrictAtom(atom);
		
		this.atomSet.add(atom);

	}

	//@Override
	public void answerTermList(Object[] terms) {
		for (Object t : terms) {
			this.answerVars.add((Term) t);
		}
	}

	//@Override
	public void endsConjunction(OBJECT_TYPE objectType) {
		switch (objectType) {
		case QUERY:
			this.createQuery(ConjunctiveQueryFactory.instance().create(this.label,
					this.atomSet, this.answerVars));
			break;
		case NEG_CONSTRAINT:
			this.createNegConstraint(new DefaultNegativeConstraint(this.label,
					this.atomSet));
			break;
		case RULE:
			if (this.atomSet2 == null) {
				this.atomSet2 = this.atomSet;
				this.atomSet = new LinkedListAtomSet();
			} else {
				Rule rule = RuleFactory.instance().create(this.label, this.atomSet,this.atomSet2);
				rule = (this.isDefeasible) ? new DefeasibleRule(rule) : new StrictRule(rule);
				this.createRule(rule);
			}
			break;
		case FACT:
			this.createAtomSet(this.atomSet);
			break;
		default:
			break;
		}
	}


	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private Predicate createPredicate(Object uri, int arity) {
		return new Predicate(uri, arity);
	}

	private Constant createConstant(Object uri) {
		return DefaultTermFactory.instance().createConstant(uri);
	}

	private Term createTerm(Object t) {
		if (t instanceof Term) {
			return (Term) t;
		} else {
			return createConstant(t);
		}
	}
	
	private boolean isDefeasible(String label) {
		boolean isDefeasible = false;
		label = label.toLowerCase();
		Pattern pattern = Pattern.compile("[deft]");
		Matcher matcher = pattern.matcher(label);
		if (matcher.find()) {
		    isDefeasible = true;
		}
		return isDefeasible;
	}

}
