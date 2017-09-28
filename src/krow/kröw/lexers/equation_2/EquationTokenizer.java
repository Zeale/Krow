package kröw.lexers.equation_2;

import java.util.ArrayList;

import kröw.lexers.equation_2.exceptions.ParserException;

public class EquationTokenizer {

	private String equation;
	private int position = -1;

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

	private char prevChar() {
		return equation.charAt(--position);
	}

	private boolean hasPrevChars(int amount) {
		return position - amount < equation.length() - 1 && position - amount >= 0;
	}

	private boolean hasNextChars(int amount) {
		return position + amount < equation.length() + 1 && position - amount >= 0;
	}

	/**
	 * <p>
	 * Returns the <i>current</i> <code>char</code> and <i>then</i> increments
	 * {@link #position}.
	 * 
	 * @return the value of {@link #getChar()} before this method was called.
	 */
	private char incChar() {
		return equation.charAt(position++);
	}

	/**
	 * <p>
	 * Returns the <i>current</i> <code>char</code> and <i>then</i> decrements
	 * {@link #position}.
	 * 
	 * @return the value of {@link #getChar()} before this method was called.
	 */
	private char decChar() {
		return equation.charAt(position--);
	}

	private char getPreviousChar() {
		return equation.charAt(position - 1);
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

	private String currentToken;
	private ArrayList<String> tokens = new ArrayList<>();

	/**
	 * Runs by process of elimination to progressively eliminate what the next
	 * token could possibly be after each character is read.
	 * 
	 * @throws ParserException
	 */
	public ArrayList<String> tokenize() throws ParserException {

		tokens = new ArrayList<>();
		currentToken = "";
		position = -1;

		// This iterates through tokens. When a token is started, a sub
		// iteration occurs.
		while (hasNext()) {
			char c = nextChar();// We don't NEED to cache this thanks to
								// #getChar(), but whatever.
			// Whitespace between tokens is ignorable.
			if (Character.isWhitespace(c))
				continue;

			if (Character.isDigit(c) || c == '.') {
				tokens.add(parseNumber(false));
			}

		}

		for (String s : tokens)
			System.out.println(s);

		return tokens;
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

	private String parseNumber(boolean startAtNextChar) throws ParserException {
		String token = "";
		char c = getChar();
		boolean expectingComma = false, commaError = false;

		if (startAtNextChar)
			if (!hasNext())
				return token;
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

			if (Character.isDigit(c)) {
				token += c;
			} else if (c == '.') {

				if (expectingComma && token.length() > 3 && !(token.charAt(token.length() - 4) == ','))
					commaError = true;

				if (token.contains(".")) {
					throw new ParserException("Multiple decimal points detected in a number.", position);
				}
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
			}
		}

		if (commaError) {
			token = token.replaceAll(",", "");
			System.out.println("Comma error");
		}

		return token.isEmpty() ? "0" : token;
	}

	public EquationTokenizer(String equation) {
		this.equation = equation;
	}

}
