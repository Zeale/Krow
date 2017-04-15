package kr�w.zeale.v1.program.core;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.text.Text;
import kr�w.libs.Construct;
import kr�w.libs.Law;
import kr�w.zeale.v1.program.guis.Window;

public final class Kr�w {

	public static final String NAME = new String("Kr�w");
	public static boolean DEBUG_MODE = true;

	public static final File KR�W_HOME_DIRECTORY = new File(System.getProperty("user.home") + "/Appdata/Roaming/",
			Kr�w.NAME);
	public final static File DATA_DIRECTORY = new File(Kr�w.KR�W_HOME_DIRECTORY, "Data");

	public static final File CONSTRUCT_SAVE_DIRECTORY = new File(Kr�w.DATA_DIRECTORY, "Constructs");
	public static final File TASK_SAVE_DIRECTORY = new File(Kr�w.DATA_DIRECTORY, "Tasks");
	public static final File PROGRAM_SAVE_DIRECTORY = new File(Kr�w.DATA_DIRECTORY, "Programs");
	public static final File SYSTEM_SAVE_DIRECTORY = new File(Kr�w.DATA_DIRECTORY, "Systems");
	public static final File LAW_SAVE_DIRECTORY = new File(Kr�w.DATA_DIRECTORY, "Laws");
	// During the instantiation of INSTANCE, the above directories are used, so
	// this object must be created after the above directories, thus, its
	// positioning here.
	public static final Kr�w INSTANCE = new Kr�w();

	public static boolean containsIgnoreCase(final List<String> list, final String string) {
		for (final String s : list)
			if (s.equalsIgnoreCase(string))
				return true;
		return false;
	}

	public static void createFolder(final File fileObj) {
		if (fileObj.isFile()) {
			System.out.println(
					"The folder " + fileObj.getAbsolutePath() + " already exists as a file. It will be deleted now...");
			if (!fileObj.delete())
				throw new RuntimeException("The folder " + fileObj.getName() + " could not be deleted.");
		}

		if (!fileObj.exists()) {
			System.out.println("The folder " + fileObj.getAbsolutePath() + " does not exist yet. It will be made now.");
			fileObj.mkdirs();
		} else
			System.out.println("The folder " + fileObj.getAbsolutePath() + " already exists as a folder. All is well.");

	}

	public static void exit() {

	}

	public static LinkedList<Construct> getDeadConstructs() {
		final LinkedList<Construct> list = new LinkedList<>();
		for (final Construct c : Kr�w.INSTANCE.constructs)
			if (!c.isAlive())
				list.add(c);
		return list;
	}

	public static LinkedList<Construct> getFemaleConstructs() {
		final LinkedList<Construct> list = new LinkedList<>();
		for (final Construct c : Kr�w.INSTANCE.constructs)
			if (c.getGender())
				list.add(c);
		return list;
	}

	public static LinkedList<Construct> getLivingConstructs() {
		final LinkedList<Construct> list = new LinkedList<>();
		for (final Construct c : Kr�w.INSTANCE.constructs)
			if (c.isAlive())
				list.add(c);
		return list;
	}

	public static LinkedList<Construct> getMaleConstructs() {
		final LinkedList<Construct> list = new LinkedList<>();
		for (final Construct c : Kr�w.INSTANCE.constructs)
			if (!c.getGender())
				list.add(c);
		return list;
	}

	@SuppressWarnings("unchecked")
	public final static <O> O loadObjectFromFile(final File file) throws IOException {
		try (final ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));) {
			return (O) stream.readObject();

		} catch (final ClassNotFoundException e) {
			System.err.println(
					"Could not load in the Object " + file.getName() + " from the path   " + file.getAbsolutePath());
		}
		return null;
	}

	public final static <O> List<O> loadObjectsFromDirectory(final File directory) {
		final ArrayList<O> list = new ArrayList<>();
		for (final File file : directory.listFiles())
			try {
				final O obj = Kr�w.<O>loadObjectFromFile(file);
				if (obj != null)
					list.add(obj);

			} catch (final EOFException e) {
				continue;
			} catch (final IOException e) {
				System.err.println("Could not load in the Object " + file.getName() + " from the path   "
						+ file.getAbsolutePath());
				if (Kr�w.DEBUG_MODE) {
					System.out.println("\n\n\n");
					e.printStackTrace();
				}
			}

		return list;
	}

	private static void loadSaveDirectories() {
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
		} catch (final RuntimeException e) {
			System.err.println(
					"An exception occurred while trying to create or check some necessary directories. The program will print its errors and exit.");
			System.out.println("\n\n");

			e.printStackTrace();
			System.exit(-1);
		}

	}

	public static void main(final String[] args) throws FileNotFoundException, IOException {
		Kr�w.INSTANCE.start(args);
	}

	public static boolean saveObject(final Serializable serializable, final File file) {

		boolean wasDir = false;
		try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file))) {
			System.out.println("\n\nAttempting to save the specified object.");

			file.getParentFile().mkdirs();

			if (file.isDirectory()) {

				System.out.println("The specified file is a directory. Checking contents.");
				if (file.listFiles().length == 0) {
					wasDir = true;
					System.out.println(
							"The directory contains nothing. It will now be erased and a file will replace it.");
					file.delete();
					file.createNewFile();
				} else {
					System.out.println("The directory contains files. The object could not be saved.");
					return false;
				}
			}
			if (wasDir)
				file.delete();
			final boolean existed = !file.createNewFile();

			if (existed) {
				System.out.println("The file exists. Writng to it now...");
				file.delete();
				file.createNewFile();
			} else
				System.out.println("The file now exists. Writing to it...");
			os.writeObject(serializable);

			return true;
		} catch (final IOException e) {
			System.err.println("An object could not be saved.");
			e.printStackTrace();
			if (wasDir && !file.exists())
				file.mkdir();
			return false;
		}
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

	private final ObservableList<Construct> constructs = FXCollections.<Construct>observableArrayList();

	private final ObservableList<Law> laws = FXCollections.<Law>observableArrayList();

	private Kr�w() {

		Kr�w.loadSaveDirectories();
		System.out.println("\n\n\n\n");
		System.out.println("Loading data...\n");
		loadData();
	}

	public ObservableList<Construct> getConstructs() {
		return constructs;
	}

	public ObservableList<Law> getLaws() {
		return laws;
	}

	private void loadData() {
		boolean cons = false;
		System.out.println("Now loading Constructs from the file system.....");
		for (final Construct c : Kr�w.<Construct>loadObjectsFromDirectory(Kr�w.CONSTRUCT_SAVE_DIRECTORY)) {
			System.out.println(" ---Loaded the Construct " + c.getName() + " successfully.");
			constructs.add(c);
			cons = true;
		}
		if (!cons)
			System.err.println("No Constructs were loaded!...");

	}

	private void start(final String[] args) {
		if (args != null) {
			final List<String> strings = Arrays.<String>asList(args);
			if (!Kr�w.containsIgnoreCase(strings, "-debug-mode") && !Kr�w.containsIgnoreCase(strings, "-debug"))
				Kr�w.DEBUG_MODE = false;
			if (Kr�w.DEBUG_MODE)
				System.out.println("\n\nDebug mode has been enabled...\n\n");
			Application.launch(Window.class, new String[] {});
		}

	}

}
