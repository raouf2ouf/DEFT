package fr.lirmm.graphik.DEFT.dialectical_tree;

import java.util.HashSet;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;

public class GeneralizedSpecificityPreference implements ArgumentPreference {

	public int compare(Argument attacker, Argument attackee) {
		// if the argument being attacked is strict then it is undefeatable.
		if(!attackee.support.isDefeasible()) {
			return ArgumentPreference.NOT_DEFEAT;
		}
		
		// else, if the attacker is strict then it wins
		if(!attacker.support.isDefeasible()) {
			return ArgumentPreference.PROPER_DEFEAT;
		}
		
		// else, the attacker and attackee are both defeasible
		try {
		
			HashSet<Atom> baseFactAttacker = attacker.support.getBaseFacts();
			HashSet<Atom> baseFactAttackee = attackee.support.getBaseFacts();
	
			// 1. if the the base facts of the attackee are included in the base facts of the attacker
			if(baseFactAttacker.containsAll(baseFactAttackee)) {
				if(attacker.support.getNumberOfAtoms() == attackee.support.getNumberOfAtoms()) {
					// Attacker and Attackee use the same base, so we take a look at the rules.
					if(attacker.support.getNumberOfRules() < attackee.support.getNumberOfRules()) {
						return ArgumentPreference.PROPER_DEFEAT;
					} else if(attacker.support.getNumberOfRules() < attackee.support.getNumberOfRules()) {
						return ArgumentPreference.NOT_DEFEAT;
					} else {
						return ArgumentPreference.BLOCKING_DEFEAT;
					}
					
				} else{
					return ArgumentPreference.PROPER_DEFEAT;
				}
			} else if(baseFactAttackee.containsAll(baseFactAttacker)) {
			// 2. if the the base facts of the attacker are included in the base facts of the attackee
				return ArgumentPreference.NOT_DEFEAT;
			} else {
			// 3. if they do not have the same base facts
				return ArgumentPreference.BLOCKING_DEFEAT;
			}
			
			
		}
		catch (AtomSetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ArgumentPreference.BLOCKING_DEFEAT;
	}

}
