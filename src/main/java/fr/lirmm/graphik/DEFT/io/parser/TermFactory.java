package fr.lirmm.graphik.DEFT.io.parser;

/**
 * A factory for creating terms (IRI, Variable, Literal)
 */
public interface TermFactory {
	
	/**
	 * Creates a new Term object.
	 *
	 * @param s the s
	 * @return the object
	 */
	public Object createIRI(String s);
	
	/**
	 * Creates a new Term object.
	 *
	 * @param dt the dt
	 * @param stringValue the string value
	 * @param langTag the lang tag
	 * @return the object
	 */
	public Object createLiteral(Object datatype,String stringValue,String langTag);
	
	/**
	 * Creates a new Term object.
	 *
	 * @param stringValue the string value
	 * @return the object
	 */
	public Object createVariable(String stringValue);
	
}