package kröw.program.api.apps.gamehub.gameloader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.jar.JarFile;

public class GameLoader extends ClassLoader {
	private JarFile gameJar;
	private Hashtable<String, Class<?>> loadedClasses = new Hashtable<>();

	public GameLoader(JarFile gameJar) {
		super(GameLoader.class.getClassLoader());
		this.gameJar = gameJar;

	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		if (loadedClasses.containsKey(name))
			return loadedClasses.get(name);
		try {
			InputStream is = gameJar.getInputStream(gameJar.getJarEntry(name + ".class"));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int in;
			while ((in = is.read()) != -1)
				baos.write(in);
			byte[] ba = baos.toByteArray();
			Class<?> result = defineClass(name, ba, 0, ba.length);
			loadedClasses.put(name, result);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return super.findClass(name);
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return findClass(name);
	}

}
