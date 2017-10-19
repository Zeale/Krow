package krow.guis.schedule_module;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

public class ScheduleEvent implements Serializable {

	private String description, name;

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	public ScheduleEvent() {
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public ScheduleEvent(String description, String name) {
		this.description = description;
		this.name = name;
	}

	private void writeObject(ObjectOutputStream os) throws IOException {
		HashMap<DataKey, Object> data = new HashMap<>();
		data.put(DataKey.DESCRIPTION, description);
		data.put(DataKey.NAME, name);
		os.writeObject(data);
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream is) throws IOException {
		HashMap<DataKey, Object> data;
		try {
			data = (HashMap<DataKey, Object>) is.readObject();
			name = (String) data.get(DataKey.NAME);
			description = (String) data.get(DataKey.DESCRIPTION);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	private enum DataKey {
		DESCRIPTION, NAME;
	}

}
