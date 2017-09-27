package kr�w.lexers.equation;

import kr�w.lexers.equation.exceptions.EmptyEquationException;
import kr�w.lexers.equation.exceptions.IrregularCharacterException;
import kr�w.lexers.equation.exceptions.UnmatchedParenthesisException;

class Number implements Element {

	private final double value;

	@Deprecated
	private Operation operation;
	@Deprecated
	private Element nextElement;

	public Number(final double value) {
		this.value = value;
	}

	@Deprecated
	public Number(final double value, final Operation operation, final Element nextElement) {
		this.value = value;
		this.operation = operation;
		this.nextElement = nextElement;
	}

	@Deprecated
	public void chain(final Operation operation, final Element nextElement) {
		this.operation = operation;
		this.nextElement = nextElement;
	}

	@Override
	public double evaluate() throws EmptyEquationException, UnmatchedParenthesisException, IrregularCharacterException {
		if (!(operation == null || nextElement == null))
			return operation.evaluate(value, nextElement.evaluate());
		return value;
	}

}