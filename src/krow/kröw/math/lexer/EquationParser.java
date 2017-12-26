package kröw.math.lexer;

import java.util.regex.Pattern;

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

	Pattern p;

	public static void main(String[] args) {
		EquationParser parser = new EquationParser();
		System.out.println("Test");
		System.out.println(parser.evaluate("1 - 9 + 3"));
	}

	private String equation;

	private int position;

	private boolean isEvaluating;

	private char getCurrentChar() {
		return equation.charAt(position);
	}

	private char getNextChar() {
		return equation.charAt(position + 1);
	}

	private boolean incrementPosition() {
		return ++position < equation.length();// Return true if getCurrentChar
												// is safe.
	}

	/**
	 * <p>
	 * This is a <strong>parse method</strong>.
	 * <p>
	 * <strong>This method expects to be called at the position it should start
	 * at.</strong> i.e., {@link #getCurrentChar()} should return the character
	 * that this method will begin to work with. If the first character that
	 * this method encounters is whitespace, a warning will be thrown and the
	 * whitespace will be skipped with {@link #getNextChar()}.
	 * <p>
	 * This method will return with {@link #position} being the start of the
	 * next element.
	 * 
	 * @return The next term in the sequence.
	 */
	private Term parseTerm() {
		while (MathChars.isWhitespace(getCurrentChar()))
			incrementPosition();
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

				// TODO if(getCurrentChar()=='.'&&numb.contains(".")) throw new
				// DuplicateDecimalException();
				numb += getCurrentChar();
				if (!incrementPosition()) {
					break;
				}
			} while (MathChars.isNumber(getCurrentChar()));
			if (numb.startsWith("."))
				numb = "0" + numb;
			return new Term(neg ? -Double.parseDouble(numb) : Double.parseDouble(numb), parseOperator());
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
	 * at.</strong> i.e., {@link #getCurrentChar()} should return the character
	 * that this method will begin to work with. If the first character that
	 * this method encounters is whitespace, a warning will be thrown and the
	 * whitespace will be skipped with {@link #getNextChar()}.
	 * <p>
	 * This method will return with {@link #position} being the start of the
	 * next element.
	 * 
	 * @return The next operator in the sequence.
	 */
	private Operator parseOperator() {

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
				// TODO throw new MisplacedOperatorException();
				throw new RuntimeException("Misplaced operator.");
			}
			operator += getCurrentChar();
		}

		incrementPosition();
		// TODO if(MathChars.isOperator(getCurrentChar()))throw new
		// InvalidOperatorException();
		return MathChars.getOperator(operator);
	}

	public double evaluate(String equation) {
		if (isEvaluating)// TODO Throw new LexerInUseException
			;
		isEvaluating = true;
		this.equation = equation;

		// We start out expecting some kind of numerical value/term, not an
		// operator.
		Equation equ = new Equation();
		while (true) {
			Term term = parseTerm();
			equ.add(term);
			if (term.getOperator() == Operator.END_LINE)
				break;
		}

		// TODO Add a testing parser that sysouts stuff like this.
		// for (Term t : equ)
		// System.out.println(t.value + " " + t.getOperator().operator);

		isEvaluating = false;

		// TODO Return value
		return equ.evaluate();

	}
}
