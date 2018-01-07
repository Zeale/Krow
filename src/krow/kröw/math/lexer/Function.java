package kröw.math.lexer;

import java.util.ArrayList;
import java.util.List;

//TODO Make these, along with operators, be based off of a file.
public class Function {

	static List<Function> functions = new ArrayList<>();

	{
		functions.add(this);
	}

	public Function(String name, FunctionImpl impl) {
		this.name = name;
		this.impl = impl;
	}

	public static final Function COSINE = new Function("cos", Math::cos);
	public static final Function SINE = new Function("sin", Math::sin);

	private final FunctionImpl impl;
	private final String name;

	public String getName() {
		return name;
	}

	public double calculate(double input) {
		return impl.evaluate(input);
	}

	// private boolean caseSensitive;

	private interface FunctionImpl {
		// This may later change to taking a varargs of Objs and returning an Object.
		double evaluate(double input);
	}

}
