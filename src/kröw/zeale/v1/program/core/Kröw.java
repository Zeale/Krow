package kröw.zeale.v1.program.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import krow.zeale.guis.home.HomeWindow;
import wolf.mindset.Construct;
import wolf.mindset.Law;
import wolf.zeale.Wolf;
import wolf.zeale.guis.Window;
import wolf.zeale.lists.ObservableNonDupeArrayListWrapper;

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
public final class Kröw extends Application {
	public static final File KRÖW_HOME_DIRECTORY = new File(
			java.lang.System.getProperty("user.home") + "/Appdata/Roaming/", Wolf.KROW_NAME);

	private static final File README_FILE = new File(Kröw.KRÖW_HOME_DIRECTORY, "Readme.txt"),
			LICENSE_FILE = new File(Kröw.KRÖW_HOME_DIRECTORY, "License.txt"),
			CREDITS_FILE = new File(Kröw.KRÖW_HOME_DIRECTORY, "Credits.txt"),
			PLANS_FILE = new File(Kröw.KRÖW_HOME_DIRECTORY, "Plans.txt");

	/**
	 * The list of loaded {@link Law}s.
	 */
	public final static ObservableNonDupeArrayListWrapper<Law> laws = new ObservableNonDupeArrayListWrapper<>();

	/**
	 * The list of {@link Construct}s that have been loaded in to the program.
	 */
	public final static ObservableNonDupeArrayListWrapper<Construct> constructs = new ObservableNonDupeArrayListWrapper<>();

	/**
	 * The list of {@link System}s.
	 */
	public final static ObservableNonDupeArrayListWrapper<wolf.mindset.System> systems = new ObservableNonDupeArrayListWrapper<>();
	/**
	 * The home directory of the application.
	 */

	static {
		// Create the following folders if they don't already exist and catch
		// any exceptions.
		try {
			Wolf.createFolder(Kröw.KRÖW_HOME_DIRECTORY);
			Wolf.createFolder(Wolf.DATA_DIRECTORY);
			Wolf.createFolder(Wolf.CONSTRUCT_SAVE_DIRECTORY);
			Wolf.createFolder(Wolf.TASK_SAVE_DIRECTORY);
			Wolf.createFolder(Wolf.PROGRAM_SAVE_DIRECTORY);
			Wolf.createFolder(Wolf.SYSTEM_SAVE_DIRECTORY);
			Wolf.createFolder(Wolf.LAW_SAVE_DIRECTORY);
			if (!Kröw.README_FILE.exists())
				Wolf.copyFileToDirectory(new File("resources/krow/zeale/readme.txt"), Kröw.KRÖW_HOME_DIRECTORY);
			if (!Kröw.LICENSE_FILE.exists())
				Wolf.copyFileToDirectory(new File("resources/krow/zeale/license.txt"), Kröw.KRÖW_HOME_DIRECTORY);
			if (!Kröw.CREDITS_FILE.exists())
				Wolf.copyFileToDirectory(new File("resources/krow/zeale/credits.txt"), Kröw.KRÖW_HOME_DIRECTORY);
			if (!Kröw.PLANS_FILE.exists())
				Wolf.copyFileToDirectory(new File("resources/krow/zeale/plans.txt"), Kröw.KRÖW_HOME_DIRECTORY);
		} catch (final RuntimeException e) {
			java.lang.System.err.println(
					"An exception occurred while trying to create or check some necessary directories. The program will print its errors and exit.");
			java.lang.System.out.println("\n\n");

			e.printStackTrace();
			java.lang.System.exit(-1);
			final float a = 4;
			if (a == a)
				java.lang.System.out.println(5);
		}

	}

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
	 * The name {@code Kröw}.
	 */
	public static final String NAME = new String("Kröw");

	public static LinkedList<Construct> getDeadConstructs() {
		final LinkedList<Construct> list = new LinkedList<>();
		for (final Construct c : Kröw.constructs.getObservableList())
			if (!c.isAlive())
				list.add(c);
		return list;
	}

	public static LinkedList<Construct> getFemaleConstructs() {
		final LinkedList<Construct> list = new LinkedList<>();
		for (final Construct c : Kröw.constructs.getObservableList())
			if (c.getGender())
				list.add(c);
		return list;
	}

	public static LinkedList<Construct> getLivingConstructs() {
		final LinkedList<Construct> list = new LinkedList<>();
		for (final Construct c : Kröw.constructs.getObservableList())
			if (c.isAlive())
				list.add(c);
		return list;
	}

	public static LinkedList<Construct> getMaleConstructs() {
		final LinkedList<Construct> list = new LinkedList<>();
		for (final Construct c : Kröw.constructs.getObservableList())
			if (!c.getGender())
				list.add(c);
		return list;
	}

