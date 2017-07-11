package wolf.mindset;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import wolf.mindset.tables.TableViewable;
import wolf.zeale.Wolf;

/**
 * <p>
 * This class represents a Construct.
 * <p>
 * Property List:
 * <ul>
 * <li><b>Name</b> - The name of this {@link Construct}.</li>
 * <li><b>Description</b> - The description of this {@link Construct}.</li>
 * <li><b>Gender</b> - This {@link Construct}'s gender converted to a
 * {@link String}. (Has a value of either <code>"Male"</code> or
 * <code>"Female"</code>.)</li>
 * <li><b>Raw Gender</b> - This {@link Construct}'s gender as a boolean.
 * <code>true</code> is female, <code>false</code> is male.</li>
 * <li><b>Living</b>/<b>Alive</b> - Whether or not this {@link Construct} is
 * alive. (<code>true</code> if it's alive, <code>false</code> if it's
 * dead.)</li>
 * <li><b>Dead</b> - Whether or not this {@link Construct} is dead.(Outputs
 * <code>true</code> if dead and <code>false</code> if alive.)</li><br>
 *
 * @author Zeale
 *
 */
public final class Construct extends MindsetObject implements TableViewable {

	/**
	 * The description of this {@link Construct}.
	 */
	private transient StringProperty description;

	/**
	 * The gender of this {@link Construct}. <code>true</code> equates to
	 * female, while <code>false</code> is male.
	 */
	private boolean gender;

	/**
	 * The various {@link Mark}s of this {@link Construct}.
	 */
	private ArrayList<Mark> marks = new ArrayList<>();

	/**
	 * Whether or not this {@link Construct} is currently alive.
	 */
	private boolean alive;

	/**
	 * <strong>Constructs</strong> a new {@link Construct} with the
	 * {@link Construct}'s properties being the specified parameters.
	 *
	 * @param name
	 *            The name of this new {@link Construct}.
	 * @param description
	 *            The description of this {@link Construct}.
	 * @param gender
	 *            The gender of this {@link Construct}. <code>true</code> for
	 *            female, <code>false</code> for male.
	 * @param alive
	 *            Whether or not this {@link Construct} is alive.
	 * @throws ObjectAlreadyExistsException
	 *             In case a {@link Construct} with the same name already
	 *             exists.
	 */
	public Construct(final String name, final String description, final boolean gender, final boolean alive) {
		super(name);
		this.description = new SimpleStringProperty(description);
		this.gender = gender;
		this.alive = alive;

	}

	public static final String TYPE = "Construct";

	/**
	 * The Serial Version Unique Identifier of this class. This should never
	 * change. To ensure backwards compatibility, the
	 * {@link #trueSerialVersionUID} is used and the
	 * {@link #readObject(ObjectInputStream)} method complies with it.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * <p>
	 * The version that this {@link Construct} was saved in to the file system.
	 * <p>
	 * When {@link Construct}s are saved, their data is written down to a file
	 * as specified in the {@link #readObject(ObjectInputStream)} method. First,
	 * their {@link #trueSerialVersionUID} is written as a <code>long</code>,
	 * then the rest of their data is saved. Since {@link Construct}s may gain
	 * more or lose some properties in later versions of this program, this
	 * program must be able to load previous versions of {@link Construct}s. We
	 * accomplish this by reading the {@link #trueSerialVersionUID} of the saved
	 * {@link Construct} and then loading it up as it would have been in that
	 * version of the {@link Construct}.
	 * <p>
	 * The {@link #trueSerialVersionUID} is like a version of this Object.
	 *
	 * @see #readObject(ObjectInputStream)
	 */
	private static final long trueSerialVersionUID = 2L;

	/**
	 * <p>
	 * This is the file extension of {@link Construct} files. When the program
	 * is closing and files are saved, a {@link Construct} is written to a file
	 * whose name is the {@link Construct}'s original name followed by the
	 * {@link #FILE_EXTENSION}.
	 * <p>
	 * For example, the file for a {@link Construct} named <code>"Kröw"</code>
	 * would be as follows: <br>
	 * <br>
	 * <ul>
	 * <li><code>Kröw.const</code></li>
	 * </ul>
	 */
	public static final String FILE_EXTENSION = ".const";

	private void loadMarks(final ObjectInputStream is, final int count) throws ClassNotFoundException, IOException {
		for (int i = 0; i < count; i++) {

			final long version = is.readLong();
			if (version == Mark.trueSerialVersionUID)
				try {
					makeMark((String) is.readObject(), is.readBoolean(), is.readBoolean(), (String) is.readObject());
				} catch (MarkAlreadyExistsException e) {
				}

		}
	}

