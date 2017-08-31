package kröw.libs.mindset;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import kröw.core.Kröw;
import kröw.libs.mindset.tables.TableViewable;

/**
 * <p>
 * This class represents a Law.
 * <p>
 * Property List:
 *
 * <ul>
 * <li><b>Name</b> - The name of this {@link Law}.</li>
 * <li><b>Rule</b> - The rule instated by this {@link Law}.</li>
 * <li><b>Creation Date</b> - The {@link Date} that this {@link Law} was
 * made.</li>
 * <li><b>Description</b> - A description of this {@link Law}.</li>
 * </ul>
 *
 *
 * @author Zeale
 *
 */
@Deprecated
public final class Law extends MindsetObject implements TableViewable {
	public static final String TYPE = "Law";

	// SerialVersionUIDs (for saving and reading objects)
	/**
	 * The Serial Version Unique Identifier for this class. This should never
	 * change. The {@link #trueSerialVersionUID} is incremented when this class
	 * is updated to signify different versions and different read/write
	 * operation necessities.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The actual version of this class. When this class, (specifically the
	 * structure of this object), is modified in such a way that the read and
	 * write operations of this object are in need of modification themselves,
	 * this long value is incremented by {@code 1} to signify a new version of
	 * this class. See the {@link #readObject(ObjectInputStream)} and
	 * {@link #writeObject(ObjectOutputStream)} methods for more details.
	 */
	private static final long trueSerialVersionUID = 1L;

	/**
	 * The file extension of saved objects that are of the {@link Law} type.
	 */
	private static final String FILE_EXTENSION = ".law";

	// Class fields
	/**
	 * <p>
	 * {@link #description} - A description of this {@link Law} as a
	 * {@link SimpleStringProperty}.
	 * <p>
	 * {@link #rule} - The rule that this {@link Law} states.
	 */
	private transient StringProperty description = new SimpleStringProperty(), rule = new SimpleStringProperty();

	/**
	 * The {@link Date} that this {@link Law} was made.
	 */
	private transient Date creationDate;

	/**
	 * <p>
	 * Constructs a new {@link Law} using the specified parameters.
	 *
	 * @param name
	 *            The name of this {@link Law}.
	 * @param description
	 *            A description of this {@link Law}.
	 * @param rule
	 *            The rules instated by this {@link Law}.
	 * @param creationDate
	 *            The {@link Date} that this {@link Law} was created.
	 * @throws ObjectAlreadyExistsException
	 *             In case a Law with the same name already exists.
	 */
	public Law(final String name, final String description, final String rule, final Date creationDate) {
		super(name);
		this.description.set(description);
		this.rule.set(rule);
		this.creationDate = creationDate;
	}

