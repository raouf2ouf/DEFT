package fr.lirmm.graphik.DEFT.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.lirmm.graphik.DEFT.dialectical_tree.Argument;
import fr.lirmm.graphik.DEFT.dialectical_tree.ArgumentationFramework;
import fr.lirmm.graphik.DEFT.gad.Derivation;
import fr.lirmm.graphik.DEFT.gad.GADRuleApplicationHandler;
import fr.lirmm.graphik.DEFT.gad.GraphOfAtomDependency;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.NegativeConstraint;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.forward_chaining.Chase;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplicationException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.atomset.graph.DefaultInMemoryGraphAtomSet;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.forward_chaining.NaiveChase;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.util.stream.CloseableIterator;

public class KB {
	public static final int NOT_ENTAILED = 0;
	public static final int STRICTLY_ENTAILED = 1;
	public static final int DEFEASIBLY_ENTAILED = 2;
	
	
	public RuleSet strictRuleSet;
	public RuleSet defeasibleRuleSet;
	public RuleSet negativeConstraintSet;
	public RuleSet rules;
	
	public AtomSet strictAtomSet;
	public AtomSet defeasibleAtomSet;
	public AtomSet facts;
	
	public GraphOfAtomDependency gad;
	
	public ArgumentationFramework af;
	/* --------------------------------
	 * Constructors
	 * -------------------------------- */
	public KB() { 
		this.strictAtomSet = new DefaultInMemoryGraphAtomSet();
		this.defeasibleAtomSet = new DefaultInMemoryGraphAtomSet();
		this.facts = new DefaultInMemoryGraphAtomSet();
		
		this.strictRuleSet = new LinkedListRuleSet();
		this.defeasibleRuleSet = new LinkedListRuleSet();
		this.rules = new LinkedListRuleSet();
		
		this.negativeConstraintSet = new LinkedListRuleSet();
		
		this.gad = new GraphOfAtomDependency();
		
		this.af = new ArgumentationFramework(this);
	}
	
	public KB(String file) throws FileNotFoundException, AtomSetException {
		this();
		DlgpParser dlgpParser = new DlgpParser(new File(file));
		
		while (dlgpParser.hasNext()) {
			Object o = dlgpParser.next();
			if (o instanceof Atom) {
				this.addAtom((Atom) o);
			} else if (o instanceof NegativeConstraint) {
				this.negativeConstraintSet.add((Rule) o);
			} else if (o instanceof Rule) {
				this.addRule((Rule) o);
			}
		}
		
		this.initialise();
	}
	
	/* --------------------------------
	 * Public Methods
	 * -------------------------------- */
	public void initialise() {
		this.gad.initialise(this.facts);
	}
	
	public void addAtom(Atom a) {
		try {
			if(isDefeasible(a)) {
				DefeasibleAtom atom = new DefeasibleAtom(a);
				this.defeasibleAtomSet.add(atom);
				this.facts.add(atom);
			} else {
				StrictAtom atom = new StrictAtom(a);
				this.strictAtomSet.add(atom);
				this.facts.add(atom);
			}
		} catch (AtomSetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addRule(Rule r) {
		try {
			if(isDefeasible(r)) {
				DefeasibleRule rule = new DefeasibleRule(r);
				this.defeasibleRuleSet.add(rule);
				this.rules.add(rule);
			} else {
				StrictRule rule = new StrictRule(r);
				this.strictRuleSet.add(rule);
				this.rules.add(rule);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void saturate() throws ChaseException {
		Chase chase = new NaiveChase(this.rules, this.facts, new GADRuleApplicationHandler(this.gad).getRuleApplier());
		chase.execute();
	}
	
	public void unsaturate() throws AtomSetException {
		this.facts.clear();
		this.facts.addAll(this.strictAtomSet);
		this.facts.addAll(this.defeasibleAtomSet);
	}
	
	public CloseableIterator<Substitution> query(String q) throws HomomorphismException {
		ConjunctiveQuery query = DlgpParser.parseQuery(q);
		return this.query(query);
	}
	
	public CloseableIterator<Substitution> query(ConjunctiveQuery query) throws HomomorphismException {
		DefaultConjunctiveQuery newquery = 
				new DefaultConjunctiveQuery(query.getLabel(), query.getAtomSet(), new LinkedList<Term>(query.getAtomSet().getTerms(Term.Type.VARIABLE)));
		// Atoms that match the query
		CloseableIterator<Substitution> substitutions = StaticHomomorphism.instance().execute(newquery, this.facts);
		
		return substitutions;
	}
	
	public AtomSet getAtomsSatisfiyingAtomicQuery(String q) throws HomomorphismException {
		ConjunctiveQuery query = DlgpParser.parseQuery(q);
		
		InMemoryAtomSet results = new DefaultInMemoryGraphAtomSet();
		CloseableIterator<Substitution> substitutions = this.query(query);
		
		while(substitutions.hasNext()) {
			Substitution sub = substitutions.next();
			InMemoryAtomSet atoms = sub.createImageOf(query.getAtomSet());
			results.add(atoms.iterator().next());
		}
		
		return results;
	}
	
	public LinkedList<Derivation> getDerivationsFor(Atom atom) throws HomomorphismException, HomomorphismFactoryException, RuleApplicationException, AtomSetException, ChaseException {
		LinkedList<Derivation> derivations = this.gad.getDerivations(atom);
		LinkedList<Derivation> acceptable_derivations = new LinkedList<Derivation>();
		
		for(Derivation d : derivations) {
			if(d.isConsistent(this.strictRuleSet, this.negativeConstraintSet)){
				acceptable_derivations.add(d);
			}
		}
		return acceptable_derivations;
	}
	
	public LinkedList<Derivation> getDerivationsFor(String atomString) throws HomomorphismException, HomomorphismFactoryException, RuleApplicationException, AtomSetException, ChaseException {
		Atom atom = DlgpParser.parseAtom(atomString);
		return this.getDerivationsFor(atom);
	}
	
	public int EntailmentStatus(Atom atom) throws AtomSetException, HomomorphismException, HomomorphismFactoryException, RuleApplicationException, ChaseException {
		int status = KB.NOT_ENTAILED;
		
		LinkedList<Derivation> derivations = this.getDerivationsFor(atom);
        
		List<Argument> arguments = new LinkedList<Argument>();
		
        for(Derivation d : derivations) {
        	arguments.add(new Argument(d, atom));
        }
        
        for(Argument arg : arguments) {
        	if(!af.isDefeated(arg)) {
        		if(arg.support.isDefeasible()) {
        			status = KB.DEFEASIBLY_ENTAILED;
        		} else {
        			status = KB.STRICTLY_ENTAILED; // No need to check other arguments supporting this atom.
        			break;
        		}
        	}
        }
		return status;
	}
	/* --------------------------------
	 * Static Methods
	 * -------------------------------- */
	public static boolean isDefeasible(Atom a) {
		String label = "";
		// TODO: implement getLabel() for Atom in Graal!
		return isDefeasible(label);
	}
	public static boolean isDefeasible(Rule r) {
		String label = r.getLabel().toLowerCase();
		return isDefeasible(label);
	}
	private static boolean isDefeasible(String label) {
		boolean isDefeasible = false;
		label = label.toLowerCase();
		Pattern pattern = Pattern.compile("[deft]");
		Matcher matcher = pattern.matcher(label);
		if (matcher.find())
		{
		    isDefeasible = true;
		}
		return isDefeasible;
	}
}
