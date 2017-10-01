package kröw.lexers.equation_2.tokens;

public class FunctionToken extends Token {

	public FunctionToken(Function value) {
		super(value, Type.FUNCTION);
	}

	@Override
	public Function getValue() {
		return (Function) super.getValue();
	}

}
