package kröw.zeale.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import krow.zeale.guis.home.HomeWindow;
import wolf.mindset.Construct;
import wolf.mindset.ConstructMindset;
import wolf.mindset.Law;
import wolf.mindset.MindsetObject;
import wolf.mindset.ObjectAlreadyExistsException;
import wolf.zeale.Wolf;
import wolf.zeale.guis.Window;

/**
 * This class is the main class of the program.
 *
 * The JVM loads up the JavaFX toolkit if the main class extends Application. If
 * this class did not extend {@link Application}, the toolkit would not be
 * loaded and the {@link #IMG_LIGHT_CROW} and {@link #IMG_DARK_CROW} images
 * found below, could not be created during <code>clinit</code> (the
 * <code>static</code> constructor).
 *
 * @author Zeale
 *
 */
public final class Kröw extends Application {

	/**
	 * The {@link ConstructMindset} object of {@link Kröw}. This tracks all of
	 * {@link Kröw}'s {@link MindsetObject}s.
	 */
	public final static ConstructMindset CONSTRUCT_MINDSET = new ConstructMindset();

	/**
	 * The Home directory of the {@link Kröw} application.
	 */
	public static final File KRÖW_HOME_DIRECTORY = new File(System.getProperty("user.home") + "/Appdata/Roaming/",
			Wolf.KROW_NAME);

