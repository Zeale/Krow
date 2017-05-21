package kröw.zeale.v1.program.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;

import wolf.mindset.Construct;
import wolf.mindset.Law;
import wolf.mindset.MindsetObject;

/**
 * An internal object used by the {@link Kröw} program to backwards-compatibly
 * load objects from previous versions of {@link Kröw}. This may be
 * removed/changed at any time, so it should not be used externally.
 *
 * @author Zeale
 * @internal This class is internal.
 *
 */
final class OldVersionLoader {

	/**
	 * Prevent instantiation of this class.
	 */
	private OldVersionLoader() {

	}

	/**
	 * Returns an {@link ObjectInputStream} which can be used to load previous
	 * versions of {@link MindsetObject}s or current versions of
	 * {@link MindsetObject}s. Loaded objects should be saved using this class's
	 * {@link #getOutputStream(File)} output stream. Doing so will save the
	 * object with the class of the newer version of {@link MindsetObject}, so
	 * the saved files will be updated to the current version of the program.
	 *
	 * @param file
	 *            The {@link File} to build the {@link ObjectInputStream} with.
	 * @return A new {@link ObjectInputStream} which can backwards-compatibly
	 *         load {@link MindsetObject}s. The stream will be built using the
	 *         specified {@link File}.
	 * @throws FileNotFoundException
	 *             As specified by
	 *             {@link FileInputStream#FileInputStream(File)}.
	 * @throws IOException
	 *             As specified by
	 *             {@link java.io.ObjectInputStream#ObjectInputStream(InputStream)}.
	 */
	public static ObjectInputStream getInputStream(final File file) throws FileNotFoundException, IOException {
		return new ObjectInputStream(new FileInputStream(file));
	}

	/**
	 * <p>
	 * Returns an {@link ObjectInputStream} which can safely save old versions
	 * of {@link MindsetObject}s as well as current versions. This
	 * {@link ObjectOutputStream} will save previous versions of objects as
	 * objects of the current version. This means that an object of the version
	 * <code>0.1</code> can be loaded with an {@link #getInputStream(File) Old
	 * Input Stream} and then saved with this method's returned
	 * <code>ObjectOutputStream</code> and the saved object will be an object of
	 * the current version of this program, not version <code>0.1</code>.
	 *
	 * @param file
	 *            The {@link File} to save the object to.
	 * @return A new {@link ObjectOutputStream} which will change the version of
	 *         old objects to that of the current version.
	 * @throws FileNotFoundException
	 *             As specified by
	 *             {@link FileOutputStream#FileOutputStream(File)}.
	 * @throws IOException
	 *             As specified by
	 *             {@link java.io.ObjectOutputStream#ObjectOutputStream(OutputStream)}.
	 */
	public static ObjectOutputStream getOutputStream(final File file) throws FileNotFoundException, IOException {
		return new ObjectOutputStream(new FileOutputStream(file));
	}

	/**
	 * This class is a 'hacked' {@link java.io.ObjectInputStream}. It checks the
	 * given object's class descriptor and modifies it if it equals one of the
	 * following:
	 *
	 * <ol>
	 * <li><code>kröw.libs.Construct</code></li>
	 * <li><code>kröw.libs.System</code></li>
	 * <li><code>kröw.libs.Law</code></li>
	 * </ol>
	 *
	 * This way, Serialization won't throw an error when we try to load an
	 * object and assign it to one of the classes of the current version of this
	 * program. The current classes of Kröw are made capable of loading previous
	 * objects so we use this nested class to bypass serialization's error and
	 * do that.
	 *
	 * @author Zeale
	 *
	 */
	private static class ObjectInputStream extends java.io.ObjectInputStream {

		// Build the input stream just like a normal stream.
		public ObjectInputStream(final InputStream in) throws IOException {
			super(in);
		}

		@Override
		protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
			// Get the loading object's current class
			ObjectStreamClass objectClassDescriptior = super.readClassDescriptor();

			// Check if the object's specified class is the same as one of the
			// old Kröw objects
			if (objectClassDescriptior.getName().equals("kröw.libs.Construct"))
				// If it is, change it
				objectClassDescriptior = ObjectStreamClass.lookup(Construct.class);

			// Repeat
			else if (objectClassDescriptior.getName().equals("kröw.libs.System"))
				objectClassDescriptior = ObjectStreamClass.lookup(wolf.mindset.System.class);

			else if (objectClassDescriptior.getName().equals("kröw.libs.Law"))
				objectClassDescriptior = ObjectStreamClass.lookup(Law.class);

			// Return the object's class whether it was modified by us, or not.
			// (Note that this isn't an actual class object.)
			return objectClassDescriptior;
		}

	}

	private static class ObjectOutputStream extends java.io.ObjectOutputStream {

		public ObjectOutputStream(final OutputStream arg0) throws IOException {
			super(arg0);
		}

		@Override
		protected void writeClassDescriptor(ObjectStreamClass classDescriptor) throws IOException {
			if (classDescriptor.getName().equals("kröw.libs.Construct"))
				classDescriptor = ObjectStreamClass.lookup(Construct.class);
			else if (classDescriptor.getName().equals("kröw.libs.System"))
				classDescriptor = ObjectStreamClass.lookup(wolf.mindset.System.class);
			else if (classDescriptor.getName().equals("kröw.libs.Law"))
				classDescriptor = ObjectStreamClass.lookup(Law.class);
			super.writeClassDescriptor(classDescriptor);
		}

	}

}
