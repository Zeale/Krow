package kröw.core.managers;

import java.awt.MouseInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import krow.backgrounds.Background;
import krow.backgrounds.ShapeBackground;
import kröw.core.Kröw;
import kröw.data.common.kvp.KVPDataObject;
import kröw.data.common.kvp.KeyValuePairData;
import kröw.data.in.KVPDataReader;
import kröw.data.out.KVPDataWriter;
import kröw.events.Event;
import kröw.events.EventHandler;

public final class WindowManager {

	private static Background defaultBackground = null;

	private static final Background loadDefaultBackground() {
		ShapeBackground sb = new ShapeBackground();
		sb.addRandomShapes(50);
		return sb;
	}

	/**
	 * <p>
	 * Thrown when someone tries to switch the current {@link Scene} but the current
	 * {@link Scene}'s controller's {@link App#canSwitchScenes()} method returns
	 * false.
	 *
	 * @author Zeale
	 */
	public final static class NotSwitchableException extends Exception {

		private static final long serialVersionUID = 1L;

		private final Window<? extends App> currentWindow;
		private final App controller;
		private final Class<? extends App> controllerClass;

		public NotSwitchableException(final Window<? extends App> currentWindow, final App controller,
				final Class<? extends App> cls) {
			this.currentWindow = currentWindow;
			this.controller = controller;
			controllerClass = cls;
		}

		/**
		 * @return the controller
		 */
		public final App getController() {
			return controller;
		}

		/**
		 * @return the controllerClass
		 */
		public final Class<? extends App> getControllerClass() {
			return controllerClass;
		}

		/**
		 * @return the currentWindow
		 */
		public final Window<? extends App> getCurrentWindow() {
			return currentWindow;
		}

	}

	public static abstract class App {

		private KeyValuePairData data = new KeyValuePairData();
		private KVPDataReader reader;// = get reader for this page somehow.
		private KVPDataWriter writer;// This stuff will be replaced with the Protection API stuff. :)

		protected final KVPDataObject storeData(String key, KVPDataObject data) {
			return this.data.put(key, data);
		}

		protected final KVPDataObject getData(String key) {
			return data.get(key);
		}

		protected final void saveData() {
			// TODO Write #data to this Page's file so it can be loaded back up later.
		}

		protected final void loadData() {
			// TODO Populate #data from a file.
		}

		/**
		 * Constructs a {@link App} object.
		 */
		protected App() {

		}

		/**
		 * <p>
		 * Sets the default {@link Background}.
		 * <p>
		 * The default {@link Background} is used at the expense of other apps. This
		 * means that it is completely up to another app of whether or not to render
		 * a background from {@link #getDefaultBackground()}.
		 * <p>
		 * For the purpose of allowing the user to keep whatever background they choose
		 * in apps that support it, <b>this method should not be called unless the
		 * user wishes to modify the default background.</b> If a app or page wants
		 * to render a custom background, it should not change any part of the program's
		 * default {@link Background}. The said app should make use of a <i>custom
		 * {@link Background}</i>, so that whatever settings the user has applied can be
		 * kept in this default one.
		 * <p>
		 * One last thing: If the given manager is null, this method will throw an
		 * {@link IllegalArgumentException}. The default {@link Background} is expected
		 * to be used by other apps for rendering the user's chosen background, so if
		 * it's null and other apps try to call methods off of it, there'd be
		 * {@link NullPointerException}s all over the place.
		 * 
		 * @param manager
		 *            The new default {@link Background}. Apps that reference
		 *            {@link #getDefaultBackground()} will be given this new manager
		 *            when they are called.
		 *            <p>
		 *            This cannot be null; such will result in an
		 *            {@link IllegalArgumentException}.
		 */
		protected final void setDefaultBackground(Background manager) {
			if (manager == null)
				throw new IllegalArgumentException("The BackgroundManager mustn't be null.");
			defaultBackground = manager;
		}

