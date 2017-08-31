package kröw.core;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

import javafx.collections.ObservableList;
import kröw.app.api.collections.ObservableListWrapper;
import kröw.libs.mindset.MindsetObject;
import kröw.libs.mindset.ObjectAlreadyExistsException;

@Deprecated
public class Backup implements Serializable {

	/**
	 * The System's save directory of {@link Backup}s.
	 */
	public static final File BACKUP_SAVE_DIRECTORY = new File(Kröw.DATA_DIRECTORY, "Backups");;
	/**
	 * An observable list of all {@link Backup}s that are kept track of.
	 */
	private final static ObservableList<Backup> LOADED_BACKUPS = new ObservableListWrapper<>(new ArrayList<>());

	/**
	 * The version of this class.
	 */
	private static final long trueSerialVersionUID = 1L;

	/**
	 * The Serial Version UID for this class. This should never change.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Getter for {@link #LOADED_BACKUPS}. This returns {@link #LOADED_BACKUPS}
	 * as it is.
	 *
	 * @return {@link #LOADED_BACKUPS}.
	 */
	public static ObservableList<Backup> getObservableBackupList() {
		return Backup.LOADED_BACKUPS;
	}

	/**
	 * This method is called when the program is loaded by the {@link Kröw}
	 * class. It loads all the {@link Backup}s found in the
	 * {@link #BACKUP_SAVE_DIRECTORY}.
	 *
	 * @internal This is an internal method.
	 */
	static void loadBackupsFromSystem() {

		System.out.println("\n\nNow loading backups from the file system.");

		if (Backup.BACKUP_SAVE_DIRECTORY.listFiles() != null)
			for (final File f : Backup.BACKUP_SAVE_DIRECTORY.listFiles())
				try (ObjectInputStream is = OldVersionLoader.getInputStream(f)) {
					Backup.LOADED_BACKUPS.add((Backup) is.readObject());
				} catch (final ClassNotFoundException e) {
					System.err.println("The backup folder has an invalid backup: " + f.getName());
					System.err.println("\t• The backup is not a readable object.");
				} catch (final IOException e) {
					System.err.println("The backup " + f.getName() + " could not be loaded.");
				}
		else
			System.err.println("No backups found.");

	}

	/**
	 * The date that this {@link Backup} was made.
	 */
	private Date creationDate = Date.from(Instant.now(Clock.systemDefaultZone()));

	/**
	 * An array containing all the objects that this {@link Backup} stores.
	 */
	private MindsetObject[] mindsetObjects;

	/**
	 * <p>
	 * Constructs a {@link Backup} object using the objects controlled by the
	 * {@link Kröw} program.
	 * <p>
	 * The {@link Backup} is constructed using whatever
	 *
	 * <pre>
	 * Kröw.CONSTRUCT_MINDSET.getAllObjects().toArray();
	 * </pre>
	 *
	 * returns.
	 */
	public Backup() {
		final Object[] mindsetObjects = Kröw.CONSTRUCT_MINDSET.getAllObjects().toArray();
		final MindsetObject[] arr = new MindsetObject[mindsetObjects.length];
		for (int i = 0; i < arr.length; i++)
			arr[i] = (MindsetObject) mindsetObjects[i];
		this.mindsetObjects = arr;
	}

	/**
	 * Constructs a {@link Backup} using only the specified
	 * {@link MindsetObject}s.
	 *
	 * @param mindsetObjects
	 *            The {@link MindsetObject}s to store in this {@link Backup}.
	 */
	public Backup(final MindsetObject... mindsetObjects) {
		this.mindsetObjects = mindsetObjects;
	}

	/**
	 * <p>
	 * Clears all the {@link MindsetObject}s stored by the {@link Kröw} program
	 * and restores the {@link Kröw} program using the instance it was called
	 * from.
	 * <p>
	 * This method also returns a {@link Backup} which was made prior to the
	 * clearing.
	 *
	 * @return A {@link Backup} object storing all the {@link MindsetObject}s in
	 *         the {@link Kröw} program before the {@link Kröw} program was
	 *         cleared.
	 */
	public Backup freshRestore() {
		final Backup backup = Kröw.clearAllObjects();
		restore(true, false);
		return backup;
	}

	/**
	 * A getter for the {@link #creationDate} of this {@link Backup}.
	 *
	 * @return {@link #creationDate}
	 */
	public Date getDateCreated() {
		return creationDate;
	}

