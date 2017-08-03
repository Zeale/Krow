package kr�w.core;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.filechooser.FileSystemView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import kr�w.libs.Timer;
import kr�w.libs.guis.Window;
import kr�w.libs.mindset.Construct;
import kr�w.libs.mindset.ConstructMindset;
import kr�w.libs.mindset.Law;
import kr�w.libs.mindset.MindsetObject;
import kr�w.libs.mindset.ObjectAlreadyExistsException;
import zeale.guis.Home;

/**
 * This class is the main class of the program.
 *
 * The JVM loads up the JavaFX toolkit if the main class extends Application. If
 * this class did not extend {@link Application}, the toolkit would not be
 * loaded and the {@link #IMAGE_LIGHT_CROW} and {@link #IMAGE_DARK_CROW} images
 * found below, could not be created during <code>clinit</code> (the
 * <code>static</code> constructor).
 *
 * @author Zeale
 *
 */
public final class Kr�w extends Application {

	public static final EventHandler<KeyEvent> CLOSE_ON_ESCAPE_HANADLER = (event -> {
		if (event.getCode() == KeyCode.ESCAPE)
			Platform.exit();
	});

	/*
	 * Screen width and height
	 */
	public final static double SCREEN_HEIGHT, SCREEN_WIDTH;

	static {
		final Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();

		SCREEN_HEIGHT = screenDimensions.getHeight();
		SCREEN_WIDTH = screenDimensions.getWidth();
	}

	/*
	 * Construct Mindset
	 */
	/**
	 * The {@link ConstructMindset} object of {@link Kr�w}. This tracks all of
	 * {@link Kr�w}'s {@link MindsetObject}s.
	 */
	public final static ConstructMindset CONSTRUCT_MINDSET = new ConstructMindset();

	/*
	 * Files and directories.
	 */
	/**
	 * The Home directory of the {@link Kr�w} application.
	 */
	public static final File KR�W_HOME_DIRECTORY = new File(System.getProperty("user.home") + "/Appdata/Roaming/",
			Kr�w.KROW_NAME);

	/**
	 * This variable defines whether or not debug mode is on.
	 */
	public static boolean DEBUG_MODE;

	/**
	 * Kr�w's name.
	 */
	public static final String KROW_NAME = "Kr�w";

	/**
	 * The directory for data storage of this application.
	 */
	public final static File DATA_DIRECTORY = new File(KR�W_HOME_DIRECTORY, "Data");

	/**
	 * The directory for storing {@link Construct}s.
	 */
	public static final File CONSTRUCT_SAVE_DIRECTORY = new File(DATA_DIRECTORY, "Constructs");

	/**
	 * The directory for storing Tasks. (These currently don't exist.)
	 */
	public static final File TASK_SAVE_DIRECTORY = new File(DATA_DIRECTORY, "Tasks");

	/**
	 * The directory for storing Programs. (These currently don't exist.)
	 */
	public static final File PROGRAM_SAVE_DIRECTORY = new File(DATA_DIRECTORY, "Programs");

	/**
	 * The directory for storing {@link kr�w.libs.mindset.System}s.
	 */
	public static final File SYSTEM_SAVE_DIRECTORY = new File(DATA_DIRECTORY, "Systems");

	/**
	 * The directory for storing {@link Law}s
	 */
	public static final File LAW_SAVE_DIRECTORY = new File(DATA_DIRECTORY, "Laws");

	/**
	 * The directory for storing Policies.
	 */
	public static final File POLICY_SAVE_DIRECTORY = new File(DATA_DIRECTORY, "Policies");

	/**
	 * The directory for storing Families.
	 */
	public static final File FAMILY_SAVE_DIRECTORY = new File(DATA_DIRECTORY, "Families");

	public static final File SETTINGS_FILE = new File(KR�W_HOME_DIRECTORY, "settings.cnfg");

