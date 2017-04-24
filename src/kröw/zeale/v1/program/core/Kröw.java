package kröw.zeale.v1.program.core;

import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.scene.text.Text;
import kröw.libs.Construct;
import kröw.zeale.v1.program.guis.Window;

/**
 * This class is the main class of the program.
 *
 * @author Zeale
 *
 */
public final class Kröw {

	// Constructor
	/**
	 * This constructor is meant to deny any construction of the class
	 * {@link Kröw}.
	 *
	 * @param krow
	 *            A {@link Kröw} object that has already been created.
	 * @throws Throwable
	 *             If you try to use this.
	 */
	private Kröw(final Kröw krow) throws Throwable {
		if (!krow.equals(this))
			throw new Throwable("The class Kröw is not meant to be constructed.");
	}

	// Constants
	/**
	 * The name {@code Kröw}.
	 */
	public static final String NAME = new String("Kröw");

	/**
	 * The {@link DataManager} of the program. This manages data such as the
	 * loaded {@link Construct}s.
	 */
	private static final DataManager DATA_MANAGER = DataManager.getDataManager();

	/**
	 * A static boolean which defines whether or not debug mode is on.
	 */
	public static boolean DEBUG_MODE = true;

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
		return Kröw.DATA_MANAGER;
	}

	// Main method
	/**
	 * The main method.
	 *
	 * @param args
	 *            The program arguments given to the program on launch.
	 */
	public static void main(final String[] args) {
		Kröw.start(args);
	}

	/**
	 * A helper method used to split a {@link String} into an array of
	 * {@link String}s. This will likely be moved to the upcoming API project.
	 *
	 * @param string
	 *            The {@link String} to split.
	 * @return An array containing separated, 1 character long {@link String}s
	 *         that make up the original {@link String} passed in as an
	 *         argument. Basically the same as {@link String#toCharArray()} but
	 *         each <code>char</code> is a {@link String} in a {@link String}
	 *         array.
	 * @see #splitStringToTextArray(String) for more information. It does the
	 *      same thing as this method but with {@link Text} nodes as an output
	 *      rather than a {@link String} array.
	 */
	public static String[] splitStringToStringArray(final String string) {
		final String[] strarr = new String[string.length()];
		for (int i = 0; i < string.length(); i++)
			strarr[i] = String.valueOf(string.charAt(i));
		return strarr;
	}

	/**
	 * A helper method used to split a {@link String} into an array of
	 * {@link Text} nodes. This will likely be moved to the upcoming API
	 * project, just like the {@link #splitStringToStringArray(String)}.
	 *
	 * @param string
	 *            The {@link String} that will be split into an array of
	 *            {@link Text} objects.
	 * @return An array containing {@link Text} objects, each initialized with a
	 *         character from the original given {@link String}, in the order
	 *         that the characters of the {@link String} are.
	 * @see #splitStringToStringArray(String) for more details. It does the
	 *      exact same thing but the output is an array of {@link String}s,
	 *      rather than an array of {@link Text} nodes.
	 */
	public static Text[] splitStringToTextArray(final String string) {
		final Text[] textarr = new Text[string.length()];
		for (int i = 0; i < string.length(); i++)
			textarr[i] = new Text(String.valueOf(string.charAt(i)));
		return textarr;
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
	 *            actual arguments given by the user, <code>Kröw</code> will not
	 *            be able to determine things like debug mode (which depends on
	 *            program arguments).
	 */
	public static void start(final String[] args) {

		// This isn't fixing JavaFX's bad text textures but I'll keep it here...
		System.setProperty("prism.lcdtext", "false");
		System.setProperty("prism.text", "t2k");

		System.out.println("\n\n\n\n");
		System.out.println("Loading data...\n");
		DataManager.loadData();

		if (args != null) {
			final List<String> strings = Arrays.<String>asList(args);
			if (!Kröw.containsIgnoreCase(strings, "-debug-mode") && !Kröw.containsIgnoreCase(strings, "-debug"))
				Kröw.DEBUG_MODE = false;
			if (Kröw.DEBUG_MODE)
				System.out.println("\n\nDebug mode has been enabled...\n\n");
			// If the Window class is loaded from somewhere other than this
			// method, its static constructor causes a RuntimeException.
			Application.launch(Window.LaunchImpl.class, args);
		}

	}

}
