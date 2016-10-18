package fr.lirmm.graphik.DEFT.dialectical_tree.argument_preference;

import org.apache.commons.collections4.SetUtils;
import org.apache.commons.collections4.SetUtils.SetView;

import fr.lirmm.graphik.DEFT.core.Preference;
import fr.lirmm.graphik.DEFT.dialectical_tree.Argument;

public class OrderingBasedArgumentPreference implements ArgumentPreference {
	private ArgumentPreference fallbackPreference;
	
	/**
	 * Constructor, creates an OrderingBasedPreference object and indicates what ArgumentPreference to use in undecided cases.
	 */
	public OrderingBasedArgumentPreference(ArgumentPreference argPreference) {
		this.fallbackPreference = argPreference;
	}
	
	/**
	 * Constructor, creates an OrderingBasedPreference object and uses SimpleArgumentPreference in undecided cases.
	 */
	public OrderingBasedArgumentPreference() {
		this(new SimpleArgumentPreference());
	}
	
	@Override
	public int compare(Argument attacker, Argument attackee) {
		boolean attackerPreferred = false;
		boolean attackeePreferred = false;
		
		// I- get prefs where attacker > attackee
		// get the preference set of the intersection of the preferences used in both argument
		SetView<Preference> intersection = SetUtils.intersection(attacker.leftPreferences, attackee.rightPreferences);
		
		if(intersection.size() > 0) {
			attackerPreferred = true;
		}
		// II- get prefs where attacker < attakee
		// get the preference set of the intersection of the preferences used in both argument
		intersection = SetUtils.intersection(attackee.leftPreferences, attacker.rightPreferences);
		
		if(intersection.size() > 0) {
			attackeePreferred = true;
		}
		
		if(attackerPreferred == attackeePreferred) {
			return this.fallbackPreference.compare(attacker, attackee);
		} else if(attackerPreferred){
			return ArgumentPreference.PROPER_DEFEAT;
		} else {
			return ArgumentPreference.NOT_DEFEAT;
		}
	}

}