	/**
	 * Files created in the {@link #KR�W_HOME_DIRECTORY}.
	 */
	private static final File README_FILE = new File(Kr�w.KR�W_HOME_DIRECTORY, "Readme.txt"),
			LICENSE_FILE = new File(Kr�w.KR�W_HOME_DIRECTORY, "License.txt"),
			CREDITS_FILE = new File(Kr�w.KR�W_HOME_DIRECTORY, "Credits.txt"),
			PLANS_FILE = new File(Kr�w.KR�W_HOME_DIRECTORY, "Plans.txt"),
			DATA_INCLUDES_FILE = new File(KR�W_HOME_DIRECTORY, "includes.kcfg");

	/**
	 * <p>
	 * {@link #IMAGE_LIGHT_CROW} - The light colored crow image that is used for
	 * this {@link Application}'s icon. This image can be set as the program's
	 * icon via the home window.
	 * <p>
	 * {@link #IMAGE_DARK_CROW} - The dark colored crow image that is used for
	 * this {@link Application}'s icon. This image is the program's default
	 * icon.
	 *
	 */
	public static final Image IMAGE_LIGHT_CROW, IMAGE_DARK_CROW, IMAGE_KR�W;

	/**
	 * The name {@code Kr�w}.
	 */
	public static final String NAME = new String("Kr�w");

	static {
		// Create the following folders if they don't already exist and catch
		// any exceptions.
		try {
			Kr�w.createFolder(Kr�w.KR�W_HOME_DIRECTORY);
			Kr�w.createFolder(Kr�w.DATA_DIRECTORY);
			Kr�w.createFolder(Kr�w.CONSTRUCT_SAVE_DIRECTORY);
			Kr�w.createFolder(Kr�w.TASK_SAVE_DIRECTORY);
			Kr�w.createFolder(Kr�w.PROGRAM_SAVE_DIRECTORY);
			Kr�w.createFolder(Kr�w.SYSTEM_SAVE_DIRECTORY);
			Kr�w.createFolder(Kr�w.LAW_SAVE_DIRECTORY);
			Kr�w.createFolder(Backup.BACKUP_SAVE_DIRECTORY);
			try {
				if (!Kr�w.README_FILE.exists())
					Kr�w.copyFileToDirectory(new File("src/resources/krow/readme.txt"), Kr�w.KR�W_HOME_DIRECTORY);
				if (!Kr�w.LICENSE_FILE.exists())
					Kr�w.copyFileToDirectory(new File("src/resources/krow/license.txt"), Kr�w.KR�W_HOME_DIRECTORY);
				if (!Kr�w.CREDITS_FILE.exists())
					Kr�w.copyFileToDirectory(new File("src/resources/krow/credits.txt"), Kr�w.KR�W_HOME_DIRECTORY);
				if (!Kr�w.PLANS_FILE.exists())
					Kr�w.copyFileToDirectory(new File("src/resources/krow/plans.txt"), Kr�w.KR�W_HOME_DIRECTORY);
				if (!Kr�w.DATA_INCLUDES_FILE.exists())
					Kr�w.copyFileToDirectory(new File("src/resources/krow/includes.kcfg"), KR�W_HOME_DIRECTORY);
			} catch (IOException e) {
				if(DEBUG_MODE)
					e.printStackTrace();
			}
		} catch (final RuntimeException e) {
			System.err.println(
					"An exception occurred while trying to create or check some necessary directories. The program will print its errors and exit.");
			System.out.println("\n\n");

			e.printStackTrace();
			System.exit(-1);
		}

	}

	static {

		Image dark = null, light = null, kr�w = null;
		try {
			dark = new Image("krow/resources/DarkKr�w.png");
		} catch (final IllegalArgumentException e) {
			e.printStackTrace();
		}

		try {
			light = new Image("krow/resources/LightKr�w.png");
		} catch (final IllegalArgumentException e) {
			e.printStackTrace();
		}

		try {
			kr�w = new Image("krow/resources/Kr�w_hd.png");
		} catch (final IllegalArgumentException e) {
			e.printStackTrace();
		}

		IMAGE_DARK_CROW = dark;
		IMAGE_LIGHT_CROW = light;
		IMAGE_KR�W = kr�w;
	}

