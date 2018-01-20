package kröw.core;

import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

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
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import kröw.annotations.AutoLoad;
import kröw.annotations.LoadTime;
import kröw.core.managers.ProgramSettings;
import kröw.core.managers.SoundManager;
import kröw.core.managers.SystemProperties;
import kröw.core.managers.SystemTrayManager;
import kröw.gui.WindowManager;
import sun.awt.shell.ShellFolder;
import sun.instrument.InstrumentationImpl;
import sun.misc.Unsafe;
import sun.reflect.ReflectionFactory;
import zeale.guis.Home;
import zeale.guis.developer_page.ConsoleApp;

/**
 * The main class of Krow. It contains many useful methods.
 *
 * @author Zeale
 *
 */
public final class Kröw extends Application {

	public static final Color SOLID_BACKGROUND = new Color(0.23, 0.23, 0.23, 1),
			MODERATELY_TRANSPARENT_BACKGROUND = new Color(0, 0, 0, 0.76862745098),
			COMPLETELY_TRANSPARENT_BACKGROUND = Color.TRANSPARENT;

	private static ArrayList<Class<?>> reflectionClasses = new ArrayList<>();

	public static final EventHandler<KeyEvent> CLOSE_ON_ESCAPE_HANADLER = event -> {
		if (event.getCode() == KeyCode.ESCAPE)
			Kröw.exit();
	};

	public static final EventHandler<Event> CLOSE_PROGRAM_EVENT_HANDLER = event -> Kröw.exit();

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
	public final static File MODULE_DIRECTORY = new File(DATA_DIRECTORY, "Apps");
	public static final File MANAGER_DIRECTORY = new File(KRÖW_HOME_DIRECTORY, "Program Managers");

	public static final File USER_STARTUP_FOLDER = new File(USER_APPDATA_DIRECTORY,
			"Microsoft/Windows/Start Menu/Programs/Startup");

	public static final File COMMON_STARTUP_FOLDER = new File(
			"C:/ProgramData/Microsoft/Windows/Start Menu/Programs/Startup");

	/**
	 * <p>
	 * {@link #IMAGE_LIGHT_CROW} - The light colored crow image that is used for
	 * this {@link Application}'s icon. This image can be set as the program's icon
	 * via the home window.
	 * <p>
	 * {@link #IMAGE_DARK_CROW} - The dark colored crow image that is used for this
	 * {@link Application}'s icon. This image is the program's default icon.
	 *
	 */
	public static final String IMAGE_LIGHT_CROW_LOCATION = "krow/resources/LightKröw.png",
			IMAGE_DARK_CROW_LOCATION = "krow/resources/DarkKröw.png",
			IMAGE_KRÖW_LOCATION = "krow/resources/Kröw_hd.png";

	/**
	 * The name {@code Kröw}.
	 */
	public static final String NAME = new String("Kröw");

	private static ProgramSettings programSettings;
	private static SoundManager soundManager = new SoundManager();
	private static SystemProperties systemProperties = new SystemProperties();
	private static SystemTrayManager systemTrayManager = new SystemTrayManager();

