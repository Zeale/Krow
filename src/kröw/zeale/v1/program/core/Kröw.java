package kr�w.zeale.v1.program.core;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import kr�w.libs.Construct;
import kr�w.libs.Law;
import kr�w.zeale.v1.program.guis.Window;

public final class Kr�w {

	private final ObservableList<Construct> constructs = FXCollections.<Construct>observableArrayList();
	private final ObservableList<Law> laws = FXCollections.<Law>observableArrayList();

	public static final String NAME = new String("Kr�w");

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

	private Kr�w() {
		Kr�w.loadSaveDirectories();
		System.out.println("\n\n\n\n");
		System.out.println("Loading data...\n");
		loadData();
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
			System.err.println("No Constructs were found!...");
		System.out.println("\nNow loading Programs from the file system.....");

	}

	private void start(final String[] args) {
		Application.launch(Window.class, args);
	}

	public ObservableList<Construct> getConstructs() {
		return constructs;
	}

	public ObservableList<Law> getLaws() {
		return laws;
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
			}

		return list;
	}

	public static void main(final String[] args) throws FileNotFoundException, IOException {
		Kr�w.INSTANCE.start(args);
		final ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(new File(Kr�w.CONSTRUCT_SAVE_DIRECTORY, "const.const")));
		oos.close();
	}
}
