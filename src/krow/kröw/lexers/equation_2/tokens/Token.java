package kröw.lexers.equation_2.tokens;

public abstract class Token {
	protected Token next, previous;

	/**
	 * @return the next
	 */
	public final Token getNext() {
		return next;
	}

	/**
	 * @param next
	 *            the next to set
	 */
	public abstract void setNext(Token next);

	/**
	 * @return the previous
	 */
	public final Token getPrevious() {
		return previous;
	}

	/**
	 * @param previous
	 *            the previous to set
	 */
	public abstract <T extends Token> void setPrevious(T previous);

}
