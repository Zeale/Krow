package kröw.libs;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import kröw.zeale.v1.program.core.DataManager;

/**
 * <p>
 * This class is at the top of the class hierarchy for objects immediately
 * pertaining to the Construct Mindset. It contains methods that ease client
 * code and would otherwise be duplicated throughout all Construct Mindset
 * objects needlessly.
 * <p>
 * This class manages:
 * <ul>
 * <li>Naming</li>
 * <li>Deletion and Saving</li>
 * <li>TableView CellValueFactory management</li>
 * </ul>
 *
 * <p>
 * Names are simple fields with getters and setters that are set when creating a
 * {@link MindsetObject} via the {@link #MindsetObject(String)} constructor.
 * <p>
 * {@link MindsetObject}s can be deleted using the {@link #delete()} method and
 * are saved automatically when the program is closed.
 * <p>
 * The {@link MindsetObjectTableViewCellValueFactory} is used to display the
 * properties of a {@link MindsetObject} in a {@link TableView}.
 * {@link MindsetObjectTableViewCellValueFactory} are constructed with a
 * <code>key</code> value which determines what value of the
 * {@link MindsetObject} that the {@link MindsetObjectTableViewCellValueFactory}
 * will display. See each individual {@link MindsetObject}'s subclass's for
 * information on what <code>key</code>s are valid.
 *
 * @author Zeale
 *
 */
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
