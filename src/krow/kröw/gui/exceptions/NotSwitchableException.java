package kröw.gui.exceptions;

import javafx.scene.Scene;
import kröw.gui.Application;
import kröw.gui.WindowManager;
import kröw.gui.WindowManager.Window;

/**
 * <p>
 * Thrown when someone tries to switch the current {@link Scene} but the current
 * {@link Scene}'s controller's {@link Application#canSwitchScenes()} method returns
 * false.
 *
 * @author Zeale
 */
public final class NotSwitchableException extends Exception {

	private static final long serialVersionUID = 1L;

	private final Window<? extends Application> currentWindow;
	private final Application controller;
	private final Class<? extends Application> controllerClass;

	public NotSwitchableException(final Window<? extends Application> currentWindow, final Application controller,
			final Class<? extends Application> cls) {
		this.currentWindow = currentWindow;
		this.controller = controller;
		controllerClass = cls;
	}

	/**
	 * @return the controller
	 */
	public final Application getController() {
		return controller;
	}

	/**
	 * @return the controllerClass
	 */
	public final Class<? extends Application> getControllerClass() {
		return controllerClass;
	}

	/**
	 * @return the currentWindow
	 */
	public final Window<? extends Application> getCurrentWindow() {
		return currentWindow;
	}

}