	/**
	 * A static void method that loads data.
	 */
	public static void loadData() {

		if (new File(Kröw.KRÖW_HOME_DIRECTORY, "Data").exists()) {
			TOP: for (final File f : new File(Kröw.KRÖW_HOME_DIRECTORY, "Data/Constructs").listFiles()) {
				for (final File f0 : Wolf.CONSTRUCT_SAVE_DIRECTORY.listFiles())
					if (f0.getName().equals(f.getName())) {
						final File f1 = new File(FileSystemView.getFileSystemView().getHomeDirectory(),
								"Kröw/Constructs");
						f1.mkdir();
						try {
							Files.move(f.toPath(), new File(f1, f.getName()).toPath(),
									StandardCopyOption.REPLACE_EXISTING);
						} catch (final IOException e) {
							e.printStackTrace();
						}
						continue TOP;
					}
				try {
					Files.move(f.toPath(), new File(Wolf.CONSTRUCT_SAVE_DIRECTORY, f.getName()).toPath(),
							StandardCopyOption.REPLACE_EXISTING);
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
			Wolf.deleteDirectory(new File(Kröw.KRÖW_HOME_DIRECTORY, "Data"));
		}
		boolean cons = false, lws = false, systs = false;

		{// Construct Block.
			java.lang.System.out.println("Now loading Constructs from the file system.....");
			final List<Construct> loadedConstructs = new LinkedList<>();
			for (final File f : Wolf.CONSTRUCT_SAVE_DIRECTORY.listFiles())
				try {
					loadedConstructs.add(Wolf.loadObjectFromFile(OldVersionLoader.getInputStream(f)));
				} catch (final IOException e) {
					java.lang.System.err.println("An error occurred while loading a Construct.");
				}

			for (final Construct c : loadedConstructs) {

				java.lang.System.out.println("   \n---Loaded the Construct " + c.getName() + " successfully.");
				Kröw.constructs.add(c);
				cons = true;
			}
			if (!cons)
				java.lang.System.err.println("No Constructs were loaded!...");

		}

		{
			java.lang.System.out.println("\n\nNow loading Laws from the file system.....");
			final List<Law> loadedLaws = new LinkedList<>();
			for (final File f : Wolf.LAW_SAVE_DIRECTORY.listFiles())
				try {
					loadedLaws.add(Wolf.loadObjectFromFile(OldVersionLoader.getInputStream(f)));
				} catch (final IOException e) {
					java.lang.System.err.println("An error occurred while loading a Law.");
				}
			for (final Law l : loadedLaws) {
				java.lang.System.out.println("   \n---Loaded the Law " + l.getName() + " successfully.");
				Kröw.laws.add(l);
				lws = true;
			}

			if (!lws)
				java.lang.System.err.println("No Laws were loaded!...");

		}

		{

			java.lang.System.out.println("\n\nNow loading Systems from the file system.....");
			final List<wolf.mindset.System> loadedSystems = new LinkedList<>();
			for (final File f : Wolf.SYSTEM_SAVE_DIRECTORY.listFiles())
				try {
					loadedSystems.add(Wolf.loadObjectFromFile(OldVersionLoader.getInputStream(f)));
				} catch (final IOException e) {
					java.lang.System.err.println("An error occurred while loading a System.");
				}
			for (final wolf.mindset.System s : loadedSystems) {
				Kröw.systems.add(s);
				systs = true;
			}

			if (!systs)
				java.lang.System.err.println("No Systems were loaded!...");
		}

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

	public static void saveObjects() {
		for (final Construct c : Kröw.constructs.getObservableList())
			try {
				Wolf.saveObject(c, c.getFile(), OldVersionLoader.getOutputStream(c.getFile()));
			} catch (final IOException e) {
				System.err.println("Could not save the Construct " + c.getName());
			}
		for (final Law l : Kröw.laws.getObservableList())
			try {
				Wolf.saveObject(l, l.getFile(), OldVersionLoader.getOutputStream(l.getFile()));
			} catch (final IOException e) {
				System.err.println("Could not save the Law " + l.getName());
			}
		for (final wolf.mindset.System s : Kröw.systems.getObservableList())
			try {
				Wolf.saveObject(s, s.getFile(), OldVersionLoader.getOutputStream(s.getFile()));
			} catch (final IOException e) {
				System.err.println("Could not save the System " + s.getName());
			}
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
		Kröw.loadData();

		if (args != null) {
			final List<String> strings = Arrays.<String>asList(args);
			if (Wolf.containsIgnoreCase(strings, "-debug-mode") || Wolf.containsIgnoreCase(strings, "-debug"))
				Wolf.DEBUG_MODE = true;
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
		primaryStage.setTitle(Kröw.NAME);
		if (Kröw.DARK_CROW != null)
			primaryStage.getIcons().add(Kröw.DARK_CROW);
		else if (Kröw.LIGHT_CROW != null)
			primaryStage.getIcons().add(Kröw.LIGHT_CROW);
		primaryStage.show();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javafx.application.Application#stop()
	 */
	@Override
	public void stop() throws Exception {
		Kröw.saveObjects();
		super.stop();
	}
}