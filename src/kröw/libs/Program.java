package kröw.libs;

import java.io.File;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import kröw.zeale.v1.program.core.DataManager;

public class Program extends MindsetObject {

	public Program(final String name) {
		super(name);
	}

	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see kröw.libs.MindsetObject#getExtension()
	 */
	@Override
	String getExtension() {
		return ".prgrm";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kröw.libs.MindsetObject#getSaveDirectory()
	 */
	@Override
	File getSaveDirectory() {
		return DataManager.PROGRAM_SAVE_DIRECTORY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kröw.libs.MindsetObject#getFile()
	 */
	@Override
	public File getFile() {
		return new File(getSaveDirectory(), getName() + getExtension());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kröw.libs.MindsetObject#getProperty(java.lang.String)
	 */
	@Override
	public ObservableValue<?> getProperty(final String key) {
		if (key.equalsIgnoreCase("Name"))
			return new ReadOnlyObjectWrapper<>(name.get());
		return null;
	}

}
