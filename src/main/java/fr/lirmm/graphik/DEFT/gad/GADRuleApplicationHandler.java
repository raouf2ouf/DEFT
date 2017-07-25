package fr.lirmm.graphik.DEFT.gad;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseHaltingCondition;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplicationHandler;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplier;
import fr.lirmm.graphik.graal.forward_chaining.halting_condition.FrontierRestrictedChaseHaltingCondition;
import fr.lirmm.graphik.graal.forward_chaining.halting_condition.HaltingConditionWithHandler;
import fr.lirmm.graphik.graal.forward_chaining.rule_applier.ExhaustiveRuleApplier;
import fr.lirmm.graphik.graal.homomorphism.SmartHomomorphism;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

public class GADRuleApplicationHandler implements RuleApplicationHandler{
	
	private GraphOfAtomDependency graph;
	
	public GADRuleApplicationHandler(GraphOfAtomDependency graph) {
		super();
		this.graph = graph;
	}
	
	public void setGraphOfAtomDependency(GraphOfAtomDependency graph) {
		this.graph = graph;
	}
	
	public boolean preRuleApplication(Rule rule, Substitution substitution,
			AtomSet data) {
		return true;
	}

	public CloseableIterator<Atom> postRuleApplication(Rule rule,
			Substitution substitution, AtomSet data, CloseableIterator<Atom> atomsToAdd) {
		
		AtomSet newFacts = substitution.createImageOf(rule.getHead());
		try {
			graph.addEdges(substitution.createImageOf(rule.getBody()), newFacts.iterator(), rule, substitution);
		} catch (IteratorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return atomsToAdd;
	}
	
	
	public RuleApplier<Rule, AtomSet> getRuleApplier() {
		return getRuleApplier(new FrontierRestrictedChaseHaltingCondition());
	}
	
	public RuleApplier<Rule, AtomSet> getRuleApplier(ChaseHaltingCondition chaseCondition) {
		HaltingConditionWithHandler chaseConditionHandler = new HaltingConditionWithHandler(chaseCondition, this);
		RuleApplier<Rule, AtomSet> ruleApplier = new ExhaustiveRuleApplier<AtomSet>(SmartHomomorphism.instance(), chaseConditionHandler); 
		
		return ruleApplier;
	}
	
}
