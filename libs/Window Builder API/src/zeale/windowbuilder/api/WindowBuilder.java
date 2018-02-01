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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public final class WindowBuilder extends AbstractedWindow {

	// TODO Make owned windows close when this builder closes.
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
	private final Button addTextButton = new Button("Add Text");
	{
		addTextButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// Add a testing element that is clearly visible. (This works! :D)
				selectedWindow.get().addNode(setupNode(new ImageView("/krow/resources/Testing.png")));
			}
		});
	}

	private final TilePane nodeSelectionPane = new TilePane(makeNewWindowButton, deleteFocusedWindowButton,
			addTextButton, editModeBox);

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
		node.addEventHandler(InputEvent.ANY, event -> {
			if (editMode.get())
				event.consume();
		});

		new Object() {
			private double relX, relY;
			private Effect effect;

			{
				node.addEventFilter(MouseEvent.DRAG_DETECTED, event -> {
					if (!editMode.get())
						return;
					effect = node.getEffect();
					node.setEffect(new DropShadow(35, Color.GOLD));
					relX = event.getX();
					relY = event.getY();
				});

				node.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
					if (!editMode.get())
						return;
					node.setLayoutX(event.getSceneX() - relX);
					node.setLayoutY(event.getSceneY() - relY);
				});

				node.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
					if (!editMode.get())
						return;
					node.setEffect(effect);
				});

			}
		};

		return node;
	}

}
