package kröw.mindset;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TableView;
import kröw.mindset.tables.TableViewable;

/**
 * <p>
 * This class is at the top of the class hierarchy for objects immediately
 * pertaining to the Construct Mindset. It contains methods that ease client
 * code and would otherwise be duplicated throughout all Construct Mindset
 * objects needlessly.
 * <p>
 * This class manages:
 * <ul>
 * <li>Naming</li>
 * <li>Deletion and Saving</li>
 * <li>TableView CellValueFactory management</li>
 * </ul>
 *
 * <p>
 * Names are simple fields with getters and setters that are set when creating a
 * {@link MindsetObject} via the {@link #MindsetObject(String)} constructor.
 * <p>
 * {@link MindsetObject}s can be deleted using the {@link #delete()} method and
 * are saved automatically when the program is closed.
 * <p>
 * The {@link TableViewable.TableViewCellValueFactory} is used to display the
 * properties of a {@link MindsetObject} in a {@link TableView}.
 * {@link TableViewable.TableViewCellValueFactory} are constructed with a
 * <code>key</code> value which determines what value of the
 * {@link MindsetObject} that the
 * {@link TableViewable.TableViewCellValueFactory} will display. See each
 * individual {@link MindsetObject}'s subclass's for information on what
 * <code>key</code>s are valid.
 *
 * @author Zeale
 *
 */
@Deprecated
public abstract class MindsetObject implements Serializable, TableViewable {

	public class ConstructMindsetModel {

		private ConstructMindset attatchedMindset = null;

		public void attatch(final ConstructMindset mindset) throws ObjectAlreadyExistsException {

			if (mindset != null) {
				mindset.attatch(MindsetObject.this);
				attatchedMindset = mindset;
			}
		}

		public void detatch() {
			attatchedMindset.detatch(MindsetObject.this);
			attatchedMindset = null;
		}

		public ConstructMindset getAttatchedMindset() {
			return attatchedMindset;
		}

		public boolean isAttatched() {
			return attatchedMindset != null;
		}

	}

	public class Mark {
		public Mark() {
		}
	}

	public class MarkAlreadyExistsException extends Exception {

		/**
		 * SerialVersionUID.
		 */
		private static final long serialVersionUID = 1L;

		private final Mark thrower, victim;

		MarkAlreadyExistsException(final Mark thrower, final Mark victim) {
			this.thrower = thrower;
			this.victim = victim;
		}

		public Mark getThrower() {
			return thrower;
		}

		public Mark getVictim() {
			return victim;
		}

	}

	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The name of this {@link MindsetObject}.
	 */
	protected transient StringProperty name;

	/**
	 * A boolean to determine whether or not this {@link MindsetObject} has been
	 * deleted.
	 */
	protected transient boolean deleted = false;

	private transient ConstructMindsetModel mindsetModel = new ConstructMindsetModel();

	/**
	 * Sets the {@link #name} of this {@link MindsetObject}.
	 *
	 * @param objName
	 *            The {@link #name} of this new {@link MindsetObject}.
	 * @throws ObjectAlreadyExistsException
	 */
	MindsetObject(final String objName) {
		if (name == null)
			name = new SimpleStringProperty();
		try {
			setName(objName);
		} catch (final ObjectAlreadyExistsException e) {
		}
	}

	MindsetObject(final String objName, final ConstructMindset mindset) throws ObjectAlreadyExistsException {
		this(objName);
		getMindsetModel().attatch(mindset);
	}

	protected void checkAttatchedMindset() throws ObjectAlreadyExistsException {
		if (getMindsetModel().isAttatched())
			for (final MindsetObject object : getMindsetModel().getAttatchedMindset().getAllObjects())
				if (object != MindsetObject.this && object.getType().equals(getType())
						&& object.name.get().equals(name))
					throw new ObjectAlreadyExistsException(MindsetObject.this, object);
	}

	/**
	 * Deletes this {@link MindsetObject}.
	 */
	public void delete() {

		java.lang.System.out.println("\nDeleting the object " + getName() + ".");

		final File constructFile = getFile();
		if (constructFile.isFile())
			constructFile.delete();
		else
			java.lang.System.out.println("The object " + getName()
					+ " does not have an associated file... The object will only be deleted from RAM.");

		getMindsetModel().detatch();
		deleted = true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof MindsetObject))
			return false;
		final MindsetObject other = (MindsetObject) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.get().equals(other.name.get()))
			return false;
		return true;
	}

	// These may become public in following versions.
	abstract String getExtension();

	/**
	 * Gets the file of this {@link MindsetObject}. This is the {@link File}
	 * object where this {@link MindsetObject} will be saved to when the program
	 * closes.
	 *
	 * @return The file of this {@link MindsetObject}.
	 */
	public abstract File getFile();

	public ConstructMindsetModel getMindsetModel() {
		return mindsetModel;
	}

	/**
	 * @return This Object's name field.
	 */
	public final String getName() {
		try {
			return name.get();
		} catch (final NullPointerException e) {
			return null;
		}
	}

	public StringProperty getNameProperty() {
		return name;
	}

	abstract File getSaveDirectory();

	public abstract String getType();

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (name == null ? 0 : name.hashCode());
		return result;
	}

	public boolean isDeleted() {
		return deleted;
	}

	protected void makeEdit() {
		if (getMindsetModel().isAttatched())
			getMindsetModel().getAttatchedMindset().getSaveQueue().add(this);
	}

	public StringProperty nameProperty() {
		return name;
	}

	private void readObject(final ObjectInputStream is) throws IOException {
		mindsetModel = new ConstructMindsetModel();
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public final void setName(final String name) throws ObjectAlreadyExistsException {
		checkAttatchedMindset();
		if (!(name.equals(getName()) || isDeleted())) {
			final File old = getFile();
			makeEdit();
			this.name.set(name);
			old.renameTo(getFile());
		}

	}

	@Override
	public String toString() {
		return name.get();
	}

}
