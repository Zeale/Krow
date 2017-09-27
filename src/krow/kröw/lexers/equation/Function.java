package kröw.lexers.equation;

import kröw.lexers.equation.exceptions.EmptyEquationException;
import kröw.lexers.equation.exceptions.IrregularCharacterException;
import kröw.lexers.equation.exceptions.UnmatchedParenthesisException;

abstract class Function implements Element {

	private static final class ArcCosine extends Function {

		public ArcCosine(final String input) {
			super(input);
		}

		@Override
		public double evaluate()
				throws EmptyEquationException, UnmatchedParenthesisException, IrregularCharacterException {
			return Math.acos(autoParse());
		}

	}

	private static final class ArcSine extends Function {

		public ArcSine(final String input) {
			super(input);
		}

		@Override
		public double evaluate()
				throws EmptyEquationException, UnmatchedParenthesisException, IrregularCharacterException {
			return Math.asin(autoParse());
		}

	}

	private static final class ArcTangent extends Function {

		public ArcTangent(final String input) {
			super(input);
		}

		@Override
		public double evaluate()
				throws EmptyEquationException, UnmatchedParenthesisException, IrregularCharacterException {
			return Math.atan(autoParse());
		}

	}

	private static final class Cbrt extends Function {

		public Cbrt(final String input) {
			super(input);
		}

		@Override
		public double evaluate()
				throws EmptyEquationException, UnmatchedParenthesisException, IrregularCharacterException {
			return Math.cbrt(autoParse());
		}

	}

	private static final class Cosine extends Function {

		public Cosine(final String input) {
			super(input);
		}

		@Override
		public double evaluate()
				throws EmptyEquationException, UnmatchedParenthesisException, IrregularCharacterException {
			return Math.cos(autoParse());
		}

	}

	private static final class Log extends Function {

		public Log(final String input) {
			super(input);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see krow.guis.calculator.Calculator.Parser.Element# evaluate()
		 */
		@Override
		public double evaluate()
				throws EmptyEquationException, UnmatchedParenthesisException, IrregularCharacterException {
			return Math.log(autoParse());
		}

	}

	private static final class Log10 extends Function {

		public Log10(final String input) {
			super(input);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see krow.guis.calculator.Calculator.Parser.Element# evaluate()
		 */
		@Override
		public double evaluate()
				throws EmptyEquationException, UnmatchedParenthesisException, IrregularCharacterException {
			return Math.log10(autoParse());
		}
	}

	private static final class Sine extends Function {

		public Sine(final String input) {
			super(input);
		}

		@Override
		public double evaluate()
				throws EmptyEquationException, UnmatchedParenthesisException, IrregularCharacterException {
			return Math.sin(autoParse());
		}

	}

	private static final class Sqrt extends Function {

		public Sqrt(final String input) {
			super(input);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see krow.guis.calculator.Calculator.Parser.Element# evaluate()
		 */
		@Override
		public double evaluate()
				throws EmptyEquationException, UnmatchedParenthesisException, IrregularCharacterException {
			return Math.sqrt(autoParse());
		}

	}

	private static final class Tangent extends Function {

		public Tangent(final String input) {
			super(input);
		}

		@Override
		public double evaluate()
				throws EmptyEquationException, UnmatchedParenthesisException, IrregularCharacterException {
			return Math.tan(autoParse());
		}

	}

	public static Function getFunction(final String name, final String input) {
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
		case "cbrt":
			return new Cbrt(input);
		default:
			return null;
		}
	}

	protected final String params;

	public Function(final String input) {
		params = input;
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
	 * @throws IrregularCharacterException
	 *             Incase there is an irregularly placed character in the
	 *             equation.
	 */
	protected double autoParse()
			throws EmptyEquationException, UnmatchedParenthesisException, IrregularCharacterException {
		return new EquationParser().evaluate(params);
	}
}