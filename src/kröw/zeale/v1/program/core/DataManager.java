package kröw.zeale.v1.program.core;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.sun.javafx.collections.ObservableListWrapper;

import kröw.libs.Construct;
import kröw.libs.Law;
import kröw.libs.MindsetObject;

public final class DataManager {

	public final ConstructList constructs = new ConstructList();
	public final LawList laws = new LawList();
	public final SystemList systems = new SystemList();

	private DataManager() {
	}

	// Directory Constants
	public static final File KRÖW_HOME_DIRECTORY = new File(System.getProperty("user.home") + "/Appdata/Roaming/",
			Kröw.NAME);

	public final static File DATA_DIRECTORY = new File(DataManager.KRÖW_HOME_DIRECTORY, "Data");
	public static final File CONSTRUCT_SAVE_DIRECTORY = new File(DataManager.DATA_DIRECTORY, "Constructs");
	public static final File TASK_SAVE_DIRECTORY = new File(DataManager.DATA_DIRECTORY, "Tasks");
	public static final File PROGRAM_SAVE_DIRECTORY = new File(DataManager.DATA_DIRECTORY, "Programs");
	public static final File SYSTEM_SAVE_DIRECTORY = new File(DataManager.DATA_DIRECTORY, "Systems");
	public static final File LAW_SAVE_DIRECTORY = new File(DataManager.DATA_DIRECTORY, "Laws");

	static {
		// Create the following folders if they don't already exist and catch
		// any exceptions.
		try {
			DataManager.createFolder(DataManager.KRÖW_HOME_DIRECTORY);
			DataManager.createFolder(DataManager.DATA_DIRECTORY);
			DataManager.createFolder(DataManager.CONSTRUCT_SAVE_DIRECTORY);
			DataManager.createFolder(DataManager.TASK_SAVE_DIRECTORY);
			DataManager.createFolder(DataManager.PROGRAM_SAVE_DIRECTORY);
			DataManager.createFolder(DataManager.SYSTEM_SAVE_DIRECTORY);
			DataManager.createFolder(DataManager.LAW_SAVE_DIRECTORY);
		} catch (final RuntimeException e) {
			System.err.println(
					"An exception occurred while trying to create or check some necessary directories. The program will print its errors and exit.");
			System.out.println("\n\n");

			e.printStackTrace();
			System.exit(-1);
		}

	}

	static DataManager getDataManager() {
		return new DataManager();
	}

	static void loadData() {
		boolean cons = false, laws = false;
		System.out.println("Now loading Constructs from the file system.....");
		for (final Construct c : DataManager
				.<Construct>loadObjectsFromDirectory(DataManager.CONSTRUCT_SAVE_DIRECTORY)) {
			System.out.println("   \n---Loaded the Construct " + c.getName() + " successfully.");
			Kröw.getDataManager().getConstructs().add(c);
			cons = true;
		}
		if (!cons)
			System.err.println("No Constructs were loaded!...");

		System.out.println("\n\nNow loading Laws from the file system.....");
		for (final Law l : DataManager.<Law>loadObjectsFromDirectory(DataManager.LAW_SAVE_DIRECTORY)) {
			System.out.println("   \n---Loaded the Law " + l.getName() + " successfully.");
			Kröw.getDataManager().getLaws().add(l);
			laws = true;
		}
		if (!laws)
			System.err.println("No Laws were loaded!...");

		System.out.println("\n\nNow loading Systems from the file system.....");
		for (final kröw.libs.System l : DataManager.<kröw.libs.System>loadObjectsFromDirectory(
				DataManager.SYSTEM_SAVE_DIRECTORY)) {
			System.out.println("   \n---Loaded the Law " + l.getName() + " successfully.");
			Kröw.getDataManager().getSystems().add(l);
			laws = true;
		}
		if (!laws)
			System.err.println("No Laws were loaded!...");

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
				final O obj = DataManager.<O>loadObjectFromFile(file);
				if (obj != null)
					list.add(obj);

			} catch (final EOFException e) {
				continue;
			} catch (final IOException e) {
				System.err.println("Could not load in the Object " + file.getName() + " from the path   "
						+ file.getAbsolutePath());
				if (Kröw.DEBUG_MODE) {
					System.out.println("\n\n\n");
					e.printStackTrace();
				}
			}

		return list;
	}

	// More static methods
	public static boolean saveObject(final MindsetObject object, final File file) {

		boolean wasDir = false;
		try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file))) {
			System.out.println("\n\nAttempting to save " + object.getName() + ".");

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
			os.writeObject(object);

			return true;
		} catch (final IOException e) {
			System.err.println(object.getName() + " could not be saved.");
			e.printStackTrace();
			if (wasDir && !file.exists())
				file.mkdir();
			return false;
		}
	}

	public ConstructList getConstructs() {
		return constructs;
	}

	public LawList getLaws() {
		return laws;
	}

	public SystemList getSystems() {
		return systems;
	}

	public class ConstructList extends ObservableListWrapper<Construct> {

		private ConstructList() {
			super(new ArrayList<>());
		}

		public LinkedList<Construct> getDeadConstructs() {
			final LinkedList<Construct> list = new LinkedList<>();
			for (final Construct c : this)
				if (!c.isAlive())
					list.add(c);
			return list;
		}

		public LinkedList<Construct> getFemaleConstructs() {
			final LinkedList<Construct> list = new LinkedList<>();
			for (final Construct c : this)
				if (c.getGender())
					list.add(c);
			return list;
		}

		public LinkedList<Construct> getLivingConstructs() {
			final LinkedList<Construct> list = new LinkedList<>();
			for (final Construct c : this)
				if (c.isAlive())
					list.add(c);
			return list;
		}

		public LinkedList<Construct> getMaleConstructs() {
			final LinkedList<Construct> list = new LinkedList<>();
			for (final Construct c : this)
				if (!c.getGender())
					list.add(c);
			return list;
		}

	}

	public class LawList extends ObservableListWrapper<Law> {

		private LawList() {
			super(new ArrayList<>());
		}

	}

	public class SystemList extends ObservableListWrapper<System> {

		private SystemList() {
			super(new ArrayList<>());
		}

		public void add(final kröw.libs.System l) {
			// TODO Auto-generated method stub

		}

	}

}