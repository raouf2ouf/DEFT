package fr.lirmm.graphik.DEFT.core;

import fr.lirmm.graphik.graal.api.core.Rule;

public class Preference {
	private String leftSideLabel;
	private String rightSideLabel;
	private Rule leftSideRule;
	private Rule rightSideRule;
	
	public Preference(String leftSideLabel, String rightSideLabel) {
		this.leftSideLabel = leftSideLabel;
		this.rightSideLabel = rightSideLabel;
	}
	
	public Preference(Rule leftSideRule, Rule rightSideRule) {
		this.leftSideRule = leftSideRule;
		this.leftSideLabel = this.leftSideRule.getLabel();
		this.rightSideRule = rightSideRule;
		this.rightSideLabel = this.rightSideRule.getLabel();
	}
	
	public String getLeftSide() {
		return leftSideLabel;
	}
	
	public String getRightSide() {
		return rightSideLabel;
	}
	
	public Rule getLeftSideRule() {
		return this.leftSideRule;
	}
	
	public Rule getRightSideRule() {
		return this.rightSideRule;
	}
	
	public String toString() {
		return "[" + this.getLeftSide() + "]" + " > " + "[" + this.getRightSide() + "].";
	}
}
