package kröw.libs;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import kröw.zeale.v1.program.core.DataManager;

public abstract class MindsetObject implements Serializable {

	protected transient StringProperty name;
	protected transient boolean deleted = false;

	public MindsetObject(final String objName) {
		name.set(objName);
		for (final MindsetObject o : MindsetObject.objectsToSave)
			if (o.getName().equals(objName))
				return;
		MindsetObject.objectsToSave.add(this);
	}

	protected static List<MindsetObject> objectsToSave = new LinkedList<>();

	private static final long serialVersionUID = 1L;

	public static boolean removeObjectFromSaveQueue(final MindsetObject object) {
		return MindsetObject.objectsToSave.remove(object);
	}

	public static void saveObjects() {
		for (final MindsetObject m : MindsetObject.objectsToSave)
			DataManager.saveObject(m, new File(m.getSaveDirectory(), m.getName() + m.getExtension()));
	}

	// These may become public in following versions.
	abstract String getExtension();

	abstract File getSaveDirectory();

	public void delete() {

		java.lang.System.out.println("\nDeleting the object " + getName() + ".");

		MindsetObject.objectsToSave.remove(this);

		final File constructFile = getFile();
		if (constructFile.isFile())
			constructFile.delete();
		else
			java.lang.System.out.println("The object " + getName()
					+ " does not have an associated file... The object will only be deleted from RAM.");
	}

	public abstract File getFile();

	/**
	 * @return This Object's name field.
	 */
	public final String getName() {
		try {
			return name.get();
		} catch (final NullPointerException e) {
			return null;
		}
	}

	public StringProperty getNameProperty() {
		return name;
	}

	public abstract ObservableValue<?> getProperty(String key);

	public StringProperty nameProperty() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public final void setName(final String name) {
		this.name.set(name);
		if (!MindsetObject.objectsToSave.contains(this) && !deleted)
			MindsetObject.objectsToSave.add(this);
	}

	public static class MindsetObjectTableViewCellValueFactory<S extends MindsetObject, T>
			implements Callback<CellDataFeatures<S, T>, ObservableValue<T>> {
		private final String key;

		public MindsetObjectTableViewCellValueFactory(final String key) {
			this.key = key;
		}

		protected String getKey() {
			return key;
		}

		@SuppressWarnings("unchecked")
		@Override
		public ObservableValue<T> call(final CellDataFeatures<S, T> param) {
			return (ObservableValue<T>) param.getValue().getProperty(key);
		}

	}

}
