package fr.lirmm.graphik.DEFT.gad;

import java.util.Iterator;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;

public class GADEdge {
	private Rule rule;
	private Substitution substitution;
	
	private AtomSet from;
	private Atom to;
	
	/* --------------------------------
	 * Constructors
	 * -------------------------------- */
	
	public GADEdge(AtomSet from, Atom to, Rule rule, Substitution substitution) {
		this.rule = rule;
		this.substitution = substitution;
		this.from = from;
		this.to = to;
	}
	
	/* --------------------------------
	 * Public Methods
	 * -------------------------------- */
	
	public Atom getTarget() {
		return this.to;
	}
	
	public Rule getRule() {
		return this.rule;
	}
	
	public Substitution getSubstitution() {
		return this.substitution;
	}

	public AtomSet getSources() {
		return this.from;
	}
	/*
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(this.getTarget());
		if(null != this.getSources()) {
			s.append(" <-- ");
			for(Atom atom : this.getSources()) {
				s.append(atom);
			}
			s.append(" \t Rule: ");
			s.append(this.getRule());
			
			s.append("; ");
			s.append(this.getSubstitution());
		}
		s.append('\n');
		return s.toString();
	}*/
	
	public String toString() {
		StringBuilder s = new StringBuilder();
		//s.append(this.getTarget());
		if(null != this.getSources()) {
			s.append(" <-- ");
			s.append("(");
			Iterator<Atom> it = this.getSources().iterator();
			if(it.hasNext()) {
				Atom a = it.next();
				s.append(a.getPredicate().getIdentifier());
				s.append(a.getTerms());
			}
			
			while(it.hasNext()) {
				s.append(" , ");
				Atom a = it.next();
				s.append(a.getPredicate().getIdentifier());
				s.append(a.getTerms());
			}
		}
		s.append(")");
		return s.toString();
	}
}