	/**
	 * Creates a new {@link File} object that represents the System file of this
	 * {@link Backup}. If the {@link Backup} has not yet been saved to the
	 * file-system, the returned {@link File}'s {@link File#exists() exists()}
	 * method will return false.
	 *
	 * @return A new {@link File} representing this {@link Backup} in the
	 *         file-system.
	 */
	public File getFile() {
		return new File(Backup.BACKUP_SAVE_DIRECTORY,
				"Backup_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(creationDate) + ".krbu");
	}

	/**
	 * Returns the number of {@link MindsetObject}'s that this {@link Backup}
	 * stores.
	 *
	 * @return The number of {@link MindsetObject}'s that this {@link Backup}
	 *         stores.
	 */
	public int getObjectCount() {
		return mindsetObjects.length;
	}

	/**
	 * Returns the size, in bytes, of this {@link Backup} on the file-system.
	 * <b>Note that this method will create the {@link Backup}'s file if it does
	 * not already exist.</b>
	 *
	 * @return The size, in bytes, as a long, of this {@link Backup} on the
	 *         file-system.
	 */
	public long getSize() {
		if (!getFile().exists())
			try {
				make();
			} catch (final IOException e) {
			}
		return getFile().length();
	}

	/**
	 * Creates this {@link Backup} as an actual file on the user's computer.
	 *
	 * @return The {@link File} that was created. This is the same as
	 *         {@link #getFile()}.
	 * @throws IOException
	 *             As specified by {@link ObjectOutputStream}'s
	 *             {@link ObjectOutputStream#ObjectOutputStream(java.io.OutputStream)
	 *             ObjectOutputStream(java.io.OutputStream)} constructor,
	 *             <b>and</b> {@link ObjectOutputStream}'s
	 *             {@link ObjectOutputStream#writeObject(Object)
	 *             writeObject(Object)} method.
	 */
	public File make() throws IOException {
		final File file = getFile();
		if (!file.createNewFile())
			return file;
		final ObjectOutputStream os = OldVersionLoader.getOutputStream(file);
		os.writeObject(this);
		os.close();
		Backup.LOADED_BACKUPS.add(this);
		return file;
	}

	/**
	 * <p>
	 * The {@code readObject} method of this {@link Serializable} instance.
	 *
	 * @param is
	 *            The {@link ObjectInputStream} used to read-in this
	 *            {@link Backup}.
	 * @throws IOException
	 *             As specified by any of the input stream's reading methods.
	 *             (For example, {@link ObjectInputStream#readLong() readLong()}
	 *             or {@link ObjectInputStream#readObject() readObject()}.
	 * @throws ClassNotFoundException
	 *             As specified by any of the input stream's reading methods,
	 *             such as {@link ObjectInputStream#readObject() readObject()}
	 *             or {@link ObjectInputStream#readLong() readLong()}.
	 * @internal This method is used for serialization and should be treated as
	 *           if it were internal.
	 */
	private void readObject(final ObjectInputStream is) throws IOException, ClassNotFoundException {
		if (is.readLong() == Backup.trueSerialVersionUID) {
			creationDate = (Date) is.readObject();
			mindsetObjects = (MindsetObject[]) is.readObject();
		}
	}

	/**
	 * Restores the {@link Kröw} program using this {@link Backup}.
	 *
	 * @param overwrite
	 *            Whether or not previous objects should be overwritten.
	 * @param backup
	 *            Whether or not a {@link Backup} should be made prior to the
	 *            restore, and saved.
	 */
	public void restore(final boolean overwrite, final boolean backup) {
		if (backup)
			try {
				new Backup().make();
			} catch (final IOException e) {
			}
		for (final MindsetObject o : mindsetObjects)
			try {
				o.getMindsetModel().attatch(Kröw.CONSTRUCT_MINDSET);
			} catch (final ObjectAlreadyExistsException e) {
				final String name = e.getThrower().getName();
				if (overwrite) {
					System.err.println(
							"The Object, " + name + ", already exists. The existing copy is being overwritten.");
					e.getVictim().delete();
					try {
						e.getThrower().getMindsetModel().attatch(Kröw.CONSTRUCT_MINDSET);
					} catch (final ObjectAlreadyExistsException e1) {
					}
				} else
					System.err.println("The Object, " + name + ", already exists. It could not be restored.");
			}
	}

	/**
	 * <p>
	 * The {@code writeObject} method of this {@link Serializable} instance.
	 *
	 * @param os
	 *            The {@link ObjectOutputStream} used to save this
	 *            {@link Backup} instance.
	 * @throws IOException
	 *             As specified by any of the {@link ObjectInputStream}'s write
	 *             methods, such as {@link ObjectOutputStream#writeLong(long)
	 *             writeLong()} or {@link ObjectOutputStream#writeObject(Object)
	 *             writeObject()}.
	 */
	private void writeObject(final ObjectOutputStream os) throws IOException {
		os.writeLong(Backup.trueSerialVersionUID);
		os.writeObject(creationDate);
		os.writeObject(mindsetObjects);
	}

}
