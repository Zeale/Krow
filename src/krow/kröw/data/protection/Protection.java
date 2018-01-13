package kröw.data.protection;

import kröw.core.Kröw;
import kröw.data.DataDirectory;
import sun.reflect.CallerSensitive;

public final class Protection {

	private static DataDirectory DOMAINS_DIRECTORY;

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static Domain hackIntoTestsDomain;

	public static void main(String[] args) {
		// Exploit: Gives the domain of Test, even though
		new Test() {
			{
				hackIntoTestsDomain = getDomain();
			}
		};

		System.out.println(hackIntoTestsDomain.getPath());

	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * @return the private domain belonging to the calling class.
	 */
	public static @CallerSensitive Domain getDomain() {

		// "Get caller method" code adapted from javafx.application.Application
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

		String name = null;
		boolean found = false;

		for (StackTraceElement ste : stackTrace) {

			// The "getDomain" string literal may be replaced later with code that will
			// dynamically get this method's name.
			if (found) {

				name = ste.getClassName();
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
			throw new RuntimeException("Couldn't find the calling class and generate a domain for it.");
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
		while (callingClass.isAnonymousClass() || callingClass.isSynthetic()) {
			// THIS LOOP IS WRONG. Doing this allows me to instantiate a class (say "Test")
			// from my class and access its domain. When I make an anonymous class off of
			// Test, this method (because of this loop) will return the domain of my
			// anonymous class's superclass. That would give me Test's domain. What should
			// happen, is that I am given the domain of the class in which I
			// called upon/instantiated my anonymous class. See the above main method.

			// This loop should never cause callingClass to be null, since Object is the
			// only class (for the purposes of this method) whose return value for
			// "getSuperclass()" will be null, and it, itself, is a concrete class.
			callingClass = callingClass.getSuperclass();
		}

		// Return a Domain whose path is checked to be that of an inner class. Call
		// some or other character replacement methods on the path based off of this
		// condition.
		return Domain.getDomain(callingClass.getName().contains("$")
				? callingClass.getName().replaceAll("\\.", "/").replaceAll("$", "#")
				: callingClass.getCanonicalName().replaceAll("\\.", "/"));// We gotta use a \ escape char to replace
																			// literal periods.
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
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
