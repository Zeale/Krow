package kr�w.libs.mindset;

import java.io.File;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import kr�w.core.Kr�w;
import kr�w.libs.mindset.tables.TableViewable;

/**
 * <p>
 * This class represents a Program.
 *
 * <p>
 * <i>Note: This class is not yet complete and will not function as
 * expected.</i>
 *
 * @author Zeale
 *
 */
@Deprecated
public final class Program extends MindsetObject implements TableViewable {

	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public final static String TYPE = "Program";

	public Program(final String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see kr�w.libs.MindsetObject#getExtension()
	 */
	@Override
	String getExtension() {
		return ".prgrm";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see kr�w.libs.MindsetObject#getFile()
	 */
	@Override
	public File getFile() {
		return new File(getSaveDirectory(), getName() + getExtension());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see kr�w.libs.MindsetObject#getProperty(java.lang.String)
	 */
	@Override
	public ObservableValue<?> getProperty(final String key) {
		if (key.equalsIgnoreCase("Name"))
			return new ReadOnlyObjectWrapper<>(name.get());
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see kr�w.libs.MindsetObject#getSaveDirectory()
	 */
	@Override
	File getSaveDirectory() {
		return Kr�w.PROGRAM_SAVE_DIRECTORY;
	}

	@Override
	public String getType() {
		return TYPE;
	}

}