	/**
	 * <p>
	 * This is a helper method for {@link #readObject(ObjectInputStream)}. It is
	 * meant to load old {@link Construct}s which do not have a
	 * {@link #trueSerialVersionUID} saved.
	 *
	 * @param is
	 *            The {@link ObjectInputStream} passed into the
	 *            {@link #readObject(ObjectInputStream)} method.
	 */
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

	/**
	 * <p>
	 * This method loads in {@link Construct}s with serialization via a file.
	 * <p>
	 * When {@link Construct}s are loaded, first the file is checked for a
	 * version. This is necessary for backwards compatibility. The program
	 * writes a {@link Construct}'s {@link #trueSerialVersionUID} down when it
	 * saves the {@link Construct} in the
	 * {@link #writeObject(ObjectOutputStream)} method.
	 * <p>
	 * When reading a {@link Construct}, the program determines what data the
	 * {@link Construct} has and how to load it in depending on its
	 * {@link #trueSerialVersionUID}.
	 * <p>
	 * See {@link #writeObject(ObjectOutputStream)} for more details on saving
	 * the {@link Construct}.
	 *
	 * @param is
	 *            The {@link ObjectInputStream} passed in by the Serialization
	 *            API.
	 * @throws IOException
	 *             If an {@link IOException} occurs. This is also necessary for
	 *             Serialization to recognize this method as the notorious
	 *             readObject method.
	 * @see #writeObject(ObjectOutputStream)
	 */
	private void readObject(final ObjectInputStream is) throws IOException {
		long version;
		try {
			version = is.readLong();
		} catch (final Exception e) {
			version = 0;
			loadOldObjects(is);
			return;
		}
		if (version == Construct.trueSerialVersionUID) {
			try {
				name = new SimpleStringProperty((String) is.readObject());
			} catch (final ClassNotFoundException e) {
				java.lang.System.err.println("Could not load a construct.");
				if (Wolf.DEBUG_MODE) {
					java.lang.System.out.println("\n\n\n");
					e.printStackTrace();
				} else
					java.lang.System.err.println("Enable Debug Mode to see stack traces.");
			}
			try {
				description = new SimpleStringProperty((String) is.readObject());
				gender = is.readBoolean();
				alive = is.readBoolean();
				int markCount = 0;
				try {
					markCount = is.readInt();
				} catch (final Exception e) {

				}
				marks = new ArrayList<>();
				loadMarks(is, markCount);

				marks.sort(null);

			} catch (final ClassNotFoundException e) {
				java.lang.System.err.println("Could not load the construct " + name.get() + ".");
				if (Wolf.DEBUG_MODE) {
					java.lang.System.out.println("\n\n\n");
					e.printStackTrace();
				} else
					java.lang.System.err.println("Enable Debug Mode to see stack traces.");
			}
		}
		if (version == 1) {
			marks = new ArrayList<>();// So that version 2 can save this object
										// correctly.
			try {
				name = new SimpleStringProperty((String) is.readObject());
			} catch (final ClassNotFoundException e) {
				java.lang.System.err.println("Could not load a construct.");
				if (Wolf.DEBUG_MODE) {
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
				if (Wolf.DEBUG_MODE) {
					java.lang.System.out.println("\n\n\n");
					e.printStackTrace();
				} else
					java.lang.System.err.println("Enable Debug Mode to see stack traces.");
			}
		}

		if (version <= 1)
			return;

	}

	/**
	 * <p>
	 * This method writes down {@link Construct}s to a file via Serialization.
	 * <p>
	 * First, a {@link Construct}'s {@link #trueSerialVersionUID} is written.
	 * Afterwards, the {@link Construct}'s properties are written (including the
	 * inherited {@link MindsetObject#name} property). See the
	 * {@link #readObject(ObjectInputStream)} method for more details on
	 * backwards compatibility and the {@link #trueSerialVersionUID}.
	 *
	 * @param os
	 *            The {@link ObjectOutputStream} passed in by the Serialization
	 *            API.
	 * @throws IOException
	 *             If an {@link IOException} occurs. This is also necessary for
	 *             Serialization to recognize this method as the famous
	 *             writeObject method.
	 * @see #readObject(ObjectInputStream)
	 */
	private void writeObject(final ObjectOutputStream os) throws IOException {
		os.writeLong(Construct.trueSerialVersionUID);
		os.writeObject(name.get());
		os.writeObject(description.get());
		os.writeBoolean(gender);
		os.writeBoolean(alive);
		os.writeInt(marks.size());
		for (final Mark m : marks)
			m.writeObject(os);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see kröw.libs.MindsetObject#getExtension()
	 */
	@Override
	String getExtension() {
		return Construct.FILE_EXTENSION;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see kröw.libs.MindsetObject#getSaveDirectory()
	 */
	@Override
	File getSaveDirectory() {
		return Wolf.CONSTRUCT_SAVE_DIRECTORY;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see kröw.libs.MindsetObject#delete()
	 */
	@Override
	public void delete() {

		java.lang.System.out.println("\nDeleting the construct " + getName() + "... :(");

		getMindsetModel().detatch();

		final File constructFile = new File(Wolf.CONSTRUCT_SAVE_DIRECTORY, getName() + ".const");
		if (constructFile.isFile())
			constructFile.delete();
		else
			java.lang.System.out.println("The construct " + getName()
					+ " does not have an associated file... The construct will only be deleted from RAM.");
		deleted = true;
	}

	/**
	 * <p>
	 * The {@link #description} property of this {@link Construct}. It is really
	 * just a {@link String} that describes the {@link Construct}.
	 *
	 * @return This {@link Construct}'s {@link #description} property.
	 */
	public StringProperty descriptionProperty() {
		return description;
	}

	/**
	 * @return This Object's description field.
	 */
	public final String getDescription() {
		return description.get();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see kröw.libs.MindsetObject#getFile()
	 */
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

	/*
	 * (non-Javadoc)
	 *
	 * @see kröw.libs.MindsetObject#getProperty(java.lang.String)
	 */
	@Override
	public ObservableValue<?> getProperty(final String key) {
		if (key.equalsIgnoreCase("Name"))
			return new ReadOnlyObjectWrapper<>(name.get());
		else if (key.equalsIgnoreCase("Raw Gender") || key.equalsIgnoreCase("Raw-Gender")
				|| key.equalsIgnoreCase("RawGender"))
			return new ReadOnlyBooleanWrapper(gender);
		else if (key.equalsIgnoreCase("Alive") || key.equalsIgnoreCase("Living"))
			return new ReadOnlyBooleanWrapper(alive);
		else if (key.equalsIgnoreCase("Description"))
			return new ReadOnlyObjectWrapper<>(description.get());
		else if (key.equalsIgnoreCase("Dead"))
			return new ReadOnlyBooleanWrapper(!alive);
		else if (key.equalsIgnoreCase("Gender"))
			return new ReadOnlyObjectWrapper<>(gender ? "Female" : "Male");
		return null;
	}

	@Override
	public String getType() {
		return TYPE;
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
		result = prime * result + (alive ? 1231 : 1237);
		result = prime * result + (gender ? 1231 : 1237);
		result = prime * result + (marks == null ? 0 : marks.hashCode());
		return result;
	}

	/**
	 * Checks if this {@link Construct} is alive.
	 *
	 * @return <code>true</code> if this {@link Construct} is alive,
	 *         <code>false</code> otherwise.
	 */
	public boolean isAlive() {
		return alive;
	}

	public Mark makeMark(final String description, final boolean gender, final boolean alive, final String mark)
			throws MarkAlreadyExistsException {
		for (final Mark m : marks)
			if (m.mark.equals(mark))
				throw new MarkAlreadyExistsException(null, m);
		final Mark mrk = new Mark(mark, description);
		mrk.alive = alive;
		mrk.gender = gender;
		marks.add(mrk);
		return mrk;
	}

	/**
	 * Sets whether or not this {@link Construct} is alive.
	 *
	 * @param alive
	 *            <code>true</code> if this {@link Construct} is alive,
	 *            <code>false</code> otherwise.
	 */
	public void setAlive(final boolean alive) {
		if (!(alive == this.alive || isDeleted())) {
			this.alive = alive;
			makeEdit();
		}
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public final void setDescription(final String description) {
		this.description.set(description);
	}

	/**
	 * Sets this {@link Construct}'s gender. <code>true</code> for female,
	 * <code>false</code> for male. See {@link #gender}.
	 *
	 * @param female
	 *            The new gender of this {@link Construct}. <code>true</code>
	 *            for female, <code>false</code> for male.
	 */
	public void setGender(final boolean female) {
		gender = female;
		if (!(female == gender || isDeleted())) {
			gender = female;
			makeEdit();
		}
	}

	/**
	 * <p>
	 * Compares the actual equality of this {@link Construct} with another by
	 * checking equality between all non-transient fields.
	 * <p>
	 * This method may be moved to the {@link MindsetObject} class and made
	 * <code>abstract</code>.
	 *
	 * @param obj
	 *            The {@link Object} to compare equality to.
	 * @return
	 */
	public boolean trueEquals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Construct other = (Construct) obj;
		if (alive != other.alive)
			return false;
		if (gender != other.gender)
			return false;
		if (marks == null) {
			if (other.marks != null)
				return false;
		} else if (!marks.equals(other.marks))
			return false;
		return true;
	}

	/**
	 * The representation of a {@link Construct}'s Mark as a class.
	 *
	 * @author Zeale
	 *
	 */
	public final class Mark extends MindsetObject.Mark implements Comparable<Mark>, TableViewable {

		/**
		 * A description of this {@link Construct} at this specific Mk.
		 */
		private String description;
		/**
		 * The gender of this {@link Construct} at this specific Mk.
		 */
		private boolean gender;

		/**
		 * Whether or not this {@link Construct} was alive at this Mk.
		 */
		private boolean alive;

		/**
		 * Which Mk this {@link Mark} object represents.
		 */
		private String mark;

		/**
		 * Creates a {@link Mark} object with a description and a
		 * <code>mark</code>. The <code>mark</code> portrays this
		 * {@link Construct}'s mark number. For example, if this was a
		 * {@link Construct}'s 8th mark, the {@code mark} argument might be
		 * something like <code>8</code>. The program will probably display this
		 * as something such as "ConstructName Mk8" in the GUI, where
		 * "ConstructName" is the name of this {@link Construct}.
		 *
		 * @param mark
		 *            The name of the Mk that this {@link Mark} represents.
		 * @param description
		 *            A description of this {@link Construct} at the time of
		 *            this {@link Mark}.
		 */
		private Mark(final String mark, final String description) {
			this.description = description;
			this.mark = mark;
		}

		/**
		 * This value is best described as the version of this class. It's used
		 * for backwards compatibility when reading and writing objects. The
		 * {@code trueSerialVersionUID} is read from a file <i>first</i> when
		 * loading an object, then the program uses this version to decide what
		 * values the file has and how to load it.
		 */
		private static final long trueSerialVersionUID = 1L;

		private Construct getOuterType() {
			return Construct.this;
		}

		/**
		 * The {@code writeObject} method of this class. It's used to save
		 * objects via Serialization.
		 *
		 * @param os
		 *            The {@link ObjectOutputStream} passed in via
		 *            Serialization.
		 * @throws IOException
		 *             If an {@link IOException} occurs. Also needed to match
		 *             the specified method signature of the
		 *             <code>readObject</code> method used for Serialization.
		 */
		private void writeObject(final ObjectOutputStream os) throws IOException {
			os.writeLong(Mark.trueSerialVersionUID);
			os.writeObject(description);
			os.writeBoolean(gender);
			os.writeBoolean(alive);
			os.writeObject(mark);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(final Mark o) {
			return mark.compareTo(o.mark);
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
			if (!(obj instanceof Mark))
				return false;
			final Mark other = (Mark) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			return true;
		}

		/**
		 * @return This Object's description field.
		 */
		public final String getDescription() {
			return description;
		}

		public String getMark() {
			return mark;
		}

		@Override
		public ObservableValue<?> getProperty(final String key) {
			if (key.equalsIgnoreCase("Name"))
				return new ReadOnlyObjectWrapper<>(getMark());
			else if (key.equalsIgnoreCase("Description"))
				return new ReadOnlyObjectWrapper<>(description);
			else if (key.equalsIgnoreCase("Living") || key.equalsIgnoreCase("Alive"))
				return new ReadOnlyObjectWrapper<>(alive ? "Living" : "Dead");
			else if (key.equalsIgnoreCase("Gender"))
				return new ReadOnlyObjectWrapper<>(gender ? "Female" : "Male");
			else if (key.equalsIgnoreCase("Raw-Gender") || key.equalsIgnoreCase("Raw Gender")
					|| key.equalsIgnoreCase("Raw_Gender") || key.equalsIgnoreCase("RawGender"))
				return new ReadOnlyBooleanWrapper(gender);
			return null;
		}

		public boolean getRawGender() {
			return gender;
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
			result = prime * result + (alive ? 1231 : 1237);
			result = prime * result + (gender ? 1231 : 1237);
			return result;
		}

		public boolean isAlive() {
			return alive;
		}

		public void setAlive(final boolean alive) {
			if (!(this.alive && isDeleted())) {
				this.alive = alive;
				makeEdit();
			}
		}

		/**
		 * @param description
		 *            the description to set
		 */
		public final void setDescription(final String description) {
			if (!(this.description.equals(description) || isDeleted())) {
				this.description = description;
				makeEdit();
			}
		}

		public void setGender(final boolean gender) {
			if (!(this.gender == gender || isDeleted())) {
				this.gender = gender;
				makeEdit();
			}
		}

		public void setMark(final String mark) throws MarkAlreadyExistsException {
			if (!(this.mark.equals(mark) || isDeleted())) {
				for (final Mark m : marks)
					if (m != this && m.getMark().equals(mark))
						throw new MindsetObject.MarkAlreadyExistsException(this, m);
				this.mark = mark;
				makeEdit();
			}
		}

	}

}
