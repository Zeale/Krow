package kröw.lexers.equation_2.exceptions;

public class ParserException extends Exception {

	private int position;

	public ParserException(String message, int position) {
		super(message);
		this.position = position;
	}

	public ParserException(int position) {
		this.position = position;
	}

	public int getPosition() {
		return position;
	}

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	public ParserException() {
		// TODO Auto-generated constructor stub
	}

	public ParserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public ParserException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public ParserException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ParserException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
