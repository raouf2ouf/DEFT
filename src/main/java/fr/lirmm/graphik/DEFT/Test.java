package fr.lirmm.graphik.DEFT;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public  class Test {

		public static void main(String[] args) {
			String label = "[tefd]";
			boolean isDefeasible = false;
			label = label.toLowerCase();
			Pattern pattern = Pattern.compile("\\[deft\\]");
			Matcher matcher = pattern.matcher(label);
			if (matcher.find()) {
			    isDefeasible = true;
			}
			System.out.println(isDefeasible);
		}
}
