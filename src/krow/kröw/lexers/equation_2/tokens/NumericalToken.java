package kröw.lexers.equation_2.tokens;

public class NumericalToken extends Token {

	public NumericalToken(double value) {
		super(value, Type.NUMBER);
	}

	@Override
	public Double getValue() {
		return (Double) super.getValue();
	}

}
