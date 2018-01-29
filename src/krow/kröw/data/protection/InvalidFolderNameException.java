package kröw.data.protection;

public class InvalidFolderNameException extends Exception {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	public InvalidFolderNameException() {
		super();
	}

	public InvalidFolderNameException(String message) {
		super(message);
	}

	public InvalidFolderNameException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidFolderNameException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidFolderNameException(Throwable cause) {
		super(cause);
	}

}
