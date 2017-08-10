package kröw.core.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

import kröw.core.Kröw;

public class GlobalSettingsManager implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final long version = 0;

	public static final File DEFAULT_FILE_PATH = new File(Kröw.MANAGER_DIRECTORY, "GlobalSettingsManager.kmgr");

	public static GlobalSettingsManager loadManager(File file) throws IOException {
		GlobalSettingsManager gsm = null;
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
			gsm = (GlobalSettingsManager) ois.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("The file does not point to this class. Refactoring may have ocurred.");
		}
		gsm.bootup();
		return gsm;
	}

	private void bootup() {

	}
	

	public static final GlobalSettingsManager createManager(File systemPath) throws IOException {
		GlobalSettingsManager gsm = new GlobalSettingsManager();
		gsm.save(systemPath);
		return gsm;
	}

	public static GlobalSettingsManager loadManager(Path path) throws IOException {

		return loadManager(path.toFile());
	}

	public static GlobalSettingsManager loadManager(String path) throws IOException {
		return loadManager(Paths.get(path));
	}

	private boolean launchOnSystemLogIn;
	private boolean launchOnUserLogIn;

	/**
	 * @return the launchOnUserLogIn
	 */
	public final boolean isLaunchOnUserLogIn() {
		return launchOnUserLogIn;
	}

	/**
	 * @param launchOnUserLogIn
	 *            the launchOnUserLogIn to set
	 */
	public final void setLaunchOnUserLogIn(boolean launchOnUserLogIn) {
		this.launchOnUserLogIn = launchOnUserLogIn;
	}

	/**
	 * @return the launchOnSystemLogIn
	 */
	public final boolean isLaunchOnSystemLogIn() {
		return launchOnSystemLogIn;
	}

	/**
	 * @param launchOnSystemLogIn
	 *            the launchOnSystemLogIn to set
	 */
	public final void setLaunchOnSystemLogIn(boolean launchOnSystemLogIn) {
		this.launchOnSystemLogIn = launchOnSystemLogIn;
	}

	private void readObject(ObjectInputStream is) throws IOException {
		if (!(is.readLong() == version))
			throw new RuntimeException("Outdated file.");
		try {
			is.defaultReadObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void save(File savePath) throws IOException {
		if (!savePath.exists()) {
			if (!savePath.getParentFile().isDirectory()) {
				if (savePath.getParentFile().exists())
					savePath.getParentFile().delete();
				savePath.getParentFile().mkdirs();
			}
		} else
			savePath.delete();
		savePath.createNewFile();

		ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(savePath));
		os.writeObject(this);
		os.close();

	}

	private void writeObject(ObjectOutputStream os) throws IOException {
		os.writeLong(version);
		os.defaultWriteObject();

	}
}
