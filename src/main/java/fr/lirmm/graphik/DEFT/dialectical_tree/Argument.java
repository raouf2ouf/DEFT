package fr.lirmm.graphik.DEFT.dialectical_tree;

import fr.lirmm.graphik.DEFT.gad.Derivation;
import fr.lirmm.graphik.graal.api.core.Atom;

public class Argument {
	public Derivation support;
	public Atom conclusion;
	
	public Argument(Derivation d, Atom conclusion) {
		this.support = d;
		this.conclusion = conclusion;
	}
	
	@Override
	public String toString() {
		return conclusion.toString() + ": " + support.toString();
	}
}
