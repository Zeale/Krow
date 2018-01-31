package zeale.windowbuilder.api;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public final class WindowBuilder {

	private final ObservableList<Stage> windows = FXCollections.observableArrayList();

	/**
	 * <p>
	 * Adds the specified window to the list of this WindowBuilder's managed windows
	 * if it is not already contained. {@link #setupWindow(Stage)} is called on the
	 * parameter when this method is invoked.
	 * 
	 * @param window
	 *            The window to add to this WindowBuilder.
	 *            {@link #setupWindow(Stage)} will be called with this parameter.
	 * @throws WindowAlreadyContainedException
	 *             If this WindowBuilder already contains the specified window.
	 */
	public void addWindow(Stage window) throws WindowAlreadyContainedException {
		if (windows.contains(window))
			throw new WindowAlreadyContainedException(window);
		windows.add(window);
		setupWindow(window);
	}

	/**
	 * Called when a window is added to this WindowBuilder's list of managed
	 * windows. This setup method may do different things to the window in question,
	 * such as show the window.
	 * 
	 * @param window
	 *            The window that will be set up.
	 */
	protected void setupWindow(Stage window) {
		window.show();
	}

	/**
	 * This {@link WindowBuilder}'s main window. This is the {@link Stage} that the
	 * editing options for other windows are shown on.
	 */
	protected final Stage stage = new Stage();

	protected final Button makeNewWindowButton = new Button("New Window");
	protected final Button deleteFocusedWindowButton = new Button("Delete Focused Window");

	{
		// This will disable any window-dependent nodes in this WindowBuilder's window.
		windows.addListener((ListChangeListener<Object>) c -> {
			if (!isManagingWindows())
				disableWindowEditingElements();
		});
	}

	/**
	 * @return <code>true</code> if this {@link WindowBuilder} is not managing any
	 *         windows. <code>false</code> otherwise.
	 */
	public boolean isManagingWindows() {
		return !windows.isEmpty();
	}

	/**
	 * <p>
	 * Called when there is no focus on a specific window, so pushing a button that
	 * makes a change to a window should not be possible.
	 * <p>
	 * For example, if the user just opened this WindowBuilder and there are
	 * currently no open windows, the user should not be allowed to push a "Add Text
	 * Node" button, because there is no window open for this Window Builder to add
	 * the text node to. The "Add Text Node" button would be disabled in this
	 * method, in such a case.
	 */
	protected void disableWindowEditingElements() {
		deleteFocusedWindowButton.setDisable(true);
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
	public void show() {
		stage.show();
	}

	/**
	 * Hides this WindowBuilder's main window, <b>NOT the window of any of its
	 * children.</b>
	 */
	public void hide() {
		stage.hide();
	}

	/**
	 * A synonymous method for {@link #hide()}; this method simply calls
	 * {@link #hide()} and returns.
	 */
	public void close() {
		hide();
	}

	/**
	 * <p>
	 * Called when this WindowBuilder's main window is resized.
	 * <p>
	 * This method should perform any necessary layout changes that depend on the
	 * size of the main window.
	 * 
	 * @param width
	 *            The new width of the window.
	 * @param height
	 *            The new height of the window.
	 */
	protected void resize(double width, double height) {
		// TODO
	}

}
