package fr.lirmm.graphik.DEFT.core;

public class Preference {
	private String prefLabel1;
	private String prefLabel2;
	
	public Preference(String prefLabel1, String prefLabel2) {
		this.prefLabel1 = prefLabel1;
		this.prefLabel2 = prefLabel2;
	}
	
	public String getPrefLabel1() {
		return prefLabel1;
	}
	
	public String getPrefLabel2() {
		return prefLabel2;
	}
}
