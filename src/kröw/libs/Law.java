package kröw.libs;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Law {
	private transient StringProperty description = new SimpleStringProperty();
	private transient StringProperty name = new SimpleStringProperty();

	public Law(final String name, final String description) {
		this.name.set(name);
		this.description.set(description);
	}

	private void readObject(final ObjectInputStream io) throws IOException {
		try {
			name = new SimpleStringProperty((String) io.readObject());
			description = new SimpleStringProperty((String) io.readObject());
			io.defaultReadObject();
		} catch (final ClassNotFoundException e) {
			System.err.println(
					"An error has occurred. Please notify the program author of what happened. Include the following message.");
			System.out.println("\n\n");
			System.err.println("---START OF MESSAGE---");
			e.printStackTrace();
			System.err.println("---END OF MESSAGE---");
		}
	}

	private void writeObject(final ObjectOutputStream os) {
		try {
			os.writeObject(name.get());
			os.writeObject(description.get());
			os.defaultWriteObject();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public String getDescription() {
		return description.get();
	}

	public StringProperty getDescriptionProperty() {
		return description;
	}

	public String getName() {
		return name.get();
	}

	public StringProperty getNameProperty() {
		return name;
	}

	public void setDescription(final String description) {
		this.description.set(description);
	}

	public void setName(final String name) {
		this.name.set(name);
	}

}
