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

public class System extends MindsetObject {

	private transient StringProperty name, description;

	private transient Date creationDate;

	public System(final String name, final String description, final Date creationDate) {
		super(name);
		this.name = new SimpleStringProperty(name);
		this.description = new SimpleStringProperty(description);
		this.creationDate = creationDate;
	}

	private static final long serialVersionUID = 1L;

	private static final long trueSerialVersionUID = 1L;

	public static final String FILE_EXTENSION = ".syst";

	private void readObject(final ObjectInputStream is) throws IOException {
		try {
			final long version = is.readLong();
			if (version == System.trueSerialVersionUID) {

				name = new SimpleStringProperty((String) is.readObject());
				description = new SimpleStringProperty((String) is.readObject());
				creationDate = (Date) is.readObject();
			}
		} catch (final ClassNotFoundException e) {
			// TODO: handle exception
		}
	}

	private void writeObject(final ObjectOutputStream os) throws IOException {
		os.writeLong(System.trueSerialVersionUID);
		os.writeObject(name.get());
		os.writeObject(description.get());
		os.writeObject(creationDate);
	}

	@Override
	String getExtension() {
		return System.FILE_EXTENSION;
	}

	@Override
	File getSaveDirectory() {
		return DataManager.SYSTEM_SAVE_DIRECTORY;
	}

	public StringProperty descriptionProperty() {
		return description;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof System && ((System) obj).getName().equals(getName());
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public String getDescription() {
		return description.get();
	}

	@Override
	public File getFile() {
		return new File(getSaveDirectory(), getName() + getExtension());
	}

	@Override
	public ObservableValue<?> getProperty(final String key) {
		if (key.equalsIgnoreCase("Name"))
			return new ReadOnlyObjectWrapper<>(name.get());
		else if (key.equalsIgnoreCase("Description"))
			return new ReadOnlyObjectWrapper<>(description.get());
		else if (key.equalsIgnoreCase("CreationDate") || key.equalsIgnoreCase("Creation Date")
				|| key.equalsIgnoreCase("Creation-Date"))
			return new ReadOnlyObjectWrapper<>(creationDate);
		return null;
	}

	public void setCreationDate(final Date creationDate) {
		this.creationDate = creationDate;
	}

	public void setDescription(final String description) {
		this.description.set(description);
	}

}
