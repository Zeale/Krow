package kröw.lexers.equation_2;

import java.util.ArrayList;

import kröw.lexers.equation_2.exceptions.ParserException;
import kröw.lexers.equation_2.tokens.Function;
import kröw.lexers.equation_2.tokens.Token;
import kröw.lexers.equation_2.tokens.Token.Type;

public class EquationTokenizer {

	private final String equation;
	private int position = -1;

	private ArrayList<Token> tokens = new ArrayList<>();

	public EquationTokenizer(final String equation) {
		this.equation = equation;
	}

	/**
	 * <p>
	 * Gets the current <code>char</code>.
	 * <p>
	 * <code>nextChar()</code> must be called at least from construction once
	 * before this function is called.
	 *
	 * @return the current <code>char</code>.
	 */
	private char getChar() {
		return equation.charAt(position);
	}

	/**
	 * <p>
	 * Checks if {@link #nextChar()} is legal.
	 * <p>
	 * This method will return <code>true</code> if
	 * {@link #position}<code>+1</code> is less than
	 * {@link #equation}<code>.length()</code>.
	 * <p>
	 * Returns <code>true</code> if there are any more characters after the
	 * current character to be read.
	 *
	 * @return <code>true</code> if there are any more available characters to
	 *         be read.
	 */
	private boolean hasNext() {
		return position + 1 < equation.length();
	}

	/**
	 * <p>
	 * Returns the next <code>char</code>.
	 * <p>
	 * This will increment {@link #position} by <code>1</code> and return the
	 * <i>NEW</i> <code>char</code> at this position. This will throw any
	 * exceptions by the underlying {@link #equation} String's
	 * {@link String#charAt(int)} method if {@link #position} is out of bounds.
	 * <p>
	 * {@link #hasNext()} will return <code>true</code> if this method can be
	 * called without an exception being thrown.
	 *
	 * @return the next <code>char</code> in {@link #equation}.
	 */
	private char nextChar() {
		return equation.charAt(++position);
	}

	private Token parseAlphabeticSequence(final boolean startAtNextChar, final boolean multParentheses)
			throws ParserException {
		String token = "";
		char c = getChar();
		boolean identifierEnded = false;

		if (startAtNextChar)
			if (!hasNext())
				return new Token(null, Type.NULL);
			else
				nextChar();
		position--;// So that we can call nextChar below.

		while (hasNext()) {
			c = nextChar();
			if (Character.isWhitespace(c)) {
				identifierEnded = true;
				continue;
			}
			if (Character.isAlphabetic(c) || Character.isDigit(c) || c == '_') {
				if (identifierEnded)
					throw new ParserException("Multiple identifiers next to each other.", position);
				token += c;
				continue;
			}
			if (c == '[' || c == '(' && !multParentheses) {
				final String functionContents = parseFunctionContents(Wrapper.getWrapper(c, true));
				return new Token(new Function(token, functionContents), Type.FUNCTION);
			} else if (c == '(')
				if (multParentheses) {
					position--;
					return new Token(token, Type.VARIABLE);
				} else
					;
			else {
				position--;
				return new Token(token, Type.VARIABLE);
			}
		}
		return new Token(token, Type.VARIABLE);
	}

	private String parseFunctionContents(final Wrapper wrapper) {
		// open & close wrappers will be used l8r.
		String token = "";

		char c = getChar();
		int layer = 1;
		while (hasNext()) {
			c = nextChar();
			if (c == wrapper.open)
				layer++;
			else if (c == wrapper.close)
				layer--;

			if (layer == 0)
				return token;

			token += c;

		}

		return token;
	}

	/**
	 * <p>
	 * Parses the number from the current position.
	 * <p>
	 * If the token that gets parsed is empty, this method will return a
	 * numerical token with a value of 0.
	 * <p>
	 * startAtNextChar determines whether this method will change the position
	 * of this tokenizer before it starts parsing for the number.
	 * <p>
	 * Specifically, if this tokenizer's {@link #position} is 2 and
	 * {@link #equation} is equal to the following:
	 *
	 * <pre>
	 * <code>equation = "12345"</code>
	 * </pre>
	 * <p>
	 * then calling this method with <code>true</code> would return a token of
	 * <code>45</code>. Calling it with <code>false</code> would yield
	 * <code>345</code>.
	 *
	 * @param startAtNextChar
	 *            <code>true</code> would make this method start parsing with
	 *            the return value of {@link #nextChar()}, while
	 *            <code>false</code> will make this method start parsing with a
	 *            value taken from {@link #getChar()}.
	 * @return A token representing the number parsed.
	 */

	private Token parseNumber(final boolean startAtNextChar) throws ParserException {
		String token = "";
		char c = getChar();
		boolean expectingComma = false, commaError = false;

		if (startAtNextChar)
			if (!hasNext())
				return new Token(0, Type.NUMBER);
			else
				nextChar();
		position--;// So that we can call nextChar below.
		while (hasNext()) {
			c = nextChar();

			if (!token.contains(".") && c != '.' && token.length() > 3 && token.charAt(token.length() - 4) == ',')
				if (c == ',')
					token += c;
				else
					commaError = true;

			if (Character.isDigit(c))
				token += c;
			else if (c == '.') {

				if (expectingComma && token.length() > 3 && !(token.charAt(token.length() - 4) == ','))
					commaError = true;

				if (token.contains("."))
					throw new ParserException("Multiple decimal points detected in a number.", position);
				token += c;
			} else if (c == ',') {
				if (commaError)
					continue;

				if (token.contains(".")) {
					commaError = true;
					continue;
				}

				if (!expectingComma) {
					expectingComma = true;
					if (token.length() > 3) {
						commaError = true;
						continue;
					}

					token += c;

				}
			} else {
				position--;
				break;
			}

		}

		if (commaError)
			token = token.replaceAll(",", "");

		return token.isEmpty() ? new Token(0, Type.NUMBER) : new Token(Double.parseDouble(token), Type.NUMBER);
	}

	private Token parseOperator() /* TODO: Fix operator size */ {
		return new Token(Operator.getOperator(getChar()), Type.OPERATOR);
	}

	/**
	 * Runs by process of elimination to progressively eliminate what the next
	 * token could possibly be after each character is read.
	 *
	 * @throws ParserException
	 */
	public ArrayList<Token> tokenize() throws ParserException {

		tokens = new ArrayList<>();
		position = -1;

		// This iterates through tokens. When a token is started, a sub
		// iteration occurs.
		while (hasNext()) {
			final char c = nextChar();// We don't NEED to cache this thanks to
			// #getChar(), but whatever.

			// Whitespace between tokens is ignorable.
			if (Character.isWhitespace(c))
				continue;

			if (Operator.isOperator(c)) /* TODO: Fix operator size */ {
				if (tokens.get(tokens.size() - 1).type == Type.OPERATOR)
					throw new ParserException("Multiple consecutive operators detected.", position);
				tokens.add(parseOperator());
				continue;
			} else if (tokens.size() > 0 && tokens.get(tokens.size() - 1).type != Type.OPERATOR)
				tokens.add(new Token(Operator.MULTIPLICATION, Type.OPERATOR));
			if (Character.isDigit(c) || c == '.')
				tokens.add(parseNumber(false));
			else if (Character.isAlphabetic(c) || c == '_')
				tokens.add(parseAlphabeticSequence(false, false));

		}

		for (final Token s : tokens)
			System.out.println(s);

		return tokens;
	}

}
