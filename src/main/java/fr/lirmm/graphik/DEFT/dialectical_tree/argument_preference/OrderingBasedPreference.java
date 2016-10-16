package fr.lirmm.graphik.DEFT.dialectical_tree.argument_preference;

import fr.lirmm.graphik.DEFT.dialectical_tree.Argument;

public class OrderingBasedPreference implements ArgumentPreference {
	private ArgumentPreference fallbackPreference;
	
	/**
	 * Constructor, creates an OrderingBasedPreference object and indicates what ArgumentPreference to use in undecided cases.
	 */
	public OrderingBasedPreference(ArgumentPreference argPreference) {
		this.fallbackPreference = argPreference;
	}
	
	/**
	 * Constructor, creates an OrderingBasedPreference object and uses SimpleArgumentPreference in undecided cases.
	 */
	public OrderingBasedPreference() {
		this(new SimpleArgumentPreference());
	}
	
	@Override
	public int compare(Argument attacker, Argument attackee) {
		
		return 0;
	}

}
