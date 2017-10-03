package kröw.lexers.equation_2.tokens;

public class Token {

	public final Object value;
	public final Type type;

	public Token(Object value, Type type) {
		this.value = value;
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public enum Type {
		NUMBER, FUNCTION, MODIFIER, VARIABLE, END, BEGIN, OPERATOR, NULL;
	}

	@Override
	public String toString() {
		return "Token " + super.toString() + " value: " + value;
	}

}
