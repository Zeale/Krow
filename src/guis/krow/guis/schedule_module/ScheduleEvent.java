package krow.guis.schedule_module;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class ScheduleEvent implements Serializable {

	public final SimpleStringProperty description = new SimpleStringProperty(), name = new SimpleStringProperty();
	public final SimpleObjectProperty<Date> dueDate = new SimpleObjectProperty<>();

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	public ScheduleEvent() {
		this(null);
	}

	public ScheduleEvent(String description, String name) {
		this(description, name, null);
	}

	public ScheduleEvent(String description, String name, Date dueDate) {
		this.description.set(description);
		this.name.set(name == null ? "Unnamed" : name);
		this.dueDate.set(dueDate);
	}

	public ScheduleEvent(Date dueDate) {
		this(null, null);
		this.dueDate.set(dueDate);
	}

	private void writeObject(ObjectOutputStream os) throws IOException {
		HashMap<DataKey, Object> data = new HashMap<>();

		data.put(DataKey.DESCRIPTION, description.get());
		data.put(DataKey.NAME, name.get());
		data.put(DataKey.DUE_DATE, dueDate);

		os.writeObject(data);
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream is) throws IOException {
		HashMap<DataKey, Object> data;
		try {
			data = (HashMap<DataKey, Object>) is.readObject();
			name.set((String) data.get(DataKey.NAME));
			description.set((String) data.get(DataKey.DESCRIPTION));
			dueDate.setValue((Date) data.get(DataKey.DUE_DATE));
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	private enum DataKey {
		DESCRIPTION, NAME, DUE_DATE;
	}

	public long getTimeUntilDue() throws IllegalArgumentException {
		if (dueDate.get() == null)
			throw new IllegalArgumentException(
					"The date for the schedule event named " + name.get() + " can not be null.");
		return dueDate.get().getTime() - new Date().getTime();
	}

}
