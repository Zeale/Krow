package kröw.math.lexer;

import kröw.math.lexer.exceptions.DuplicateDecimalException;
import kröw.math.lexer.exceptions.LexerInUseException;
import kröw.math.lexer.exceptions.MisplacedOperatorException;

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

	/**
	 * Method for debugging purposes.
	 * 
	 * @param args
	 *            args.
	 */
	public static void main(String[] args) {
		// Lack of operator precedence
		System.out.println(getDebuggingParser().evaluate("6+6/6"));
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
			return new Term(neg ? -Double.parseDouble(numb) : Double.parseDouble(numb),
					outOfBounds() ? Operator.END_LINE : parseOperator());
		} else {
			// Parse the following string of characters for a function. We know
			// our term has ended when a character that can not be used in a
			// function/constant name has been reached.
			String func = "" + getCurrentChar();

		}

		return null;
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

		if (!(position < equation.length()))
			return Operator.END_LINE;

		while (MathChars.isWhitespace(getCurrentChar()))
			if (!incrementPosition())
				return Operator.END_LINE;

		String operator = "" + getCurrentChar();

		// If "what we've read so far" + "the next character" is a possible
		// operator...
		while (MathChars.possibleOperator(operator + getNextChar())) {
			// ...then add it to our operator's name.
			if (!incrementPosition()) {
				// This is called when an operator is found but there is no
				// second argument. E.g. "4 + 3 + "
				// There is no argument after the second "+". In the above
				// example, we don't know what to add 7 (4+3) to, so we throw an
				// error.
				//
				// Just to reinforce, end lines should only be called after a
				// Term.
				throw new MisplacedOperatorException();
			}
			operator += getCurrentChar();
		}

		incrementPosition();
		// TODO if(MathChars.isOperator(getCurrentChar()))throw new
		// InvalidOperatorException();
		return MathChars.getOperator(operator);
	}

	public double evaluate(String equation) {
		stddeb("STARTING EVALUATION");
		stddeb();
		stddeb();

		if (isEvaluating)
			throw new LexerInUseException();
		isEvaluating = true;
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
		return equ.evaluate();

	}

	private boolean outOfBounds() {
		return !(position < equation.length());
	}

	private void stddeb(String text) {
		if (debug)
			System.out.println(text);
	}

	private void stddeb() {
		if (debug)
			System.out.println();
	}

	private void errdeb(String text) {
		if (debug)
			System.err.println(text);
	}

	private void errdeb() {
		if (debug)
			System.err.println();
	}
}
