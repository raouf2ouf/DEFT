package fr.lirmm.graphik.DEFT.gad;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseHaltingCondition;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplicationHandler;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplier;
import fr.lirmm.graphik.graal.forward_chaining.halting_condition.ChaseStopConditionWithHandler;
import fr.lirmm.graphik.graal.forward_chaining.halting_condition.RestrictedChaseStopCondition;
import fr.lirmm.graphik.graal.forward_chaining.rule_applier.ExhaustiveRuleApplier;
import fr.lirmm.graphik.util.stream.GIterator;

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

	public GIterator<Atom> postRuleApplication(Rule rule,
			Substitution substitution, AtomSet data, GIterator<Atom> atomsToAdd) {
		
		AtomSet newFacts = substitution.createImageOf(rule.getHead());
		graph.addEdges(substitution.createImageOf(rule.getBody()), newFacts.iterator(), rule, substitution); 
		
		return atomsToAdd;
	}
	
	
	public RuleApplier<Rule, AtomSet> getRuleApplier() {
		return getRuleApplier(new RestrictedChaseStopCondition());
	}
	
	public RuleApplier<Rule, AtomSet> getRuleApplier(ChaseHaltingCondition chaseCondition) {
		ChaseStopConditionWithHandler chaseConditionHandler = new ChaseStopConditionWithHandler(chaseCondition, this);
		RuleApplier<Rule, AtomSet> ruleApplier = new ExhaustiveRuleApplier<AtomSet>(chaseConditionHandler);
		
		/*Maven workaround*/
		//RuleApplier<Rule, AtomSet> ruleApplier = new DefaultRuleApplier<AtomSet>(chaseCondition);
		return ruleApplier;
	}
	
}
