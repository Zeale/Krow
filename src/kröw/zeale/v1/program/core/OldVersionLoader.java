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

final class OldVersionLoader {

	private OldVersionLoader() {

	}

	public static ObjectInputStream getInputStream(final File file) throws FileNotFoundException, IOException {
		return new ObjectInputStream(new FileInputStream(file));
	}

	public static ObjectOutputStream getOutputStream(final File file) throws FileNotFoundException, IOException {
		return new ObjectOutputStream(new FileOutputStream(file));
	}

	private static class ObjectInputStream extends java.io.ObjectInputStream {

		public ObjectInputStream(final InputStream in) throws IOException {
			super(in);
		}

		@Override
		protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
			ObjectStreamClass resultClassDescriptor = super.readClassDescriptor();

			if (resultClassDescriptor.getName().equals("kröw.libs.Construct"))
				resultClassDescriptor = ObjectStreamClass.lookup(Construct.class);
			else if (resultClassDescriptor.getName().equals("kröw.libs.System"))
				resultClassDescriptor = ObjectStreamClass.lookup(wolf.mindset.System.class);
			else if (resultClassDescriptor.getName().equals("kröw.libs.Law"))
				resultClassDescriptor = ObjectStreamClass.lookup(Law.class);
			return resultClassDescriptor;
		}

	}

	private static class ObjectOutputStream extends java.io.ObjectOutputStream {

		public ObjectOutputStream(final OutputStream arg0) throws IOException {
			super(arg0);
		}

		@Override
		protected void writeClassDescriptor(ObjectStreamClass desc) throws IOException {
			if (desc.getName().equals("kröw.libs.Construct"))
				desc = ObjectStreamClass.lookup(Construct.class);
			else if (desc.getName().equals("kröw.libs.System"))
				desc = ObjectStreamClass.lookup(wolf.mindset.System.class);
			else if (desc.getName().equals("kröw.libs.Law"))
				desc = ObjectStreamClass.lookup(Law.class);
			super.writeClassDescriptor(desc);
		}

	}

}
