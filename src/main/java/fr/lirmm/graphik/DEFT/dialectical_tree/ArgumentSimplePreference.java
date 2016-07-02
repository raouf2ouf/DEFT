package fr.lirmm.graphik.DEFT.dialectical_tree;

public class ArgumentSimplePreference implements ArgumentPreference {

	public int compare(Argument attacker, Argument attackee) {
		if(!attackee.support.isDefeasible()) {
			return ArgumentPreference.NOT_DEFEAT;
		}
		
		if(attacker.support.isDefeasible()) {
			return ArgumentPreference.BLOCKING_DEFEAT;
		} else {
			return ArgumentPreference.PROPER_DEFEAT;
		}
	}

}
