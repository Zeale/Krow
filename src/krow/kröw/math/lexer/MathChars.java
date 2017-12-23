package kröw.math.lexer;

public final class MathChars {
	private MathChars() {
	}

	public static final boolean isNumber(char c) {
		return Character.isDigit(c) || c == '.';
	}

	public static final boolean isAlphabetic(char c) {
		return Character.isLetter(c);
	}

	public static final boolean isFunction(String name) {
		// TODO FunctionLookup.lookup(name) != null;
		return false;
	}
}
