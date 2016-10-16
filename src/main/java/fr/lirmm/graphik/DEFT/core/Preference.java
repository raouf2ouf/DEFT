package fr.lirmm.graphik.DEFT.core;

public class Preference {
	private String leftSideLabel;
	private String rightSideLabel;
	
	public Preference(String leftSideLabel, String rightSideLabel) {
		this.leftSideLabel = leftSideLabel;
		this.rightSideLabel = rightSideLabel;
	}
	
	public String getLeftSide() {
		return leftSideLabel;
	}
	
	public String getRightSide() {
		return rightSideLabel;
	}
	
	
	public String toString() {
		return "[" + this.getLeftSide() + "]" + " > " + "[" + this.getRightSide() + "].";
	}
}
