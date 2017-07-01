package kröw.zeale.math;

import kröw.zeale.math.exceptions.EmptyEquationException;
import kröw.zeale.math.exceptions.UnmatchedParenthesisException;

public class EquationParser {

	public double evaluate(String equation) throws EmptyEquationException, UnmatchedParenthesisException {
		if (equation.isEmpty())
			throw new EmptyEquationException();
		reset();
		this.equation = equation;

		Equation equ = new Equation();
		Element e = getElement();
		equ.start(e);
		while (position < equation.length()) {
			equ.add(getOperation(), getElement());
		}

		return equ.evaluate();
	}

	private volatile String equation;
	private int position;

	private Number getNumber() {
		// TODO Fix up these methods...
		if (!isNumb())
			throw new NumberFormatException();
		// Forward length and backward length.
		int flen = 0;
		while (position + ++flen < equation.length() && isNumb(position + flen))
			;
		/* The above needs to be worked on... */
		double value = Double.valueOf(equation.substring(position, position + flen));
		position += flen;
		return new Number(value);

	}

	private Element getElement() throws UnmatchedParenthesisException {
		if (isOperator(getCurrChar()))
			throw new NumberFormatException();
		if (isNumb())
			return getNumber();
		if (isFunc(position))
			return getFunction();
		throw new NumberFormatException();
	}

	private Function getFunction() throws UnmatchedParenthesisException {
		if (!isFunc(position))
			throw new NumberFormatException();
		int flen = -1;
		
		while (position + ++flen < equation.length() && isFunc(position + flen))
			;
		String name = equation.substring(position, position + flen);
		position += flen;// This covers the opening parenthesis as well as
							// the function's name...
		int posSubEquOpen = ++position;

		for (int parentheses = 1; parentheses > 0; nextChar()) {

			if (position >= equation.length())
				throw new UnmatchedParenthesisException();
			else if (getCurrChar().equals("("))
				parentheses++;
			else if (getCurrChar().equals(")"))
				parentheses--;
		}
		return Function.getFunction(name, equation.substring(posSubEquOpen, (position) - 1));
	}

	private boolean isFunc(int pos) {
		// TODO Implement an iteration technique once we add constants.
		return !(isNumb(pos) || isOperator(equation.charAt(pos)) || equation.charAt(pos) == '('
				|| equation.charAt(pos) == ')');
	}

	private Operation getOperation() {
		if (isNumb())
			throw new NumberFormatException();

		// Forward length and backward length.
		int flen = 0;
		while (position + ++flen < equation.length() && isOperator(equation.charAt(position + flen)))
			;
		// For now each operation should be one character long, but better
		// safe than sorry.
		Operation operation = Operation.getOperation(equation.substring(position, position + flen));
		position += flen;
		return operation;
	}

	static boolean isOperator(char c) {
		return c == '+' || c == '-' || c == '*' || c == '/' || c == '^' || c == 'x' || c == '÷' || c == '%';
	}

	private static boolean isOperator(String c) {
		return isOperator(c.charAt(0));
	}

	private boolean isNumb(int position) {
		if (position < 0 || position >= equation.length())
			return false;
		int flen = 0, blen = 0;
		if (isOperator(equation.charAt(position)))
			return false;
		// Check behind the given position by iterating through each
		// character.
		while (position + --blen > -1) {
			// Get the character at the current position.
			char c = equation.charAt(position + blen);

			if (Character.isDigit(c) || c == '.')// If its part of a numb
				continue;
			else if (c == 'x')// Both operator and part of func name
				continue;
			else if (Character.isAlphabetic(c) || c == '(')// It's a
															// function.
				return false;
			else if (isOperator(c))// No letters behind the
									// given position. Now to
									// check infront.
				break;
		}
		// Check infront of the given position...
		while (true) {
			if (!(position + ++flen < equation.length())) {
				return true;
			}
			char c = equation.charAt(position + flen);

			if (Character.isDigit(c) || c == '.')// If its part of a numb
				continue;
			else if (c == 'x')// Both an operator and part of func name; we
								// can't determine anything with this.
				continue;
			else if (Character.isAlphabetic(c) || c == ')')// It's a
															// function.
				return false;
			else if (isOperator(c))// No letters infront or
									// behind the given
									// position. This is
									// definitely a number...
				return true;
		}
	}

	private boolean isNumb() {
		return isNumb(position);
	}

	private String getNextChar() {
		return equation.substring(position + 1, position + 1);
	}

	private String getCurrChar() {
		return equation.substring(position, position + 1);
	}

	/**
	 * Increments the position <b>after</b> returning the current character.
	 * 
	 * @return The current char.
	 */
	private String nextChar() {
		return equation.substring(position, ++position);
	}

	/**
	 * <p>
	 * Much like the {@link #nextChar()} method, this method will return the
	 * current character and move the pinhead (position) down to the previous
	 * character.
	 * 
	 * 
	 * 
	 * @return The current character.
	 */
	private String previousChar() {
		return equation.substring(position, position-- + 1);
	}

	private String getPreviousChar() {
		return equation.substring(position - 1, position);
	}

	private void reset() {
		position = 0;
	}

}