		/**
		 * <p>
		 * Returns {@code Kröw}'s default {@link Background}. This is used to store the
		 * user's chosen background settings.
		 * <p>
		 * The default {@link Background} should only be retrieved for
		 * <b><i>showing</i></b> the user's chosen background settings, unless the
		 * {@link App} that makes use of this method allows the user to modify the
		 * default background.
		 * <p>
		 * If a page edited the default {@link Background}, (rather than using its own
		 * to show a background), every time it was initialized, then any settings that
		 * the user saved to the default background manager, would be changed by the
		 * page every time the page was initialized. This is not good. :(
		 * 
		 * @return {@code Kröw}'s default {@link Background}.
		 */
		protected final Background getDefaultBackground() {
			if (defaultBackground == null)
				defaultBackground = loadDefaultBackground();
			return defaultBackground;
		}

		protected final void applyDefaultBackground(Pane pane) {
			getDefaultBackground().show(pane);
		}

		/**
		 * <p>
		 * Checked when switching {@link App}s to verify that the current page permits
		 * the user to go to a different page. This should be overridden to return false
		 * when a window reaches a scenario in which it does not want its user to leave.
		 * <p>
		 * Basically, return false when {@link App}s shouldn't be switched.
		 *
		 * @return Whether or not the scene can currently be switched.
		 */
		public boolean canSwitchPage(final Class<? extends App> newSceneClass) {
			return true;
		}

		/**
		 * <p>
		 * Returns the relative path to the FXML file that this {@link App} represents.
		 *
		 * @return A {@link String} object which represents the relative path of the
		 *         FXML file that this {@link App} represents.
		 */
		public abstract String getWindowFile();

		/**
		 * <p>
		 * This method is called when a {@link App} is initialized.
		 * <p>
		 * Specifically, this method is called after a {@link App} has had its
		 * <code>@FXML</code> fields set. This method is the optimal place for
		 * subclasses to use the {@link WindowManager#setPaneDraggableByNode(Node)}
		 * method.
		 * <p>
		 * This would have been reduced to <code>protected</code> visibility, however
		 * JavaFX needs it public to be able to call it. It should be treated as if it
		 * were <code>protected</code>.
		 */
		public void initialize() {

		}

		/**
		 * <p>
		 * This method is called when {@link WindowManager#goBack()} is called and this
		 * {@link App} is shown. It's is like an extra initialize method which is
		 * called only when the {@link WindowManager#goBack()} method shows this
		 * {@link App}.
		 */
		public void onBack() {

		}

		protected void onPageSwitched() {

		}

	}

	public static class PageChangedEvent extends Event {

		public final Window<? extends App> oldWindow, newWindow;

		private PageChangedEvent(final Window<? extends App> currentPage, final Window<? extends App> window) {
			oldWindow = currentPage;
			newWindow = window;
		}
	}

	public static class PageChangeRequestedEvent extends Event {
		public final Window<? extends App> oldWindow;
		public final Class<? extends App> newPageClass;

		public PageChangeRequestedEvent(final Window<? extends App> oldWindow,
				final Class<? extends App> newPageClass) {
			this.oldWindow = oldWindow;
			this.newPageClass = newPageClass;
		}
	}

	public static final class Window<T extends App> {
		private final T controller;
		private final Parent root;
		private final Stage stageUsed;
		private final Scene sceneUsed;

		public Window(final T controller, final Parent root, final Stage stageUsed, final Scene sceneUsed) {
			this.controller = controller;
			this.root = root;
			this.stageUsed = stageUsed;
			this.sceneUsed = sceneUsed;
		}

		/**
		 * @return the controller
		 */
		public final T getController() {
			return controller;
		}

		/**
		 * @return the root
		 */
		public final Parent getRoot() {
			return root;
		}

		/**
		 * @return the sceneUsed
		 */
		public final Scene getSceneUsed() {
			return sceneUsed;
		}

		/**
		 * @return the stageUsed
		 */
		public final Stage getStageUsed() {
			return stageUsed;
		}

	}

	private static final Stack<Window<? extends App>> history = new Stack<>();

	private static Window<? extends App> currentPage;

	/**
	 * The current {@link Stage}. This is set when the program starts.
	 */
	public static Stage stage;

	private static List<EventHandler<? super PageChangedEvent>> pageChangeHandlers = new ArrayList<>();

	private static List<EventHandler<? super PageChangeRequestedEvent>> pageChangeRequestedHandlers = new ArrayList<>();

