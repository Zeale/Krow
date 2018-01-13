package kröw.data.protection;

import kröw.core.Kröw;
import kröw.data.DataDirectory;
import sun.reflect.CallerSensitive;

public final class Protection {

	private static DataDirectory DOMAINS_DIRECTORY;

	/**
	 * @return the private domain belonging to the calling class.
	 */
	public static @CallerSensitive Domain getDomain() {

		// "Get caller method" code adapted from javafx.application.Application
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

		String name = null;
		StackTraceElement element;
		boolean found = false;

		for (StackTraceElement ste : stackTrace) {

			// The "getDomain" string literal may be replaced later with code that will
			// dynamically get this method's name.
			if (found) {

				name = ste.getClassName();
				element = ste;
				break;

			} else if (ste.getClassName().equals(Protection.class.getName()) && ste.getMethodName().equals("getDomain"))

				// We are iterating over stackTrace with an enhanced for loop (Item i:itemList)
				// so we don't have an index that we can use to get the next element from here,
				// (which is (hopefully) the class/method name we want). To solve this, we
				// continue the loop, setting "found" to true. The above if will be called in
				// the for loop with the item we want.
				found = true;

		}
		if (name == null)
			throw new RuntimeException(
					"Couldn't find the calling class and generate a domain for it. (For some reason, its not in the stack trace.)");
		Class<?> callingClass;
		try {
			callingClass = Class.forName(name, false, Thread.currentThread().getContextClassLoader());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("A class name was found, (" + name
					+ "), but attempting to get its corresponding object threw a ClassNotfoundException.", e);
		}

		// If callingClass is null, (because our above method failed,) then a
		// RuntimeException was thrown. We can assume that callingClass is safe to use.

		// Loop until we get a usable class.
		while (callingClass.isAnonymousClass()) {
			callingClass = callingClass.getEnclosingClass();
		}
		// TODO LEARN HOW SYNTHETIC CLASSES WORK AND DEAL WITH THEM!!!

		// Return a Domain whose path is checked to be that of an inner class. Call
		// some or other character replacement methods on the path based off of this
		// condition.
		return Domain.getDomain(callingClass.getName().contains("$")
				? callingClass.getName().replaceAll("\\.", "/").replaceAll("\\$", "#")// Apparently, $s also have to be
																						// escaped... Otherwise they
																						// mean endline or something.
				: callingClass.getCanonicalName().replaceAll("\\.", "/"));// We gotta use a \ escape char to replace
																			// literal periods.
	}

	static {
		// This needs to be commented to run the Demonstration class. We don't want to
		// be calling anything in the Kröw class, as that would call its static
		// initializers, leading to its futile initialization and some exceptions.
		// tryEnable();
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
