package kröw.lexers.equation_2.tokens;

import kröw.lexers.equation_2.Operator;

public class OperatorToken extends Token {

	public OperatorToken(Operator value) {
		super(value, Type.OPERATOR);
	}

	@Override
	public Operator getValue() {
		return (Operator) super.getValue();
	}

}
