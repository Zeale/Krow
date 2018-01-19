package kröw.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;

public final class DataUtils {

	private DataUtils() {
	}

	public static final boolean deleteDirectory(File directory) {
		if (!directory.exists())
			return true;
		try {
			// Try to delete the directory. We skip all the catch blocks if this directory
			// is able to be deleted, or if it's empty.
			Files.delete(directory.toPath());
		}

		// The directory wasn't empty, so we'll try to go through and delete its
		// contents recursively.
		catch (DirectoryNotEmptyException e) {
			// This is clearly a valid directory, so, if listFiles() returns false,
			// there was an IO error, which means we've failed. :(
			if (directory.listFiles() == null)
				return false;
			// We can now safely iterate over this directory's contents. :D
			for (File f : directory.listFiles()) {
				// If we fail to delete any subfiles/directories, then we can't delete this one,
				// so we return false.
				if (!deleteDirectory(f))
					return false;
			}
			// An IOException occurred. Return false.
		} catch (IOException e) {
			return false;
		}
		// Everything went well. Return true.
		return true;
	}

}
