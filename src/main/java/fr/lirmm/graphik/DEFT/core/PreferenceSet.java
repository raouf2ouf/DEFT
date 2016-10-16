package fr.lirmm.graphik.DEFT.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class PreferenceSet {
	public HashMap<String, LinkedList<Preference>> leftPreferenceHash;
	public HashMap<String, LinkedList<Preference>> rightPreferenceHash;
	public LinkedList<Preference> preferenceSet;
	
	public PreferenceSet() {
		this.leftPreferenceHash = new HashMap<String, LinkedList<Preference>>();
		this.rightPreferenceHash = new HashMap<String, LinkedList<Preference>>();
		this.preferenceSet = new LinkedList<Preference>();
	}
	
	public void add(Preference preference) {
		this.preferenceSet.add(preference);
		LinkedList<Preference> preferenceList = new LinkedList<Preference>();
		preferenceList.add(preference);
		this.leftPreferenceHash.put(preference.getLeftSide(), preferenceList);
		
		preferenceList = new LinkedList<Preference>();
		preferenceList.add(preference);
		this.rightPreferenceHash.put(preference.getRightSide(), preferenceList);
	}
	
	public Iterator<Preference> iterator() {
		return this.preferenceSet.iterator();
	}
}
