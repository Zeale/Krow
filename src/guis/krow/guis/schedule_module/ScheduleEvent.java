package krow.guis.schedule_module;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

public class ScheduleEvent implements Serializable {

	public final ObservableString description = new ObservableString(), name = new ObservableString();

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	public ScheduleEvent() {
	}

	public ScheduleEvent(String description, String name) {
		this.description.set(description);
		this.name.set(name);
	}

	private void writeObject(ObjectOutputStream os) throws IOException {
		HashMap<DataKey, Object> data = new HashMap<>();

		data.put(DataKey.DESCRIPTION, description.get());
		data.put(DataKey.NAME, name.get());

		os.writeObject(data);
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream is) throws IOException {
		HashMap<DataKey, Object> data;
		try {
			data = (HashMap<DataKey, Object>) is.readObject();
			name.set((String) data.get(DataKey.NAME));
			description.set((String) data.get(DataKey.DESCRIPTION));
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	private enum DataKey {
		DESCRIPTION, NAME;
	}

}
