package zeale.windowbuilder.api;

import java.util.Map;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import krow.fx.dialogues.promptdialogues.PromptDialogue;

public final class WindowBuilder extends AbstractedWindow {

	{
		// Add a listener to the stage's dimensions as soon as it is created.
		ChangeListener<Number> listener = (observable, oldValue, newValue) -> resize(oldValue.doubleValue(),
				newValue.doubleValue());
		stage.widthProperty().addListener(listener);
		stage.heightProperty().addListener(listener);
	}

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
			if (newValue == true)
				selectedWindow.set(window);
		});
		window.show();
	}

	public WindowBuilder() {
	}

	private final Button makeNewWindowButton = new Button("New Window");
	{
		makeNewWindowButton.setOnAction(event -> addWindow(new Window(this)));
	}
	private final Button deleteFocusedWindowButton = new Button("Delete Focused Window");
	private final Button addTextButton = new Button("Add Text");
	{

		// Testing the PromptDialogue class and its features.
		addTextButton.setOnAction(event -> {
			if (selectedWindow.get() == null)
				return;

			PromptDialogue<Object> buttonPrompt = new PromptDialogue<>("New Button");
			Object textKey = new Object(), idKey = new Object();

			buttonPrompt.new Prompt(textKey, "Button Text:");
			buttonPrompt.new Prompt(idKey, "Button ID:");

			Map<Object, String> promptedData = buttonPrompt.showAndWait().orElse(null);

			NodeWrapper<Button> wrapper;
			try {
				wrapper = setupNode(new Button(promptedData.get(textKey)), promptedData.get(idKey));

				wrapper.getNode().addEventFilter(MouseEvent.MOUSE_CLICKED, event1 -> {
					if (editMode.get() && event1.getButton() == MouseButton.SECONDARY) {

						PromptDialogue<Object> prompt = new PromptDialogue<>("New Button");
						Object width = new Object(), length = new Object(), text = new Object(), id = new Object();

						prompt.new Prompt(width, "Button Width:");
						prompt.new Prompt(length, "Button Length:");
						prompt.new Prompt(text, "Button Text:");
						prompt.new Prompt(id, "Button ID:");

						Map<Object, String> map = prompt.showAndWait().orElse(null);
						if (map == null)
							throw new RuntimeException("Prompt Error");
						try {
							wrapper.getNode().setPrefWidth(Double.parseDouble(map.get(length)));
						} catch (Exception e) {
						}

						try {
							wrapper.getNode().setPrefHeight(Double.parseDouble(map.get(width)));
						} catch (Exception e) {
						}

						wrapper.getNode().setText(map.get(text));
						try {
							wrapper.setId(map.get(idKey));
						} catch (IDTakenException e) {
							e.printStackTrace();
						}

					}
				});
				selectedWindow.get().addNode(wrapper);
				selectedWindow.get().addToRoot(wrapper);
			} catch (IDTakenException e1) {
				// Deal with this later.
				e1.printStackTrace();
			}

		});
	}

	private final VBox optionsBox = new VBox(25, makeNewWindowButton, deleteFocusedWindowButton, addTextButton,
			editModeBox);

	private final Tab nodesTab = new Tab("Nodes");
	private final Tab optionsTab = new Tab("Options");

	private final TilePane nodeSelectionPane = new TilePane();
	{
		optionsTab.setContent(optionsBox);
		nodesTab.setContent(nodeSelectionPane);
	}

	private static final Object NODE_SELECTION_BUTTON_KEY = new Object();
	{
		nodeSelectionPane.setPadding(new Insets(30));
		selectedWindow.addListener((ChangeListener<Window>) (observable, oldValue, newValue) -> {
			System.out.println("ABCD");
			if (selectedWindow.isNull().get())
				return;
			nodeSelectionPane.getChildren().clear();
			for (NodeWrapper<?> n : newValue.getTrackedNodes()) {
				Button button = new Button();
				button.getProperties().put(NODE_SELECTION_BUTTON_KEY, n);
				button.setText(n.getNode().getId());
				nodeSelectionPane.getChildren().add(button);
			}
		});
	}

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
	private final TabPane pane = new TabPane(optionsTab, nodesTab), root = pane;

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

	private static final DropShadow NODE_EDIT_GOLD_GLOW = new DropShadow(35, Color.GOLD),
			NODE_EDIT_BLACK_GLOW = new DropShadow(42, Color.BLACK),
			NODE_EDIT_BLUE_GLOW = new DropShadow(40, Color.CORNFLOWERBLUE);
	{
		NODE_EDIT_BLUE_GLOW.setSpread(0.2);
	}

	private <T extends Node> NodeWrapper<T> setupNode(T node, String id) throws IDTakenException {

		// Instead of passing in the selected window, it may be better to pass in a
		// parameter.
		NodeWrapper<T> wrapper = new NodeWrapper<T>(node, selectedWindow.get(), id);

		node.addEventHandler(InputEvent.ANY, event -> {
			if (editMode.get())
				event.consume();
		});

		new Object() {
			private double relX, relY;
			private boolean dragDetected;

			{

				node.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
					if (!editMode.get())
						return;
					wrapper.pushEditEffect(NODE_EDIT_BLACK_GLOW);
				});

				node.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
					if (!editMode.get())
						return;
					wrapper.removeEditEffect();
				});

				node.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
					if (!editMode.get())
						return;

					if (event.getButton() != MouseButton.PRIMARY)
						return;

					wrapper.pushEditEffect(NODE_EDIT_BLUE_GLOW);
					relX = event.getX();
					relY = event.getY();
				});

				node.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
					if (!editMode.get())
						return;

					if (event.getButton() != MouseButton.PRIMARY)
						return;

					// If the last effect was from the click, it's removed from the stack. If the
					// last effect was from a drag, it is removed from the stack, and we remove the
					// click effect below.
					wrapper.removeEditEffect();
					if (dragDetected)
						wrapper.removeEditEffect();
					dragDetected = false;
				});

				node.addEventFilter(MouseEvent.DRAG_DETECTED, event -> {
					if (!editMode.get())
						return;

					dragDetected = true;
					wrapper.pushEditEffect(NODE_EDIT_GOLD_GLOW);
				});

				node.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
					if (!(editMode.get() && dragDetected))
						return;
					node.setLayoutX(event.getSceneX() - relX);
					node.setLayoutY(event.getSceneY() - relY);
				});

			}
		};

		return wrapper;
	}

}
