package kröw.data.protection;

import kröw.core.Kröw;
import kröw.data.DataDirectory;
import sun.reflect.CallerSensitive;

public final class Protection {
	private static DataDirectory DOMAINS_DIRECTORY;

	/**
	 * <p>
	 * This method returns a Protection {@link Domain} that belongs to the calling
	 * class. More info about Domains can be found in the {@link Domain}s class.
	 * <p>
	 * If the calling class is an anonymous class, then the class in which it is
	 * defined is used to get a Domain. The returned domain is, resultingly, the
	 * domain if the enclosing class.
	 * <p>
	 * If the calling class is anonymous and its enclosing class is anonymous as
	 * well, (and that class’s enclosing class is anonymous, etc. etc.,) then this
	 * method iterates until it finds an enclosing class that it can use.
	 * 
	 * @return the private domain belonging to the calling class.
	 */
	public static @CallerSensitive Domain getDomain() {

		doAvailabilityCheck();

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
		// TODO LEARN HOW SYNTHETIC CLASSES WORK AND DEAL WITH THEM (if needed)!!!

		// Return a Domain whose path is converted from a package name to a Domain name.
		// See the Domain class (literally referenced below) for more details.
		return Domain.getDomain(Domain.pkgToDom(callingClass.getName(), callingClass.getCanonicalName()));
	}

	public static Domain getDomain(String fullPath) throws ClassNotFoundException {

		doAvailabilityCheck();

		// Check access to this Domain and see if the calling class is allowed to obtain
		// an instance of it.

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
			throw new RuntimeException(
					"Couldn't find the calling class and generate a domain for it. (For some reason, its not in the stack trace.)");
		Class<?> callingClass;
		try {
			callingClass = Class.forName(name, false, Thread.currentThread().getContextClassLoader());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("A class name was found, (" + name
					+ "), but attempting to get its corresponding object threw a ClassNotfoundException.", e);
		}

		// We have to get a class object (or try to get one) off of the given "fullPath"
		// variable, then compare it with the calling class and see if the calling
		// class has any access to its domain.
		//
		// Converting "fullPath" to something we can pass to Class.forName(...):
		Class<?> victim = Class.forName(Domain.domToPkg(fullPath));
		if (!checkAccess(callingClass, victim))
			throw new RuntimeException("Illegal class accessing.");// This should be a checked exception.
		return Domain.getDomain(victim);
	}

	public static Domain getDomain(Class<?> owner) {

		doAvailabilityCheck();

		// Check access to this Domain and see if the calling class is allowed to obtain
		// an instance of it.

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
			throw new RuntimeException(
					"Couldn't find the calling class and generate a domain for it. (For some reason, its not in the stack trace.)");
		Class<?> callingClass;
		try {
			callingClass = Class.forName(name, false, Thread.currentThread().getContextClassLoader());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("A class name was found, (" + name
					+ "), but attempting to get its corresponding object threw a ClassNotfoundException.", e);
		}

		if (!checkAccess(callingClass, owner))
			throw new RuntimeException("Illegal class accessing.");// This should also be a checked exception.
		return Domain.getDomain(owner);
	}

	private static boolean checkAccess(Class<?> accessor, Class<?> victim) {
		while (accessor.isAnonymousClass())
			accessor = accessor.getEnclosingClass();
		while (victim.isAnonymousClass())
			victim = victim.getEnclosingClass();
		if (accessor == victim)
			return true;
		// I feel like this is kinda edgy, but I can't think of a better solution.
		// Probably cuz it's almost 1 AM.
		if (accessor.getName().equals(victim.getName()))
			return true;
		// In a little bit, we'll add a little checkup for annotations. There will be an
		// annotation (that can be applied to "Types") that will allow a class to let
		// another, explicitly defined class, access its domain.
		//
		// Also, public domains still need to be added.
		return false;
	}

	private static void doAvailabilityCheck() {
		if (!isAvailable())
			tryEnable();
		if (!isAvailable())
			throw new RuntimeException("Protection API Unavailable.");
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
