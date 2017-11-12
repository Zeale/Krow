package kröw.lexers.equation_2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kröw.math.Operations;

public enum Operator {
	ADDITION('+', Operations::add), SUBTRACTION('-', Operations::subtract), MULTIPLICATION('*', Operations::multiply,
			'\u22C5'), DIVISION('/', Operations::divide, '\u00F7'), FACTORIAL('!', Operations::factorial),
	// LOGICAL_NEGATION('\u00AC'), GREATER_THAN('>'), LESS_THAN('<'),
	// GREATER_THAN_OR_EQUAL_TO(">=", "\u2265"), LESS_THAN_OR_EQUAL_TO("<=",
	// "\u2264")
	;
	public static Operator getOperator(final char operator) {
		for (final Operator o : Operator.values()) {
			if (o.operator.length() == 1 && operator == o.operator.charAt(0))
				return o;
			for (final String s : o.aliases)
				if (s.length() == 1 && s.charAt(0) == operator)
					return o;
		}
		return null;
	}

	public static Operator getOperator(final String operator) {
		for (final Operator o : Operator.values()) {
			if (operator.equals(o.operator))
				return o;
			for (final String s : o.aliases)
				if (operator.equals(s))
					return o;
		}
		return null;
	}

	public static boolean isOperator(final char c) {
		return getOperator(c) != null;
	}

	public static boolean isOperator(final String s) {
		return getOperator(s) != null;
	}

	public final String operator;

	private final ArrayList<String> aliases = new ArrayList<>();

	private final Operation operation;

	private Operator(final char operator, final Operation operation) {
		this.operator = "" + operator;
		this.operation = operation;
	}

	private Operator(final char operator, final Operation operation, final char... aliases) {
		this(operator, operation);
		for (final char a : aliases)
			this.aliases.add("" + a);
	}

	private Operator(final String operator, final Operation operation) {
		this.operator = operator;
		this.operation = operation;
	}

	private Operator(final String operator, final Operation operation, final String... aliases) {
		this(operator, operation);
		for (final String s : aliases)
			this.aliases.add(s);
	}

	public List<String> getAliasesUnmodifiable() {
		return Collections.unmodifiableList(aliases);
	}

	public double operate(final double x, final double y) {
		return operation.operate(x, y);
	}

}
