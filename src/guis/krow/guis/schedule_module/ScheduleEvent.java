package krow.guis.schedule_module;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import zeale.guis.schedule_module.ScheduleModule;

public class ScheduleEvent implements Serializable, Comparable<ScheduleEvent> {

	public final SimpleStringProperty description = new SimpleStringProperty(), name = new SimpleStringProperty();
	public final SimpleLongProperty dueDate = new SimpleLongProperty();
	private transient File file = new File(ScheduleModule.DATA_DIR, UUID.randomUUID().toString());

	public boolean autoSave = true;

	private final ChangeListener<Object> onChanged = (observable, oldValue, newValue) -> {
		if (autoSave)
			save();
	};

	{
		description.addListener(onChanged);
		name.addListener(onChanged);
		dueDate.addListener(onChanged);
	}

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
	}

	public ScheduleEvent(long dueDate) {
		this(null, null);
		this.dueDate.set(dueDate);
	}

	private void writeObject(ObjectOutputStream os) throws IOException {
		HashMap<DataKey, Object> data = new HashMap<>(3);

		data.put(DataKey.DESCRIPTION, description.get());
		data.put(DataKey.NAME, name.get());
		data.put(DataKey.DUE_DATE, dueDate.get());

		os.writeObject(data);
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream is) throws IOException {
		HashMap<DataKey, Object> data;
		Field nameField = null, descField = null, dateField = null;
		try {
			// Allow modification of final fields

			nameField = getClass().getDeclaredField("name");
			descField = getClass().getDeclaredField("description");
			dateField = getClass().getDeclaredField("dueDate");

			nameField.setAccessible(true);
			descField.setAccessible(true);
			dateField.setAccessible(true);

			data = (HashMap<DataKey, Object>) is.readObject();

			getClass().getField("name").set(this, new SimpleStringProperty((String) data.get(DataKey.NAME)));
			getClass().getField("description").set(this,
					new SimpleStringProperty((String) data.get(DataKey.DESCRIPTION)));
			getClass().getField("dueDate").set(this, new SimpleLongProperty((long) data.get(DataKey.DUE_DATE)));
		} catch (Exception e) {
			try {
				nameField.setAccessible(false);
				descField.setAccessible(false);
				dateField.setAccessible(false);
			} catch (Exception e1) {
			}
			throw new IOException(e);
		}

		nameField.setAccessible(false);
		descField.setAccessible(false);
		dateField.setAccessible(false);

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

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
			oos.writeObject(this);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

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
