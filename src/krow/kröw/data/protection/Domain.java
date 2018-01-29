package kröw.data.protection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import kröw.data.protection.Protection.ProtectionKey;

//Until the workings of this class are sorted out, I'm gonna keep it final.
/**
 * A {@link Domain} is an object that represents an area where data can be
 * stored on the filesystem. Each class has its own domain where it can store
 * data.
 * <p>
 * As per naming, a class, <code>com.loser.MyClass</code>, would have a Domain
 * named <code>com/loser/MyClass</code>. This domain would be <b>"owned"</b> by
 * <code>MyClass</code>, since <code>MyClass</code> will never be denied access
 * to it, and because it is, quite literally, <code>MyClass</code>'s domain;
 * <code>MyClass</code> can put its data there, its configuration files, etc.
 * etc.
 * 
 * @author Zeale
 *
 */
public final class Domain {

	private class DomainConfigData {

		private int buildNumber = -1;
		@SuppressWarnings("unused")
		private InputStreamReader configReader;
		@SuppressWarnings("unused")
		private OutputStreamWriter configWriter;

		private HashMap<String, Object> data = new HashMap<>();

		private DomainConfigData() throws DomainInitializeException {

			try {
				configReader = new InputStreamReader(new FileInputStream(configurationFile));
				configWriter = new OutputStreamWriter(new FileOutputStream(configurationFile));
			} catch (IOException e) {
				throw new DomainInitializeException(e);
			}

			if (buildNumber == -1)
				throw new DomainInitializeException(
						"Couldn't read the build number of this program that was stored in this Domain. The file may be corrupt or something, or maybe the build number couldn't be read from an internal location, if you were making a new Domain.");
		}

		public int getBuildNumber() {
			return buildNumber;
		}

		@SuppressWarnings("unused")
		public Map<String, Object> getData() {
			return Collections.unmodifiableMap(data);
		}

	}

	public class DomainFile {

		private final FileLock lock;
		private final FileChannel channel;
		private final RandomAccessFile rafile;

		private final File backing;

		public DomainFile(DomainFolder parent, String name) {
			this(new File(parent.backing, name));
		}

		private DomainFile(File backing) {
			this.backing = backing;
			if (backing.isDirectory())
				throw new RuntimeException("There already exists a folder where this file was going to be.");
			try {
				rafile = new RandomAccessFile(backing, "rw");
				channel = rafile.getChannel();
				lock = channel.lock();

			} catch (IOException e) {
				throw new RuntimeException("File not found.");
			}

		}

		public DomainFile(String file) {
			this(new File(directory, file));
		}

		private void checkDeleted() {
			if (!lock.isValid())
				throw new RuntimeException("File has been deleted.");
		}

		public boolean delete() throws IOException {
			checkDeleted();
			lock.release();
			channel.close();
			rafile.close();
			return backing.delete();
		}

		public void deleteOnExit() {
			checkDeleted();
			backing.deleteOnExit();
		}

		public boolean exists() {
			checkDeleted();
			return backing.exists();
		}

		@Override
		protected void finalize() throws Throwable {
			delete();
		}

		public long getTotalSpace() {
			checkDeleted();
			return backing.getTotalSpace();
		}

		public long getUsableSpace() {
			checkDeleted();
			return backing.getUsableSpace();
		}

		public boolean isHidden() {
			checkDeleted();
			return backing.isHidden();
		}

		public long lastModified() {
			checkDeleted();
			return backing.lastModified();
		}

		public long length() {
			checkDeleted();
			return backing.length();
		}

		public <A extends BasicFileAttributes> A readAttributes(Class<A> type, LinkOption... options)
				throws IOException {
			checkDeleted();
			return Files.readAttributes(backing.toPath(), type, options);
		}

		public boolean setLastModified(long time) {
			checkDeleted();
			return backing.setLastModified(time);
		}

	}
	/**
	 * An object used to manipulate and get information of a folder in a
	 * {@link Domain}. <b>Note</b> that making multiple {@link DomainFolder}s that
	 * represent the same folder in a {@link Domain} may result in problems.
	 * 
	 * @author Zeale
	 *
	 */
	public class DomainFolder {

		private boolean deleted;

		/**
		 * The {@link File} object that backs this {@link DomainFolder}.
		 */
		private final File backing;

		/**
		 * Constructor for wrapping {@link Domain}; this constructor sets its backing
		 * file to its {@link Domain}'s {@link Domain#directory} variable without going
		 * through any of the checkups or attempts to make the directory.
		 */
		private DomainFolder() {
			backing = directory;
		}

