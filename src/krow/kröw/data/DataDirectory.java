package kröw.data;

import java.io.File;
import java.net.URI;
import java.nio.file.FileAlreadyExistsException;

public class DataDirectory extends File {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	public DataDirectory(String pathname) throws FileAlreadyExistsException {
		super(pathname);
		initcheck();
	}

	private void initcheck() throws FileAlreadyExistsException {
		if (exists() && !isDirectory())
			throw new FileAlreadyExistsException(getPath());
	}

	public DataDirectory(URI uri) throws FileAlreadyExistsException {
		super(uri);
		initcheck();
	}

	public DataDirectory(String parent, String child) throws FileAlreadyExistsException {
		super(parent, child);
		initcheck();
	}

	public DataDirectory(File parent, String child) throws FileAlreadyExistsException {
		super(parent, child);
		initcheck();
	}

}
