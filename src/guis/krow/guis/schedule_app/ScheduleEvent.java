package krow.guis.schedule_app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WritableValue;
import zeale.guis.schedule_app.ScheduleApp;

public class ScheduleEvent implements Serializable, Comparable<ScheduleEvent> {

	private enum DataKey {
		DESCRIPTION, NAME, DUE_DATE, AUTOSAVE, URGENT, COMPLETE;
	}

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	public static ScheduleEvent load(final File file) throws FileNotFoundException, IOException {
		try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(file))) {
			final ScheduleEvent sc = (ScheduleEvent) is.readObject();
			sc.file = file;
			return sc;
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public final SimpleStringProperty description = new SimpleStringProperty(), name = new SimpleStringProperty();

	/**
	 * The number of milliseconds from January the first, 1970, 00:00:00 GMT,
	 * that this {@link ScheduleEvent}'s due date is.
	 */
	public final SimpleLongProperty dueDate = new SimpleLongProperty();

	public final SimpleBooleanProperty urgent = new SimpleBooleanProperty(false),
			complete = new SimpleBooleanProperty(false);

	private transient File file = new File(ScheduleApp.DATA_DIR, UUID.randomUUID().toString());

	public boolean autoSave = true;

	private HashMap<DataKey, Object> serializationData;

	private final ChangeListener<Object> onChanged = (observable, oldValue, newValue) -> {
		if (autoSave)
			try {
				save();
			} catch (final IOException e) {
				e.printStackTrace();
			}
	};

	{
		description.addListener(onChanged);
		name.addListener(onChanged);
		dueDate.addListener(onChanged);
		urgent.addListener(onChanged);
		complete.addListener(onChanged);
	}

	public ScheduleEvent() {
		this(new Date().getTime() + TimeUnit.DAYS.toMillis(1));
	}

	public ScheduleEvent(final long dueDate) {
		this(null, null);
		this.dueDate.set(dueDate);
	}

	private ScheduleEvent(final ScheduleEvent copy) {

		// Temporarily set this event's SerializationData variable, for the
		// purpose of copying
		serializationData = copy.serializationData;

		// Make sure we don't automatically save during copying.
		autoSave = false;

		// Copy data
		{
			file = copy.file;
			setProperty(description, DataKey.DESCRIPTION);
			setProperty(name, DataKey.NAME);
			setProperty(dueDate, DataKey.DUE_DATE);

			setProperty(urgent, DataKey.URGENT);
			setProperty(complete, DataKey.COMPLETE);

			// Enable autosaving *at the end* of copying, otherwise every
			// property
			// we make on this object (the copy) will try to save itself (even
			// before we've copied the file path.
			autoSave = serializationData.get(DataKey.AUTOSAVE) instanceof Boolean
					? (boolean) serializationData.get(DataKey.AUTOSAVE) : false;
		}

		// Unset the value of this event's SerializationData variable.
		serializationData = null;
	}

	public ScheduleEvent(final String description, final String name) {
		this(description, name, new Date().getTime() + TimeUnit.DAYS.toMillis(1));
	}

	public ScheduleEvent(final String description, String name, final long dueDate) {
		this.description.set(description);
		name = name == null ? "Unnamed" : name;
		this.name.set(name);
		this.dueDate.set(dueDate);
	}

	@Override
	public int compareTo(final ScheduleEvent o) {
		// Return - if this < o
		// ...
		if (complete.get()) {
			if (!o.complete.get())
				return 1;
			else /* Both complete */ {
				// If both complete, urgency is ignored. Default to simple due
				// date comparisons.
				return compareDueDates(o);
			}
		} else {
			if (o.complete.get())
				return -1;
			if (urgent.get()) {
				if (!o.urgent.get())
					return -1;
				else
					return compareDueDates(o);
			} else if (o.urgent.get())
				return 1;
			else
				return compareDueDates(o);
		}

	}

	private int compareDueDates(ScheduleEvent o) {
		if (dueDate.get() < o.dueDate.get())// Other event comes after
			return -1;// Place this event before other event.
		else if (dueDate.get() > o.dueDate.get())// Other event comes before
			return 1;// Place this event after other event.
		else
			return 0;
	}

	public void delete() {
		if (file != null && file.isFile())
			file.delete();
	}

	public File getFile() {
		return file;
	}

	public long getTimeUntilDue() throws IllegalArgumentException {
		return dueDate.get() - System.currentTimeMillis();
	}

	@SuppressWarnings("unchecked")
	private void readObject(final ObjectInputStream is) throws IOException {
		try {
			serializationData = (HashMap<DataKey, Object>) is.readObject();
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private Object readResolve() throws ObjectStreamException {
		return new ScheduleEvent(this);
	}

	public void save() throws IOException {
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		else if (file.exists())
			file.delete();
		try {
			file.createNewFile();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
			oos.writeObject(this);
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}

	}

	public void setFile(final File file) {
		this.file = file;
	}

	private <T> void setProperty(final WritableValue<T> property, final DataKey key) {
		@SuppressWarnings("unchecked")
		final T value = (T) serializationData.get(key);
		if (value == null)
			return;
		property.setValue(value);

	}

	private void writeObject(final ObjectOutputStream os) throws IOException {
		final HashMap<DataKey, Object> data = new HashMap<>(3);

		data.put(DataKey.DESCRIPTION, description.get());
		data.put(DataKey.NAME, name.get());
		data.put(DataKey.DUE_DATE, dueDate.get());
		data.put(DataKey.AUTOSAVE, autoSave);
		data.put(DataKey.URGENT, urgent.get());
		data.put(DataKey.COMPLETE, complete.get());

		os.writeObject(data);
	}

}
