package fr.lirmm.graphik.DEFT.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.DEFT.core.Preference;
import fr.lirmm.graphik.DEFT.io.parser.DLGP2Parser;
import fr.lirmm.graphik.DEFT.io.parser.ParseException;
import fr.lirmm.graphik.DEFT.io.parser.TermFactory;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.KnowledgeBase;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.VariableGenerator;
import fr.lirmm.graphik.graal.api.io.ParseError;
import fr.lirmm.graphik.graal.api.io.Parser;
import fr.lirmm.graphik.graal.core.DefaultNegativeConstraint;
import fr.lirmm.graphik.graal.core.DefaultVariableGenerator;
import fr.lirmm.graphik.graal.core.FreshVarSubstitution;
import fr.lirmm.graphik.graal.core.stream.filter.AtomFilterIterator;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.io.dlp.Directive;
import fr.lirmm.graphik.util.DefaultURI;
import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.URI;
import fr.lirmm.graphik.util.stream.AbstractCloseableIterator;
import fr.lirmm.graphik.util.stream.ArrayBlockingStream;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

/**
 * This parser is Exactly the same as DlgpParser except it uses 
 * AbstractDlgpDEFTListener rather than AbstractDlgpListener.
 * 
 * @author Abdelraouf Hecham (INRIA) <hecham.abdelraouf@gmail.com>
 */
public final class DlgpDEFTParser extends AbstractCloseableIterator<Object> implements Parser<Object> {
	private static final Logger LOGGER = LoggerFactory.getLogger(DlgpDEFTParser.class);

	private static VariableGenerator freeVarGen = new DefaultVariableGenerator("I");

	private ArrayBlockingStream<Object> buffer = new ArrayBlockingStream<Object>(
			512);

	private static class DlgpListener extends AbstractDlgpDEFTListener {

		private ArrayBlockingStream<Object> set;
		private Collection<Object> setNonBlock;
		
		DlgpListener(ArrayBlockingStream<Object> buffer) {
			this.set = buffer;
		}
		
		DlgpListener(Collection<Object> buffer) {
			this.setNonBlock = buffer;
		}

		@Override
		protected void createAtomSet(InMemoryAtomSet atomset) {
			FreshVarSubstitution s = new FreshVarSubstitution(freeVarGen);
			CloseableIteratorWithoutException<Atom> it = atomset.iterator();
			while (it.hasNext()) {
				Atom a = it.next();
				int i = 0;
				for (Term term : a.getTerms()) {
					a.setTerm(i, s.createImageOf(term));
					i++;
				}
				this.write(a);
			}
		}

		@Override
		protected void createQuery(ConjunctiveQuery query) {
			this.write(query);
		}

		@Override
		protected void createRule(Rule rule) {
			this.write(rule);
		}

		@Override
		protected void createNegConstraint(DefaultNegativeConstraint negativeConstraint) {
			this.write(negativeConstraint);
		}
		
		@Override
		protected void createPreference(Preference preference) {
			this.write(preference);
		}
		//@Override
		public void declarePrefix(String prefix, String ns) {
			this.write(new Prefix(prefix.substring(0, prefix.length() - 1),
					ns));
		}

		//@Override
		public void declareBase(String base) {
			this.write(new Directive(Directive.Type.BASE, base));
		}

		//@Override
		public void declareTop(String top) {
			this.write(new Directive(Directive.Type.TOP, top));
		}

		//@Override
		public void declareUNA() {
			this.write(new Directive(Directive.Type.UNA, ""));
		}

		//@Override
		public void directive(String text) {
			this.write(new Directive(Directive.Type.COMMENT, text));
		}
		
		private void write(Object object) {
			if(this.setNonBlock == null) {
				this.set.write(object);
			} else {
				this.setNonBlock.add(object);
			}
		}
	};
	
	private static class InternalTermFactory implements TermFactory {

		//@Override
		public Object createIRI(String s) {
			if (s.indexOf(':') == -1) {
				return s;
			}
			return new DefaultURI(s);
		}

		//@Override
		public Object createLiteral(Object datatype, String stringValue, String langTag) {
			if (langTag != null) {
				stringValue += "@" + langTag;
			}
			return DefaultTermFactory.instance().createLiteral((URI) datatype,
					stringValue);
		}

		//@Override
		public Object createVariable(String stringValue) {
			return DefaultTermFactory.instance().createVariable(stringValue);
		}
	}

	private static class Producer implements Runnable {

		private Reader reader;
		private ArrayBlockingStream<Object> bufferBlock;
		private Collection<Object> bufferNonBlock;
		
		Producer(Reader reader, ArrayBlockingStream<Object> buffer) {
			this.reader = reader;
			this.bufferBlock = buffer;
		}

		Producer(Reader reader, Collection<Object> buffer) {
			this.reader = reader;
			this.bufferNonBlock = buffer;
		}
		
