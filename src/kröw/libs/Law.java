package kröw.libs;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Law {
	private final StringProperty description = new SimpleStringProperty();
	private final StringProperty name = new SimpleStringProperty();

	public Law(final String name, final String description) {
		this.name.set(name);
		this.description.set(description);
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
