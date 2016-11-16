package fr.lirmm.graphik.DEFT.gad;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.DEFT.core.DefeasibleAtom;
import fr.lirmm.graphik.DEFT.core.DefeasibleRule;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.core.atomset.graph.DefaultInMemoryGraphAtomSet;

public class CompactDerivation {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Derivation.class);
	
	private Atom forAtom;
	private AtomSet branchingAtoms;
	private AtomSet nonBranchingAtoms;
	private boolean isDefeasible;
	
	public CompactDerivation(Atom a) {
		this.forAtom = a;
		this.branchingAtoms = new DefaultInMemoryGraphAtomSet();
		this.nonBranchingAtoms = new DefaultInMemoryGraphAtomSet();
		this.isDefeasible = false;
	}
	
	public void addBranchingAtom(Atom a) throws AtomSetException {
		this.branchingAtoms.add(a);
	}
	
	public void addNonBranchingAtom(Atom a) throws AtomSetException {
		this.nonBranchingAtoms.add(a);
	}
	
	public AtomSet getBranchingAtoms() {
		return this.branchingAtoms;
	}
	
	public AtomSet getNonBranchingAtoms() {
		return this.nonBranchingAtoms;
	}
	
	public Atom getFor() {
		return this.forAtom;
	}
	
	public String toString() {
		String str = "Branching: ";
	/*	for(Atom a : this.branchingAtoms) {
			str += " " + a.toString() + " ";
		}
		str += ", NonBrancing ";
		for(Atom a : this.nonBranchingAtoms) {
			str += " " + a.toString() + " ";
		} */
		str += " ; defeasible: " + this.isDefeasible;
		return str;
	}
	
	public void setIsDefeasible(boolean bool) {
		this.isDefeasible = bool;
	}
	
	public boolean isDefeasible() {
		return this.isDefeasible;
	}
}
