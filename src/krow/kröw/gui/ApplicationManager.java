package kröw.gui;

import java.awt.MouseInfo;
import java.io.IOException;
import java.net.URL;
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
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import kröw.core.Kröw;
import kröw.events.EventHandler;
import kröw.gui.events.PageChangeRequestedEvent;
import kröw.gui.events.PageChangedEvent;
import zeale.guis.Home;

public final class ApplicationManager {

	private static final Callback<Class<?>, Object> CONTROLLER_FACTORY = new Callback<Class<?>, Object>() {

		@Override
		public Object call(Class<?> param) {
			try {
				return param.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				throw new RuntimeException(
						"Ur constructor is retarded. (Kröw couldn't call an Application class's constructor. :(   )");
			}
		}
	};

	public static final class Frame<T extends Application> {
		private final T controller;
		private final Parent root;
		private final Stage stageUsed;
		private final Scene sceneUsed;

		public Frame(final T controller, final Parent root, final Stage stageUsed, final Scene sceneUsed) {
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

	private static final Stack<Frame<? extends Application>> history = new Stack<>();

	private static Frame<? extends Application> currentPage;

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
	 * @return The application's current {@link Application#stage}.
	 */
	public static Stage getStage() {
		return ApplicationManager.stage;
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
	 * Sets this application's {@link Application#stage} as draggable by the
	 * specified {@link Node}.
	 * <p>
	 * The {@link Node#setOnMousePressed(javafx.event.EventHandler)} and
	 * {@link Node#setOnMouseDragged(javafx.event.EventHandler)} methods are called
	 * on the given {@link Node} to allow the current {@link Application#stage}
	 * object to be moved via the user dragging the given {@link Node}.
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
					xOffset = ApplicationManager.stage.getX() - event.getScreenX();
					yOffset = ApplicationManager.stage.getY() - event.getScreenY();
				});

				node.setOnMouseDragged(event -> {
					ApplicationManager.stage.setX(event.getScreenX() + xOffset);
					ApplicationManager.stage.setY(event.getScreenY() + yOffset);
				});
			}

		};
	}

	// NOTE: InputStream prevent JavaFX from resolving locations of CSS files
	// that are specified in an FXML document when the document is loaded from an
	// InputStream, rather than a URL. This is because an InputStream gives a stream
	// of data that can be read by JavaFX, but does not give a location of the file
	// it's streaming data from, like a URL would.
	//
	// Testing loading with InputStreams seemed to follow this theory, and the name
	// kinda implies it...
	public static <W> W setScene(URL fxmlFile) {
		try {
			FXMLLoader loader = getNewLoader(fxmlFile);
			stage.getScene().setRoot(loader.load());
			return loader.getController();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("IOException while opening a URL.");
		}
	}

	public static <W> W setScene(URL fxmlFile, W controller) {
		FXMLLoader loader = new FXMLLoader(fxmlFile);
		if (controller != null)
			loader.setController(controller);
		try {
			stage.getScene().setRoot(loader.load());
			return loader.getController();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("IOException while opening a URL.");
		}
	}

	private static FXMLLoader getNewLoader(URL location) {
		FXMLLoader loader = new FXMLLoader(location);
		loader.setControllerFactory(CONTROLLER_FACTORY);
		return loader;
	}

	public static void initialize(Kröw.InitData data) {
		stage = data.stage;
		try {
			stage.setScene(new Scene(getNewLoader(Home.class.getResource("Home.fxml")).load()));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error initializing.");
		}
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
		pc.show(ApplicationManager.stage);
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
