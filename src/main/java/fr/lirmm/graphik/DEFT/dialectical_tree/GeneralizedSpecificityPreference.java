package fr.lirmm.graphik.DEFT.dialectical_tree;

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
		if(attacker.support.getNumberOfRules() > attackee.support.getNumberOfRules()) {
			return ArgumentPreference.PROPER_DEFEAT;
		} else if(attacker.support.getNumberOfRules() < attackee.support.getNumberOfRules()) {
			return ArgumentPreference.NOT_DEFEAT;
		} else {
			if(attacker.support.getNumberOfAtoms() > attackee.support.getNumberOfAtoms()) {
				return ArgumentPreference.PROPER_DEFEAT;
			} else if(attacker.support.getNumberOfAtoms() < attackee.support.getNumberOfAtoms()) {
				return ArgumentPreference.NOT_DEFEAT;
			} else {
				return ArgumentPreference.BLOCKING_DEFEAT;
			}
		}
	}

}
