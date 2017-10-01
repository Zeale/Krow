package kröw.lexers.equation_2.tokens;

public class NullToken extends Token {

	public NullToken() {
		super(null, Type.NULL);
	}

	@Override
	public Object getValue() {
		return null;
	}
}
