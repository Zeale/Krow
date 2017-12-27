package kröw.math.lexer;

import kröw.math.lexer.exceptions.DuplicateDecimalException;
import kröw.math.lexer.exceptions.IllegalCloseWrapperPlacementException;
import kröw.math.lexer.exceptions.LexerInUseException;
import kröw.math.lexer.exceptions.MisplacedOperatorException;
import kröw.math.lexer.exceptions.MisplacedWrapperException;
import kröw.math.lexer.exceptions.UnmatchedWrapperException;

/**
 * <p>
 * Quick note that all "parse..." methods will increment {@link #position} to
 * the character after they are done parsing. If they find a whitespace
 * character, which will end their parsing spree, they will swallow that
 * whitespace character too.
 * <p>
 * A whitespace character signifies that a parse method (either
 * {@link #parseTerm()} or {@link #parseBinaryOperator()}) should stop parsing
 * because it has reached the next element of the equation. For example:
 * 
 * <pre>
 * <code>1 + 72 +    316</code>
 * </pre>
 * 
 * <p>
 * Although the above mathematical sequence is awkwardly spaced, it is
 * syntactically valid. The spaces force an element to end. So if there was a
 * space between the <code>3</code> and <code>16</code> in the above sequence,
 * like so:
 * 
 * <pre>
 * <code>1 + 72 +    3 16</code>
 * </pre>
 * 
 * <p>
 * then this {@link EquationParser} is expected to find the following elements:
 * <br>
 * <br>
 * 
 * <table cellpadding="1" cellspacing="0" border="1">
 * <tr>
 * <th>Parsed Token</th>
 * <th>Value</th>
 * </tr>
 * <tr>
 * <td>NUMBER</td>
 * <td>1</td>
 * </tr>
 * <tr>
 * <td>OPERATOR</td>
 * <td>+</td>
 * </tr>
 * <tr>
 * <td>NUMBER</td>
 * <td>72</td>
 * </tr>
 * <tr>
 * <td>OPERATOR</td>
 * <td>+</td>
 * </tr>
 * <tr>
 * <td><strong>NUMBER</strong></td>
 * <td>3</td>
 * </tr>
 * <tr>
 * <td><strong>NUMBER</strong></td>
 * <td>16</td>
 * </tr>
 * </table>
 * <br>
 * <p>
 * This {@link EquationParser} should not parse two numbers separated solely by
 * whitespace.
 * 
 * @author Zeale
 *
 */
public class EquationParser {

	public static final EquationParser getDebuggingParser() {
		EquationParser parser = new EquationParser();
		parser.debug = true;
		return parser;
	}

	private EquationParser(EquationParser parent) {
		if (parent.debug)
			debug = true;
		this.debugTabbing = parent.debugTabbing + 1;
	}

	private final int debugTabbing;

	/**
	 * Method for debugging purposes.
	 * 
	 * @param args
	 *            args.
	 */
	public static void main(String[] args) {
		// Lack of operator precedence
		EquationParser parser = getDebuggingParser();
		System.out.println("-=-=-Basic Parsing-=-=-");
		parser.evaluate("5*3");
		parser.evaluate("5/3");
		parser.evaluate("5+3");
		parser.evaluate("5-3");
		parser.evaluate("5%3");
		parser.evaluate("5^3");
		System.out.println("-=-=-Parsing with parentheses-=-=-");
		parser.evaluate("(5+7)");
		parser.evaluate("(72/2)+(9-8)");
		parser.evaluate("5(3)");
		parser.evaluate("(5)3");
		parser.evaluate("(5)(3)");
		parser.evaluate("5*(3)");
		parser.evaluate("(5)*3");
		parser.evaluate("5*3");
		parser.evaluate("(5)*(3)");
		System.out.println("-=-=-Parsing with whitespace-=-=-");
		parser.evaluate("5 * 3");
		parser.evaluate("5 / 3");
		parser.evaluate("5 + 3");
		parser.evaluate("5 - 3");
		parser.evaluate("5 % 3");
		parser.evaluate("5 ^ 3");
		System.out.println();
		parser.evaluate(" 5*3");
		parser.evaluate("5*3 ");
		parser.evaluate(" 5 * 3 ");
		parser.evaluate("\t\t\t5\t*\t3");
		System.out.println("-=-=-Advanced Parsing-=-=-");
		parser.evaluate("5*3/2");
		parser.evaluate("(5+7)/6");
		parser.evaluate("(19*3)^0.7/(6^1.3*5-71)--8.9762381321");

	}