		/**
		 * Makes a {@link DomainFolder} given its parent and its name.
		 * 
		 * @param parent
		 *            The parent folder of this new {@link DomainFolder}.
		 * @param name
		 *            The name of this {@link DomainFolder}.
		 */
		public DomainFolder(DomainFolder parent, String name) {
			this(new File(parent.backing, name));
		}

		/**
		 * Used by this class to create a {@link DomainFolder} given a specific
		 * {@link File}. This {@link DomainFolder} will not automatically be a direct
		 * child of this {@link Domain}.
		 * 
		 * @param backing
		 *            The {@link File} object that backs this {@link DomainFolder}.
		 */
		private DomainFolder(File backing) {
			if (!backing.exists())
				throw new RuntimeException("Folder does not exist");
			if (backing.isFile())
				throw new RuntimeException("There already exists a file where this folder was going to be.");
			this.backing = backing;
		}

		/**
		 * Makes a {@link DomainFolder} that is a child of this {@link Domain} with a
		 * specified name.
		 * 
		 * @param file
		 *            The name of this {@link DomainFolder}.
		 */
		public DomainFolder(String file) {
			this(new File(directory, file));
		}

		private void checkDeleted() {
			if (deleted)
				throw new RuntimeException("Folder has been deleted.");
		}

		public void clearDir() {
			checkDeleted();
			for (File f : backing.listFiles())
				if (!f.getPath().endsWith(".kdc"))
					f.delete();
		}

		public boolean delete() {
			checkDeleted();
			deleted = true;
			return backing.delete();
		}

		public DomainFile getFile(String name) throws InvalidFileNameException {
			checkDeleted();
			if (!validFileName(name))
				throw new InvalidFileNameException(name, "Invalid file name.");
			return new DomainFile(new File(backing, name));

		}

		public DomainFolder getFolder(String name) throws InvalidFolderNameException {
			checkDeleted();
			if (!validFileName(name))
				throw new InvalidFolderNameException("Invalid folder name");
			return new DomainFolder(this, name);

		}

		public DomainFile makeFile(String name) throws FileAlreadyExistsException {
			checkDeleted();
			File file = new File(backing, name);
			if (file.exists())
				throw new FileAlreadyExistsException(name);
			return new DomainFile(file);
		}

		public DomainFolder makeFolder(String name) throws FileAlreadyExistsException {
			checkDeleted();
			File folder = new File(backing, name);
			if (folder.exists())
				throw new FileAlreadyExistsException(name);
			return new DomainFolder(folder);
		}

		public void serialize(String fileName, Serializable serializableObject)
				throws IOException, InvalidFileNameException {
			checkDeleted();
			if (!validFileName(fileName))
				throw new InvalidFileNameException(fileName);
			File f = new File(backing, fileName);

			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
				oos.writeObject(serializableObject);
			} catch (Exception e) {
				f.delete();
				throw e;
			}
		}

