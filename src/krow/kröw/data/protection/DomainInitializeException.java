package kröw.data.protection;

public class DomainInitializeException extends Exception {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	public DomainInitializeException() {
	}

	public DomainInitializeException(String message) {
		super(message);
	}

	public DomainInitializeException(String message, Throwable cause) {
		super(message, cause);
	}

	public DomainInitializeException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DomainInitializeException(Throwable cause) {
		super(cause);
	}

}
