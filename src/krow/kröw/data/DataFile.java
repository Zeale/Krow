package kröw.data;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class DataFile extends File {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	public DataFile(String pathname) throws IOException {
		super(pathname);
		initcheck();
	}

	private void initcheck() throws IOException {
		if (exists() && !isFile())
			DataUtils.deleteDirectory(this);
		createNewFile();
	}

	public DataFile(URI uri) throws IOException {
		super(uri);
		initcheck();
	}

	public DataFile(String parent, String child) throws IOException {
		super(parent, child);
		initcheck();
	}

	public DataFile(File parent, String child) throws IOException {
		super(parent, child);
		initcheck();
	}

}
