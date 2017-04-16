package kröw.libs;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import kröw.zeale.v1.program.core.DataManager;

public abstract class MindsetObject implements Serializable {
	public MindsetObject(final String objName) {
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

	public abstract String getName();
}
