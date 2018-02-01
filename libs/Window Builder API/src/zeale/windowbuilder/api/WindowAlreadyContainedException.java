package zeale.windowbuilder.api;

public class WindowAlreadyContainedException extends IllegalArgumentException {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = -966672211157069133L;

	public final Window window;

	public WindowAlreadyContainedException(Window window) {
		this.window = window;
	}

}
