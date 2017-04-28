package kr�w.zeale.v1.program.core;

import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import krow.zeale.guis.home.HomeWindow;
import wolf.mindset.Construct;
import wolf.mindset.DataManager;
import wolf.mindset.MindsetObject;
import wolf.zeale.Wolf;
import wolf.zeale.guis.Window;

/**
 * This class is the main class of the program.
 *
 * The JVM loads up the JavaFX toolkit if the main class extends Application. If
 * this class did not extend {@link Application}, the toolkit would not be
 * loaded and the {@link #LIGHT_CROW} and {@link #DARK_CROW} images found below,
 * could not be created during <code>clinit</code> (the <code>static</code>
 * constructor).
 *
 * @author Zeale
 *
 */
public final class Kr�w extends Application {

	static {

		Image dark = null, light = null;
		try {
			dark = new Image("/krow/zeale/DarkKr�w.png");
		} catch (final IllegalArgumentException e) {
			System.err.println("The Dark Crow icon could not be loaded. Only the Light Crow Icon will be available.");

		}

		try {
			light = new Image("krow/zeale/LightKr�w.png");
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
	 * <p>
	 * {@link #LIGHT_CROW} - The light colored crow image that is used for this
	 * {@link Application}'s icon. This image can be set as the program's icon
	 * via the home window.
	 * <p>
	 * {@link #DARK_CROW} - The dark colored crow image that is used for this
	 * {@link Application}'s icon. This image is the program's default icon.
	 */
	public static final Image LIGHT_CROW, DARK_CROW;

	// Constants
	/**
	 * The name {@code Kr�w}.
	 */
	public static final String NAME = new String("Kr�w");

	/**
	 * The {@link DataManager} of the program. This manages data such as the
	 * loaded {@link Construct}s.
	 */
	private static final DataManager DATA_MANAGER = new DataManager();

	// Public static methods
	/**
	 * A static helper method that checks if a {@link List} contains a given
	 * {@link String}. The {@link List} must have been instantiated with a type
	 * argument of {@link String}.
	 *
	 * @param list
	 *            The {@link List} to check through.
	 * @param string
	 *            The {@link String} that will be checked for existence in the
	 *            {@link List}
	 * @return true if this {@link List} contains the given {@link String}
	 *         ignoring case.
	 */
	public static boolean containsIgnoreCase(final List<String> list, final String string) {
		for (final String s : list)
			if (s.equalsIgnoreCase(string))
				return true;
		return false;
	}

	/**
	 * A getter for the {@link DataManager}.
	 *
	 * @return The {@link DataManager} of the current running instance of the
	 *         program.
	 */
	public static DataManager getDataManager() {
		return Kr�w.DATA_MANAGER;
	}

	// Main method
	/**
	 * The main method.
	 *
	 * @param args
	 *            The program arguments given to the program on launch.
	 */
	public static void main(final String[] args) {
		Kr�w.start(args);
	}

	// Start method
	/**
	 * The start method of the program. This will load up and initialize the
	 * entire program. Nothing else is needed. It can be called from a main
	 * method or somewhere else.
	 *
	 * @param args
	 *            The program arguments passed in from the main method. This can
	 *            be null or can say whatever, but if it does not contain the
	 *            actual arguments given by the user, <code>Kr�w</code> will not
	 *            be able to determine things like debug mode (which depends on
	 *            program arguments).
	 */
	public static void start(final String[] args) {

		// This isn't fixing JavaFX's bad text textures but I'll keep it here...
		System.setProperty("prism.lcdtext", "false");
		System.setProperty("prism.text", "t2k");

		System.out.println("\n\n\n\n");
		System.out.println("Loading data...\n");
		Kr�w.DATA_MANAGER.loadData();

		if (args != null) {
			final List<String> strings = Arrays.<String>asList(args);
			if (!Kr�w.containsIgnoreCase(strings, "-debug-mode") && !Kr�w.containsIgnoreCase(strings, "-debug"))
				Wolf.DEBUG_MODE = false;
			if (Wolf.DEBUG_MODE)
				System.out.println("\n\nDebug mode has been enabled...\n\n");
			// If the Window class is loaded from somewhere other than this
			// method, its static constructor causes a RuntimeException.

			Application.launch(args);
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(final Stage primaryStage) throws Exception {
		Window.setStage_Impl(primaryStage);
		Window.setScene(HomeWindow.class, "Home.fxml");
		primaryStage.initStyle(StageStyle.UNDECORATED);
		primaryStage.setTitle(Kr�w.NAME);
		if (Kr�w.DARK_CROW != null)
			primaryStage.getIcons().add(Kr�w.DARK_CROW);
		else if (Kr�w.LIGHT_CROW != null)
			primaryStage.getIcons().add(Kr�w.LIGHT_CROW);
		primaryStage.show();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javafx.application.Application#stop()
	 */
	@Override
	public void stop() throws Exception {
		MindsetObject.saveObjects();
		super.stop();
	}

}
