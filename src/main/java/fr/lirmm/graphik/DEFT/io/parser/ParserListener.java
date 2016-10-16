package fr.lirmm.graphik.DEFT.io.parser;

import java.util.EventListener;

/**
 * The listener interface for receiving parser events.
 * The class that is interested in processing a parser
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addParserListener<code> method. When
 * the parser event occurs, that object's appropriate
 * method is invoked.
 *
 * @see ParserEvent
 */
public interface ParserListener extends EventListener {

	public static enum OBJECT_TYPE {
		UNKNOWN, FACT, RULE, QUERY, NEG_CONSTRAINT, PREFERENCE
	}

	/**
	 * Invoked when parser starts a new object definition
	 * 
	 * @param objectType
	 *            the type of new object
	 * @param name
	 *            the name of the object (null if anonymous)
	 */
	public void startsObject(OBJECT_TYPE objectType, String name);
	
	/**
	 * Declare prefix associated to a NameSpace.
	 *
	 * @param prefix the prefix
	 * @param uri the uri
	 */
	public void declarePrefix(String prefix,String ns);
	
	/**
	 * Declare the base to complete relative IRI.
	 *
	 * @param base the base uri
	 */
	public void declareBase(String base);
	
	/** 
	 * Declare the top type
	 * @param top
	 */
	public void declareTop(String top);
	
	/**
	 *  Invoked when @UNA annotation occurs. 
	 *  According to Unique Name Assumption, different names always refer to different entities. 
	 */
	public void declareUNA();
	/**
	 * Invoked when parser find %% directive
	 *
	 * @param text the text
	 */
	public void directive(String text);
	/**
	 * Invoked when a new Atom is found
	 * 
	 * @param predicate
	 *            the predicate
	 * @param terms
	 *            the terms
	 */
	public void createsAtom(Object predicate, Object[] terms);

	/**
	 * Invoked when a new equality Atom is found
	 * 
	 * @param term1
	 *            the term1
	 * @param term2
	 *            the term2
	 */
	public void createsEquality(Object term1, Object term2);

	/**
	 * Invoked when the Answer term list of the query is found
	 * 
	 * @param terms
	 *            the terms
	 */
	public void answerTermList(Object[] terms);

	/**
	 * Invoked when a conjunction of atoms is ending. It is called once for every
	 * type of objects except Rules that include two atom lists. 
	 * IMPORTANT NOTE:
	 * The parameter provided here is normally sent previously to the
	 * startsObject method. But for the facts and rules, the type sent may be
	 * different if the @Annotations are wrong. The method startsObject receives
	 * the expected type and this method gets the actual type of the current
	 * object.
	 * 
	 * @param objectType
	 *            the type of the current object
	 */
	public void endsConjunction(OBJECT_TYPE objectType);
	
	/**
	 * Invoked when a Preference is found
	 * 
	 * @param label1
	 *            the label of the first rule
	 * @param label2
	 *            the label of the second rule
	 */
	public void createsPreference(String label1, String label2);

}