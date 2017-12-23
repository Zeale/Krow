package kr�w.math.lexer;

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
		parser.evaluate("7 + 3- 2");
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

	private char incrementPosition() {
		return equation.charAt(++position);
	}

	public double evaluate(String equation) {
		if (isEvaluating)// TODO Throw new LexerInUseException
			;
		isEvaluating = true;
		this.equation = equation;

		// We start out expecting some kind of numerical value/term, not an
		// operator.

		isEvaluating = false;

		// TODO Return value
		return 0;

	}
}
