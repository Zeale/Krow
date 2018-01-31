package zeale.windowbuilder.api;

import javafx.stage.Stage;

public class WindowAlreadyContainedException extends IllegalArgumentException {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = -966672211157069133L;

	public final Stage window;

	public WindowAlreadyContainedException(Stage window) {
		this.window = window;
	}

}
