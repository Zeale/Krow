package kröw.libs;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import kröw.zeale.v1.program.core.DataManager;
import kröw.zeale.v1.program.core.Kröw;

public class Construct extends MindsetObject {

	private transient boolean deleted = false;

	private transient StringProperty name;

	private transient StringProperty description;

	private boolean gender;

	private final ArrayList<Mark> marks = new ArrayList<>();

	private boolean alive;

	public Construct(final String name, final String description, final boolean gender, final boolean alive) {
		super(name);
		this.name = new SimpleStringProperty(name);
		this.description = new SimpleStringProperty(description);
		this.gender = gender;
		this.alive = alive;

	}

	private static final long serialVersionUID = 1L;// This can't change...

	private static final long trueSerialVersionUID = 1L;

	public static final String FILE_EXTENSION = ".const";

	private void loadOldObjects(final ObjectInputStream is) {
		try {
			name = new SimpleStringProperty((String) is.readObject());
			description = new SimpleStringProperty((String) is.readObject());
			gender = ((SimpleBooleanProperty) is.readObject()).get();
		} catch (final ClassNotFoundException e1) {
			java.lang.System.err.println("An error occurred while reading a construct.");
			e1.printStackTrace();
		} catch (final IOException e) {

		}
	}

	private void readObject(final ObjectInputStream is) throws IOException {
		long version;
		try {
			version = is.readLong();
		} catch (final Exception e) {
			version = 0;
			loadOldObjects(is);
			return;
		}
		try {
			name = new SimpleStringProperty((String) is.readObject());
		} catch (final ClassNotFoundException e) {
			java.lang.System.err.println("Could not load a construct.");
			if (Kröw.DEBUG_MODE) {
				java.lang.System.out.println("\n\n\n");
				e.printStackTrace();
			} else
				java.lang.System.err.println("Enable Debug Mode to see stack traces.");
		}
		try {
			description = new SimpleStringProperty((String) is.readObject());
			gender = is.readBoolean();
			alive = is.readBoolean();
		} catch (final ClassNotFoundException e) {
			java.lang.System.err.println("Could not load the construct " + name.get() + ".");
			if (Kröw.DEBUG_MODE) {
				java.lang.System.out.println("\n\n\n");
				e.printStackTrace();
			} else
				java.lang.System.err.println("Enable Debug Mode to see stack traces.");
		}

		if (version <= 1)
			return;

	}

	private void writeObject(final ObjectOutputStream os) throws IOException {
		os.writeLong(Construct.trueSerialVersionUID);
		os.writeObject(name.get());
		os.writeObject(description.get());
		os.writeBoolean(gender);
		os.writeBoolean(alive);
	}

	@Override
	String getExtension() {
		return Construct.FILE_EXTENSION;
	}

	@Override
	File getSaveDirectory() {
		return DataManager.CONSTRUCT_SAVE_DIRECTORY;
	}

	@Override
	public void delete() {

		java.lang.System.out.println("\nDeleting the construct " + getName() + "... :(");

		MindsetObject.objectsToSave.remove(this);

		final File constructFile = new File(DataManager.CONSTRUCT_SAVE_DIRECTORY, getName() + ".const");
		if (constructFile.isFile())
			constructFile.delete();
		else
			java.lang.System.out.println("The construct " + getName()
					+ " does not have an associated file... The construct will only be deleted from RAM.");
		deleted = true;
	}

	public StringProperty descriptionProperty() {
		return description;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Construct))
			return false;
		else
			return ((Construct) obj).getName().equals(getName());
	}

	/**
	 * @return This Object's description field.
	 */
	public final String getDescription() {
		return description.get();
	}

	@Override
	public File getFile() {
		return new File(getSaveDirectory(), getName() + getExtension());
	}

	public boolean getGender() {
		return gender;
	}

	/**
	 * @return This Object's marks field.
	 */
	public final ArrayList<Mark> getMarks() {
		return marks;
	}

	/**
	 * @return This Object's name field.
	 */
	@Override
	public final String getName() {
		return name.get();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (marks == null ? 0 : marks.hashCode());
		return result;
	}

	public boolean isAlive() {
		return alive;
	}

	public StringProperty nameProperty() {
		return name;
	}

	public void setAlive(final boolean alive) {
		this.alive = alive;
		if (!MindsetObject.objectsToSave.contains(this) && !deleted)
			MindsetObject.objectsToSave.add(this);
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public final void setDescription(final String description) {
		this.description.set(description);
		if (!MindsetObject.objectsToSave.contains(this) && !deleted)
			MindsetObject.objectsToSave.add(this);
	}

	public void setGender(final boolean female) {
		gender = female;
		if (!MindsetObject.objectsToSave.contains(this) && !deleted)
			MindsetObject.objectsToSave.add(this);
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public final void setName(final String name) {
		this.name.set(name);
		if (!MindsetObject.objectsToSave.contains(this) && !deleted)
			MindsetObject.objectsToSave.add(this);
	}

	public boolean trueEquals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Construct))
			return false;
		final Construct other = (Construct) obj;
		if (marks == null) {
			if (other.marks != null)
				return false;
		} else if (!marks.equals(other.marks))
			return false;
		return true;
	}

	public static class ConstructCellValueFactory<S extends Construct, T>
			implements Callback<CellDataFeatures<S, T>, ObservableValue<T>> {

		private final Type type;

		public ConstructCellValueFactory(final Type type) {
			this.type = type;
		}

		@SuppressWarnings("unchecked")
		@Override
		public ObservableValue<T> call(final CellDataFeatures<S, T> param) {
			switch (type) {
			case NAME:
				return new ReadOnlyObjectWrapper<>((T) param.getValue().getName());
			case GENDER:
				return new ReadOnlyObjectWrapper<>((T) (param.getValue().getGender() ? "Female" : "Male"));
			case ALIVE:
				return (ObservableValue<T>) new ReadOnlyBooleanWrapper(param.getValue().isAlive());
			case DESCRIPTION:
				return new ReadOnlyObjectWrapper<>((T) param.getValue().getDescription());
			case MARKS:
				return new ReadOnlyObjectWrapper<>((T) param.getValue().getMarks());
			default:
				return null;
			}
		}

		public static enum Type {
			NAME, GENDER, ALIVE, DESCRIPTION, MARKS;
		}

	}

	public final class Mark implements Serializable {
		private transient StringProperty description;
		private transient StringProperty mark;

		public Mark(final String description) {
			marks.add(this);
		}

		public Mark(final String mark, final String description) {
			this.description = new SimpleStringProperty(description);
			this.mark = new SimpleStringProperty(mark);
		}

		private static final long serialVersionUID = 1L;

		private void readObject(final ObjectInputStream is) throws IOException {
			try {
				description = new SimpleStringProperty((String) is.readObject());
				mark = new SimpleStringProperty((String) is.readObject());
			} catch (final ClassNotFoundException e) {
				e.printStackTrace();
			}

		}

		private void writeObject(final ObjectOutputStream os) throws IOException {
			os.writeObject(description.get());
			os.writeObject(mark.get());
			os.defaultWriteObject();
		}

		/**
		 * @return This Object's description field.
		 */
		public final String getDescription() {
			return description.get();
		}

		/**
		 * @param description
		 *            the description to set
		 */
		public final void setDescription(final String description) {
			this.description.set(description);
		}

	}

}