	// Getters and setters
	/**
	 * Gets the description of this {@link Law}.
	 *
	 * @return The {@link StringProperty} representation of this {@link Law}'s
	 *         description.
	 */
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
		return obj instanceof Law && ((Law) obj).getName().equals(getName());
	}

	/**
	 * Gets the {@link Date} that this {@link Law} was created.
	 *
	 * @return The {@link Date} that this {@link Law} was created.
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * Gets the description of this {@link Law} in the form of a {@link String}.
	 *
	 * @return The description of this {@link Law} in the form of a
	 *         {@link String}.
	 */
	public String getDescription() {
		return description.get();
	}

	/**
	 * Gets the description property of this {@link Law}. this is equivalent to
	 * calling {@link #descriptionProperty()}.
	 *
	 * @return The {@link #description} field.
	 */
	public StringProperty getDescriptionProperty() {
		return description;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see kröw.libs.MindsetObject#getExtension()
	 */
	@Override
	String getExtension() {
		return Law.FILE_EXTENSION;
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

	/*
	 * (non-Javadoc)
	 *
	 * @see kröw.libs.MindsetObject#getProperty(java.lang.String)
	 */
	@Override
	public ObservableValue<?> getProperty(final String key) {
		if (key.equalsIgnoreCase("Name"))
			return new ReadOnlyObjectWrapper<>(name.get());
		else if (key.equalsIgnoreCase("Rule") || key.equalsIgnoreCase("Rules"))
			return new ReadOnlyObjectWrapper<>(rule.get());
		else if (key.equalsIgnoreCase("Description"))
			return new ReadOnlyObjectWrapper<>(description.get());
		else if (key.equalsIgnoreCase("CreationDate") || key.equalsIgnoreCase("Creation Date")
				|| key.equalsIgnoreCase("Creation-Date"))
			return new ReadOnlyObjectWrapper<>(creationDate);
		else
			return null;
	}

	/**
	 * Getter for this {@link Law}'s {@link #rule} property as a {@link String}.
	 *
	 * @return {@link #rule} as a {@link String}.
	 */
	public String getRule() {
		return rule.get();
	}

	/**
	 * Getter for this {@link Law}'s {@link #rule} property. This is equivalent
	 * to calling {@link #ruleProperty()}.
	 *
	 * @return {@link #rule}
	 */
	public StringProperty getRuleProperty() {
		return rule;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see kröw.libs.MindsetObject#getSaveDirectory()
	 */
	@Override
	File getSaveDirectory() {
		return Kröw.LAW_SAVE_DIRECTORY;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	// Serialization methods
	/**
	 * <p>
	 * This method is automatically called by Java's Serialization API when
	 * reading an object.
	 * <p>
	 * This method first reads a {@code long} from the given
	 * {@link ObjectOutputStream} to check for the version of the saved
	 * {@link Law}. This method then decides how to load the {@link Law} using
	 * the version it found. For example, if the version found matches that of a
	 * previously released class, the {@link Law} being instantiated is read
	 * using the previous reading technique and any values that are not found
	 * are set to there defaults.
	 *
	 * @param is
	 *            The {@link ObjectInputStream} passed in via Serialization.
	 * @throws IOException
	 *             Needed so that Java can call this method reflectively.
	 */
	private void readObject(final ObjectInputStream is) throws IOException {

		final long version = is.readLong();

		try {
			if (version == Law.trueSerialVersionUID) {

				name = new SimpleStringProperty((String) is.readObject());
				description = new SimpleStringProperty((String) is.readObject());
				rule = new SimpleStringProperty((String) is.readObject());
				creationDate = (Date) is.readObject();
			}

		} catch (final ClassNotFoundException e) {
		}
	}

	/**
	 * Getter for this {@link Law}'s {@link #rule} property. This is equivalent
	 * to calling {@link #getRuleProperty()}.
	 *
	 * @return {@link #rule}
	 */
	public StringProperty ruleProperty() {
		return rule;
	}

	/**
	 * Sets this {@link Law}'s {@link #creationDate}.
	 *
	 * @param creationDate
	 *            The new {@link #creationDate}.
	 */
	public void setCreationDate(final Date creationDate) {
		if (!(this.creationDate.equals(creationDate) || isDeleted())) {
			this.creationDate = creationDate;
			makeEdit();
		}
	}

	/**
	 * Sets this {@link Law}'s {@link #description} value.
	 *
	 * @param description
	 *            The new {@link #description} value.
	 */
	public void setDescription(final String description) {
		if (!(this.description.get().equals(description) || isDeleted())) {
			this.description.set(description);
			makeEdit();
		}
	}

	/**
	 * Sets this {@link Law}'s {@link #rule} value.
	 *
	 * @param rule
	 *            The new {@link #rule} value.
	 */
	public void setRule(final String rule) {
		if (!(this.rule.get().equals(rule) || isDeleted())) {
			this.rule.set(rule);
			makeEdit();
		}

	}

	/**
	 * <p>
	 * This method is automatically called by Java's Serialization API when
	 * attempting to save this object.
	 * <p>
	 * First, this method saves the version of the {@link Law} class that this
	 * object was created in. Next, this method saves everything that is needed
	 * to replicate this object in such a state that there would be no
	 * significant difference to the user if the original object is replaced
	 * with the new.
	 *
	 * @param os
	 *            The {@link ObjectOutputStream} passed in by Serialization.
	 */
	private void writeObject(final ObjectOutputStream os) {
		try {
			os.writeLong(Law.trueSerialVersionUID);
			os.writeObject(name.get());
			os.writeObject(description.get());
			os.writeObject(rule.get());
			os.writeObject(creationDate);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
