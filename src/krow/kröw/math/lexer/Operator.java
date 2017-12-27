package kröw.math.lexer;

import java.util.ArrayList;
import java.util.List;

/**
 * Temporarily empty class for development purposes.
 * 
 * @author Zeale
 *
 */
public final class Operator {

	static List<Operator> operators = new ArrayList<>();

	{
		operators.add(this);
	}

	public static final Operator ADD = new Operator("+", (x, y) -> x + y);
	public static final Operator MULTIPLY = new Operator("*", (x, y) -> x * y);
	public static final Operator SUBTRACT = new Operator("-", (x, y) -> x - y);
	public static final Operator DIVIDE = new Operator("/", (x, y) -> x / y);
	public static final Operator POWER = new Operator("^", (x, y) -> Math.pow(x, y));
	public static final Operator MODULUS = new Operator("%", (x, y) -> x % y);

	public static final Operator END_LINE = new Operator(";", null);

	public final String operator;
	private final OperatorImpl impl;

	private Operator(String operator, OperatorImpl impl) {
		this.operator = operator;
		this.impl = impl;
	}

	public double operate(double x, double y) {
		return impl.operate(x, y);
	}

	private static interface OperatorImpl {
		double operate(double x, double y);
	}

}
