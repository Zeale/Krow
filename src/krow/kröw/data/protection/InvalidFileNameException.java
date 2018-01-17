package kröw.data.protection;

public class InvalidFileNameException extends RuntimeException {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	public final String name;

	public InvalidFileNameException(String name) {
		this.name = name;
	}

	public InvalidFileNameException(String message, String name) {
		super(message);
		this.name = name;
	}

	public InvalidFileNameException(String message, Throwable cause, String name) {
		super(message, cause);
		this.name = name;
	}

}
