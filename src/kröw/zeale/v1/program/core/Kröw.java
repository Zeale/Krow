package kr�w.zeale.v1.program.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.scene.text.Text;
import kr�w.zeale.v1.program.guis.Window;

public final class Kr�w {

	// Constructor
	private Kr�w() {

	}

	// Constants
	public static final String NAME = new String("Kr�w");

	// The DataManager.
	private static final DataManager DATA_MANAGER = DataManager.getDataManager();

	// DebugMode boolean.
	public static boolean DEBUG_MODE = true;

	// Public static methods
	public static boolean containsIgnoreCase(final List<String> list, final String string) {
		for (final String s : list)
			if (s.equalsIgnoreCase(string))
				return true;
		return false;
	}

	public static DataManager getDataManager() {
		return Kr�w.DATA_MANAGER;
	}

	// Main method
	public static void main(final String[] args) throws FileNotFoundException, IOException {
		Kr�w.start(args);
	}

	public static String[] splitStringToStringArray(final String string) {
		final String[] strarr = new String[string.length()];
		for (int i = 0; i < string.length(); i++)
			strarr[i] = String.valueOf(string.charAt(i));
		return strarr;
	}

	public static Text[] splitStringToTextArray(final String string) {
		final Text[] textarr = new Text[string.length()];
		for (int i = 0; i < string.length(); i++)
			textarr[i] = new Text(String.valueOf(string.charAt(i)));
		return textarr;
	}

	// Start method
	public static void start(final String[] args) {

		// This isn't fixing JavaFX's bad text textures but I'll keep it here...
		System.setProperty("prism.lcdtext", "false");
		System.setProperty("prism.text", "t2k");

		System.out.println("\n\n\n\n");
		System.out.println("Loading data...\n");
		DataManager.loadData();

		if (args != null) {
			final List<String> strings = Arrays.<String>asList(args);
			if (!Kr�w.containsIgnoreCase(strings, "-debug-mode") && !Kr�w.containsIgnoreCase(strings, "-debug"))
				Kr�w.DEBUG_MODE = false;
			if (Kr�w.DEBUG_MODE)
				System.out.println("\n\nDebug mode has been enabled...\n\n");
			// If the Window class is loaded from somewhere other than this
			// method, its static constructor causes a RuntimeException.
			Application.launch(Window.LaunchImpl.class, args);
		}

	}

}
