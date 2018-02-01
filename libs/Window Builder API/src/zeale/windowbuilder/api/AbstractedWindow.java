package zeale.windowbuilder.api;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

//TODO Fix documentation.
public abstract class AbstractedWindow {

	/**
	 * This {@link WindowBuilder}'s main window. This is the {@link Stage} that the
	 * editing options for other windows are shown on.
	 */
	protected final Stage stage = new Stage();
	private static final double DEFAULT_PREFERRED_STAGE_WIDTH = 1000;
	private static final double DEFAULT_PREFERRED_STAGE_HEIGHT = 800;
	private static final double DEFAULT_MINIMUM_STAGE_WIDTH = 400;
	private static final double DEFAULT_MINIMUM_STAGE_HEIGHT = 400;

	{
		stage.setMinHeight(DEFAULT_MINIMUM_STAGE_HEIGHT);
		stage.setMinWidth(DEFAULT_MINIMUM_STAGE_WIDTH);
		stage.setWidth(DEFAULT_PREFERRED_STAGE_WIDTH);
		stage.setHeight(DEFAULT_PREFERRED_STAGE_HEIGHT);
	}

	/**
	 * <p>
	 * Sets the root of this WindowBuilder's main window.
	 * <p>
	 * It is recommended for a subclass to keep its own reference to whatever they
	 * pass into this method, since the {@link #getRoot()} method returns the root
	 * casted to a {@link Parent}, and casting back to the root's real type will
	 * need to take place.
	 * 
	 * @param root
	 *            The new {@link Parent} object that will be the root of this
	 *            WindowBuilder's main window. This can be retrieved using
	 *            {@link #getRoot()}.
	 */
	protected void setRoot(Parent root) {
		getScene().setRoot(root);
	}

	/**
	 * Returns the scene of this {@link WindowBuilder}, not any of its managed
	 * windows.
	 * 
	 * @return {@link Stage#getScene()}, called on {@link #stage}.
	 */
	protected final Scene getScene() {
		return stage.getScene();
	}

	/**
	 * Gets the root parent of the scene graph <b>for this WindowBuilder's
	 * window,</b> not the root of any of its managed windows.
	 * 
	 * @return {@link Scene#getRoot()}, called on the result of {@link #getScene()}.
	 */
	protected final Parent getRoot() {
		return stage.getScene().getRoot();
	}

	/**
	 * Shows this WindowBuilder's main window, <b>NOT the window of any of its
	 * children.</b>
	 */
	public final void show() {
		_beforeShown();
		stage.show();
		_afterShown();
	}

	/**
	 * Called right after the main window is shown. Declared for overriding.
	 */
	protected void _afterShown() {

	}

	/**
	 * Called right before the main window is shown. Declared for overriding.
	 */
	protected void _beforeShown() {

	}

	/**
	 * Called right before the main window is hidden.
	 */
	protected void _beforeHidden() {

	}

	/**
	 * Called right after the main window is hidden.
	 */
	protected void _afterHidden() {

	}

	public final void requestFocus() {
		stage.requestFocus();
	}

	/**
	 * Hides this WindowBuilder's main window, <b>NOT the window of any of its
	 * children.</b>
	 */
	public final void hide() {
		_beforeHidden();
		stage.hide();
		_afterHidden();
	}

	/**
	 * A synonymous method for {@link #hide()}; this method simply calls
	 * {@link #hide()} and returns.
	 */
	public final void close() {
		hide();
	}

	/**
	 * <p>
	 * Called when this WindowBuilder's main window is resized.
	 * <p>
	 * This method should perform any necessary layout changes that depend on the
	 * size of the main window.
	 * <p>
	 * It is an exigency that <i>this method does not resize the stage itself</i>,
	 * as this will result in infinite recursion. (By that logic, it should not
	 * resize something that should, in turn, resize the {@link #stage}.)
	 * 
	 * @param width
	 *            The new width of the window.
	 * @param height
	 *            The new height of the window.
	 */
	protected void resize(double width, double height) {
		// TODO Resize children and lay them out correctly.

	}

}
