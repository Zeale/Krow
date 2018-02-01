package zeale.windowbuilder.api;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

public final class WindowBuilder extends AbstractedWindow {

	private final ObservableList<Window> windows = FXCollections.observableArrayList();

	private final CheckBox editModeBox = new CheckBox("Edit Mode");
	private final BooleanProperty editMode = editModeBox.selectedProperty();

	private final ObjectProperty<Window> selectedWindow = new SimpleObjectProperty<Window>(null);

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
	public void addWindow(Window window) throws WindowAlreadyContainedException {
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
	private void setupWindow(Window window) {
		window.focusedProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
			if (newValue == true) {
				selectedWindow.set(window);
			}
		});
		window.show();
	}

	public WindowBuilder() {
	}

	{
		// Add a listener to the stage's dimensions as soon as it is created.
		ChangeListener<Number> listener = (observable, oldValue, newValue) -> resize(oldValue.doubleValue(),
				newValue.doubleValue());
		stage.widthProperty().addListener(listener);
		stage.heightProperty().addListener(listener);
	}

	// TODO Remove "protected" modifier, as this class is final and can't have
	// subclasses. At first glance, the "private" modifier would probably be better.

	private final Button makeNewWindowButton = new Button("New Window");
	{
		makeNewWindowButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				addWindow(new Window());
			}
		});
	}
	private final Button deleteFocusedWindowButton = new Button("Delete Focused Window");

	private final TilePane nodeSelectionPane = new TilePane(makeNewWindowButton, deleteFocusedWindowButton);

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
	private final HBox pane = new HBox(nodeSelectionPane), root = pane;

	/**
	 * Just like the default {@link #root}, the default {@link Scene} is cached.
	 */
	private Scene scene = new Scene(root);

	{
		// TODO Make AbstractedWindow constructor take a scene obj.
		stage.setScene(scene);
	}

	{
		// This will disable any window-dependent nodes in this WindowBuilder's window.
		windows.addListener((ListChangeListener<Object>) c -> {
			if (!isManagingWindows())
				disableWindowEditingElements();
		});
	}

	/**
	 * @return <code>true</code> if this {@link WindowBuilder} is managing one or
	 *         more windows. <code>false</code> otherwise.
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
	private void disableWindowEditingElements() {
		deleteFocusedWindowButton.setDisable(true);
	}

	private Node setupNode(Node node) {
		node.addEventFilter(EventType.ROOT, event -> {
			if (editMode.get())
				event.consume();
		});

		return node;
	}

}