	private String equation;
	private int position;

	private boolean isEvaluating, debug;

	private char getCurrentChar() {
		return equation.charAt(position);
	}

	private char getNextChar() {
		return equation.charAt(position + 1);
	}

	private boolean incrementPosition() {
		stddeb("\tPosition is now " + (position + 1) + ". " + (!(position + 1 < equation.length()) ? "O" : "Not o")
				+ "ut of bounds.");
		if (position + 1 < equation.length())
			stddeb("\t\tAt char " + getNextChar());
		return ++position < equation.length();// Return true if getCurrentChar
												// is safe.
	}

	/**
	 * <p>
	 * This is a <strong>parse method</strong>.
	 * <p>
	 * <strong>This method expects to be called at the position it should start
	 * at.</strong> i.e., {@link #getCurrentChar()} should return the character that
	 * this method will begin to work with. If the first character that this method
	 * encounters is whitespace, a warning will be thrown and the whitespace will be
	 * skipped with {@link #getNextChar()}.
	 * <p>
	 * This method will return with {@link #position} being the start of the next
	 * element.
	 * 
	 * @return The next term in the sequence.
	 */
	private Term parseTerm() {
		stddeb("Starting at pos=" + position + (outOfBounds() ? "" : ",char=" + getCurrentChar()));
		boolean neg = false;
		double val;
		// Handle negatives in front of the value. We'll cache getCurrChar's
		// value to a variable later for speed, since this method may be called
		// many times during evaluation.
		//
		// TODO Make this loop not double check everything... Wish we had
		// batch/C++ GOTO statements...
		while (getCurrentChar() == '-' || getCurrentChar() == '+' || MathChars.isWhitespace(getCurrentChar())) {
			if (getCurrentChar() == '-')
				neg = !neg;
			else if (getCurrentChar() == '+')
				neg = false;
			incrementPosition();
		}
		if (MathChars.isNumber(getCurrentChar())) {
			String numb = "";

			do {

				if (getCurrentChar() == '.' && numb.contains("."))
					throw new DuplicateDecimalException();
				numb += getCurrentChar();
				stddeb("\tCOLLECTED CHAR \"" + getCurrentChar() + "\". So far, number is \"" + numb + "\"");
				if (!incrementPosition()) {
					break;
				}
			} while (MathChars.isNumber(getCurrentChar()));
			if (numb.startsWith("."))
				numb = "0" + numb;
			stddeb("\tTerm parsing complete with number " + numb + "; negation=" + neg
					+ ". Attempting String to double conversion...");
			// TODO Move endline checkup to operator method. END_LINE is an operator, so it
			// should be parsed by the method that parses operators.
			return new Term(neg ? -Double.parseDouble(numb) : Double.parseDouble(numb),
					outOfBounds() ? Operator.END_LINE : parseOperator());
		} else if (MathChars.isOpenWrapper(getCurrentChar())) {
			char nestingChar = getCurrentChar();
			stddeb("\tFound a wrapper; " + nestingChar + ". Starting term collection...");
			String term = "";// Don't concat these outer parentheses.
			int nesting = 1;

			boolean endLine = false;

			if (!incrementPosition())
				throw new MisplacedWrapperException();

			while (nesting > 0) {
				if (getCurrentChar() == nestingChar) {
					nesting++;
					stddeb("\t\tFound equal open wrapper. Incrementing nesting to " + nesting);
				} else if (getCurrentChar() == MathChars.getWrapperPair(nestingChar)) {
					nesting--;
					stddeb("\t\tFound equal close wrapper. Decrementing nesting to " + nesting);
					if (nesting == 0) {
						stddeb("\t\tNesting has reached 0. Finishing parsing of wrapped section...");
						if (!incrementPosition()) {
							endLine = true;
						}
						break;// We break so that the parenthesis character
					}
				}

				term += getCurrentChar();

				if (!incrementPosition())
					throw new UnmatchedWrapperException();
			}

			stddeb("Finished parsing wrapped section. Attempting to evaluate what we're left with...\n\n");

			return new Term(new EquationParser(this).evaluate(term), endLine ? Operator.END_LINE : parseOperator());

		} else if (MathChars.isCloseWrapper(getCurrentChar())) {
			throw new IllegalCloseWrapperPlacementException();
		} else {
			// Parse the following string of characters for a function. We know
			// our term has ended when a character that can not be used in a
			// function/constant name has been reached.
			String func = "" + getCurrentChar();

		}

		return null;
	}