	public static void addOnPageChanged(final EventHandler<? super PageChangedEvent> handler) {
		pageChangeHandlers.add(handler);
	}

	public static void addOnPageChangeRequested(final EventHandler<? super PageChangeRequestedEvent> handler) {
		pageChangeRequestedHandlers.add(handler);
	}

	/**
	 * A getter for the {@link Stage} of the running application.
	 *
	 * @return The application's current {@link App#stage}.
	 */
	public static Stage getStage() {
		return WindowManager.stage;
	}

	/**
	 * <p>
	 * Reverts the current {@link Scene} to the previous {@link Scene}.
	 * <p>
	 * If the user switches from the home {@link App} to another {@link App} and
	 * then this method is called, the program will switch back to the home
	 * {@link App}.
	 * <p>
	 * <i>NOTE that this only works when switching prior to this method's call is
	 * done via any of the <code>setScene(...)</code> methods.</i> Using these
	 * methods rather than switching the {@link App} manually is almost necessary
	 * for full functionality.
	 *
	 * @throws NotSwitchableException
	 *             If the current {@link Scene} can't be switched.
	 */
	public static void goBack() throws NotSwitchableException {

		try {
			setScene(WindowManager.history.pop().controller.getClass());
		} catch (InstantiationException | IllegalAccessException | IOException e) {
			e.printStackTrace();
		}

		// Call this method.
		WindowManager.currentPage.controller.onBack();

	}

	/**
	 * Allows the user to drag the given {@link Node} to move the given
	 * {@link javafx.stage.Window}.
	 *
	 * @param window
	 *            The {@link javafx.stage.Window} that will be moved when the
	 *            {@link Node} is dragged.
	 * @param node
	 *            The {@link javafx.stage.Window} that the user will drag to move
	 *            the given {@link Stage}.
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
	 * Sets this application's {@link App#stage} as draggable by the specified
	 * {@link Node}.
	 * <p>
	 * The {@link Node#setOnMousePressed(javafx.event.EventHandler)} and
	 * {@link Node#setOnMouseDragged(javafx.event.EventHandler)} methods are called
	 * on the given {@link Node} to allow the current {@link App#stage} object to
	 * be moved via the user dragging the given {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} that will be used to move the WindowManager.
	 */
	public static void setPaneDraggableByNode(final Node node) {
		/**
		 * This object is made so that the <code>xOffset</code> and <code>yOffset</code>
		 * variables can be used inside the lambda expressions without being made final.
		 *
		 * @author Zeale
		 *
		 */
		new Object() {

			private double xOffset, yOffset;

			{
				node.setOnMousePressed(event -> {
					xOffset = WindowManager.stage.getX() - event.getScreenX();
					yOffset = WindowManager.stage.getY() - event.getScreenY();
				});

				node.setOnMouseDragged(event -> {
					WindowManager.stage.setX(event.getScreenX() + xOffset);
					WindowManager.stage.setY(event.getScreenY() + yOffset);
				});
			}

		};
	}

	/**
	 * <p>
	 * Loads a {@link Scene} object given a subclass of {@link App}.
	 * <p>
	 * Subclasses of the {@link App} object are required to define the
	 * {@link #getWindowFile()} method. (This may change soon). We already have the
	 * {@link Class} object required to call the method
	 * {@link #setScene(Class, String)}, so when we instantiate this {@link Class},
	 * we can get the {@link String} object as well.
	 *
	 * @param cls
	 *            The {@link App} {@link Class} to get the new {@link Scene} from.
	 * @throws InstantiationException
	 *             if the given {@link Class} represents an abstract class, an
	 *             interface, an array class, a primitive type, or void, if the
	 *             class has no nullary constructor, or if the instantiation fails
	 *             for some other reason.
	 * @throws IllegalAccessException
	 *             if the given {@link Class} or its nullary constructor is not
	 *             visible.
	 * @throws IOException
	 *             in case this method fails to load the FXML file.
	 * @throws NotSwitchableException
	 *             If the current {@link Scene} can't be switched.
	 */
	public static <W extends App> Window<W> setScene(final Class<W> cls)
			throws InstantiationException, IllegalAccessException, IOException, NotSwitchableException {

		for (final EventHandler<? super PageChangeRequestedEvent> handler : pageChangeRequestedHandlers)
			handler.handle(new PageChangeRequestedEvent(currentPage, cls));

		final W controller = cls.newInstance();

		// Instantiate the loader
		final FXMLLoader loader = new FXMLLoader(cls.getResource(controller.getWindowFile()));
		loader.setController(controller);
		if (currentPage != null)
			if (!currentPage.getController().canSwitchPage(cls))
				throw new NotSwitchableException(currentPage, controller, cls);
			else {
				WindowManager.history.push(currentPage);
				currentPage.getController().onPageSwitched();
			}

		final Parent root = loader.load();
		final Window<W> window = new Window<>(controller, root, stage, stage.getScene());

		for (final EventHandler<? super PageChangedEvent> handler : pageChangeHandlers)
			handler.handle(new PageChangedEvent(currentPage, window));
		WindowManager.currentPage = window;
		// Set the new root.
		WindowManager.stage.getScene().setRoot(root);

		return window;

	}