	static {

		// Create the following folders if they don't already exist and catch
		// any exceptions.
		try {
			Kröw.createFolder(Kröw.KRÖW_HOME_DIRECTORY);
			Kröw.createFolder(Kröw.DATA_DIRECTORY);
			Kröw.createFolder(MANAGER_DIRECTORY);

			try {
				programSettings = ProgramSettings.loadManager(ProgramSettings.DEFAULT_FILE_PATH);
			} catch (final FileNotFoundException e) {
				try {
					programSettings = ProgramSettings.createManager(ProgramSettings.DEFAULT_FILE_PATH);
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

	public static PrintStream defout = System.out, deferr = System.err;

	public static final PrintStream out = ConsoleApp.out, err = ConsoleApp.err, wrn = ConsoleApp.wrn,
			scs = ConsoleApp.scs;

	private static void addDefaultLoadupClasses() {
		addReflectionClass(SystemTrayManager.class);
	}

	public static void addReflectionClass(final Class<?> cls) {
		reflectionClasses.add(cls);
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

	public final static void exit() {
		for (final Class<?> c : reflectionClasses)
			for (final Method m : c.getDeclaredMethods())
				if (m.isAnnotationPresent(AutoLoad.class)
						&& m.getAnnotation(AutoLoad.class).value().equals(LoadTime.PROGRAM_EXIT)) {
					m.setAccessible(true);
					Object invObj = new Object();
					if (!Modifier.isStatic(m.getModifiers()))
						try {
							final Constructor<?> constructor = c.getDeclaredConstructor();
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
		Platform.exit();
	}

	public static final Image getImageFromFile(final File dir, final int width, final int height)
			throws FileNotFoundException {
		return SwingFXUtils
				.toFXImage(Kröw.toBufferedImage(ShellFolder.getShellFolder(dir).getIcon(true), width, height), null);
	}

	public static ProgramSettings getProgramSettings() {
		return programSettings;
	}

	/**
	 * @return the soundManager
	 */
	public static final SoundManager getSoundManager() {
		return soundManager;
	}

	public static SystemProperties getSystemProperties() {
		return systemProperties;
	}

	/**
	 * @return the systemTrayManager
	 */
	public static final SystemTrayManager getSystemTrayManager() {
		return systemTrayManager;
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

	public static boolean openLink(final String link) {
		try {
			Desktop.getDesktop().browse(new URI(link));
			return true;
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static double scaleHeight(final double height) {
		return height / 1080 * Kröw.getSystemProperties().getScreenHeight();
	}

	public static int scaleHeight(final int height) {
		return (int) ((double) height / 1080 * Kröw.getSystemProperties().getScreenHeight());
	}

	public static double scaleWidth(final double width) {
		return width / 1920 * Kröw.getSystemProperties().getScreenWidth();
	}

	public static int scaleWidth(final int width) {
		return (int) ((double) width / 1920 * Kröw.getSystemProperties().getScreenWidth());
	}

	/**
	 * The start method of the program. This will load up and initialize the entire
	 * program. Nothing else is needed. It can be called from a main method or
	 * somewhere else.
	 *
	 * @param args
	 *            The program arguments passed in from the main method. This can be
	 *            null or can say whatever, but if it does not contain the actual
	 *            arguments given by the user, <code>Kröw</code> will not be able to
	 *            determine things like debug mode (which depends on program
	 *            arguments).
	 */
	public static void start(final String[] args) {
		System.out.println("\n\n\n\n");
		System.out.println("Loading data...\n");

		if (Kröw.DEBUG_MODE)
			System.out.println("\n\nDebug mode has been enabled...\n\n");
		Application.launch(args);
	}

	public static BufferedImage toBufferedImage(final java.awt.Image image, final int width, final int height) {
		final BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D outputGraphics = output.createGraphics();
		outputGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		outputGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		outputGraphics.drawImage(image, 0, 0, width, height, null);
		outputGraphics.dispose();

		return output;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(final Stage primaryStage) throws Exception {

		System.out.println("--SWITCHING OUTPUT STREAMS--");
		// Set std & err output for System cls.
		// TODO Uncomment
		// System.setOut(ConsoleApp.out);
		// System.setErr(ConsoleApp.err);
		defout.println("Streams have successfully been switched.");

		Platform.setImplicitExit(false);

		addDefaultLoadupClasses();
		// Run things in reflectionClasses
		for (final Class<?> c : reflectionClasses)
			for (final Method m : c.getDeclaredMethods())
				if (m.isAnnotationPresent(AutoLoad.class)
						&& m.getAnnotation(AutoLoad.class).value().equals(LoadTime.PROGRAM_ENTER)) {
					m.setAccessible(true);
					Object invObj = new Object();
					if (!Modifier.isStatic(m.getModifiers()))
						try {
							final Constructor<?> constructor = c.getDeclaredConstructor();
							constructor.setAccessible(true);
							invObj = constructor.newInstance();
						} catch (NoSuchMethodException | IllegalArgumentException | InstantiationException
								| InvocationTargetException | ExceptionInInitializerError e) {
							e.printStackTrace();
						}
					m.invoke(invObj);
				}
		WindowManager.setStage_Impl(primaryStage, Home.class);

		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setTitle(Kröw.NAME);
		primaryStage.getIcons().add(new Image(Kröw.IMAGE_KRÖW_LOCATION));
		WindowManager.getStage().getScene()
				.setFill(programSettings.getGlobalProgramBackground() == 0 ? SOLID_BACKGROUND
						: programSettings.getGlobalProgramBackground() == 1 ? MODERATELY_TRANSPARENT_BACKGROUND
								: COMPLETELY_TRANSPARENT_BACKGROUND);
		if (programSettings.getGlobalProgramBackground() == 2) {
			primaryStage.setAlwaysOnTop(true);
		}
		primaryStage.setWidth(Kröw.getSystemProperties().getScreenWidth());
		primaryStage.setHeight(Kröw.getSystemProperties().getScreenHeight());
		primaryStage.setFullScreen(true);
		primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

		primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, CLOSE_ON_ESCAPE_HANADLER);

		primaryStage.show();

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javafx.application.Application#stop()
	 */
	@Override
	public void stop() throws Exception {
		programSettings.save(ProgramSettings.DEFAULT_FILE_PATH);

		super.stop();
	}

}