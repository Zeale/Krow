package kröw.data.protection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import kröw.core.Kröw;
import kröw.data.protection.DomainAccess.AccessOption;
import kröw.data.protection.DomainAccess.SingleAccessOption;
import sun.reflect.CallerSensitive;

public final class Protection {

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

		private ProtectionKey(Class<?> owner) {
			this(Domain.pkgToDom(owner.getName(), owner.getCanonicalName()));
		}

		private ProtectionKey(String path) {
			this.path = path;
		}
	}

	/**
	 * The directory where {@link Domain}s are stored on the physical hard drive.
	 */
	static File DOMAINS_DIRECTORY = new File(Kröw.DATA_DIRECTORY, "Protection/domains/default-set");

	/**
	 * Checks to see if <code>accessor</code> can access <code>victim</code>'s
	 * {@link Domain}.
	 * 
	 * @param accessor
	 *            The accessing class.
	 * @param victim
	 *            The class whose {@link Domain} is being accessed.
	 * @return <code>true</code> if <code>accessor</code> can access
	 *         <code>victim</code>'s {@link Domain}, <code>false</code> otherwise.
	 */
	private static boolean checkAccess(Class<?> accessor, Class<?> victim) {

		while (accessor.isAnonymousClass())
			accessor = accessor.getEnclosingClass();
		while (victim.isAnonymousClass())
			victim = victim.getEnclosingClass();
		if (accessor == victim)
			return true;

		// Solved the edginess. I hope. Loading a class through multiple loaders is
		// probably gonna cause bigger problems that are irrelevent to the Protection
		// API, but whatever.
		if (accessor.getClassLoader() != victim.getClassLoader() && accessor.getName().equals(victim.getName()))
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

	/**
	 * Convenience method for checking availability by <code>getDomain(...)</code>
	 * methods.
	 * 
	 * @throws UnavailableException
	 *             If the Protection API is unavailable.
	 */
	private static void doAvailabilityCheck() throws UnavailableException {
		if (!isAvailable())
			tryEnable();
		if (!isAvailable())
			throw new UnavailableException("Protection API Unavailable.");
	}

	/**
	 * <p>
	 * This method returns a Protection {@link Domain} that belongs to the calling
	 * class. More info about Domains can be found in the {@link Domain}s class.
	 * <p>
	 * If the calling class is an anonymous class, then the class in which it is
	 * defined is used to get a Domain. The returned domain is, resultingly, the
	 * domain of the enclosing class.
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

	/**
	 * <p>
	 * This method attempts to return a {@link Domain} given the class that
	 * {@link Domain owns} it.
	 * <p>
	 * This method is {@link CallerSensitive} and will throw a
	 * {@link RuntimeException} if the calling class is not allowed to access the
	 * specified {@link Class}'s {@link Domain}. (Access can be given to different
	 * classes via the {@link DomainAccess} annotation.)
	 * <p>
	 * If the calling class is an anonymous class, then the class in which it is
	 * defined is used to access the Domain. That means that the enclosing class is
	 * what is checked for accessibility. Inner and nested classes have their own
	 * domains, like normal classes.
	 * <p>
	 * If the calling class is anonymous and its enclosing class is anonymous as
	 * well, (and that class’s enclosing class is anonymous, etc. etc.,) then this
	 * method iterates until it finds an enclosing class that it can use. That class
	 * is then checked to see if it has permission to access the domain.
	 * 
	 * @param owner
	 *            The {@link Class} that owns the domain.
	 * @return The domain belonging to the specified class.
	 * @throws DomainInitializeException
	 *             In case there was an exception while initializing the domain.
	 * @throws UnavailableException
	 *             In case the Protection API is unavailable. See
	 *             {@link #isAvailable()} and {@link #tryEnable()} for details and
	 *             fixes.
	 */
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

	/**
	 * <p>
	 * This method returns the {@link Domain} given the {@link Domain}'s full path.
	 * <p>
	 * This method is {@link CallerSensitive} and will throw a
	 * {@link RuntimeException} if the calling class is not allowed to access the
	 * specified {@link Domain}. (Access can be given to different classes via the
	 * {@link DomainAccess} annotation.)
	 * <p>
	 * If the calling class is an anonymous class, then the class in which it is
	 * defined is used to access the Domain. That means that the enclosing class is
	 * what is checked for accessibility. Inner and nested classes have their own
	 * domains, like normal classes.
	 * <p>
	 * If the calling class is anonymous and its enclosing class is anonymous as
	 * well, (and that class’s enclosing class is anonymous, etc. etc.,) then this
	 * method iterates until it finds an enclosing class that it can use. That class
	 * is then checked to see if it has permission to access the domain.
	 * 
	 * @param fullPath
	 *            The full path of the {@link Domain}. This is basically the same as
	 *            the {@link Domain}'s owning class's full name, but all
	 *            <code>.</code> characters become <code>/</code> characters, and
	 *            all <code>$</code> are <code>#</code>s. See
	 *            {@link Domain#domToPkg(String)} for the exact method that converts
	 *            this.
	 * @return The {@link Domain} represented by the <code>fullPath</code>
	 *         parameter, so long as no exceptions are thrown.
	 * @throws ClassNotFoundException
	 *             The fullPath is converted to the path of a class, (the owner of
	 *             the specified {@link Domain}), in the classpath. If this path
	 *             doesn't lead to an actual class, then a
	 *             {@link ClassNotFoundException} is thrown.<br>
	 *             <br>
	 * @throws DomainInitializeException
	 *             In case there is an exception while trying to create the
	 *             {@link Domain} requested. Do note that once a {@link Domain} is
	 *             created, a reference to it is stored in a {@link WeakHashMap}. If
	 *             the {@link Domain} is requested again, instead of creating a new
	 *             object, the Domain will be returned from the hashmap. If the
	 *             domain is not found in the hashmap, the domain is created again.
	 *             Anyways, so long as the Domain is in use somewhere apart from the
	 *             hashmap, re-requesting it should never throw an initialization
	 *             error.<br>
	 *             <br>
	 * @throws UnavailableException
	 *             This is thrown if the Protection API is unavailable. This can be
	 *             caused by a few different things, like
	 *             {@value #DOMAINS_DIRECTORY} not being made, and such. You can use
	 *             {@link #isAvailable()} to check availability and
	 *             {@link #tryEnable()} to try and enable the API.<br>
	 *             <br>
	 * @throws RuntimeException
	 *             If anything random goes wrong, that shouldn't, OR if the calling
	 *             class does not have access to the requested {@link Domain}, a
	 *             {@link RuntimeException} will be thrown.<br>
	 *             <br>
	 */
	public static @CallerSensitive Domain getDomain(String fullPath)
			throws ClassNotFoundException, DomainInitializeException, UnavailableException, RuntimeException {

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

	/**
	 * @return <code>true</code> if the Protection API is available,
	 *         <code>false</code> otherwise. See {@link #tryEnable()}.
	 */
	public static boolean isAvailable() {
		return DOMAINS_DIRECTORY != null && DOMAINS_DIRECTORY.isDirectory();
	}

	/**
	 * Attempts to enable the Protection API. A {@link RuntimeException} is thrown
	 * if this fails. The protection API would be unavailable if
	 * {@link #DOMAINS_DIRECTORY} is null or it's directory doesn't exist.
	 * 
	 * @return <code>false</code> if enabling the API failed and <code>true</code>
	 *         if it succeeded.
	 * @throws RuntimeException
	 *             If the API is already enabled.
	 */
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
				DOMAINS_DIRECTORY = new File(Kröw.DATA_DIRECTORY, "Protection/domains/default-set");
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return tryEnable();
		}

	}

	/**
	 * Prevent instantiation by other classes.
	 */
	private Protection() {
	}

}
