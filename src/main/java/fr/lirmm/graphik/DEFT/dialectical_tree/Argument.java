package fr.lirmm.graphik.DEFT.dialectical_tree;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import fr.lirmm.graphik.DEFT.core.Preference;
import fr.lirmm.graphik.DEFT.core.PreferenceSet;
import fr.lirmm.graphik.DEFT.gad.Derivation;
import fr.lirmm.graphik.DEFT.gad.GADEdge;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Rule;

public class Argument {
	public Derivation support;
	public Atom conclusion;
	
	public HashSet<Preference> rightPreferences;
	public HashSet<Preference> leftPreferences;
	
	public Argument(Derivation d, Atom conclusion) {
		this.support = d;
		this.conclusion = conclusion;
		this.leftPreferences = new HashSet<Preference>();
		this.rightPreferences = new HashSet<Preference>();
	}
	
	public Argument(Derivation d, Atom conclusion, PreferenceSet preferenceSet) {
		this(d, conclusion);
		if(null == preferenceSet || preferenceSet.preferenceSet.size() == 0) return;
		
		Iterator<GADEdge> it = d.iterator();
		while(it.hasNext()) {
			Rule rule = it.next().getRule();
			if(null != rule) {
				// Adding left side preferences
				LinkedList<Preference> leftSidePrefs = preferenceSet.leftPreferenceHash.get(rule.getLabel());
				
				if(null != leftSidePrefs) {
					Iterator<Preference> leftSideIt = leftSidePrefs.iterator();
					while(leftSideIt.hasNext()) {
						this.leftPreferences.add(leftSideIt.next());
					}
				}
				
				// Adding right side preferences
				LinkedList<Preference> rightSidePrefs = preferenceSet.rightPreferenceHash.get(rule.getLabel());
				
				if(null != rightSidePrefs) {
					Iterator<Preference> rightSideIt = rightSidePrefs.iterator();
					while(rightSideIt.hasNext()) {
						this.rightPreferences.add(rightSideIt.next());
					}
				}
				
			}
		}

	}
	
	@Override
	public String toString() {
		return conclusion.toString() + ": " + support.toString();
	}
}
