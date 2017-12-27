package kröw.math.lexer;

import java.util.HashMap;
import java.util.Map;

import kröw.math.lexer.exceptions.NotAWrapperException;

public final class MathChars {

	private static final Map<Character, Character> wrapperMap = new HashMap<>();
	static {
		addPairToWrapperMap('(', ')');
		addPairToWrapperMap('[', ']');
		addPairToWrapperMap('<', '>');
		addPairToWrapperMap('{', '}');
	}

	private static void addPairToWrapperMap(char a, char b) {
		wrapperMap.put(a, b);
		wrapperMap.put(b, a);
	}

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

	public static final boolean possibleFunction(String chars) {
		// TODO Check name list.
		return false;
	}

	public static final boolean possibleOperator(String chars) {
		for (Operator o : Operator.operators)
			// We must explicitly detect if chars is empty because the
			// EquationParser was coded expecting this method to stick up to
			// it's name; see method documentation.
			if (o.operator.startsWith(chars))
				return true;
		return false;
	}

	public static Operator getOperator(String operator) {
		if (operator.isEmpty())
			return Operator.MULTIPLY;
		for (Operator o : Operator.operators)
			if (operator.equalsIgnoreCase(o.operator))
				return o;
		return null;
	}

	public static boolean isWrapper(char c) {
		return isOpenWrapper(c) || isCloseWrapper(c);
	}

	public static boolean isOpenWrapper(char c) {
		return c == '(' || c == '[' || c == '<' || c == '{';
	}

	public static boolean isCloseWrapper(char c) {
		return c == ')' || c == ']' || c == '>' || c == '}';
	}

	public static boolean isWhitespace(char c) {
		return Character.isWhitespace(c);
	}

	public static char getWrapperPair(char wrapperChar) {
		if (!isWrapper(wrapperChar))
			throw new NotAWrapperException();
		return wrapperMap.get(wrapperChar);
	}
}
