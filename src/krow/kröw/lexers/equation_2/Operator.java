package kröw.lexers.equation_2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum Operator {
	ADDITION('+'), SUBTRACTION('-'), MULTIPLICATION('*', '\u22C5'), DIVISION('/', '\u00F7'), FACTORIAL(
			'!'), LOGICAL_NEGATION('\u00AC'), GREATER_THAN('>'), LESS_THAN(
					'<'), GREATER_THAN_OR_EQUAL_TO(">=", "\u2265"), LESS_THAN_OR_EQUAL_TO("<=", "\u2264");
	public final String operator;
	private final ArrayList<String> aliases = new ArrayList<>();

	private Operator(String operator) {
		this.operator = operator;
	}

	private Operator(char operator) {
		this.operator = "" + operator;
	}

	private Operator(char operator, char... aliases) {
		this(operator);
		for (char a : aliases)
			this.aliases.add("" + a);
	}

	private Operator(String operator, String... aliases) {
		this(operator);
		for (String s : aliases)
			this.aliases.add(s);
	}

	public List<String> getAliasesUnmodifiable() {
		return Collections.unmodifiableList(aliases);
	}

	public static Operator getOperator(String operator) {
		for (Operator o : Operator.values()) {
			if (operator.equals(o.operator))
				return o;
			for (String s : o.aliases)
				if (operator.equals(s))
					return o;
		}
		return null;
	}

	public static Operator getOperator(char operator) {
		for (Operator o : Operator.values()) {
			if (o.operator.length() == 1 && operator == o.operator.charAt(0))
				return o;
			for (String s : o.aliases)
				if (s.length() == 1 && s.charAt(0) == operator)
					return o;
		}
		return null;
	}

	public static boolean isOperator(char c) {
		return getOperator(c) != null;
	}

	public static boolean isOperator(String s) {
		return getOperator(s) != null;
	}

}
