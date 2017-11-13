package kröw.lexers.equation_2.exceptions;

public class ParserException extends Exception {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	private int position;

	public ParserException() {
		// TODO Auto-generated constructor stub
	}

	public ParserException(final int position) {
		this.position = position;
	}

	public ParserException(final String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ParserException(final String message, final int position) {
		super(message);
		this.position = position;
	}

	public ParserException(final String message, final Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public ParserException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public ParserException(final Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public int getPosition() {
		return position;
	}

}
