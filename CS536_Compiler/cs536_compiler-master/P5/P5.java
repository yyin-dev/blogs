import java.io.*;

import java_cup.runtime.*;

/**
 * Main program to test the parser.
 *
 * There should be 2 command-line arguments: 1. the file to be parsed 2. the
 * output file into which the AST built by the parser should be unparsed The
 * program opens the two files, creates a scanner and a parser, and calls the
 * parser. If the parse is successful, the AST is unparsed.
 */
public class P5 {
	FileReader inFile;
	private PrintWriter outFile;
	private static PrintStream outStream = System.err;

	public static final int RESULT_CORRECT = 0;
	public static final int RESULT_SYNTAX_ERROR = 1;
	public static final int RESULT_NAME_ANALYSIS_ERROR = 2;
	public static final int RESULT_TYPE_ERROR = 3;
	public static final int RESULT_OTHER_ERROR = -1;

	/**
	 * P5 constructor for client programs and testers. Note that users MUST invoke
	 * {@link setInfile} and {@link setOutfile}
	 */
	public P5() {
	}

	/**
	 * If we are directly invoking P5 from the command line, this is the command
	 * line to use. It shouldn't be invoked from outside the class (hence the
	 * private constructor) because it
	 * 
	 * @param args command line args array for [<infile> <outfile>]
	 */
	private P5(String[] args) {
		// Parse arguments
		if (args.length < 2) {
			String msg = "please supply name of file to be parsed" + "and name of file for unparsed version.";
			pukeAndDie(msg);
		}

		try {
			setInfile(args[0]);
			setOutfile(args[1]);
		} catch (BadInfileException e) {
			pukeAndDie(e.getMessage());
		} catch (BadOutfileException e) {
			pukeAndDie(e.getMessage());
		}
	}

	/**
	 * Source code file path
	 * 
	 * @param filename path to source file
	 */
	public void setInfile(String filename) throws BadInfileException {
		try {
			inFile = new FileReader(filename);
		} catch (FileNotFoundException ex) {
			throw new BadInfileException(ex, filename);
		}
	}

	/**
	 * Text file output
	 * 
	 * @param filename path to destination file
	 */
	public void setOutfile(String filename) throws BadOutfileException {
		try {
			outFile = new PrintWriter(filename);
		} catch (FileNotFoundException ex) {
			throw new BadOutfileException(ex, filename);
		}
	}

	/**
	 * Perform cleanup at the end of parsing. This should be called after both good
	 * and bad input so that the files are all in a consistent state
	 */
	public void cleanup() {
		if (inFile != null) {
			try {
				inFile.close();
			} catch (IOException e) {
				// At this point, users already know they screwed
				// up. No need to rub it in.
			}
		}
		if (outFile != null) {
			// If there is any output that needs to be
			// written to the stream, force it out.
			outFile.flush();
			outFile.close();
		}
	}

	/**
	 * Private error handling method. Convenience method for
	 * 
	 * @link pukeAndDie(String, int) with a default error code
	 * @param error message to print on exit
	 */
	private void pukeAndDie(String error) {
		pukeAndDie(error, -1);
	}

	/**
	 * Private error handling method. Prints an error message
	 * 
	 * @link pukeAndDie(String, int) with a default error code
	 * @param error message to print on exit
	 */
	private void pukeAndDie(String error, int retCode) {
		outStream.println(error);
		cleanup();
		System.exit(-1);
	}

	/**
	 * the parser will return a Symbol whose value field is the translation of the
	 * root nonterminal (i.e., of the nonterminal "program")
	 * 
	 * @return root of the CFG
	 */
	private Symbol parseCFG() {
		try {
			parser P = new parser(new Yylex(inFile));
			return P.parse();
		} catch (Exception e) {
			return null;
		}
	}

	public int process() {
		Symbol cfgRoot = parseCFG();

		ProgramNode astRoot = (ProgramNode) cfgRoot.value;
		if (ErrMsg.getErr()) {
			return P5.RESULT_SYNTAX_ERROR;
		}

		astRoot.nameAnalysis(); // perform name analysis
		if (ErrMsg.getErr()) {
			return P5.RESULT_NAME_ANALYSIS_ERROR;
		}

		astRoot.typeCheck();
		if (ErrMsg.getErr()) {
			return P5.RESULT_TYPE_ERROR;
		}

		astRoot.unparse(outFile, 0);
		return P5.RESULT_CORRECT;
	}

	public void run() {
		int resultCode = process();
		if (resultCode == RESULT_CORRECT) {
			cleanup();
			return;
		}

		switch (resultCode) {
			case RESULT_SYNTAX_ERROR:
				pukeAndDie("Syntax error", resultCode);
			case RESULT_NAME_ANALYSIS_ERROR:
				pukeAndDie("Name analysis error", resultCode);
			case RESULT_TYPE_ERROR:
				pukeAndDie("Type checking error", resultCode);
			default:
				pukeAndDie("Type checking error", RESULT_OTHER_ERROR);
		}
	}

	private class BadInfileException extends Exception {
		private static final long serialVersionUID = 1L;
		private String message;

		public BadInfileException(Exception cause, String filename) {
			super(cause);
			this.message = "Could not open " + filename + " for reading";
		}

		@Override
		public String getMessage() {
			return message;
		}
	}

	private class BadOutfileException extends Exception {
		private static final long serialVersionUID = 1L;
		private String message;

		public BadOutfileException(Exception cause, String filename) {
			super(cause);
			this.message = "Could not open " + filename + " for reading";
		}

		@Override
		public String getMessage() {
			return message;
		}
	}

	public static void main(String[] args) {
		P5 instance = new P5(args);
		instance.run();
	}
}
