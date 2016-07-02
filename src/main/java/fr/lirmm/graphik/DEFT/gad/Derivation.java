package fr.lirmm.graphik.DEFT.gad;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import fr.lirmm.graphik.DEFT.core.DefeasibleAtom;
import fr.lirmm.graphik.DEFT.core.DefeasibleRule;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.NegativeConstraint;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.forward_chaining.Chase;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplicationException;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplier;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.factory.ConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.forward_chaining.NaiveChase;
import fr.lirmm.graphik.graal.forward_chaining.halting_condition.RestrictedChaseStopCondition;
import fr.lirmm.graphik.graal.forward_chaining.rule_applier.DefaultRuleApplier;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;
import fr.lirmm.graphik.util.stream.CloseableIterator;


public class Derivation implements Iterable<GADEdge>{
	private LinkedList<GADEdge> path;
	
	/* --------------------------------
	 * Constructors
	 * -------------------------------- */
	public Derivation() {
		this(new LinkedList<GADEdge>());
	}
	
	public Derivation(GADEdge edge) {
		this();
		this.path.add(edge);
	}
	
	public Derivation(LinkedList<GADEdge> path) {
		this.path = path;
	}
	
	public Derivation(Derivation d) {
		this.path = new LinkedList<GADEdge>();
		this.path.addAll(d.getPath());
	}
	
	/* --------------------------------
	 * Public Methods
	 * -------------------------------- */
	public void add(GADEdge edge) {
		this.path.add(edge);
	}
	
	public void addAll(Derivation d) {
		this.path.addAll(d.getPath());
	}
	
	public LinkedList<GADEdge> getPath() {
		return this.path;
	}
	
	
	public boolean isConsistent(RuleSet strictRules, RuleSet negativeConstaints) 
			throws HomomorphismException, HomomorphismFactoryException, RuleApplicationException, AtomSetException, ChaseException {
		
		AtomSet store = this.getAtoms();
		
		// Apply the rules and saturate the set of facts
		Chase chase = new NaiveChase(strictRules, store, new RestrictedChaseStopCondition());
		chase.execute();
		
		RuleApplier<Rule, AtomSet> ruler = new DefaultRuleApplier<AtomSet>();
		for(Rule r : negativeConstaints) {
			if(r instanceof NegativeConstraint) {
				ruler.apply(r, store);
			}
		}
		
		ConjunctiveQuery cq = ConjunctiveQueryFactory.instance().create(new LinkedListAtomSet(new DefaultAtom(Predicate.BOTTOM, DefaultTermFactory.instance().createVariable("X"))));
			
		CloseableIterator<Substitution> consistency = StaticHomomorphism.instance().execute(cq, store);
		return !consistency.hasNext();
	}
	
	public boolean isDefeasible() {
		boolean isDefeasible = false;
		
		for(GADEdge edge : this.path) {
			if(edge.getRule() == null) { // This is a fact
				if(edge.getTarget() instanceof DefeasibleAtom) {
					isDefeasible = true;
					break;
				}
			} else { // Not a fact
				if(edge.getRule() instanceof DefeasibleRule) {
					isDefeasible = true;
					break;
				}
			}
		}
		
		return isDefeasible;
	}
	
	public AtomSet getAtoms() throws AtomSetException {
		HashSet<Atom> set = new HashSet<Atom>();
		AtomSet store = new LinkedListAtomSet();
		
		for(GADEdge edge: this.path) {
			if(edge.getSources() != null) {
				for(Atom a : edge.getSources()) {
					set.add(a);
				}
			}
			set.add(edge.getTarget());
		}
		
		for(Atom a : set) {
			store.add(a);
		}
		return store;
	}
	
	public Iterator<GADEdge> iterator() {
		return path.iterator();
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		Iterator<GADEdge> it = this.path.iterator();
		if(it.hasNext()) {
			s.append(it.next().getTarget());
		}
		for(GADEdge edge : this.path) {
			s.append(edge);
		}
		s.append("\n");
		return s.toString();
	}
}