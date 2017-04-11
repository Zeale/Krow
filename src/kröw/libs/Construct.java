package kröw.libs;

import java.util.ArrayList;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Construct {
	private final StringProperty name = new SimpleStringProperty(), description = new SimpleStringProperty();

	private final ArrayList<Mark> marks = new ArrayList<>();

	public Construct(final String name, final String description) {
		this.name.set(name);
		this.description.set(description);
	}

	/**
	 * @return This Object's description field.
	 */
	public final String getDescription() {
		return description.get();
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
	public final String getName() {
		return name.get();
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public final void setDescription(final String description) {
		this.description.set(description);
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public final void setName(final String name) {
		this.name.set(name);
	}

	public final class Mark {
		private String description;

		public Mark(final String description) {
			marks.add(this);
		}

		/**
		 * @return This Object's description field.
		 */
		public final String getDescription() {
			return description;
		}

		/**
		 * @param description
		 *            the description to set
		 */
		public final void setDescription(final String description) {
			this.description = description;
		}

	}

}
