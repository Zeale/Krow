package zeale.windowbuilder.api;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public final class WindowBuilder {

	private static final double DEFAULT_PREFERRED_STAGE_WIDTH = 1000, DEFAULT_PREFERRED_STAGE_HEIGHT = 800;
	private static final double DEFAULT_MINIMUM_STAGE_WIDTH = 400, DEFAULT_MINIMUM_STAGE_HEIGHT = 400;

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

	{
		// Add a listener to the stage's dimensions as soon as it is created.
		ChangeListener<Number> listener = (observable, oldValue, newValue) -> resize(oldValue.doubleValue(),
				newValue.doubleValue());
		stage.widthProperty().addListener(listener);
		stage.heightProperty().addListener(listener);
	}

	protected final Button makeNewWindowButton = new Button("New Window");
	protected final Button deleteFocusedWindowButton = new Button("Delete Focused Window");

	/**
	 * <p>
	 * The default implementation of this class (meaning, not a subclass
	 * implementation), caches the main window's root, as recommended in
	 * {@link #setRoot(Parent)}.
	 * <p>
	 * {@link #pane} and {@link #root} are synonymous.
	 * <p>
	 * In the default implementation, (which is defined by this class), the GUI is
	 * created using this {@link AnchorPane}.
	 */
	private final AnchorPane pane = new AnchorPane(makeNewWindowButton, deleteFocusedWindowButton), root = pane;
	{
		stage.setMinHeight(DEFAULT_MINIMUM_STAGE_HEIGHT);
		stage.setMinWidth(DEFAULT_MINIMUM_STAGE_WIDTH);
		stage.setWidth(DEFAULT_PREFERRED_STAGE_WIDTH);
		stage.setHeight(DEFAULT_PREFERRED_STAGE_HEIGHT);
	}

	/**
	 * Just like the default {@link #root}, the default {@link Scene} is cached.
	 */
	private Scene scene = new Scene(root);

	{
		stage.setScene(scene);
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