	public EquationParser() {
		debugTabbing = 0;
	}

	/**
	 * <p>
	 * This is a <strong>parse method</strong>.
	 * <p>
	 * <strong>This method expects to be called at the position it should start
	 * at.</strong> i.e., {@link #getCurrentChar()} should return the character that
	 * this method will begin to work with. If the first character that this method
	 * encounters is whitespace, a warning will be thrown and the whitespace will be
	 * skipped with {@link #getNextChar()}.
	 * <p>
	 * This method will return with {@link #position} being the start of the next
	 * element.
	 * 
	 * @return The next operator in the sequence.
	 */
	private Operator parseOperator() {
		stddeb("Parsing the next operator. Starting at pos=" + position
				+ (outOfBounds() ? "" : ",char=" + getCurrentChar()));

		if (outOfBounds())
			return Operator.END_LINE;

		// Erase any whitespace malarkey. :P
		while (MathChars.isWhitespace(getCurrentChar()))
			if (!incrementPosition())
				return Operator.END_LINE;

		// First, handle a lack of operator.
		if (!MathChars.possibleOperator("" + getCurrentChar())) {
			// We're in this block because the character after the last term is not a
			// possible operator. It must be part of the NEXT term, in which case the terms
			// are being multiplied. e.g., 1(2)
			//
			// 1 is the first term, (2) is the second term. They are being multiplied.
			return Operator.MULTIPLY;
		}

		if (!canIncPos())
			throw new MisplacedOperatorException();

		String operator = "" + getCurrentChar();
		// If "what we've read so far" + "the next character" is a possible
		// operator...
		while (MathChars.possibleOperator(operator + getNextChar())) {
			// ...then add it to our operator's name.
			incrementPosition();
			operator += getCurrentChar();
			// We're in this while loop because we've hit an operator. If there is no next
			// character...
			if (!canIncPos())
				// ...then throw an error. (You can't evaluate "1 + 3 - " because you don't know
				// what to subtract at the end.)
				throw new MisplacedOperatorException();
		}

		stddeb("\tFinished operator parsing loop. Left with \'" + operator
				+ (operator.isEmpty() ? "{NO_OPERATOR}\'\n\t\tMultiplication implicitly assumed." : "\'"));
		if (!operator.isEmpty())// If "operator" is empty, then there was no operator between the two terms, and
								// we need not skip to the next char, as the current one is part of the next
								// term.
			incrementPosition();
		// TODO if(MathChars.isOperator(getCurrentChar()))throw new
		// InvalidOperatorException();
		return MathChars.getOperator(operator);
	}

	public double evaluate(String equation) {
		stddeb("STARTING EVALUATION OF EQUATION: " + equation);
		stddeb();
		stddeb();

		if (isEvaluating)
			throw new LexerInUseException();
		isEvaluating = true;
		position = 0;

		stddeb("Trimming equation before parsing...");
		stddeb();
		equation = equation.trim();

		this.equation = equation;

		// We start out expecting some kind of numerical value/term, not an
		// operator.
		Equation equ = new Equation();

		int round = 0;

		while (true) {
			round++;
			stddeb("Parsing Term " + round);
			Term term = parseTerm();
			stddeb("Term parsed as " + term.value + ". Operator parsed as " + term.getOperator().operator + "\n\n");
			equ.add(term);
			if (term.getOperator() == Operator.END_LINE)
				break;
		}

		// TODO Add a testing parser that sysouts stuff like this:
		// for (Term t : equ)
		// System.out.println(t.value + " " + t.getOperator().operator);

		isEvaluating = false;

		// TODO Return value
		double result = equ.evaluate();
		stddeb("RESULT = " + result);
		if (!(debugTabbing > 0)) {
			stddeb();
			stddeb();
		}
		stddeb();
		return result;

	}

	private boolean outOfBounds() {
		return !(position < equation.length());
	}

	private void stddeb(String text) {
		if (debug) {
			for (int i = 0; i < debugTabbing; i++)
				text = '\t' + text;
			System.out.println(text);
		}
	}

	private void stddeb() {
		if (debug)
			System.out.println();
	}

	private void errdeb(String text) {
		if (debug) {
			for (int i = 0; i < debugTabbing; i++)
				text = '\t' + text;
			System.err.println(text);
		}
	}

	private void errdeb() {
		if (debug)
			System.err.println();
	}

	private boolean canIncPos() {
		return position + 1 < equation.length();
	}
}
