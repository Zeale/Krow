package kröw.zeale.math;

import kröw.zeale.math.exceptions.EmptyEquationException;
import kröw.zeale.math.exceptions.UnmatchedParenthesisException;

public abstract class Function implements Element {

	protected final String params;

	public Function(String input) {
		this.params = input;
	}

	/**
	 * <p>
	 * Automatically takes the string input and parses it using the
	 * default parser. If your Function does not read its parameter
	 * contents abnormally, you should use this method to evaluate
	 * your parameters so you can read it as a number, rather than
	 * parse it yourself as a String.
	 * <p>
	 * This method takes {@link #params} and parses it as a
	 * mathematical equation. The result is returned.
	 * <p>
	 * {@link #params} isn't modified through this method so
	 * repeated calls to {@link #autoParse()} should return the same
	 * value. (So long as {@link #params} isn't modified
	 * externally.)
	 * 
	 * @return The parsed value of {@link #params}. (Assuming that
	 *         {@link #params} is a valid equation.)
	 * @throws UnmatchedParenthesisException
	 *             Incase {@link #params} has unmatched parentheses
	 * @throws EmptyEquationException
	 *             Incase {@link #params} is empty.
	 */
	protected double autoParse() throws EmptyEquationException, UnmatchedParenthesisException {
		return new Parser().evaluate(params);
	}

	public static Function getFunction(String name, String input) {
		switch (name.toLowerCase()) {
		case "log":
			return new Log(input);
		case "log10":
			return new Log10(input);
		case "loge":
			return new Log(input);
		case "sqrt":
			return new Sqrt(input);
		default:
			return null;
		}
	}

	public static final class Log extends Function {

		public Log(String input) {
			super(input);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * krow.zeale.guis.calculator.Calculator.Parser.Element#
		 * evaluate()
		 */
		@Override
		public double evaluate() throws EmptyEquationException, UnmatchedParenthesisException {
			return Math.log(autoParse());
		}

	}

	public static final class Log10 extends Function {

		public Log10(String input) {
			super(input);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * krow.zeale.guis.calculator.Calculator.Parser.Element#
		 * evaluate()
		 */
		@Override
		public double evaluate() throws EmptyEquationException, UnmatchedParenthesisException {
			return Math.log10(autoParse());
		}
	}

	public static final class Sqrt extends Function {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * krow.zeale.guis.calculator.Calculator.Parser.Element#
		 * evaluate()
		 */
		@Override
		public double evaluate() throws EmptyEquationException, UnmatchedParenthesisException {
			return Math.sqrt(autoParse());
		}

		public Sqrt(String input) {
			super(input);
		}

	}

}