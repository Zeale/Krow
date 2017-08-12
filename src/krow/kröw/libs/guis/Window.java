package kröw.libs.guis;

import java.awt.MouseInfo;
import java.io.IOException;
import java.util.Stack;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * <p>
 * This class represents a Graphical Window. Windows can be displayed to a user.
 * <p>
 * This class contains static methods involved in switching the current scene of
 * the running JavaFX Application, along with the Application's class itself.
 * All Scene switching should be performed via this class's
 * <code>setScene(...)</code> methods, such as {@link #setScene(Class)} or
 * {@link #setScene(Class, String)}.
 * <p>
 * This class keeps track of the current {@link Scene} and the previous
 * {@link Scene}, so any {@link Scene} switching done outside of this class (via
 * the {@link #stage} object) will not be recognized by this class.
 * <p>
 * This class also contains a {@link #goBack()} method which changes the
 * currently displayed {@link Scene} to the {@link Scene} being shown prior to
 * that.
 *
 * @author Zeale
 *
 */
public abstract class Window {

	private static final Stack<Class<? extends Window>> history = new Stack<>();
	private static Class<? extends Window> currentWindow;

	/**
	 * <p>
	 * Thrown when someone tries to switch the current {@link Scene} but the
	 * current {@link Scene}'s controller's {@link Window#canSwitchScenes()}
	 * method returns false.
	 *
	 * @author Zeale
	 */
	public final static class NotSwitchableException extends Exception {

		private static final long serialVersionUID = 1L;

		private final Parent currentParent, newParent;

		private NotSwitchableException(final Parent currentScene, final Parent newScene) {
			super("Current scene: " + currentScene + " New scene: " + newScene);
			currentParent = currentScene;
			newParent = newScene;
		}

		/**
		 * @return the currentParent
		 */
		public final Parent getCurrentParent() {
			return currentParent;
		}

		/**
		 * @return the newParent
		 */
		public final Parent getNewParent() {
			return newParent;
		}

	}

	/**
	 * The current {@link Stage}. This is set when the program starts.
	 */
	private static Stage stage;

	/**
	 * The controller of the current {@link Scene}.
	 */
	private static Window controller;

	private static Object keyObject = new Object();

	public static Window getController() {
		return controller;
	}

	public static final String getNamePropertyFromParent(final Parent parent) {
		return (String) parent.getProperties().get(keyObject);
	}

	/**
	 * A getter for the {@link Stage} of the running application.
	 *
	 * @return The application's current {@link #stage}.
	 */
	public static Stage getStage() {
		return Window.stage;
	}

	/**
	 * Allows the user to drag the given {@link Node} to move the given
	 * {@link javafx.stage.Window}.
	 *
	 * @param window
	 *            The {@link javafx.stage.Window} that will be moved when the
	 *            {@link Node} is dragged.
	 * @param node
	 *            The {@link javafx.stage.Window} that the user will drag to
	 *            move the given {@link Stage}.
	 */
	public static void setPaneDraggableByNode(final javafx.stage.Window window, final Node node) {
		new Object() {

			private double xOffset, yOffset;

			{
				node.setOnMousePressed(event -> {
					xOffset = window.getX() - event.getScreenX();
					yOffset = window.getY() - event.getScreenY();
				});

				node.setOnMouseDragged(event -> {
					window.setX(event.getScreenX() + xOffset);
					window.setY(event.getScreenY() + yOffset);
				});
			}

		};
	}

	/**
	 * <p>
	 * Sets this application's {@link #stage} as draggable by the specified
	 * {@link Node}.
	 * <p>
	 * The {@link Node#setOnMousePressed(javafx.event.EventHandler)} and
	 * {@link Node#setOnMouseDragged(javafx.event.EventHandler)} methods are
	 * called on the given {@link Node} to allow the current {@link #stage}
	 * object to be moved via the user dragging the given {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} that will be used to move the window.
	 */
	public static void setPaneDraggableByNode(final Node node) {
		/**
		 * This object is made so that the <code>xOffset</code> and
		 * <code>yOffset</code> variables can be used inside the lambda
		 * expressions without being made final.
		 *
		 * @author Zeale
		 *
		 */
		new Object() {

			private double xOffset, yOffset;

			{
				node.setOnMousePressed(event -> {
					xOffset = Window.stage.getX() - event.getScreenX();
					yOffset = Window.stage.getY() - event.getScreenY();
				});

				node.setOnMouseDragged(event -> {
					Window.stage.setX(event.getScreenX() + xOffset);
					Window.stage.setY(event.getScreenY() + yOffset);
				});
			}

		};
	}

	/**
	 * <p>
	 * Loads a {@link Scene} object given a subclass of {@link Window}.
	 * <p>
	 * Subclasses of the {@link Window} object are required to define the
	 * {@link #getWindowFile()} method. (This may change soon). We already have
	 * the {@link Class} object required to call the method
	 * {@link #setScene(Class, String)}, so when we instantiate this
	 * {@link Class}, we can get the {@link String} object as well.
	 *
	 * @param cls
	 *            The {@link Window} {@link Class} to get the new {@link Scene}
	 *            from.
	 * @throws InstantiationException
	 *             if the given {@link Class} represents an abstract class, an
	 *             interface, an array class, a primitive type, or void, if the
	 *             class has no nullary constructor, or if the instantiation
	 *             fails for some other reason.
	 * @throws IllegalAccessException
	 *             if the given {@link Class} or its nullary constructor is not
	 *             visible.
	 * @throws IOException
	 *             in case this method fails to load the FXML file.
	 * @throws NotSwitchableException
	 *             If the current {@link Scene} can't be switched.
	 */
	public static <W extends Window> void setScene(final Class<W> cls)
			throws InstantiationException, IllegalAccessException, IOException, NotSwitchableException {
		history.push(currentWindow);
		// Instantiate the loader
		final FXMLLoader loader = new FXMLLoader(cls.getResource(cls.newInstance().getWindowFile()));
		final Parent root = loader.load();

		if (!controller.canSwitchScenes(cls))
			throw new NotSwitchableException(stage.getScene().getRoot(), root);

		currentWindow = cls;
		// Set the new root.
		Window.stage.getScene().setRoot(root);

		// Assign the controller.
		Window.controller = loader.<Window>getController();
		root.getProperties().put(keyObject, controller.getWindowName());
	}

	/**
	 * <p>
	 * Reverts the current {@link Scene} to the previous {@link Scene}.
	 * <p>
	 * If the user switches from the home {@link Window} to another
	 * {@link Window} and then this method is called, the program will switch
	 * back to the home {@link Window}.
	 * <p>
	 * <i>NOTE that this only works when switching prior to this method's call
	 * is done via any of the <code>setScene(...)</code> methods.</i> Using
	 * these methods rather than switching the {@link Window} manually is almost
	 * necessary for full functionality.
	 *
	 * @throws NotSwitchableException
	 *             If the current {@link Scene} can't be switched.
	 */
	public static void goBack() throws NotSwitchableException {

		if (!controller.canSwitchScenes(history.peek()))
			throw new NotSwitchableException(stage.getScene().getRoot(), null);

		try {
			setScene(history.pop());
		} catch (InstantiationException | IllegalAccessException | IOException e) {
			e.printStackTrace();
		}

		// Call this method.
		Window.controller.onRevertToThisWindow();

	}

	public static Class<? extends Window> getPreviousWindowClass() {
		return history.peek();
	}

	/**
	 * Used to set the {@link Stage} contained in this class.
	 *
	 * @param stage
	 *            The new {@link Stage}.
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @Internal This method is meant for internal use only.
	 *
	 */
	public static void setStage_Impl(final Stage stage, final Class<? extends Window> cls)
			throws IOException, InstantiationException, IllegalAccessException {
		Window.stage = stage;
		final FXMLLoader loader = new FXMLLoader(cls.getResource(((Window) cls.newInstance()).getWindowFile()));
		stage.setScene(new Scene(loader.load()));
		controller = loader.getController();
		currentWindow = cls;
	}

	/**
	 * Spawns a floating piece of text that flies upwards a little then
	 * disappears. The source point of the text is specified via the {@code x}
	 * and {@code y} parameters.
	 *
	 * @param text
	 *            The text to render.
	 * @param color
	 *            The color of the rendered text.
	 * @param x
	 *            The starting x position of the text.
	 * @param y
	 *            The starting y position of the text.
	 */
	public static void spawnLabel(final String text, final Color color, final double x, final double y) {
		final Popup pc = new Popup();
		final Label label = new Label(text);
		final TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(2), label);
		final FadeTransition opacityTransition = new FadeTransition(Duration.seconds(2), label);

		pc.getScene().setRoot(label);
		/* Style label */
		label.setTextFill(color);
		label.setBackground(null);
		label.setStyle("-fx-font-weight: bold;");
		/* Set Popup positions */
		pc.setX(x);
		pc.setWidth(label.getMaxWidth());
		pc.setY(y - 50);
		/* Build transitions */
		translateTransition.setFromY(30);
		translateTransition.setFromX(0);
		translateTransition.setToX(0);
		translateTransition.setToY(5);
		translateTransition.setInterpolator(Interpolator.EASE_OUT);
		opacityTransition.setFromValue(0.7);
		opacityTransition.setToValue(0.0);
		opacityTransition.setOnFinished(e -> pc.hide());
		/* Show the Popup */
		pc.show(stage);
		pc.setHeight(50);
		/* Play the transitions */
		translateTransition.play();
		opacityTransition.play();
	}

	public static void spawnLabelAtMousePos(final String text, final Color color) {
		spawnLabel(text, color, MouseInfo.getPointerInfo().getLocation().getX(),
				MouseInfo.getPointerInfo().getLocation().getY());
	}

	/**
	 * Constructs a {@link Window} object.
	 */
	protected Window() {

	}

	/**
	 * <p>
	 * Checked when switching {@link Scene}s to verify that the current window
	 * permits the user to go to a different window. This should be overridden
	 * to return false when a window reaches a scenario in which it does not
	 * want its user to leave.
	 * <p>
	 * Basically, return false when {@link Scene}s shouldn't be switched.
	 *
	 * @return Whether or not the scene can currently be switched.
	 */
	public boolean canSwitchScenes(final Class<? extends Window> newSceneClass) {
		return true;
	}

	/**
	 * <p>
	 * Returns the relative path to the FXML file that this {@link Window}
	 * represents.
	 *
	 * @return A {@link String} object which represents the relative path of the
	 *         FXML file that this {@link Window} represents.
	 */
	public abstract String getWindowFile();

	public abstract String getWindowName();

	/**
	 * <p>
	 * This method is called when a {@link Window} is initialized.
	 * <p>
	 * Specifically, this method is called after a {@link Window} has had its
	 * <code>@FXML</code> fields set. This method is the optimal place for
	 * subclasses to use the {@link Window#setPaneDraggableByNode(Node)} method.
	 * <p>
	 * This would have been reduced to <code>protected</code> visibility,
	 * however JavaFX needs it public to be able to call it. It should be
	 * treated as if it were <code>protected</code>.
	 */
	public void initialize() {

	}

	/**
	 * <p>
	 * This method is called when {@link Window#goBack()} is called and this
	 * {@link Window} is shown. It's is like an extra initialize method which is
	 * called only when the {@link #goBack()} method shows this {@link Window}.
	 */
	public void onRevertToThisWindow() {

	}

}