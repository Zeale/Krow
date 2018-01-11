package kröw.data.protection;

import kröw.core.Kröw;
import kröw.data.DataDirectory;

public final class Protection {

	private static DataDirectory DOMAINS_DIRECTORY;

	static {
		tryEnable();
	}

	public static final boolean tryEnable() {

		// Why is this method being called?
		if (isAvailable())
			throw new RuntimeException("The Protection API is already enabled.");

		if (DOMAINS_DIRECTORY != null) {
			if (!DOMAINS_DIRECTORY.exists()) {
				return DOMAINS_DIRECTORY.mkdirs();
			} else if (!DOMAINS_DIRECTORY.isDirectory()) {
				DOMAINS_DIRECTORY.delete();
				return DOMAINS_DIRECTORY.mkdirs();
			} else {
				// This block is only run if DOMAINS_DIRECTORY!=null, the directory exists, and
				// it is a directory. This code should never get run, since a runtime exception
				// is thrown above if these conditions are true.
				return true;
			}
		} else {
			try {
				DOMAINS_DIRECTORY = new DataDirectory(Kröw.DATA_DIRECTORY, "Domains");
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

	}

	public static boolean isAvailable() {
		return DOMAINS_DIRECTORY != null && DOMAINS_DIRECTORY.isDirectory();
	}

	private Protection() {
	}

}
