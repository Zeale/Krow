package kröw.zeale.v1.program.core;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import kröw.libs.Construct;
import kröw.libs.Law;
import kröw.libs.MindsetObject;

/**
 * This class is used by the {@link Kröw} class to manage data for the program.
 *
 * @author Zeale
 *
 */
public final class DataManager {

	/**
	 * The list of {@link Construct}s that have been loaded in to the program.
	 */
	public final ConstructList constructs = new ConstructList();
	/**
	 * The list of loaded {@link Law}s.
	 */
	public final LawList laws = new LawList();
	/**
	 * The list of {@link System}s.
	 */
	public final SystemList systems = new SystemList();

	/**
	 * This constructor has been made private to prevent instantiation. It
	 * should only be used by the {@link Kröw} class.
	 */
	private DataManager() {
	}

	// Directory Constants
	/**
	 * The home directory of the application.
	 */
	public static final File KRÖW_HOME_DIRECTORY = new File(System.getProperty("user.home") + "/Appdata/Roaming/",
			Kröw.NAME);

	/**
	 * The directory for data storage of this application.
	 */
	public final static File DATA_DIRECTORY = new File(DataManager.KRÖW_HOME_DIRECTORY, "Data");
	/**
	 * The directory for storing {@link Construct}s.
	 */
	public static final File CONSTRUCT_SAVE_DIRECTORY = new File(DataManager.DATA_DIRECTORY, "Constructs");
	/**
	 * The directory for storing Tasks. (These currently don't exist.)
	 */
	public static final File TASK_SAVE_DIRECTORY = new File(DataManager.DATA_DIRECTORY, "Tasks");
	/**
	 * The directory for storing Programs. (These currently don't exist.)
	 */
	public static final File PROGRAM_SAVE_DIRECTORY = new File(DataManager.DATA_DIRECTORY, "Programs");
	/**
	 * The directory for storing {@link kröw.libs.System}s.
	 */
	public static final File SYSTEM_SAVE_DIRECTORY = new File(DataManager.DATA_DIRECTORY, "Systems");
	/**
	 * The directory for storing {@link Law}s
	 */
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
			final float a = 4;
			if (a == a)
				System.out.println(5);
		}

	}

	/**
	 * A static method that allows {@link Kröw} to get a {@link DataManager}
	 * object.
	 *
	 * @return A brand new {@link DataManager}.
	 */
	static DataManager getDataManager() {
		return new DataManager();
	}

	/**
	 * A static void method that loads data.
	 */
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

	/**
	 * An API method that attempts to create a folder. This will likely be moved
	 * to the upcoming API project.
	 *
	 * @param fileObj
	 */
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

	/**
	 * A method that loads a {@link Serializable} object from a given file. The
	 * file should contain only one {@link Serializable} object.
	 *
	 * @param file
	 *            The {@link File} to load the object from.
	 * @return The {@link Object} that was loaded.
	 * @throws IOException
	 *             If something goes wrong as stated in
	 *             {@link ObjectInputStream#ObjectInputStream(java.io.InputStream)}.
	 */
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

	/**
	 * This method will return an {@link ArrayList} (casted to a {@link List})
	 * which contains multiple objects loaded from a directory of files. The
	 * objects must be of the given type parameter.
	 *
	 * @param directory
	 *            The directory to load objects from.
	 * @return A new {@link ArrayList} containing all the loaded objects.
	 */
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
	/**
	 * This method attempts to save a {@link MindsetObject} into the specified
	 * {@link File}.
	 *
	 * @param object
	 *            The {@link MindsetObject} to attempt to save.
	 * @param file
	 *            The {@link File} to attempt to save to.
	 * @return {@code true} if the save worked, {@code false} otherwise.
	 */
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

	/**
	 * A getter for {@link #constructs}.
	 *
	 * @return {@link #constructs}.
	 */
	public ConstructList getConstructs() {
		return constructs;
	}

	/**
	 * A getter for {@link #laws}.
	 *
	 * @return {@link #laws}.
	 */
	public LawList getLaws() {
		return laws;
	}

	/**
	 * A getter for {@link #systems}.
	 *
	 * @return {@link #systems}.
	 */
	public SystemList getSystems() {
		return systems;
	}

	/**
	 * A wrapper for an {@link ObservableList} of {@link Construct} objects with
	 * a few extra helpful methods.
	 *
	 * @author Zeale
	 *
	 */
	public class ConstructList {

		private final ObservableList<Construct> constructs = FXCollections.observableArrayList();

		private ConstructList() {
		}

		public boolean add(final Construct e) {
			return constructs.add(e);
		}

		public void add(final int index, final Construct element) {
			constructs.add(index, element);
		}

		public boolean addAll(final Collection<? extends Construct> c) {
			return constructs.addAll(c);
		}

		public boolean addAll(final Construct... elements) {
			return constructs.addAll(elements);
		}

		public boolean addAll(final int index, final Collection<? extends Construct> c) {
			return constructs.addAll(index, c);
		}

		public void addListener(final InvalidationListener listener) {
			constructs.addListener(listener);
		}

		public void addListener(final ListChangeListener<? super Construct> listener) {
			constructs.addListener(listener);
		}

		public void clear() {
			constructs.clear();
		}

		public boolean contains(final Object o) {
			return constructs.contains(o);
		}

		public boolean containsAll(final Collection<?> c) {
			return constructs.containsAll(c);
		}

		@Override
		public boolean equals(final Object o) {
			return constructs.equals(o);
		}

		public FilteredList<Construct> filtered(final Predicate<Construct> predicate) {
			return constructs.filtered(predicate);
		}

		public void forEach(final Consumer<? super Construct> action) {
			constructs.forEach(action);
		}

		public Construct get(final int index) {
			return constructs.get(index);
		}

		public ObservableList<Construct> getConstructList() {
			return constructs;
		}

		public LinkedList<Construct> getDeadConstructs() {
			final LinkedList<Construct> list = new LinkedList<>();
			for (final Construct c : constructs)
				if (!c.isAlive())
					list.add(c);
			return list;
		}

		public LinkedList<Construct> getFemaleConstructs() {
			final LinkedList<Construct> list = new LinkedList<>();
			for (final Construct c : constructs)
				if (c.getGender())
					list.add(c);
			return list;
		}

		public LinkedList<Construct> getLivingConstructs() {
			final LinkedList<Construct> list = new LinkedList<>();
			for (final Construct c : constructs)
				if (c.isAlive())
					list.add(c);
			return list;
		}

		public LinkedList<Construct> getMaleConstructs() {
			final LinkedList<Construct> list = new LinkedList<>();
			for (final Construct c : constructs)
				if (!c.getGender())
					list.add(c);
			return list;
		}

		@Override
		public int hashCode() {
			return constructs.hashCode();
		}

		public int indexOf(final Object o) {
			return constructs.indexOf(o);
		}

		public boolean isEmpty() {
			return constructs.isEmpty();
		}

		public Iterator<Construct> iterator() {
			return constructs.iterator();
		}

		public int lastIndexOf(final Object o) {
			return constructs.lastIndexOf(o);
		}

		public ListIterator<Construct> listIterator() {
			return constructs.listIterator();
		}

		public ListIterator<Construct> listIterator(final int index) {
			return constructs.listIterator(index);
		}

		public Stream<Construct> parallelStream() {
			return constructs.parallelStream();
		}

		public Construct remove(final int index) {
			return constructs.remove(index);
		}

		public void remove(final int from, final int to) {
			constructs.remove(from, to);
		}

		public boolean remove(final Object o) {
			return constructs.remove(o);
		}

		public boolean removeAll(final Collection<?> c) {
			return constructs.removeAll(c);
		}

		public boolean removeAll(final Construct... elements) {
			return constructs.removeAll(elements);
		}

		public boolean removeIf(final Predicate<? super Construct> filter) {
			return constructs.removeIf(filter);
		}

		public void removeListener(final InvalidationListener listener) {
			constructs.removeListener(listener);
		}

		public void removeListener(final ListChangeListener<? super Construct> listener) {
			constructs.removeListener(listener);
		}

		public void replaceAll(final UnaryOperator<Construct> operator) {
			constructs.replaceAll(operator);
		}

		public boolean retainAll(final Collection<?> c) {
			return constructs.retainAll(c);
		}

		public boolean retainAll(final Construct... elements) {
			return constructs.retainAll(elements);
		}

		public Construct set(final int index, final Construct element) {
			return constructs.set(index, element);
		}

		public boolean setAll(final Collection<? extends Construct> col) {
			return constructs.setAll(col);
		}

		public boolean setAll(final Construct... elements) {
			return constructs.setAll(elements);
		}

		public int size() {
			return constructs.size();
		}

		public void sort(final Comparator<? super Construct> c) {
			constructs.sort(c);
		}

		public SortedList<Construct> sorted() {
			return constructs.sorted();
		}

		public SortedList<Construct> sorted(final Comparator<Construct> comparator) {
			return constructs.sorted(comparator);
		}

		public Spliterator<Construct> spliterator() {
			return constructs.spliterator();
		}

		public Stream<Construct> stream() {
			return constructs.stream();
		}

		public List<Construct> subList(final int fromIndex, final int toIndex) {
			return constructs.subList(fromIndex, toIndex);
		}

		public Object[] toArray() {
			return constructs.toArray();
		}

		public <T> T[] toArray(final T[] a) {
			return constructs.toArray(a);
		}

	}

	/**
	 * A wrapper for an {@link ObservableList} of {@link Law} objects.
	 *
	 * @author Zeale
	 *
	 */
	public class LawList {

		private final ObservableList<Law> laws = FXCollections.observableArrayList();

		private LawList() {

		}

		public void add(final int index, final Law element) {
			laws.add(index, element);
		}

		public boolean add(final Law e) {
			return laws.add(e);
		}

		public boolean addAll(final Collection<? extends Law> c) {
			return laws.addAll(c);
		}

		public boolean addAll(final int index, final Collection<? extends Law> c) {
			return laws.addAll(index, c);
		}

		public boolean addAll(final Law... elements) {
			return laws.addAll(elements);
		}

		public void addListener(final InvalidationListener listener) {
			laws.addListener(listener);
		}

		public void addListener(final ListChangeListener<? super Law> listener) {
			laws.addListener(listener);
		}

		public void clear() {
			laws.clear();
		}

		public boolean contains(final Object o) {
			return laws.contains(o);
		}

		public boolean containsAll(final Collection<?> c) {
			return laws.containsAll(c);
		}

		@Override
		public boolean equals(final Object o) {
			return laws.equals(o);
		}

		public FilteredList<Law> filtered(final Predicate<Law> predicate) {
			return laws.filtered(predicate);
		}

		public void forEach(final Consumer<? super Law> action) {
			laws.forEach(action);
		}

		public Law get(final int index) {
			return laws.get(index);
		}

		public ObservableList<Law> getLawList() {
			return laws;
		}

		@Override
		public int hashCode() {
			return laws.hashCode();
		}

		public int indexOf(final Object o) {
			return laws.indexOf(o);
		}

		public boolean isEmpty() {
			return laws.isEmpty();
		}

		public Iterator<Law> iterator() {
			return laws.iterator();
		}

		public int lastIndexOf(final Object o) {
			return laws.lastIndexOf(o);
		}

		public ListIterator<Law> listIterator() {
			return laws.listIterator();
		}

		public ListIterator<Law> listIterator(final int index) {
			return laws.listIterator(index);
		}

		public Stream<Law> parallelStream() {
			return laws.parallelStream();
		}

		public Law remove(final int index) {
			return laws.remove(index);
		}

		public void remove(final int from, final int to) {
			laws.remove(from, to);
		}

		public boolean remove(final Object o) {
			return laws.remove(o);
		}

		public boolean removeAll(final Collection<?> c) {
			return laws.removeAll(c);
		}

		public boolean removeAll(final Law... elements) {
			return laws.removeAll(elements);
		}

		public boolean removeIf(final Predicate<? super Law> filter) {
			return laws.removeIf(filter);
		}

		public void removeListener(final InvalidationListener listener) {
			laws.removeListener(listener);
		}

		public void removeListener(final ListChangeListener<? super Law> listener) {
			laws.removeListener(listener);
		}

		public void replaceAll(final UnaryOperator<Law> operator) {
			laws.replaceAll(operator);
		}

		public boolean retainAll(final Collection<?> c) {
			return laws.retainAll(c);
		}

		public boolean retainAll(final Law... elements) {
			return laws.retainAll(elements);
		}

		public Law set(final int index, final Law element) {
			return laws.set(index, element);
		}

		public boolean setAll(final Collection<? extends Law> col) {
			return laws.setAll(col);
		}

		public boolean setAll(final Law... elements) {
			return laws.setAll(elements);
		}

		public int size() {
			return laws.size();
		}

		public void sort(final Comparator<? super Law> c) {
			laws.sort(c);
		}

		public SortedList<Law> sorted() {
			return laws.sorted();
		}

		public SortedList<Law> sorted(final Comparator<Law> comparator) {
			return laws.sorted(comparator);
		}

		public Spliterator<Law> spliterator() {
			return laws.spliterator();
		}

		public Stream<Law> stream() {
			return laws.stream();
		}

		public List<Law> subList(final int fromIndex, final int toIndex) {
			return laws.subList(fromIndex, toIndex);
		}

		public Object[] toArray() {
			return laws.toArray();
		}

		public <T> T[] toArray(final T[] a) {
			return laws.toArray(a);
		}

	}

	/**
	 * A wrapper for an {@link ObservableList} of {@link System} objects.
	 *
	 * @author Zeale
	 *
	 */
	public class SystemList {

		private final ObservableList<kröw.libs.System> systems = FXCollections.observableArrayList();

		private SystemList() {
		}

		public void add(final int index, final kröw.libs.System element) {
			systems.add(index, element);
		}

		public boolean add(final kröw.libs.System e) {
			return systems.add(e);
		}

		public boolean addAll(final Collection<? extends kröw.libs.System> c) {
			return systems.addAll(c);
		}

		public boolean addAll(final int index, final Collection<? extends kröw.libs.System> c) {
			return systems.addAll(index, c);
		}

		public boolean addAll(final kröw.libs.System... elements) {
			return systems.addAll(elements);
		}

		public void addListener(final InvalidationListener listener) {
			systems.addListener(listener);
		}

		public void addListener(final ListChangeListener<? super kröw.libs.System> listener) {
			systems.addListener(listener);
		}

		public void clear() {
			systems.clear();
		}

		public boolean contains(final Object o) {
			return systems.contains(o);
		}

		public boolean containsAll(final Collection<?> c) {
			return systems.containsAll(c);
		}

		@Override
		public boolean equals(final Object o) {
			return systems.equals(o);
		}

		public FilteredList<kröw.libs.System> filtered(final Predicate<kröw.libs.System> predicate) {
			return systems.filtered(predicate);
		}

		public void forEach(final Consumer<? super kröw.libs.System> action) {
			systems.forEach(action);
		}

		public kröw.libs.System get(final int index) {
			return systems.get(index);
		}

		public ObservableList<kröw.libs.System> getSystemList() {
			return systems;
		}

		@Override
		public int hashCode() {
			return systems.hashCode();
		}

		public int indexOf(final Object o) {
			return systems.indexOf(o);
		}

		public boolean isEmpty() {
			return systems.isEmpty();
		}

		public Iterator<kröw.libs.System> iterator() {
			return systems.iterator();
		}

		public int lastIndexOf(final Object o) {
			return systems.lastIndexOf(o);
		}

		public ListIterator<kröw.libs.System> listIterator() {
			return systems.listIterator();
		}

		public ListIterator<kröw.libs.System> listIterator(final int index) {
			return systems.listIterator(index);
		}

		public Stream<kröw.libs.System> parallelStream() {
			return systems.parallelStream();
		}

		public kröw.libs.System remove(final int index) {
			return systems.remove(index);
		}

		public void remove(final int from, final int to) {
			systems.remove(from, to);
		}

		public boolean remove(final Object o) {
			return systems.remove(o);
		}

		public boolean removeAll(final Collection<?> c) {
			return systems.removeAll(c);
		}

		public boolean removeAll(final kröw.libs.System... elements) {
			return systems.removeAll(elements);
		}

		public boolean removeIf(final Predicate<? super kröw.libs.System> filter) {
			return systems.removeIf(filter);
		}

		public void removeListener(final InvalidationListener listener) {
			systems.removeListener(listener);
		}

		public void removeListener(final ListChangeListener<? super kröw.libs.System> listener) {
			systems.removeListener(listener);
		}

		public void replaceAll(final UnaryOperator<kröw.libs.System> operator) {
			systems.replaceAll(operator);
		}

		public boolean retainAll(final Collection<?> c) {
			return systems.retainAll(c);
		}

		public boolean retainAll(final kröw.libs.System... elements) {
			return systems.retainAll(elements);
		}

		public kröw.libs.System set(final int index, final kröw.libs.System element) {
			return systems.set(index, element);
		}

		public boolean setAll(final Collection<? extends kröw.libs.System> col) {
			return systems.setAll(col);
		}

		public boolean setAll(final kröw.libs.System... elements) {
			return systems.setAll(elements);
		}

		public int size() {
			return systems.size();
		}

		public void sort(final Comparator<? super kröw.libs.System> c) {
			systems.sort(c);
		}

		public SortedList<kröw.libs.System> sorted() {
			return systems.sorted();
		}

		public SortedList<kröw.libs.System> sorted(final Comparator<kröw.libs.System> comparator) {
			return systems.sorted(comparator);
		}

		public Spliterator<kröw.libs.System> spliterator() {
			return systems.spliterator();
		}

		public Stream<kröw.libs.System> stream() {
			return systems.stream();
		}

		public List<kröw.libs.System> subList(final int fromIndex, final int toIndex) {
			return systems.subList(fromIndex, toIndex);
		}

		public Object[] toArray() {
			return systems.toArray();
		}

		public <T> T[] toArray(final T[] a) {
			return systems.toArray(a);
		}

	}

}