	/**
	 * Files created in the {@link #KRÖW_HOME_DIRECTORY}.
	 */
	private static final File README_FILE = new File(Kröw.KRÖW_HOME_DIRECTORY, "Readme.txt"),
			LICENSE_FILE = new File(Kröw.KRÖW_HOME_DIRECTORY, "License.txt"),
			CREDITS_FILE = new File(Kröw.KRÖW_HOME_DIRECTORY, "Credits.txt"),
			PLANS_FILE = new File(Kröw.KRÖW_HOME_DIRECTORY, "Plans.txt"),
			DATA_INCLUDES_FILE = new File(KRÖW_HOME_DIRECTORY, "includes.kcfg");

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
			Wolf.createFolder(Backup.BACKUP_SAVE_DIRECTORY);
			if (!Kröw.README_FILE.exists())
				Wolf.copyFileToDirectory(new File("resources/krow/zeale/readme.txt"), Kröw.KRÖW_HOME_DIRECTORY);
			if (!Kröw.LICENSE_FILE.exists())
				Wolf.copyFileToDirectory(new File("resources/krow/zeale/license.txt"), Kröw.KRÖW_HOME_DIRECTORY);
			if (!Kröw.CREDITS_FILE.exists())
				Wolf.copyFileToDirectory(new File("resources/krow/zeale/credits.txt"), Kröw.KRÖW_HOME_DIRECTORY);
			if (!Kröw.PLANS_FILE.exists())
				Wolf.copyFileToDirectory(new File("resources/krow/zeale/plans.txt"), Kröw.KRÖW_HOME_DIRECTORY);
			if (!Kröw.DATA_INCLUDES_FILE.exists())
				Wolf.copyFileToDirectory(new File("resources/krow/zeale/includes.kcfg"), KRÖW_HOME_DIRECTORY);
		} catch (final RuntimeException e) {
			System.err.println(
					"An exception occurred while trying to create or check some necessary directories. The program will print its errors and exit.");
			System.out.println("\n\n");

			e.printStackTrace();
			System.exit(-1);
		}

	}

	static {

		Image dark = null, light = null, kröw = null;
		try {
			dark = new Image("/krow/zeale/DarkKröw.png");
		} catch (final IllegalArgumentException e) {
		}

		try {
			light = new Image("krow/zeale/LightKröw.png");
		} catch (final IllegalArgumentException e) {
		}

		try {
			kröw = new Image("krow/zeale/Kröw.png");
		} catch (final IllegalArgumentException e) {
		}

		IMG_DARK_CROW = dark;
		IMG_LIGHT_CROW = light;
		IMG_KRÖW = kröw;
	}

	/**
	 * <p>
	 * {@link #IMG_LIGHT_CROW} - The light colored crow image that is used for
	 * this {@link Application}'s icon. This image can be set as the program's
	 * icon via the home window.
	 * <p>
	 * {@link #IMG_DARK_CROW} - The dark colored crow image that is used for
	 * this {@link Application}'s icon. This image is the program's default
	 * icon.
	 *
	 */
	public static final Image IMG_LIGHT_CROW, IMG_DARK_CROW, IMG_KRÖW;

	/**
	 * The name {@code Kröw}.
	 */
	public static final String NAME = new String("Kröw");

	/**
	 * Clears all the objects in this program.
	 *
	 * @return A {@link Backup} made prior to the clear.
	 */
	public static Backup clearAllObjects() {
		final Backup b = new Backup();
		for (final MindsetObject obj : Kröw.CONSTRUCT_MINDSET.getAllObjects())
			obj.delete();
		return b;
	}

	/**
	 * @return An {@link ArrayList} of all the dead {@link Construct}s managed
	 *         by this program.
	 */
	public static ArrayList<Construct> getDeadConstructs() {
		final ArrayList<Construct> list = new ArrayList<>();
		for (final Construct c : Kröw.CONSTRUCT_MINDSET.getConstructsUnmodifiable())
			if (!c.isAlive())
				list.add(c);
		return list;
	}

	/**
	 * @return An {@link ArrayList} of all the female {@link Construct}s managed
	 *         by this program.
	 */
	public static ArrayList<Construct> getFemaleConstructs() {
		final ArrayList<Construct> list = new ArrayList<>();
		for (final Construct c : Kröw.CONSTRUCT_MINDSET.getConstructsUnmodifiable())
			if (c.getGender())
				list.add(c);
		return list;
	}

	/**
	 * @return An {@link ArrayList} of all the living {@link Construct}s managed
	 *         by this program.
	 */
	public static ArrayList<Construct> getLivingConstructs() {
		final ArrayList<Construct> list = new ArrayList<>();
		for (final Construct c : Kröw.CONSTRUCT_MINDSET.getConstructsUnmodifiable())
			if (c.isAlive())
				list.add(c);
		return list;
	}

	/**
	 * @return An {@link ArrayList} of all the male {@link Construct}s managed
	 *         by this program.
	 */
	public static ArrayList<Construct> getMaleConstructs() {
		final ArrayList<Construct> list = new ArrayList<>();
		for (final Construct c : Kröw.CONSTRUCT_MINDSET.getConstructsUnmodifiable())
			if (!c.getGender())
				list.add(c);
		return list;
	}

	/**
	 * A static void method that loads data.
	 */
	public static void loadData() {

		final File[] oldFiles = new File(Kröw.KRÖW_HOME_DIRECTORY, "Data/Constructs").listFiles();

		if (oldFiles != null)
			TOP: for (final File f : oldFiles) {
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
		final File oldDataDir = new File(Kröw.KRÖW_HOME_DIRECTORY, "Data");
		if (oldDataDir.exists())
			Wolf.deleteDirectory(oldDataDir);

		boolean cons = false;
		boolean lws = false;
		boolean systs = false;

		// Construct Block
		{
			System.out.println("Now attempting to load Constructs from the file system.....");
			for (final File f : Wolf.CONSTRUCT_SAVE_DIRECTORY.listFiles())
				try {
					final Construct c = (Construct) Wolf.loadObjectFromFile(OldVersionLoader.getInputStream(f));
					c.getMindsetModel().attatch(Kröw.CONSTRUCT_MINDSET);
					System.out.println("   \n---Loaded the Construct " + c.getName() + " successfully.");
					if (!cons)
						cons = true;
				} catch (final IOException e) {
					System.err.println("An error occurred while loading a Construct.");
				} catch (final ObjectAlreadyExistsException e) {
					System.err.println("The Construct, " + e.getThrower().getName()
							+ " already exists. It could not be loaded again.");
				}

			if (!cons)
				System.err.println("No Constructs were loaded!...");

		}

		// Law Block
		{
			System.out.println("Now attempting to load Laws from the file system.....");
			for (final File f : Wolf.LAW_SAVE_DIRECTORY.listFiles())
				try {
					final Law l = (Law) Wolf.loadObjectFromFile(OldVersionLoader.getInputStream(f));
					l.getMindsetModel().attatch(Kröw.CONSTRUCT_MINDSET);
					System.out.println("   \n---Loaded the Law " + l.getName() + " successfully.");
					if (!lws)
						lws = true;
				} catch (final IOException e) {
					System.err.println("An error occurred while loading a Law.");
				} catch (final ObjectAlreadyExistsException e) {
					System.err.println(
							"The Law, " + e.getThrower().getName() + " already exists. It could not be loaded again.");
				}

			if (!lws)
				System.err.println("No Laws were loaded!...");

		}

		// System Block
		{
			System.out.println("Now attempting to load Systems from the file system.....");
			for (final File f : Wolf.SYSTEM_SAVE_DIRECTORY.listFiles())
				try {
					final wolf.mindset.System s = (wolf.mindset.System) Wolf
							.loadObjectFromFile(OldVersionLoader.getInputStream(f));
					s.getMindsetModel().attatch(Kröw.CONSTRUCT_MINDSET);
					System.out.println("   \n---Loaded the System " + s.getName() + " successfully.");
					if (!systs)
						systs = true;
				} catch (final IOException e) {
					System.err.println("An error occurred while loading a System.");
				} catch (final ObjectAlreadyExistsException e) {
					System.err.println("The System, " + e.getThrower().getName()
							+ " already exists. It could not be loaded again.");
				}

			if (!systs)
				System.err.println("No Systems were loaded!...");

		}

		Backup.loadBackupsFromSystem();
	}

	/**
	 * Loads a {@link MindsetObject} from the given {@code file} and returns it.
	 *
	 * @param file
	 *            The {@link File} to load the object from.
	 * @return The Object that was loaded.
	 * @throws ClassNotFoundException
	 *             As specified by {@link ObjectInputStream#readObject()}.
	 * @throws FileNotFoundException
	 *             As specified by {@link ObjectInputStream#readObject()}.
	 * @throws IOException
	 *             As specified by {@link ObjectInputStream#readObject()}.
	 */
	public static MindsetObject loadMindsetObject(final File file)
			throws ClassNotFoundException, FileNotFoundException, IOException {
		return (MindsetObject) ((ObjectInputStream) OldVersionLoader.getInputStream(file)).readObject();
	}

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
		for (final Construct c : Kröw.CONSTRUCT_MINDSET.getConstructsUnmodifiable())
			try {
				Wolf.saveObject(c, c.getFile(), OldVersionLoader.getOutputStream(c.getFile()));
			} catch (final IOException e) {
				System.err.println("Could not save the Construct " + c.getName());
			}
		for (final Law l : Kröw.CONSTRUCT_MINDSET.getLawsUnmodifiable())
			try {
				Wolf.saveObject(l, l.getFile(), OldVersionLoader.getOutputStream(l.getFile()));
			} catch (final IOException e) {
				System.err.println("Could not save the Law " + l.getName());
			}
		for (final wolf.mindset.System s : Kröw.CONSTRUCT_MINDSET.getSystemsUnmodifiable())
			try {
				Wolf.saveObject(s, s.getFile(), OldVersionLoader.getOutputStream(s.getFile()));
			} catch (final IOException e) {
				System.err.println("Could not save the System " + s.getName());
			}
	}

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
		System.out.println("\n\n\n\n");
		System.out.println("Loading data...\n");
		Kröw.loadData();

		if (args != null) {
			final List<String> strings = Arrays.<String>asList(args);
			if (Wolf.containsIgnoreCase(strings, "-debug-mode") || Wolf.containsIgnoreCase(strings, "-debug"))
				Wolf.DEBUG_MODE = true;
			for (final String s : strings)
				if (s.startsWith("--import=") && s.length() > 9) {
					final String path = s.substring(9, s.length());
					final File f = new File(path);
					try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(f))) {
						final MindsetObject obj = (MindsetObject) is.readObject();
						obj.getMindsetModel().attatch(CONSTRUCT_MINDSET);

					} catch (final FileNotFoundException e) {
						System.out.println("The input file, [91m" + path + "[0m, was not found...");
					} catch (final IOException e) {
						System.out.println("There was an exception opening the file: " + f.getAbsolutePath());
					} catch (final ClassNotFoundException e) {
						System.out.println("The file, [91m" + path + "[0m, is invalid.");
					} catch (final ClassCastException e) {
						System.out.println("The file, [91m" + path + "[0m, is invalid.");
					} catch (final ObjectAlreadyExistsException e) {
						System.err.println("The " + e.getThrower().getType() + ", " + e.getThrower().getName()
								+ ", already exists. It could not be imported.");
					}
				}
		}
		if (Wolf.DEBUG_MODE)
			System.out.println("\n\nDebug mode has been enabled...\n\n");
		Application.launch(args);
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
		if (Kröw.IMG_KRÖW != null)
			primaryStage.getIcons().add(Kröw.IMG_KRÖW);
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