package kröw.data.protection;

public class UnavailableException extends Exception {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	public UnavailableException() {
		super();
	}

	public UnavailableException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UnavailableException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnavailableException(String message) {
		super(message);
	}

	public UnavailableException(Throwable cause) {
		super(cause);
	}

}
