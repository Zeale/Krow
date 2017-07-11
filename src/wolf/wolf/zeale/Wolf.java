package wolf.zeale;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.scene.text.Text;
import wolf.mindset.Construct;
import wolf.mindset.Law;

public final class Wolf {

	private Wolf(final Wolf wolf) throws Exception {
		wolf.getClass();
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
	 * Wolf's name
	 */
	public static final String WOLF_NAME = "Wolf";

	/**
	 * The home directory of this library.
	 */
	public static final File WOLF_HOME_DIR = new File(System.getProperty("user.home") + "/Appdata/Roaming/",
			Wolf.WOLF_NAME);

	/**
	 * The directory for data storage of this application.
	 */
	public final static File DATA_DIRECTORY = new File(WOLF_HOME_DIR, "Data");

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
	 * The directory for storing {@link wolf.mindset.System}s.
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
	public static void copyFileToDirectory(final File file, final File directory) {
		try {
			Files.copy(file.toPath(), new File(directory, file.getName()).toPath());
		} catch (final IOException e) {
			e.printStackTrace();
		}
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
		return new File(Wolf.class.getProtectionDomain().getCodeSource().getLocation().getFile());
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
				final O obj = Wolf.<O>loadObjectFromFile(file);
				if (obj != null)
					list.add(obj);

			} catch (final EOFException e) {
				continue;
			} catch (final IOException e) {
				java.lang.System.err.println("Could not load in the Object " + file.getName() + " from the path   "
						+ file.getAbsolutePath());
				e.printStackTrace();
				if (Wolf.DEBUG_MODE) {
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
