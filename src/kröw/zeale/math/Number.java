package kröw.zeale.math;

import kröw.zeale.math.exceptions.EmptyEquationException;
import kröw.zeale.math.exceptions.UnmatchedParenthesisException;

class Number implements Element {

	@Deprecated
	public void chain(Operation operation, Element nextElement) {
		this.operation = operation;
		this.nextElement = nextElement;
	}

	private double value;
	@Deprecated
	private Operation operation;
	@Deprecated
	private Element nextElement;

	public Number(double value) {
		this.value = value;
	}

	@Deprecated
	public Number(double value, Operation operation, Element nextElement) {
		this.value = value;
		this.operation = operation;
		this.nextElement = nextElement;
	}

	@Override
	public double evaluate() throws EmptyEquationException, UnmatchedParenthesisException {
		if (!(operation == null || nextElement == null)) {
			return operation.evaluate(value, nextElement.evaluate());
		}
		return value;
	}

}