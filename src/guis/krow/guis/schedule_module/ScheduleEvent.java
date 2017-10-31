package krow.guis.schedule_module;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class ScheduleEvent implements Serializable {

	public final SimpleStringProperty description = new SimpleStringProperty(), name = new SimpleStringProperty();
	public final SimpleLongProperty dueDate = new SimpleLongProperty();

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	public ScheduleEvent() {
		this(new Date().getTime() + TimeUnit.DAYS.toMillis(1));
	}

	public ScheduleEvent(String description, String name) {
		this(description, name, new Date().getTime() + TimeUnit.DAYS.toMillis(1));
	}

	public ScheduleEvent(String description, String name, long dueDate) {
		this.description.set(description);
		this.name.set(name == null ? "Unnamed" : name);
		this.dueDate.set(dueDate);
	}

	public ScheduleEvent(long dueDate) {
		this(null, null);
		this.dueDate.set(dueDate);
	}

	private void writeObject(ObjectOutputStream os) throws IOException {
		HashMap<DataKey, Object> data = new HashMap<>();

		data.put(DataKey.DESCRIPTION, description.get());
		data.put(DataKey.NAME, name.get());
		data.put(DataKey.DUE_DATE, dueDate.get());

		os.writeObject(data);
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream is) throws IOException {
		HashMap<DataKey, Object> data;
		try {
			data = (HashMap<DataKey, Object>) is.readObject();
			name.set((String) data.get(DataKey.NAME));
			description.set((String) data.get(DataKey.DESCRIPTION));
			dueDate.setValue((long) data.get(DataKey.DUE_DATE));
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	private enum DataKey {
		DESCRIPTION, NAME, DUE_DATE;
	}

	public long getTimeUntilDue() throws IllegalArgumentException {
		return dueDate.get() - System.currentTimeMillis();
	}

}