	/**
	 * Clears all the objects in this program.
	 *
	 * @return A {@link Backup} made prior to the clear.
	 */
	public static Backup clearAllObjects() {
		final Backup b = new Backup();
		for (final MindsetObject obj : Kr�w.CONSTRUCT_MINDSET.getAllObjects())
			obj.delete();
		return b;
	}

	/**
	 * @return An {@link ArrayList} of all the dead {@link Construct}s managed
	 *         by this program.
	 */
	public static ArrayList<Construct> getDeadConstructs() {
		final ArrayList<Construct> list = new ArrayList<>();
		for (final Construct c : Kr�w.CONSTRUCT_MINDSET.getConstructsUnmodifiable())
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
		for (final Construct c : Kr�w.CONSTRUCT_MINDSET.getConstructsUnmodifiable())
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
		for (final Construct c : Kr�w.CONSTRUCT_MINDSET.getConstructsUnmodifiable())
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
		for (final Construct c : Kr�w.CONSTRUCT_MINDSET.getConstructsUnmodifiable())
			if (!c.getGender())
				list.add(c);
		return list;
	}

	/**
	 * A static void method that loads data.
	 * 
	 * @deprecated This is not up to date and will soon be removed.
	 */
	@Deprecated
	public static void loadData() {

		final File[] oldFiles = new File(Kr�w.KR�W_HOME_DIRECTORY, "Data/Constructs").listFiles();

		if (oldFiles != null)
			TOP: for (final File f : oldFiles) {
				for (final File f0 : Kr�w.CONSTRUCT_SAVE_DIRECTORY.listFiles())
					if (f0.getName().equals(f.getName())) {
						final File f1 = new File(FileSystemView.getFileSystemView().getHomeDirectory(),
								"Kr�w/Constructs");
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
					Files.move(f.toPath(), new File(Kr�w.CONSTRUCT_SAVE_DIRECTORY, f.getName()).toPath(),
							StandardCopyOption.REPLACE_EXISTING);
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		final File oldDataDir = new File(Kr�w.KR�W_HOME_DIRECTORY, "Data");
		if (oldDataDir.exists())
			Kr�w.deleteDirectory(oldDataDir);

		boolean cons = false;
		boolean lws = false;
		boolean systs = false;

		// Construct Block
		{
			System.out.println("Now attempting to load Constructs from the file system.....");
			for (final File f : Kr�w.CONSTRUCT_SAVE_DIRECTORY.listFiles())
				try {
					final Construct c = (Construct) Kr�w.loadObjectFromFile(OldVersionLoader.getInputStream(f));
					c.getMindsetModel().attatch(Kr�w.CONSTRUCT_MINDSET);
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
			for (final File f : Kr�w.LAW_SAVE_DIRECTORY.listFiles())
				try {
					final Law l = (Law) Kr�w.loadObjectFromFile(OldVersionLoader.getInputStream(f));
					l.getMindsetModel().attatch(Kr�w.CONSTRUCT_MINDSET);
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
			for (final File f : Kr�w.SYSTEM_SAVE_DIRECTORY.listFiles())
				try {
					final kr�w.libs.mindset.System s = (kr�w.libs.mindset.System) Kr�w
							.loadObjectFromFile(OldVersionLoader.getInputStream(f));
					s.getMindsetModel().attatch(Kr�w.CONSTRUCT_MINDSET);
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
		Kr�w.start(args);
	}

	public static void saveObjects() {
		for (final Construct c : Kr�w.CONSTRUCT_MINDSET.getConstructsUnmodifiable())
			try {
				Kr�w.saveObject(c, c.getFile(), OldVersionLoader.getOutputStream(c.getFile()));
			} catch (final IOException e) {
				System.err.println("Could not save the Construct " + c.getName());
			}
		for (final Law l : Kr�w.CONSTRUCT_MINDSET.getLawsUnmodifiable())
			try {
				Kr�w.saveObject(l, l.getFile(), OldVersionLoader.getOutputStream(l.getFile()));
			} catch (final IOException e) {
				System.err.println("Could not save the Law " + l.getName());
			}
		for (final kr�w.libs.mindset.System s : Kr�w.CONSTRUCT_MINDSET.getSystemsUnmodifiable())
			try {
				Kr�w.saveObject(s, s.getFile(), OldVersionLoader.getOutputStream(s.getFile()));
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
	 *            actual arguments given by the user, <code>Kr�w</code> will not
	 *            be able to determine things like debug mode (which depends on
	 *            program arguments).
	 */
	public static void start(final String[] args) {
		System.out.println("\n\n\n\n");
		System.out.println("Loading data...\n");
		// Kr�w.loadData();

		if (Kr�w.DEBUG_MODE)
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

		Window.setStage_Impl(primaryStage, Home.class);
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setTitle(Kr�w.NAME);
		if (Kr�w.IMAGE_KR�W != null)
			primaryStage.getIcons().add(Kr�w.IMAGE_KR�W);
		Window.getStage().getScene().setFill(Color.TRANSPARENT);
		primaryStage.setFullScreen(true);
		primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

		primaryStage.getScene().setOnKeyPressed(CLOSE_ON_ESCAPE_HANADLER);
		primaryStage.show();

		// Adds the given ImageView to the window on startup. The
		// ImageView can have its own onClick event handlers attached,
		// Along with any other event handlers it wants.
		// ((Home) Window.getController()).addImage(new ImageView(IMAGE_KR�W));

		// We can also call the clear method to delete all the current images.
		// The Home window loads by default with some images, so we can use
		// This to clear those and add our own.
		// ((Home)Window.getController()).clearImages();

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javafx.application.Application#stop()
	 */
	@Override
	public void stop() throws Exception {
		Kr�w.saveObjects();
		super.stop();
	}

	/**
	 * Calculates the average runtime of a {@code runnable} after running a
	 * specific number of times.
	 *
	 * @param runs
	 *            The amount of times to run the {@code runnable}.
	 * @param runnable
	 *            The code to execute.
	 * @return The average runtime lf the executed piece of code.
	 */
	public static double calculateAverageRunTime(final long runs, final Runnable runnable) {
		final Timer timer = new Timer();

		long totalTime = 0;
		for (long i = 0; i < runs; i++) {
			timer.startTimer();
			runnable.run();
			final long t = timer.endTimer();
			totalTime += t;
		}
		return (double) totalTime / runs;

	}

	/**
	 * Calculates the average amount of time that a given {@code runnable} takes
	 * to run. This time may be lower than that returned by
	 * {@link #calculateTime(Runnable)} because this operation runs multiple
	 * times and the JIT compiler may <b>heavily optimize</b> a given operation.
	 *
	 * @param runnable
	 *            The code to execute.
	 * @return The average runtime of the executed code.
	 */
	public static double calculateAverageRunTime(final Runnable runnable) {
		return calculateAverageRunTime(200000000, runnable);
	}

	/**
	 * Calculates that amount of time that the {@code runnable} takes to execute
	 * in nanoseconds.
	 *
	 * @param runnable
	 *            The {@code runnable} to execute.
	 * @return The time that the operation took in nanoseconds.
	 */
	public static long calculateTime(final Runnable runnable) {
		final Timer timer = new Timer();
		timer.startTimer();
		runnable.run();
		return timer.endTimer();
	}

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
	 * Copies a {@link File} from a location into a given {@code directory}.
	 *
	 * @param file
	 *            The {@link File} that will be copied.
	 * @param directory
	 *            The parent folder of the copied {@link File}.
	 */
	public static void copyFileToDirectory(final File file, final File directory) throws IOException {
		Files.copy(file.toPath(), new File(directory, file.getName()).toPath());
	}

	/**
	 * This method attempts to create a folder in the specified {@link File}.
	 *
	 * @param fileObj
	 *            The {@link File} where the folder will be created.
	 */
	public static void createFolder(final File fileObj) {
		if (fileObj.isFile()) {
			java.lang.System.out.println(
					"The folder " + fileObj.getAbsolutePath() + " already exists as a file. It will be deleted now...");
			if (!fileObj.delete())
				throw new RuntimeException("The folder " + fileObj.getName() + " could not be deleted.");
		}

		if (!fileObj.exists()) {
			java.lang.System.out
					.println("The folder " + fileObj.getAbsolutePath() + " does not exist yet. It will be made now.");
			fileObj.mkdirs();
		} else
			java.lang.System.out
					.println("The folder " + fileObj.getAbsolutePath() + " already exists as a folder. All is well.");

	}

	/**
	 * <p>
	 * Removes all duplicate elements from a given {@link List}.
	 * <p>
	 * This operation depends on objects correctly implementing the
	 * {@link Object#equals(Object)} and {@link Object#hashCode()} methods.
	 * <p>
	 * This operation also needs a {@link List} that has implemented
	 * {@link List#clear()} and {@link List#addAll(java.util.Collection)} as
	 * specified.
	 * <p>
	 * After this method runs, the {@link List} given will have no duplicate
	 * elements of <code>e1</code> and <code>e2</code> such that
	 * <code>e1.equals(e2)</code> returns <code>true</code>. In other words, if
	 * you compare any two different objects in the {@code list}, the objects
	 * will never <code>.equals</code> each other.
	 *
	 * @param list
	 *            The {@link List} that this operation will be applied to.
	 */
	public static <T> void dedupeList(final List<T> list) {
		final Set<T> set = new HashSet<>();
		set.addAll(list);
		list.clear();
		list.addAll(set);
	}

	/**
	 * Deletes a given {@code directory} by recursively calling this method on
	 * every sub-folder or sub-file in the given {@code directory}.
	 *
	 * @param directory
	 *            The directory that will be deleted. This method will work fine
	 *            if this is a file.
	 */
	public static void deleteDirectory(final File directory) {
		if (directory.isDirectory())
			for (final File f : directory.listFiles())
				deleteDirectory(f);
		directory.delete();
	}

	public static List<File> getAllFilesFromDirectory(final File directory) {
		final List<File> files = new ArrayList<>();
		if (directory.isDirectory())
			for (final File f0 : directory.listFiles())
				for (final File f1 : getAllFilesFromDirectory(f0))
					files.add(f1);
		else
			files.add(directory);
		return files;
	}

	/**
	 * Returns the current directory of the running program's <code>.jar</code>
	 * file.
	 *
	 * @return A <code>new</code> {@link File} representing the program's parent
	 *         folder.
	 */
	public static File getCurrentDirectory() {
		return new File(Kr�w.class.getProtectionDomain().getCodeSource().getLocation().getFile());
	}

	/**
	 * A method that loads a {@link Serializable} object from a given file. The
	 * file should contain only one {@link Serializable} object.
	 *
	 * @param file
	 *            The {@link File} to load the object from.
	 * @param <O>
	 *            The type of the object that will be loaded.
	 * @return The {@link Object} that was loaded.
	 * @throws IOException
	 *             If something goes wrong as stated in
	 *             {@link ObjectInputStream#ObjectInputStream(java.io.InputStream)}.
	 */
	@SuppressWarnings("unchecked")
	public final static <O extends Serializable> O loadObjectFromFile(final File file) throws IOException {
		try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(file));) {
			final O o = (O) is.readObject();
			return o;

		} catch (final ClassNotFoundException e) {
			java.lang.System.err.println(
					"Could not load in the Object " + file.getName() + " from the path   " + file.getAbsolutePath());
			return null;
		}
	}

	/**
	 * Loads an object from a file given an {@link ObjectInputStream}.
	 *
	 * @param is
	 *            The {@link ObjectInputStream} to load the object with.
	 * @param <O>
	 *            The type of the object that will be loaded.
	 * @return The loaded file.
	 * @throws IOException
	 *             In case {@link ObjectInputStream#readObject()} throws an
	 *             error.
	 */
	@SuppressWarnings("unchecked")
	public final static <O extends Serializable> O loadObjectFromFile(final ObjectInputStream is) throws IOException {
		try {
			return (O) is.readObject();
		} catch (final ClassNotFoundException e) {
			java.lang.System.err.println("Could not load in an object");
			return null;
		}
	}

	/**
	 * This method will return an {@link ArrayList} (casted to a {@link List})
	 * which contains multiple objects loaded from a directory of files. The
	 * objects must be of the given type parameter.
	 *
	 * @param directory
	 *            The directory to load objects from.
	 * @param <O>
	 *            The type of the object that will be loaded.
	 * @return A new {@link ArrayList} containing all the loaded objects.
	 */
	public final static <O extends Serializable> List<O> loadObjectsFromDirectory(final File directory) {
		final ArrayList<O> list = new ArrayList<>();
		for (final File file : directory.listFiles())
			try {
				final O obj = Kr�w.<O>loadObjectFromFile(file);
				if (obj != null)
					list.add(obj);

			} catch (final EOFException e) {
				continue;
			} catch (final IOException e) {
				java.lang.System.err.println("Could not load in the Object " + file.getName() + " from the path   "
						+ file.getAbsolutePath());
				e.printStackTrace();
				if (Kr�w.DEBUG_MODE) {
					java.lang.System.out.println("\n\n\n");
					e.printStackTrace();
				}
			}

		return list;
	}

	/**
	 * Saves a {@link Serializable} object given a {@link File} path.
	 *
	 * @param object
	 *            The object that will be saved.
	 * @param file
	 *            The path to save the object to.
	 * @return <code>true</code> if the operation succeeded, <code>false</code>
	 *         otherwise.
	 */
	public static boolean saveObject(final Serializable object, final File file) {

		boolean wasDir = false;
		try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file))) {

			file.getParentFile().mkdirs();

			if (file.isDirectory())
				if (file.listFiles().length == 0) {
					wasDir = true;
					file.delete();
					file.createNewFile();
				} else
					return false;
			if (wasDir)
				file.delete();
			final boolean existed = !file.createNewFile();

			if (existed) {
				file.delete();
				file.createNewFile();
			}
			os.writeObject(object);

			return true;
		} catch (final IOException e) {
			e.printStackTrace();
			if (wasDir && !file.exists())
				file.mkdir();
			return false;
		}
	}

	/**
	 * Saves a serializable object given a {@link File} and an
	 * {@link ObjectOutputStream}. The {@link File} should be the same
	 * {@link File} used in creating the {@link ObjectOutputStream}.
	 *
	 * @param object
	 *            The {@link Serializable} object to save.
	 * @param file
	 *            The {@link File} to save the object to.
	 * @param os
	 *            The {@link ObjectOutputStream} to use to save the object.
	 * @return <code>true</code> if the operation succeeded, <code>false</code>
	 *         otherwise.
	 */
	public static boolean saveObject(final Serializable object, final File file, final ObjectOutputStream os) {

		boolean wasDir = false;
		try {

			file.getParentFile().mkdirs();

			if (file.isDirectory())
				if (file.listFiles().length == 0) {
					wasDir = true;
					file.delete();
					file.createNewFile();
				} else
					return false;
			if (wasDir)
				file.delete();
			final boolean existed = !file.createNewFile();

			if (existed) {
				file.delete();
				file.createNewFile();
			}
			os.writeObject(object);

			return true;
		} catch (final IOException e) {
			e.printStackTrace();
			if (wasDir && !file.exists())
				file.mkdir();
			return false;
		}
	}

	/**
	 * A helper method used to split a {@link String} into an array of
	 * {@link String}s.
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
	 * {@link Text} nodes.
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
}