		public Serializable unserialize(String fileName) throws Exception {
			checkDeleted();
			if (!validFileName(fileName))
				throw new InvalidFileNameException(fileName);
			File f = new File(backing, fileName);
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
				return (Serializable) ois.readObject();
			} catch (Exception e) {
				f.delete();
				throw e;
			}
		}
	}
	/**
	 * The name of the configuration file that will go in each {@link Domain}.
	 */
	private static final String DOMAIN_CONFIGURATION_FILE_NAME = "DomainConfiguration.kdc";
	/**
	 * This is a static list of all loaded {@link Domain}s. When a {@link Domain} is
	 * loaded successfully, it is placed into this map. If it is called upon again
	 * and it still exists in this map, we return it from this map.
	 */
	private static final WeakHashMap<String, Domain> loadedDomains = new WeakHashMap<>();

	static String domToPkg(String fullPath) {
		return fullPath.replaceAll("\\#", "\\$").replaceAll("\\/", ".");
	}

	static Domain getDomain(ProtectionKey key) throws DomainInitializeException, UnavailableException {

		// Availability checkup
		if (!Protection.isAvailable()) {
			if (!Protection.tryEnable())
				throw new UnavailableException();
		}

		if (loadedDomains.containsKey(key.path))
			return loadedDomains.get(key.path);
		else
			return new Domain(key.path);
	}

	static String pkgToDom(String clsName, String canonicalName) {
		return clsName.contains("$") ? clsName.replaceAll("\\.", "/").replaceAll("\\$", "#")
				: canonicalName.replaceAll("\\.", "/");
	}

	/**
	 * <p>
	 * Checks whether or not the given name is a valid filesystem name for the
	 * Protection API.
	 * <p>
	 * The name can only be made up of letters, numbers, and <code>.</code>
	 * characters.
	 * 
	 * @param name
	 *            The name of the {@link DataStorage} object that will be checked
	 *            for validity.
	 * @return <code>true</code> if this name is a legal Protection API file name,
	 *         <code>false</code> otherwise.
	 */
	public static boolean validFileName(String name) {
		if (name.isEmpty() || name.endsWith(".kdc"))
			return false;
		for (char c : name.toCharArray())
			if (!(Character.isAlphabetic(c) || Character.isDigit(c) || c == '-' || c == '_' || c == '.'))
				return false;
		return true;
	}

	/**
	 * The path of this domain. This is a String representation of this domain's
	 * owner's path relative to the root of the classpath. In other words,
	 * converting this string using the {@link #domToPkg(String)} method will return
	 * a valid path to a class that can be used in {@link Class#forName(String)} to
	 * retrieve this {@link Domain}'s owner.
	 */
	private final String path;

	/**
	 * A {@link File} of the folder in the hard drive that this {@link Domain}
	 * represents. This is kept private from client classes and code so that they
	 * don't tamper with the file in a way that isn't allowed/intended by this
	 * class.
	 */
	private final File directory;

	/**
	 * A {@link File} of the file on the hard drive that this {@link Domain}'s
	 * configuration represents. This is also kept private, for reasons described in
	 * {@link #directory}'s documentation.
	 */
	private final File configurationFile;

	/**
	 * A {@link DomainConfigData} object used to edit and manipulate this
	 * {@link Domain}'s config in a higher-level manner.
	 */
	private final DomainConfigData configuration;

	private Domain(String fullName) throws DomainInitializeException {

		// Set the directory field, so the initialization methods and such can use it.
		directory = new File(Protection.DOMAINS_DIRECTORY, path = fullName);

		// The config's directory will be used
		configurationFile = new File(directory, DOMAIN_CONFIGURATION_FILE_NAME);

		// Make the directory represented by this Domain. If there's already a folder
		// there and there is no DomainConfiguration file, then we throw an Exception
		// and s
		if (directory.exists())
			if (!(directory.isDirectory() || configurationFile.isFile()))
				throw new DomainInitializeException(
						"The file/directory denoted by this domain exists but is not a domain (it has no configuration file or something). The process of creating this domain has failed. This problem can usually be solved by deleting whatever exists where this domain does; delete this file: "
								+ directory.getAbsolutePath());
			else
				;// The directory exists and checkDirectory() returned true. This is a valid
		// directory. Let's get a configuration object that represents this Domain's
		// config (done below) and we are done constructing this Domain object :D
		else if (!directory.mkdirs())
			throw new DomainInitializeException(
					"For some reason, there was an error when trying to make the directory (folder) for a Domain. The location (where the folder was supposed to go) might be protected, or something, by admin privileges etc., but I'm not really sure how those things work... The location, ("
							+ directory.getPath()
							+ "), shouldn't exist, but its parent folder might be protected. Or something... Gl m8. (By the way, some of the parent folders might have been created.)");

		// Then set the configuration field, so it can be verified in initialize and
		// such.
		configuration = new DomainConfigData();
		loadedDomains.put(path, this);
	}

	public int getBuildNumber() {
		return configuration.getBuildNumber();
	}

	public DomainFile getFile(String name) throws InvalidFileNameException {
		return new DomainFolder().getFile(name);
	}

	public DomainFolder getFolder(String name) throws InvalidFolderNameException {
		return new DomainFolder().getFolder(name);
	}

	public String getPath() {
		return path;
	}

	public DomainFile makeFile(String name) throws FileAlreadyExistsException {
		return new DomainFolder().makeFile(name);
	}

	public DomainFolder makeFolder(String name) throws FileAlreadyExistsException {
		return new DomainFolder().makeFolder(name);
	}

	public void serialize(String fileName, Serializable serializableObject)
			throws IOException, InvalidFileNameException {
		new DomainFolder(directory).serialize(fileName, serializableObject);
	}

	@Override
	public String toString() {
		return super.toString() + "[path=\"" + getPath() + "\"]";
	}

	public Serializable unserialize(String fileName) throws Exception {
		return new DomainFolder(directory).unserialize(fileName);
	}

}
