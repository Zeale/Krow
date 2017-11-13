package kröw.lexers.equation_2.tokens;

public class Token {

	public enum Type {
		NUMBER, FUNCTION, MODIFIER, VARIABLE, END, BEGIN, OPERATOR, NULL;
	}

	public final Object value;

	public final Type type;

	public Token(final Object value, final Type type) {
		this.value = value;
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "Token " + super.toString() + " value: " + value;
	}

}
