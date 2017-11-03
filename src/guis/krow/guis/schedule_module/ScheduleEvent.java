package krow.guis.schedule_module;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import zeale.guis.schedule_module.ScheduleModule;

public class ScheduleEvent implements Serializable, Comparable<ScheduleEvent> {

	public final SimpleStringProperty description = new SimpleStringProperty(), name = new SimpleStringProperty();
	public final SimpleLongProperty dueDate = new SimpleLongProperty();
	private transient File file = new File(ScheduleModule.DATA_DIR, UUID.randomUUID().toString());

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

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
		name = name == null ? "Unnamed" : name;
		this.name.set(name);
		this.dueDate.set(dueDate);
		file = new File(ScheduleModule.DATA_DIR, name);
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

	public void save() {
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		} else if (file.exists())
			file.delete();
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		saveData();
	}

	private void saveData() {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
			oos.writeObject(this);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void updateFile() throws FileNotFoundException {
		if (!file.exists())
			throw new FileNotFoundException();
		saveData();
	}

	private enum DataKey {
		DESCRIPTION, NAME, DUE_DATE;
	}

	public long getTimeUntilDue() throws IllegalArgumentException {
		return dueDate.get() - System.currentTimeMillis();
	}

	@Override
	public int compareTo(ScheduleEvent o) {
		if (o.dueDate.get() > dueDate.get())
			return -1;
		else if (o.dueDate.get() < dueDate.get())
			return 1;
		else
			return 0;
	}

	public static ScheduleEvent load(File file) throws FileNotFoundException, IOException {
		try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(file))) {
			ScheduleEvent sc = (ScheduleEvent) is.readObject();
			sc.file = file;
			return sc;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

}
