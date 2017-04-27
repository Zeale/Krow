package kröw.zeale.v1.program.guis;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import krow.zeale.guis.home.HomeWindow;
import kröw.libs.MindsetObject;
import kröw.zeale.v1.program.core.Kröw;

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
 * This class also contains a {@link #setSceneToPreviousScene()} method which
 * changes the currently displayed {@link Scene} to the {@link Scene} being
 * shown prior to that.
 *
 * @author Zeale
 *
 */
public abstract class Window {

	/**
	 * Constructs a {@link Window} object.
	 */
	protected Window() {

	}

	/**
	 * The current {@link Stage}. This is set when the program starts.
	 */
	private static Stage stage;

	/**
	 * The previously showing {@link Scene}. This is set when any of the
	 * <code>setScene</code> methods are called.
	 */
	private static Scene previousScene;

	/**
	 * <p>
	 * {@link #LIGHT_CROW} - The light colored crow image that is used for this
	 * {@link Application}'s icon. This image can be set as the program's icon
	 * via the home window.
	 * <p>
	 * {@link #DARK_CROW} - The dark colored crow image that is used for this
	 * {@link Application}'s icon. This image is the program's default icon.
	 */
	public static final Image LIGHT_CROW, DARK_CROW;
	static {

		Image dark = null, light = null;
		try {
			dark = new Image("/krow/zeale/DarkKröw.png");
		} catch (final IllegalArgumentException e) {
			System.err.println("The Dark Crow icon could not be loaded. Only the Light Crow Icon will be available.");

		}

		try {
			light = new Image("krow/zeale/LightKröw.png");
		} catch (final IllegalArgumentException e) {
			if (dark == null)
				System.err.println(
						"The Light Crow icon could not be loaded either! The icons will be set to the default coffee mug.");
			else
				System.err
						.println("The Light Crow icon could not be loaded. Only the Dark Crow icon will be available.");
		}

		DARK_CROW = dark;
		LIGHT_CROW = light;
	}

	/**
	 * The controller of the current {@link Scene}.
	 */
	private static Window controller;

	/**
	 * The controller of the previous {@link Scene}.
	 */
	private static Window previousController;

	/**
	 * A getter for the current {@link Scene}'s controller.
	 *
	 * @return {@link #controller}
	 */
	public static Window getController() {
		return Window.controller;
	}

	/**
	 * A getter for the previous {@link Scene}'s controller.
	 *
	 * @return {@link #previousController}
	 */
	public static Window getPreviousController() {
		return Window.previousController;
	}

	/**
	 * A getter for the previous {@link Scene}.
	 *
	 * @return {@link #previousScene}
	 */
	public static Scene getPreviousScene() {
		return Window.previousScene;
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
	 * Loads and shows a {@link Scene} object given a {@link String} object and
	 * a {@link Class} object.
	 * <p>
	 * The {@link Class} object is used to load the {@link Scene} from a file,
	 * and the {@link String} is the path of the file relative to the given
	 * {@link Class} object.
	 * <p>
	 * If a class exists in the package <code>my.package</code> with the name
	 * <code>MyWindowController</code> and the FXML file of a {@link Scene}
	 * exists in that same package with a name of <code>MyWindow</code>, a
	 * sufficient call to this method to load in the {@link Scene} from the FXML
	 * file would be as follows:
	 * <ul>
	 * <code>setScene(MyWindowController.class, "MyWindow.fxml");</code>
	 * </ul>
	 * <p>
	 * If <code>MyWindow</code> existed in a sub-package of
	 * <code>my.package</code>, such as <code>my.package.subpackage</code>, then
	 * a sufficient call to this method would involve changing the given
	 * {@link String}. An example is as follows:
	 * <ul>
	 * <code>setScene(MyWindowController.class, "subpackage/MyWindow.fxml</code>
	 * </ul>
	 * <p>
	 * Similar methods exist to change the currently displayed {@link Scene} of
	 * the program. For more details, see {@link #setScene(Class)},
	 * {@link #setScene(String)}, and {@link #setSceneToPreviousScene()}.
	 * <p>
	 * <i>Note that the given class in the parameter of this method does not
	 * have to be a subclass of {@link Window}, but the controller specified in
	 * the FXML file <b>MUST</b> be of the type {@link Window}.</i>
	 *
	 * @param cls
	 *            The {@link Class} that defines the starting path of which to
	 *            get the FXML file.
	 * @param scene
	 *            The path to the FXML file, relative to the specified class.
	 * @throws IOException
	 *             in case this method fails to load the FXML file.
	 */
	public static void setScene(final Class<?> cls, final String scene) throws IOException {
		final FXMLLoader loader = new FXMLLoader(cls.getResource(scene));
		Window.previousScene = Window.stage.getScene();
		Window.previousController = Window.controller;
		Window.stage.setScene(new Scene(loader.load()));
		Window.controller = loader.<Window>getController();
		if (Kröw.DEBUG_MODE) {
			System.out.println();
			System.out.println(Window.previousController == null ? "The previous controller of the window is null."
					: "The previous controller of the window is not null.");
			System.out.println(Window.controller == null ? "The current controller of the window is null."
					: "The current controller of the window is not null.");
		}
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
	 */
	public static <W extends Window> void setScene(final Class<W> cls)
			throws InstantiationException, IllegalAccessException, IOException {
		Window.setScene(cls, cls.newInstance().getWindowFile());

	}

	/**
	 * <p>
	 * Sets the current {@link Scene} of the program to that which is specified.
	 * <p>
	 * This method acts in the same way that its overloaded counterparts do, but
	 * uses this {@link Window}. Calling this method is virtually equivalent to
	 * calling
	 * <ul>
	 * <code>Window.setScene(Window.class, str);</code>
	 * </ul>
	 * where <code>str</code> is the parameter passed into this method.<br>
	 * <br>
	 *
	 * @param scene
	 *            The relative path of the FXML file that will be loaded.
	 * @throws IOException
	 *             Incase this method fails to load the FXML file.
	 */
	public static void setScene(final String scene) throws IOException {
		final FXMLLoader loader = new FXMLLoader(Window.class.getResource(scene));
		Window.previousScene = Window.stage.getScene();
		Window.previousController = Window.controller;
		Window.stage.setScene(new Scene(loader.load()));
		Window.controller = loader.<Window>getController();
		if (Kröw.DEBUG_MODE) {
			System.out.println();
			System.out.println(Window.previousController == null ? "The previous controller of the window is null."
					: "The previous controller of the window is not null.");
			System.out.println(Window.controller == null ? "The current controller of the window is null."
					: "The current controller of the window is not null.");
		}

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
	 */
	public static void setSceneToPreviousScene() {
		final Scene tempScene = Window.getPreviousScene();
		Window.previousScene = Window.stage.getScene();
		Window.stage.setScene(tempScene);
		if (Window.previousController != null)
			Window.previousController.initialize();

		final Window tempController = Window.getPreviousController();
		Window.previousController = Window.controller;
		Window.controller = tempController;

		if (Kröw.DEBUG_MODE) {
			System.out.println();
			System.out.println(Window.previousController == null ? "The previous controller of the window is null."
					: "The previous controller of the window is not null.");
			System.out.println(Window.controller == null ? "The current controller of the window is null."
					: "The current controller of the window is not null.");
		}

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
	 * @InternalAPI
	 *
	 * 				This class is an internal class used to start the JavaFX
	 *              Application.
	 *
	 * @author Zeale
	 *
	 */
	public static class LaunchImpl extends Application {
		/*
		 * (non-Javadoc)
		 *
		 * @see javafx.application.Application#start(javafx.stage.Stage)
		 */
		@Override
		public void start(final Stage primaryStage) throws Exception {
			Window.stage = primaryStage;
			Window.setScene(HomeWindow.class, "Home.fxml");
			primaryStage.initStyle(StageStyle.UNDECORATED);
			primaryStage.setTitle(Kröw.NAME);
			if (Window.DARK_CROW != null)
				primaryStage.getIcons().add(Window.DARK_CROW);
			else if (Window.LIGHT_CROW != null)
				primaryStage.getIcons().add(Window.LIGHT_CROW);
			primaryStage.show();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see javafx.application.Application#stop()
		 */
		@Override
		public void stop() throws Exception {
			final MindsetObjec	t.saveObjects();
			super.stop();
		}
	}

}
