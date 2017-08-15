package kröw.core;

import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import kröw.annotations.AutoLoad;
import kröw.annotations.LoadTime;
import kröw.core.managers.GlobalSettingsManager;
import kröw.core.managers.SoundManager;
import kröw.core.managers.SystemProperties;
import kröw.core.managers.SystemTrayManager;
import kröw.core.managers.WindowManager;
import kröw.libs.Timer;
import kröw.libs.mindset.Construct;
import kröw.libs.mindset.ConstructMindset;
import kröw.libs.mindset.Law;
import kröw.libs.mindset.MindsetObject;
import kröw.libs.mindset.ObjectAlreadyExistsException;
import sun.awt.shell.ShellFolder;
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
public final class Kröw extends Application {

	private static ArrayList<Class<?>> reflectionClasses = new ArrayList<>();

	public static void addReflectionClass(Class<?> cls) {
		reflectionClasses.add(cls);
	}

	public static final EventHandler<KeyEvent> CLOSE_ON_ESCAPE_HANADLER = event -> {
		if (event.getCode() == KeyCode.ESCAPE)
			Platform.exit();
	};

	public static final EventHandler<Event> CLOSE_PROGRAM_EVENT_HANDLER = event -> Platform.exit();

	/*
	 * Construct Mindset
	 */
	/**
	 * The {@link ConstructMindset} object of {@link Kröw}. This tracks all of
	 * {@link Kröw}'s {@link MindsetObject}s.
	 */
	public final static ConstructMindset CONSTRUCT_MINDSET = new ConstructMindset();

	/*
	 * Files and directories.
	 */
	public static final File JAR_FILE_CURRENT_PATH;