	public static <W extends App> Window<W> setScene(final W controller) throws IOException, NotSwitchableException {

		for (final EventHandler<? super PageChangeRequestedEvent> handler : pageChangeRequestedHandlers)
			handler.handle(new PageChangeRequestedEvent(currentPage, controller.getClass()));

		// Instantiate the loader
		final FXMLLoader loader = new FXMLLoader(controller.getClass().getResource(controller.getWindowFile()));
		loader.setController(controller);
		if (currentPage != null)
			if (!currentPage.getController().canSwitchPage(controller.getClass()))
				throw new NotSwitchableException(currentPage, controller, controller.getClass());
			else {
				WindowManager.history.push(currentPage);
				currentPage.getController().onPageSwitched();
			}

		final Parent root = loader.load();
		final Window<W> window = new Window<>(controller, root, stage, stage.getScene());

		for (final EventHandler<? super PageChangedEvent> handler : pageChangeHandlers)
			handler.handle(new PageChangedEvent(currentPage, window));
		WindowManager.currentPage = window;
		// Set the new root.
		WindowManager.stage.getScene().setRoot(root);

		return window;

	}

	/**
	 * Used to set the {@link Stage} contained in this class.
	 *
	 * @param stage
	 *            The new {@link Stage}.
	 * @return the built {@link Window} object.
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @Internal This method is meant for internal use only.
	 *
	 */
	public static Window<? extends App> setStage_Impl(final Stage stage, final Class<? extends App> cls)
			throws IOException, InstantiationException, IllegalAccessException {

		WindowManager.stage = stage;
		final App controller = cls.newInstance();
		final FXMLLoader loader = new FXMLLoader(cls.getResource(controller.getWindowFile()));
		loader.setController(controller);
		final Parent root = loader.load();

		final Window<? extends App> window = new Window<>(controller, root, stage, stage.getScene());

		WindowManager.currentPage = window;
		// Set the new root.
		WindowManager.stage.setScene(new Scene(root));

		return window;
	}

	/**
	 * Spawns a floating piece of text that flies upwards a little then disappears.
	 * The source point of the text is specified via the {@code x} and {@code y}
	 * parameters.
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
		label.setMouseTransparent(true);
		final TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(2), label);
		final FadeTransition opacityTransition = new FadeTransition(Duration.seconds(2), label);

		pc.getScene().setRoot(label);
		/* Style label */
		label.setTextFill(color);
		label.setBackground(null);
		double fontSize = Kröw.getSystemProperties().isDPIOversized() ? 14 : 16;
		fontSize *= 1920 / Kröw.getSystemProperties().getScreenWidth();
		label.setStyle("-fx-font-weight: bold; -fx-font-size: " + fontSize + "px;");
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
		pc.show(WindowManager.stage);
		pc.setHeight(50);
		/* Play the transitions */
		translateTransition.play();
		opacityTransition.play();
	}

	public static void spawnLabelAtMousePos(final String text, final Color color) {
		spawnLabel(text, color, MouseInfo.getPointerInfo().getLocation().getX(),
				MouseInfo.getPointerInfo().getLocation().getY());
	}

}
