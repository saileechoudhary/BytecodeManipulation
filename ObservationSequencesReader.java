

import java.util.*;
import java.io.*;

import be.ac.ulg.montefiore.run.jahmm.*;
import be.ac.ulg.montefiore.run.jahmm.io.FileFormatException;
import be.ac.ulg.montefiore.run.jahmm.io.ObservationReader;


/**
 * This class can read observations sequences from file.
 * &lt;p>
 * The file format has been chosen to be very simple:
 * &lt;ul>
 * &lt;li> a line per observation sequence, in pure 7 bits ASCII;&lt;/li>
 * &lt;li> empty (white) lines, space and tab characters are not significant;&lt;/li>
 * &lt;li> each observation is followed by a semi-colon
 *      (&lt;i>i.e.&lt;/i> the line ends with a semi-colon);&lt;/li>
 * &lt;li> The '#' character introduce a comment; the rest of the line is
 *      skipped; &lt;/li>
 * &lt;li> A newline can be escaped using the '\' character; this character can't
 *      be used in any other context;&lt;/li>
 * &lt;li> the format of each observation is defined by the corresponding
 *      IO class.&lt;/li>
 * &lt;/ul>
 * &lt;p>
 * Those rules must be followed by {@link ObservationReader ObservationReader} 
 * subclasses.
 */
public class ObservationSequencesReader {
    
    /**
     * Reads observation sequences file.  Such a file holds a set of observation
     * sequences.
     *
     * @param oir An observation reader.
     * @param reader Holds the character stream reader the sequences are read 
     *               from.
     * @return A {@link java.util.Vector Vector} of 
     *         {@link java.util.Vector Vector}s of
     *         {@link be.ac.ulg.montefiore.run.jahmm.Observation Observation}s.
     */
    static public Vector readSequences(ObservationReader oir, 
				       Reader reader) throws IOException, FileFormatException {
	Vector sequences = new Vector();
	StreamTokenizer st = new StreamTokenizer(reader);
	
	initSyntaxTable(st);

	for (st.nextToken(); st.ttype != StreamTokenizer.TT_EOF; 
	     st.nextToken()) {
	    st.pushBack();
	    Vector sequence = readSequence(oir, st);
	    
	    if (sequence == null)
		break;
	    
	    sequences.add(sequence);
	}
	
	return sequences;
    }


    /* Initialize the syntax table of a stream tokenizer */
    static void initSyntaxTable(StreamTokenizer st) {
	st.resetSyntax();
	st.parseNumbers();
	st.whitespaceChars((int) ' ', (int) ' ');
	st.whitespaceChars((int) '\t', (int) '\t');
	st.eolIsSignificant(true);
	st.commentChar((int) '#');
    }


    /**
     * Reads an observation sequence out of a file {@link java.io.Reader
     * Reader}. 
     *
     * @param oir An observation reader.
     * @param reader Holds the character reader the sequences are read from.
     * @return An observation sequence read from &lt;code>st&lt;/code> or null if the
     *         end of the file is reached before any sequence is found.
     */
    static public Vector readSequence(ObservationReader oir, 
				      Reader reader) 
	throws IOException, FileFormatException {

	StreamTokenizer st = new StreamTokenizer(reader);
	initSyntaxTable(st);
	
	return readSequence(oir, st);
    }

        
    /*
     * Reads an observation sequence out of a {@link java.io.StreamTokenizer
     * StreamTokenizer}.  Empty lines or comments can appear before the
     * sequence itself. &lt;code>st&lt;/code>'s syntax table must be properly
     * initialized.
     */
    static Vector readSequence(ObservationReader oir, StreamTokenizer st) 
	throws IOException, FileFormatException {

	for (st.nextToken(); st.ttype == StreamTokenizer.TT_EOL;
	     st.nextToken());
	if (st.ttype == StreamTokenizer.TT_EOF)
	    return null;
	
	Vector sequence = new Vector();
	
	do {
	    st.pushBack();
	    sequence.add(oir.read(st));
	    
	    if (st.nextToken() == '\\') { /* New lines can be escaped by '\' */
		if (st.nextToken() != StreamTokenizer.TT_EOL)
		    throw new FileFormatException("'\' token is not followed " +
						  "by a new line");
		st.nextToken();
	    }
	} while (st.ttype != StreamTokenizer.TT_EOL &&
		 st.ttype != StreamTokenizer.TT_EOF);
	
	if (st.ttype == StreamTokenizer.TT_EOF)
	   throw new FileFormatException("Unexpected token: EOF"); 
	
	return sequence;
    }
}