	static {
		File temp;
		try {
			temp = new File(Kröw.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
		} catch (final URISyntaxException e) {
			temp = null;
		}

		JAR_FILE_CURRENT_PATH = temp;
	}

	public static final File USER_HOME_DIRECTORY = new File(System.getProperty("user.home"));

	public static final File USER_APPDATA_DIRECTORY = new File(USER_HOME_DIRECTORY, "Appdata/Roaming/");

	/**
	 * The Home directory of the {@link Kröw} application.
	 */
	public static final File KRÖW_HOME_DIRECTORY = new File(USER_APPDATA_DIRECTORY, "Krow");
	public static final File KRÖW_INSTALL_FILE;

	static {
		URL iconURL = null;
		try {
			iconURL = new URL("http://dusttoash.org/favicon.ico");
		} catch (final MalformedURLException e2) {
			e2.printStackTrace();
		}
		File icon;
		if (!(icon = new File(KRÖW_HOME_DIRECTORY, "Krow.ico")).exists())
			try (InputStream is = iconURL.openStream()) {
				Files.copy(is, icon.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (final IOException e1) {
				e1.printStackTrace();
			}
		if (JAR_FILE_CURRENT_PATH != null & !(KRÖW_INSTALL_FILE = new File(KRÖW_HOME_DIRECTORY, "Krow.jar")).exists()
				&& !JAR_FILE_CURRENT_PATH.equals(KRÖW_INSTALL_FILE))
			try {
				Files.copy(JAR_FILE_CURRENT_PATH.toPath(), KRÖW_INSTALL_FILE.toPath(),
						StandardCopyOption.REPLACE_EXISTING);

				final String app = "powershell.exe";

				Process proc;
				final BufferedReader stdout = new BufferedReader(
						new InputStreamReader((proc = Runtime.getRuntime().exec(app)).getInputStream())),
						errout = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
				final PrintWriter pw = new PrintWriter(proc.getOutputStream());
				pw.println("$objShell = New-Object -ComObject WScript.Shell");
				pw.println("$lnk = $objShell.CreateShortcut(\"" + "Krow.lnk" + "\")");
				pw.println("$lnk.TargetPath = \"" + KRÖW_INSTALL_FILE.getAbsolutePath() + "\"");
				pw.println("$lnk.Description = \"Use this to launch Krow...\"");
				pw.println("$lnk.IconLocation = \"" + icon.getAbsolutePath() + "\"");
				pw.println("$lnk.Save()");
				pw.flush();

				pw.close();

				String s;
				while ((s = stdout.readLine()) != null)
					System.out.println(s);
				while ((s = errout.readLine()) != null)
					System.err.println(s);

				proc.destroy();

				if (!JAR_FILE_CURRENT_PATH.delete()) {
					Runtime.getRuntime().exec("java -jar " + KRÖW_INSTALL_FILE.getAbsolutePath() + " ---"
							+ JAR_FILE_CURRENT_PATH.getAbsolutePath());

					System.exit(0);
				}

			} catch (final IOException e) {
				e.printStackTrace();
			}
	}

	public static final Image getImageFromFile(File dir, int width, int height) throws FileNotFoundException {
		return SwingFXUtils
				.toFXImage(Kröw.toBufferedImage(ShellFolder.getShellFolder(dir).getIcon(true), width, height), null);
	}

	/**
	 * This variable defines whether or not debug mode is on.
	 */
	public static boolean DEBUG_MODE;

	/**
	 * Kröw's name.
	 */
	public static final String KROW_NAME = "Kröw";

	/**
	 * The directory for data storage of this application.
	 */
	public final static File DATA_DIRECTORY = new File(KRÖW_HOME_DIRECTORY, "Data");

	public static final File MANAGER_DIRECTORY = new File(KRÖW_HOME_DIRECTORY, "Program Managers");

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
	 * The directory for storing {@link kröw.libs.mindset.System}s.
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

	public static final File SETTINGS_FILE = new File(KRÖW_HOME_DIRECTORY, "settings.cnfg");

	public static final File USER_STARTUP_FOLDER = new File(USER_APPDATA_DIRECTORY,
			"Microsoft/Windows/Start Menu/Programs/Startup");
	public static final File COMMON_STARTUP_FOLDER = new File(
			"C:/ProgramData/Microsoft/Windows/Start Menu/Programs/Startup");

	/**
	 * Files created in the {@link #KRÖW_HOME_DIRECTORY}.
	 */
	@SuppressWarnings("unused")
	private static final File README_FILE = new File(Kröw.KRÖW_HOME_DIRECTORY, "Readme.txt"),
			LICENSE_FILE = new File(Kröw.KRÖW_HOME_DIRECTORY, "License.txt"),
			CREDITS_FILE = new File(Kröw.KRÖW_HOME_DIRECTORY, "Credits.txt"),
			PLANS_FILE = new File(Kröw.KRÖW_HOME_DIRECTORY, "Plans.txt"),
			DATA_INCLUDES_FILE = new File(KRÖW_HOME_DIRECTORY, "includes.kcfg");

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
	public static final Image IMAGE_LIGHT_CROW, IMAGE_DARK_CROW, IMAGE_KRÖW;

	/**
	 * The name {@code Kröw}.
	 */
	public static final String NAME = new String("Kröw");

	private static GlobalSettingsManager globalSettingsManager;
	private static SoundManager soundManager = new SoundManager();
	private static SystemProperties systemProperties = new SystemProperties();
	private static SystemTrayManager systemTrayManager = new SystemTrayManager();

	/**
	 * @return the systemTrayManager
	 */
	public static final SystemTrayManager getSystemTrayManager() {
		return systemTrayManager;
	}

	public static SystemProperties getSystemProperties() {
		return systemProperties;
	}

	static {

		// Create the following folders if they don't already exist and catch
		// any exceptions.
		try {
			Kröw.createFolder(Kröw.KRÖW_HOME_DIRECTORY);
			Kröw.createFolder(Kröw.DATA_DIRECTORY);
			Kröw.createFolder(Kröw.CONSTRUCT_SAVE_DIRECTORY);
			Kröw.createFolder(Kröw.TASK_SAVE_DIRECTORY);
			Kröw.createFolder(Kröw.PROGRAM_SAVE_DIRECTORY);
			Kröw.createFolder(Kröw.SYSTEM_SAVE_DIRECTORY);
			Kröw.createFolder(Kröw.LAW_SAVE_DIRECTORY);
			Kröw.createFolder(Backup.BACKUP_SAVE_DIRECTORY);
			Kröw.createFolder(MANAGER_DIRECTORY);

			try {
				globalSettingsManager = GlobalSettingsManager.loadManager(GlobalSettingsManager.DEFAULT_FILE_PATH);
			} catch (final FileNotFoundException e) {
				try {
					globalSettingsManager = GlobalSettingsManager
							.createManager(GlobalSettingsManager.DEFAULT_FILE_PATH);
				} catch (final IOException e1) {
					e1.printStackTrace();
				}
			} catch (final IOException e) {
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

		Image dark = null, light = null, kröw = null;
		try {
			dark = new Image("krow/resources/DarkKröw.png");
		} catch (final IllegalArgumentException e) {
			e.printStackTrace();
		}

		try {
			light = new Image("krow/resources/LightKröw.png");
		} catch (final IllegalArgumentException e) {
			e.printStackTrace();
		}

		try {
			kröw = new Image("krow/resources/Kröw_hd.png");
		} catch (final IllegalArgumentException e) {
			e.printStackTrace();
		}

		IMAGE_DARK_CROW = dark;
		IMAGE_LIGHT_CROW = light;
		IMAGE_KRÖW = kröw;
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
	 *            if this parameter is a file.
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
		return new File(Kröw.class.getProtectionDomain().getCodeSource().getLocation().getFile());
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

	public static GlobalSettingsManager getGlobalSettingsManager() {
		return globalSettingsManager;
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
	 * @return the soundManager
	 */
	public static final SoundManager getSoundManager() {
		return soundManager;
	}

	/**
	 * A static void method that loads data.
	 *
	 * @deprecated This is not up to date and will soon be removed.
	 */
	@Deprecated
	public static void loadData() {

		final File[] oldFiles = new File(Kröw.KRÖW_HOME_DIRECTORY, "Data/Constructs").listFiles();

		if (oldFiles != null)
			TOP: for (final File f : oldFiles) {
				for (final File f0 : Kröw.CONSTRUCT_SAVE_DIRECTORY.listFiles())
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
					Files.move(f.toPath(), new File(Kröw.CONSTRUCT_SAVE_DIRECTORY, f.getName()).toPath(),
							StandardCopyOption.REPLACE_EXISTING);
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		final File oldDataDir = new File(Kröw.KRÖW_HOME_DIRECTORY, "Data");
		if (oldDataDir.exists())
			Kröw.deleteDirectory(oldDataDir);

		boolean cons = false;
		boolean lws = false;
		boolean systs = false;

		// Construct Block
		{
			System.out.println("Now attempting to load Constructs from the file system.....");
			for (final File f : Kröw.CONSTRUCT_SAVE_DIRECTORY.listFiles())
				try {
					final Construct c = (Construct) Kröw.loadObjectFromFile(OldVersionLoader.getInputStream(f));
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
			for (final File f : Kröw.LAW_SAVE_DIRECTORY.listFiles())
				try {
					final Law l = (Law) Kröw.loadObjectFromFile(OldVersionLoader.getInputStream(f));
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
			for (final File f : Kröw.SYSTEM_SAVE_DIRECTORY.listFiles())
				try {
					final kröw.libs.mindset.System s = (kröw.libs.mindset.System) Kröw
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
				final O obj = Kröw.<O>loadObjectFromFile(file);
				if (obj != null)
					list.add(obj);

			} catch (final EOFException e) {
				continue;
			} catch (final IOException e) {
				java.lang.System.err.println("Could not load in the Object " + file.getName() + " from the path   "
						+ file.getAbsolutePath());
				e.printStackTrace();
				if (Kröw.DEBUG_MODE) {
					java.lang.System.out.println("\n\n\n");
					e.printStackTrace();
				}
			}

		return list;
	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            The program arguments given to the program on launch.
	 * @throws FileNotFoundException
	 */
	public static void main(final String[] args) throws FileNotFoundException {
		new Thread(() -> {
			if (args.length == 1 && args[0].startsWith("---")) {
				final File f = new File(args[0].substring(3));

				while (!f.delete() && f.exists())
					;
			}
		}).start();

		Kröw.start(args);
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

	public static void saveObjects() {
		for (final Construct c : Kröw.CONSTRUCT_MINDSET.getConstructsUnmodifiable())
			try {
				Kröw.saveObject(c, c.getFile(), OldVersionLoader.getOutputStream(c.getFile()));
			} catch (final IOException e) {
				System.err.println("Could not save the Construct " + c.getName());
			}
		for (final Law l : Kröw.CONSTRUCT_MINDSET.getLawsUnmodifiable())
			try {
				Kröw.saveObject(l, l.getFile(), OldVersionLoader.getOutputStream(l.getFile()));
			} catch (final IOException e) {
				System.err.println("Could not save the Law " + l.getName());
			}
		for (final kröw.libs.mindset.System s : Kröw.CONSTRUCT_MINDSET.getSystemsUnmodifiable())
			try {
				Kröw.saveObject(s, s.getFile(), OldVersionLoader.getOutputStream(s.getFile()));
			} catch (final IOException e) {
				System.err.println("Could not save the System " + s.getName());
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
		// Kröw.loadData();

		if (Kröw.DEBUG_MODE)
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

		Platform.setImplicitExit(false);

		addDefaultLoadupClasses();
		// Run things in reflectionClasses
		for (Class<?> c : reflectionClasses) {
			for (Method m : c.getDeclaredMethods()) {
				if (m.isAnnotationPresent(AutoLoad.class)
						&& m.getAnnotation(AutoLoad.class).value().equals(LoadTime.PROGRAM_ENTER)) {
					m.setAccessible(true);
					Object invObj = new Object();
					if (!Modifier.isStatic(m.getModifiers()))
						try {
							Constructor<?> constructor = c.getDeclaredConstructor();
							constructor.setAccessible(true);
							invObj = constructor.newInstance();
						} catch (NoSuchMethodException | IllegalArgumentException | InstantiationException
								| InvocationTargetException | ExceptionInInitializerError e) {
							e.printStackTrace();
						}
					m.invoke(invObj);
				}
			}
		}
		WindowManager.setStage_Impl(primaryStage, Home.class);

		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setTitle(Kröw.NAME);
		if (Kröw.IMAGE_KRÖW != null)
			primaryStage.getIcons().add(Kröw.IMAGE_KRÖW);
		WindowManager.getStage().getScene().setFill(Color.TRANSPARENT);
		primaryStage.setFullScreen(true);
		primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

		primaryStage.getScene().setOnKeyPressed(CLOSE_ON_ESCAPE_HANADLER);

		primaryStage.show();

	}

	private static void addDefaultLoadupClasses() {
		addReflectionClass(SystemTrayManager.class);
	}

	public static Image iconToImage(Icon icon) {
		return iconToImage(icon, icon.getIconWidth(), icon.getIconHeight());
	}

	public static Image iconToImage(Icon icon, int width, int height) {
		if (icon instanceof ImageIcon) {
			return SwingFXUtils.toFXImage(toBufferedImage(((ImageIcon) icon).getImage(), width, height), null);
		} else {
			BufferedImage image = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
					.getDefaultConfiguration().createCompatibleImage(width, height);
			Graphics2D g = image.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			icon.paintIcon(null, g, 0, 0);
			g.dispose();

			return SwingFXUtils.toFXImage(image, null);
		}
	}

	public static BufferedImage toBufferedImage(java.awt.Image image, int width, int height) {
		BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D outputGraphics = output.createGraphics();
		outputGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		outputGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		outputGraphics.drawImage(image, 0, 0, width, height, null);
		outputGraphics.dispose();

		return output;
	}

	public static BufferedImage toBufferedImage(java.awt.Image image) {
		return toBufferedImage(image, image.getWidth(null), image.getHeight(null));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javafx.application.Application#stop()
	 */
	@Override
	public void stop() throws Exception {
		Kröw.saveObjects();
		globalSettingsManager.save(GlobalSettingsManager.DEFAULT_FILE_PATH);

		super.stop();
	}

	public final static void exit() {
		for (Class<?> c : reflectionClasses) {
			for (Method m : c.getDeclaredMethods()) {
				if (m.isAnnotationPresent(AutoLoad.class)
						&& m.getAnnotation(AutoLoad.class).value().equals(LoadTime.PROGRAM_EXIT)) {
					m.setAccessible(true);
					Object invObj = new Object();
					if (!Modifier.isStatic(m.getModifiers()))
						try {
							Constructor<?> constructor = c.getDeclaredConstructor();
							constructor.setAccessible(true);
							invObj = constructor.newInstance();
						} catch (NoSuchMethodException | IllegalArgumentException | InstantiationException
								| InvocationTargetException | ExceptionInInitializerError | IllegalAccessException e) {
							e.printStackTrace();
						}
					try {
						m.invoke(invObj);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}
		Platform.exit();
	}

}