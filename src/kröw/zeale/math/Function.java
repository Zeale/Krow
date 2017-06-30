package kröw.zeale.math;

import kröw.zeale.math.exceptions.EmptyEquationException;
import kröw.zeale.math.exceptions.UnmatchedParenthesisException;

abstract class Function implements Element {

	protected final String params;

	public Function(String input) {
		this.params = input;
	}

	/**
	 * <p>
	 * Automatically takes the string input and parses it using the default
	 * parser. If your Function does not read its parameter contents abnormally,
	 * you should use this method to evaluate your parameters so you can read it
	 * as a number, rather than parse it yourself as a String.
	 * <p>
	 * This method takes {@link #params} and parses it as a mathematical
	 * equation. The result is returned.
	 * <p>
	 * {@link #params} isn't modified through this method so repeated calls to
	 * {@link #autoParse()} should return the same value. (So long as
	 * {@link #params} isn't modified externally.)
	 * 
	 * @return The parsed value of {@link #params}. (Assuming that
	 *         {@link #params} is a valid equation.)
	 * @throws UnmatchedParenthesisException
	 *             Incase {@link #params} has unmatched parentheses
	 * @throws EmptyEquationException
	 *             Incase {@link #params} is empty.
	 */
	protected double autoParse() throws EmptyEquationException, UnmatchedParenthesisException {
		return new EquationParser().evaluate(params);
	}

	public static Function getFunction(String name, String input) {
		switch (name.toLowerCase()) {
		case "log":
		case "loge":
			return new Log(input);
		case "log10":
			return new Log10(input);
		case "sqrt":
			return new Sqrt(input);
		case "sin":
		case "sine":
			return new Sine(input);
		case "cosine":
		case "cos":
			return new Cosine(input);
		case "tan":
		case "tangent":
			return new Tangent(input);
		case "atan":
		case "arctangent":
		case "arctan":
			return new ArcTangent(input);
		case "asin":
		case "asine":
		case "arcsine":
			return new ArcSine(input);
		case "acos":
		case "arccos":
		case "arcos":// Becuz someone will do it...
		case "arccosine":
		case "arcosine":
			return new ArcCosine(input);
		default:
			return null;
		}
	}

	private static final class Log extends Function {

		public Log(String input) {
			super(input);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see krow.zeale.guis.calculator.Calculator.Parser.Element# evaluate()
		 */
		@Override
		public double evaluate() throws EmptyEquationException, UnmatchedParenthesisException {
			return Math.log(autoParse());
		}

	}

	private static final class Log10 extends Function {

		public Log10(String input) {
			super(input);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see krow.zeale.guis.calculator.Calculator.Parser.Element# evaluate()
		 */
		@Override
		public double evaluate() throws EmptyEquationException, UnmatchedParenthesisException {
			return Math.log10(autoParse());
		}
	}

	private static final class Sqrt extends Function {

		/*
		 * (non-Javadoc)
		 * 
		 * @see krow.zeale.guis.calculator.Calculator.Parser.Element# evaluate()
		 */
		@Override
		public double evaluate() throws EmptyEquationException, UnmatchedParenthesisException {
			return Math.sqrt(autoParse());
		}

		public Sqrt(String input) {
			super(input);
		}

	}

	private static final class Sine extends Function {

		public Sine(String input) {
			super(input);
		}

		@Override
		public double evaluate() throws EmptyEquationException, UnmatchedParenthesisException {
			return Math.sin(autoParse());
		}

	}

	private static final class Cosine extends Function {

		public Cosine(String input) {
			super(input);
		}

		@Override
		public double evaluate() throws EmptyEquationException, UnmatchedParenthesisException {
			return Math.cos(autoParse());
		}

	}

	private static final class Tangent extends Function {

		public Tangent(String input) {
			super(input);
		}

		@Override
		public double evaluate() throws EmptyEquationException, UnmatchedParenthesisException {
			return Math.tan(autoParse());
		}

	}

	private static final class ArcTangent extends Function {

		public ArcTangent(String input) {
			super(input);
		}

		@Override
		public double evaluate() throws EmptyEquationException, UnmatchedParenthesisException {
			return Math.atan(autoParse());
		}

	}

	private static final class ArcSine extends Function {

		public ArcSine(String input) {
			super(input);
		}

		@Override
		public double evaluate() throws EmptyEquationException, UnmatchedParenthesisException {
			return Math.asin(autoParse());
		}

	}

	private static final class ArcCosine extends Function {

		public ArcCosine(String input) {
			super(input);
		}

		@Override
		public double evaluate() throws EmptyEquationException, UnmatchedParenthesisException {
			return Math.acos(autoParse());
		}

	}

}