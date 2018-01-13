package kröw.data;

import java.io.File;
import java.net.URI;

public class DataDirectory extends File {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	public DataDirectory(String pathname) {
		super(pathname);
		initcheck();
	}

	private void initcheck() {
		if (exists() && !isDirectory())
			delete();
		mkdirs();
	}

	public DataDirectory(URI uri) {
		super(uri);
		initcheck();
	}

	public DataDirectory(String parent, String child) {
		super(parent, child);
		initcheck();
	}

	public DataDirectory(File parent, String child) {
		super(parent, child);
		initcheck();
	}

	@Override
	public DataDirectory getParentFile() {
		return new DataDirectory(super.getParent());
	}

}
