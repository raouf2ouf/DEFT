package fr.lirmm.graphik.DEFT.dialectical_tree;

public class ArgumentSimplePreference implements ArgumentPreference {

	public int compare(Argument attacker, Argument attackee) {
		// if the argument being attacked is strict then it is undefeatable.
		if(!attackee.support.isDefeasible()) {
			return ArgumentPreference.NOT_DEFEAT;
		}
		
		// else, the argument being attacked is defeasible
		if(attacker.support.isDefeasible()) {
			return ArgumentPreference.BLOCKING_DEFEAT;
		} else {
			return ArgumentPreference.PROPER_DEFEAT;
		}
	}

}