		//@Override
		public void run() {
			DLGP2Parser parser = new DLGP2Parser(new InternalTermFactory(), reader);
			if(this.bufferBlock != null) {
				parser.addParserListener(new DlgpListener(bufferBlock));
			} else {
				parser.addParserListener(new DlgpListener(bufferNonBlock));
			}
			parser.setDefaultBase("");

			try {
				parser.document();
			} catch (ParseException e) {
				throw new ParseError("An error occured while parsing", e);
			} finally {
				if(this.bufferBlock != null) {
					bufferBlock.close();
				}
			}
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////
	
	private Reader reader = null;

	private UncaughtExceptionHandler exceptionHandler;

	/**
	 * Constructor for parsing from the given reader.
	 * @param reader
	 */
	public DlgpDEFTParser(Reader reader) {
		this(reader, null);
	}
	
	/**
	 * Constructor for parsing from the standard input.
	 */
	public DlgpDEFTParser() {
		this(new InputStreamReader(System.in));
	}
	
	/**
	 * Constructor for parsing from the given file.
	 * @param file
	 * @throws FileNotFoundException
	 */
	public DlgpDEFTParser(File file) throws FileNotFoundException {
		this(new FileReader(file));
	}

	/**
	 * Constructor for parsing the content of the string s as DLGP content.
	 * @param s
	 */
	public DlgpDEFTParser(String s) {
		this(new StringReader(s));
	}
	
	/**
	 * Constructor for parsing the given InputStream.
	 * @param in
	 */
	public DlgpDEFTParser(InputStream in) {
		this(new InputStreamReader(in));
	}
	
	/**
	 * Constructor for parsing from the given reader.
	 * 
	 * @param reader
	 */
	public DlgpDEFTParser(Reader reader, Thread.UncaughtExceptionHandler exceptionHandler) {
		this.reader = reader;
		Thread t = new Thread(new Producer(reader, buffer));
		t.setUncaughtExceptionHandler(exceptionHandler);
		t.start();
	}
	

	/**
	 * Constructor for parsing from the given file.
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 */
	public DlgpDEFTParser(File file, Thread.UncaughtExceptionHandler exceptionHandler) throws FileNotFoundException {
		this(new FileReader(file), exceptionHandler);
	}

	/**
	 * Constructor for parsing the content of the string s as DLGP content.
	 * 
	 * @param s
	 */
	public DlgpDEFTParser(String s, Thread.UncaughtExceptionHandler exceptionHandler) {
		this(new StringReader(s), exceptionHandler);
	}

	/**
	 * Constructor for parsing the given InputStream.
	 * 
	 * @param in
	 */
	public DlgpDEFTParser(InputStream in, Thread.UncaughtExceptionHandler exceptionHandler) {
		this(new InputStreamReader(in), exceptionHandler);
	}

	@Override
	protected void finalize() throws Throwable {
		this.close();
		super.finalize();
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	//@Override
	public boolean hasNext() {
		return buffer.hasNext();
	}

	//@Override
	public Object next() {
		return buffer.next();
	}
	
	/**
	 * Closes the stream and releases any system resources associated with it.
	 * Closing a previously closed parser has no effect.
	 * 
	 * @throws IOException
	 */
	//@Override
	public void close() {
		if(this.reader != null) {
			try {
				this.reader.close();
			} catch (IOException e) {
				LOGGER.error("Error during closing reader", e);
			}
			this.reader = null;
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// STATIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	public static ConjunctiveQuery parseQuery(String s) {
		LinkedList<Object> buffer = new LinkedList<Object>();
		new Producer(new StringReader(s), buffer).run();
		return (ConjunctiveQuery) buffer.getFirst();
	}

	public static Atom parseAtom(String s) {
		LinkedList<Object> buffer = new LinkedList<Object>();
		new Producer(new StringReader(s), buffer).run();
		return (Atom) buffer.getFirst();
	}
	
	public static CloseableIterator<Atom> parseAtomSet(String s) {
		return new AtomFilterIterator(new DlgpDEFTParser(s));
	}
	
	public static Rule parseRule(String s) {
		LinkedList<Object> buffer = new LinkedList<Object>();
		new Producer(new StringReader(s), buffer).run();
		return (Rule) buffer.getFirst();
	}
	
	public static DefaultNegativeConstraint parseNegativeConstraint(String s) {
		LinkedList<Object> buffer = new LinkedList<Object>();
		new Producer(new StringReader(s), buffer).run();
		return (DefaultNegativeConstraint) buffer.getFirst();
	}
	
	public static Preference parsePreference(String s) {
		LinkedList<Object> buffer = new LinkedList<Object>();
		new Producer(new StringReader(s), buffer).run();
		return (Preference) buffer.getFirst();
	}
	/**
	 * Parse a DLP content and store data into the KnowledgeBase target.
	 * 
	 * @param src
	 * @param target
	 * @throws AtomSetException 
	 */
	public static void parseKnowledgeBase(Reader src, KnowledgeBase target) throws AtomSetException {
		DlgpDEFTParser parser = new DlgpDEFTParser(src);
		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof Rule) {
				target.getOntology().add((Rule) o);
			} else if (o instanceof Atom) {
				target.getFacts().add((Atom) o);
			}
		}
		parser.close();
	}
}
