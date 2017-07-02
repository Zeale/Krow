package kröw.zeale.math;

import kröw.zeale.math.exceptions.DuplicateDecimalException;
import kröw.zeale.math.exceptions.EmptyEquationException;
import kröw.zeale.math.exceptions.IrregularCharacterException;
import kröw.zeale.math.exceptions.UnmatchedParenthesisException;

public class EquationParser {

	public double evaluate(String equation)
			throws EmptyEquationException, UnmatchedParenthesisException, IrregularCharacterException {
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

	private Number getNumber() throws IrregularCharacterException {
		// TODO Fix up these methods...

		if (!isNumb())
			throw new NumberFormatException();

		// Forward length and backward length.
		int flen = 0;
		if (getCurrChar() == '-')
			flen++;

		char c;
		boolean hasDecimal = false;
		while (position + ++flen < equation.length() && isNumb(c = equation.charAt(position + flen)))
			if (c == '.')
				if (hasDecimal)
					throw new DuplicateDecimalException("A duplicate decimal was found while parsing a number...",
							equation, position + flen);
				else
					hasDecimal = true;

		/* The above needs to be worked on... */
		double value = Double.valueOf(equation.substring(position, position + flen));
		position += flen;
		return new Number(value);

	}

	private Element getElement() throws UnmatchedParenthesisException, IrregularCharacterException {
		if (isOperator(position))
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
			else if (getCurrCharAsString().equals("("))
				parentheses++;
			else if (getCurrCharAsString().equals(")"))
				parentheses--;
		}
		return Function.getFunction(name, equation.substring(posSubEquOpen, (position) - 1));
	}

	private boolean isFunc(int pos) {
		// TODO Implement an iteration technique once we add constants.
		return !(isNumb(pos) || isOperatorCharacter(pos) || equation.charAt(pos) == '(' || equation.charAt(pos) == ')');
	}

	private Operation getOperation() {
		if (isNumb())
			throw new NumberFormatException();

		// Forward length and backward length.
		int flen = 0;
		while (position + ++flen < equation.length() && isOperator(position + flen))
			;
		// For now each operation should be one character long, but better
		// safe than sorry.
		Operation operation = Operation.getOperation(equation.substring(position, position + flen));
		position += flen;
		return operation;
	}

	private boolean isNumb(int position) {
		if (position < 0 || position >= equation.length())
			return false;

		int flen = 0;
		if (isOperatorCharacter(position)) {
			if (equation.charAt(position) == '-')
				if (position != 0 && !isOperatorCharacter(position - 1))
					return false;
				else {
					flen++;
				}
			else
				return false;
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
			else if (isOperatorCharacter(position + flen))
				return true;
		}
	}

	public boolean isOperatorCharacter(int position) {
		return Operation.getOperation(equation.charAt(position)) != null;
	}

	public boolean isOperator(int position) {
		char c = equation.charAt(position);
		return ((c == '+' || c == '-') && !(position == 0 || isOperator(position - 1))) || c == '*' || c == '/'
				|| c == '^' || c == 'x' || c == '÷' || c == '%';
	}

	private boolean isNumb(char c) {
		return c == '.' || Character.isDigit(c);
	}

	private boolean isNumb() {
		return isNumb(position);
	}

	private String getNextChar() {
		return equation.substring(position + 1, position + 1);
	}

	private String getCurrCharAsString() {
		return equation.substring(position, position + 1);
	}

	private char getCurrChar() {
		return equation.charAt(position);
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