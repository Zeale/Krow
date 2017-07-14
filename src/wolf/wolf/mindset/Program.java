package wolf.mindset;

import java.io.File;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import wolf.mindset.tables.TableViewable;
import wolf.zeale.Wolf;

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
	 * @see kröw.libs.MindsetObject#getExtension()
	 */
	@Override
	String getExtension() {
		return ".prgrm";
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

	/*
	 * (non-Javadoc)
	 *
	 * @see kröw.libs.MindsetObject#getSaveDirectory()
	 */
	@Override
	File getSaveDirectory() {
		return Wolf.PROGRAM_SAVE_DIRECTORY;
	}

	@Override
	public String getType() {
		return TYPE;
	}

}
