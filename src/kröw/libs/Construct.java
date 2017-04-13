package kröw.libs;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Construct implements Serializable {

	private transient StringProperty name;

	private transient StringProperty description;
	private transient BooleanProperty gender;

	private final ArrayList<Mark> marks = new ArrayList<>();

	private static final long serialVersionUID = 1L;

	public Construct(final String name, final String description, final boolean gender) {
		this.name = new SimpleStringProperty(name);
		this.description = new SimpleStringProperty(description);
		this.gender = new SimpleBooleanProperty(gender);
		setName(name);
		setDescription(description);
	}

	private void readObject(final ObjectInputStream is) throws IOException {
		try {
			name = new SimpleStringProperty((String) is.readObject());
			description = new SimpleStringProperty((String) is.readObject());
			gender = new SimpleBooleanProperty(is.readBoolean());
			is.defaultReadObject();
		} catch (final ClassNotFoundException e) {
			System.err.println("An error occurred while reading a construct.");
			e.printStackTrace();
		}

	}

	private void writeObject(final ObjectOutputStream os) throws IOException {
		os.writeObject(name.get());
		os.writeObject(description.get());
		os.writeBoolean(gender.get());
		os.defaultWriteObject();
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
		else if (((Construct) obj).getName().equals(getName()))
			return true;
		else
			return false;
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

	public final class Mark implements Serializable {
		private transient StringProperty description;
		private transient StringProperty mark;

		private static final long serialVersionUID = 1L;

		public Mark(final String description) {
			marks.add(this);
		}

		public Mark(final String mark, final String description) {
			this.description = new SimpleStringProperty(description);
			this.mark = new SimpleStringProperty(mark);
		}

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
