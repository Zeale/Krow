package kröw.libs;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import kröw.zeale.v1.program.core.DataManager;

public class Law extends MindsetObject {

	// Class fields
	private transient StringProperty description = new SimpleStringProperty(), rule = new SimpleStringProperty();
	private transient Date creationDate;

	private boolean deleted;

	// Constructor
	public Law(final String name, final String description, final String rule, final Date creationDate) {
		super(name);
		this.description.set(description);
		this.rule.set(rule);
		this.creationDate = creationDate;
	}

	// SerialVersionUIDs (for saving and reading objects)
	private static final long serialVersionUID = 1L;

	private static final long trueSerialVersionUID = 1L;

	private static final String FILE_EXTENSION = ".law";

	// Serialization methods
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

	@Override
	String getExtension() {
		return Law.FILE_EXTENSION;
	}

	@Override
	File getSaveDirectory() {
		return DataManager.LAW_SAVE_DIRECTORY;
	}

	// Getters and setters
	public StringProperty descriptionProperty() {
		return description;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Law && ((Law) obj).getName().equals(getName());
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public String getDescription() {
		return description.get();
	}

	public StringProperty getDescriptionProperty() {
		return description;
	}

	@Override
	public File getFile() {
		return new File(getSaveDirectory(), getName() + getExtension());
	}

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

	public String getRule() {
		return rule.get();
	}

	public StringProperty getRuleProperty() {
		return rule;
	}

	public StringProperty ruleProperty() {
		return rule;
	}

	public void setCreationDate(final Date creationDate) {
		this.creationDate = creationDate;
		if (!MindsetObject.objectsToSave.contains(this) && !deleted)
			MindsetObject.objectsToSave.add(this);
	}

	public void setDescription(final String description) {
		this.description.set(description);
		if (!MindsetObject.objectsToSave.contains(this) && !deleted)
			MindsetObject.objectsToSave.add(this);
	}

	public void setRule(final String rule) {
		this.rule.set(rule);
		if (!MindsetObject.objectsToSave.contains(this) && !deleted)
			MindsetObject.objectsToSave.add(this);
	}

}
