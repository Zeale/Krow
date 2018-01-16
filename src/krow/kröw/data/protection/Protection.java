package kröw.data.protection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import kröw.core.Kröw;
import kröw.data.protection.DomainAccess.AccessOption;
import kröw.data.protection.DomainAccess.SingleAccessOption;
import sun.reflect.CallerSensitive;

public final class Protection {

	static File DOMAINS_DIRECTORY = new File(Kröw.DATA_DIRECTORY, "Domains");

	/**
	 * <p>
	 * This class was added as a protective mechanism against clients making
	 * classes, in the protection package, that would abuse the
	 * {@link Domain#getDomain(ProtectionKey)} method(s).
	 * <p>
	 * This class was not made to cause overhead or lag in any way; it is simply a
	 * secure wrapper for information passed to some of the {@link Domain} class's
	 * methods.
	 * <p>
	 * This security mechanism for communication between these classes does take
	 * away the ability for a client to customize or try and build off of the
	 * Protection API, since not every client is going to pull hacky stuff whenever
	 * they try to get into package private classes. That's why the dilemma as to
	 * whether or not I should keep this file is tearing me apart inside. (In other
	 * words, this may be changed later.)
	 * 
	 * @author Zeale
	 *
	 */
	static class ProtectionKey {

		String path;

		private ProtectionKey(String path) {
			this.path = path;
		}

		private ProtectionKey(Class<?> owner) {
			this(Domain.pkgToDom(owner.getName(), owner.getCanonicalName()));
		}
	}

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
	 * @throws DomainInitializeException
	 *             If there was an error while initializing the requested
	 *             {@link Domain}.
	 * @throws UnavailableException
	 *             Incase the Protection API is unavailable. (This happens when the
	 *             Domain's File Directory did not get made on the system, so
	 *             there's no parent folder to store Domain's and their data in. :(
	 *             See {@link #tryEnable()} and {@link #isAvailable()}.
	 */
	public static @CallerSensitive Domain getDomain() throws DomainInitializeException, UnavailableException {

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
		return Domain
				.getDomain(new ProtectionKey(Domain.pkgToDom(callingClass.getName(), callingClass.getCanonicalName())));
	}

	public static @CallerSensitive Domain getDomain(String fullPath)
			throws ClassNotFoundException, DomainInitializeException, UnavailableException {

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
		return Domain.getDomain(new ProtectionKey(victim));
	}

	public static @CallerSensitive Domain getDomain(Class<?> owner)
			throws DomainInitializeException, UnavailableException {

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
		return Domain.getDomain(new ProtectionKey(owner));
	}

	private static boolean checkAccess(Class<?> accessor, Class<?> victim) {
		if (accessor == victim)
			return true;
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

		// The above <i>should</i> cover all accessing if the domain is private (as in
		// it is only accessible by the class that it represents).

		DomainAccess[] dms = victim.getDeclaredAnnotationsByType(DomainAccess.class);
		List<AccessOption> options = new ArrayList<>();
		SingleAccessOption overallOption = null;
		List<Class<?>> explicitlyAllowedClasses = new ArrayList<>();
		for (DomainAccess dm : dms) {
			for (AccessOption ao : dm.options())
				if (options.contains(ao))
					throw new RuntimeException("Class declared with duplicate AccessOptions: " + victim.getName());
				else
					options.add(ao);
			if (dm.overallOption() != null)
				if (overallOption != null)
					throw new RuntimeException("Class declared with multiple SingleAccessOptions: " + victim.getName());
				else
					overallOption = dm.overallOption();
			for (Class<?> c : dm.allowedAccessors())
				if (!explicitlyAllowedClasses.contains(c))
					explicitlyAllowedClasses.add(c);
		}

		// First off, if our "victim" is public, then our "accessor" can jump right in
		// and access its domain.
		if (overallOption == SingleAccessOption.PUBLIC)
			return true;
		// For now, if the above isn't true, then the option is PRIVATE, since that's
		// the only other SingleAccessOption defined.

		// Now lets see if this class qualifies for one of the other customization
		// options. (Regular AccessOptions)
		if (options.contains(AccessOption.INHERITED)) {
			// INHERITED is placed on a class to signify that its subclasses can access its
			// domain.

			// If we're in this if block, then "victim" has added INHERITED to its array of
			// AccessOptions.

			// This means that we can go ahead and return true IF the "accessor" is a
			// subclass of "victim".

			Class<?> c = accessor;
			while (c != null)
				if (c.getSuperclass() == victim)
					return true;
				else
					c = c.getSuperclass();
		}

		// We do the same thing as above but by iterating through "victim"'s
		// superclasses for UP_INHERITED
		if (options.contains(AccessOption.UP_INHERITED)) {
			Class<?> c = victim;
			while (c != null)
				if (c.getSuperclass() == accessor)
					return true;
				else
					c = c.getSuperclass();
		}

		// Lastly, if they've explictly allowed this accessor access, then by golly gosh
		// m8. Let's give them access.
		if (explicitlyAllowedClasses.contains(accessor))
			return true;

		return false;
	}

	private static void doAvailabilityCheck() throws UnavailableException {
		if (!isAvailable())
			tryEnable();
		if (!isAvailable())
			throw new UnavailableException("Protection API Unavailable.");
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
				DOMAINS_DIRECTORY = new File(Kröw.DATA_DIRECTORY, "Domains");
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
