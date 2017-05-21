package kröw.zeale.v1.program.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import wolf.mindset.MindsetObject;
import wolf.mindset.ObjectAlreadyExistsException;
import wolf.zeale.Wolf;
import wolf.zeale.collections.ObservableListWrapper;

public class Backup implements Serializable {

	private Date creationDate = Date.from(Instant.now(Clock.systemDefaultZone()));;
	private MindsetObject[] mindsetObjects;

	public Backup() {
		final Object[] mindsetObjects = Kröw.CONSTRUCT_MINDSET.getAllObjects().toArray();
		final MindsetObject[] arr = new MindsetObject[mindsetObjects.length];
		for (int i = 0; i < arr.length; i++)
			arr[i] = (MindsetObject) mindsetObjects[i];
		this.mindsetObjects = arr;
	}

	public Backup(final MindsetObject... mindsetObjects) {
		this.mindsetObjects = mindsetObjects;
	}

	public static final File BACKUP_SAVE_DIRECTORY = new File(Wolf.DATA_DIRECTORY, "Backups");

	private final static ObservableList<Backup> LOADED_BACKUPS = new ObservableListWrapper<>(new ArrayList<>());

	private static final long trueSerialVersionUID = 1L;

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;

	static void loadBackupsFromSystem() {

		Wolf.createFolder(Backup.BACKUP_SAVE_DIRECTORY);

		System.out.println("\n\nNow loading backups from the file system.");

		if (Backup.BACKUP_SAVE_DIRECTORY.listFiles() != null)
			for (final File f : Backup.BACKUP_SAVE_DIRECTORY.listFiles())
				try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(f));) {
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

	public static ObservableList<Backup> getObservableBackupList() {
		return Backup.LOADED_BACKUPS;
	}

	private void readObject(final ObjectInputStream is) throws IOException, ClassNotFoundException {
		if (is.readLong() == Backup.trueSerialVersionUID) {
			creationDate = (Date) is.readObject();
			mindsetObjects = (MindsetObject[]) is.readObject();
		}
	}

	private void writeObject(final ObjectOutputStream os) throws IOException {
		os.writeLong(Backup.trueSerialVersionUID);
		os.writeObject(creationDate);
		os.writeObject(mindsetObjects);
	}

	public Backup freshRestore() {
		final Backup backup = Kröw.clearAllObjects();
		restore(true, false);
		return backup;
	}

	public Date getDateCreated() {
		return creationDate;
	}

	public File getFile() {
		return new File(Backup.BACKUP_SAVE_DIRECTORY,
				"Backup_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(creationDate) + ".krbu");
	}

	public int getObjectCount() {
		return mindsetObjects.length;
	}

	public long getSize() {
		if (!getFile().exists())
			try {
				make();
			} catch (final IOException e) {
			}
		return getFile().length();
	}

	public File make() throws IOException {
		final File file = getFile();
		if (!file.createNewFile())
			return file;
		final ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file));
		os.writeObject(this);
		os.close();
		Backup.LOADED_BACKUPS.add(this);
		return file;
	}

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

}
