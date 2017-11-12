package kröw.lexers.equation;

import kröw.lexers.equation.exceptions.DuplicateDecimalException;
import kröw.lexers.equation.exceptions.EmptyEquationException;
import kröw.lexers.equation.exceptions.IrregularCharacterException;
import kröw.lexers.equation.exceptions.UnmatchedParenthesisException;

public class EquationParser {

	private volatile String equation;

	private int position;

	public double evaluate(final String equation)
			throws EmptyEquationException, UnmatchedParenthesisException, IrregularCharacterException {
		if (equation.isEmpty())
			throw new EmptyEquationException();
		reset();
		this.equation = equation;
		final Equation equ = new Equation();
		final Element e = getElement();
		equ.start(e);
		while (position < equation.length())
			equ.add(getOperation(), getElement());

		return equ.evaluate();
	}

	private char getCurrChar() {
		return equation.charAt(position);
	}

	private String getCurrCharAsString() {
		return equation.substring(position, position + 1);
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
		final String name = equation.substring(position, position + flen);
		position += flen;// This covers the opening parenthesis as well as
							// the function's name...
		final int posSubEquOpen = ++position;

		for (int parentheses = 1; parentheses > 0; nextChar())
			if (position >= equation.length())
				throw new UnmatchedParenthesisException();
			else if (getCurrCharAsString().equals("("))
				parentheses++;
			else if (getCurrCharAsString().equals(")"))
				parentheses--;
		return Function.getFunction(name, equation.substring(posSubEquOpen, position - 1));
	}

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
		final double value = Double.valueOf(equation.substring(position, position + flen));
		position += flen;
		return new Number(value);

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
		final Operation operation = Operation.getOperation(equation.substring(position, position + flen));
		position += flen;
		return operation;
	}

	private boolean isFunc(final int pos) {
		// TODO Implement an iteration technique once we add constants.
		return !(isNumb(pos) || isOperatorCharacter(pos) || equation.charAt(pos) == '(' || equation.charAt(pos) == ')');
	}

	private boolean isNumb() {
		return isNumb(position);
	}

	private boolean isNumb(final char c) {
		return c == '.' || Character.isDigit(c);
	}

	private boolean isNumb(final int position) {
		if (position < 0 || position >= equation.length())
			return false;

		int flen = 0;
		if (isOperatorCharacter(position))
			if (equation.charAt(position) == '-')
				if (position != 0 && !isOperatorCharacter(position - 1))
					return false;
				else
					flen++;
			else
				return false;

		// Check infront of the given position...
		while (true) {
			if (!(position + ++flen < equation.length()))
				return true;
			final char c = equation.charAt(position + flen);

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

	public boolean isOperator(final int position) {
		final char c = equation.charAt(position);
		return (c == '+' || c == '-') && !(position == 0 || isOperator(position - 1)) || c == '*' || c == '/'
				|| c == '^' || c == 'x' || c == '÷' || c == '%';
	}

	public boolean isOperatorCharacter(final int position) {
		return Operation.getOperation(equation.charAt(position)) != null;
	}

	/**
	 * Increments the position <b>after</b> returning the current character.
	 *
	 * @return The current char.
	 */
	private String nextChar() {
		return equation.substring(position, ++position);
	}

	private void reset() {
		position = 0;
	}

}