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

public class ProgramSettings implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final long version = 0;

	public static final File DEFAULT_FILE_PATH = new File(Kröw.MANAGER_DIRECTORY, "ProgramSettings.kmgr");

	public static final ProgramSettings createManager(final File systemPath) throws IOException {
		final ProgramSettings gsm = new ProgramSettings();
		gsm.save(systemPath);
		return gsm;
	}

	public static ProgramSettings loadManager(final File file) throws IOException {
		ProgramSettings gsm = null;
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
			gsm = (ProgramSettings) ois.readObject();
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("The file does not point to this class. Refactoring may have ocurred.");
		}
		gsm.bootup();
		return gsm;
	}

	public static ProgramSettings loadManager(final Path path) throws IOException {

		return loadManager(path.toFile());
	}

	public static ProgramSettings loadManager(final String path) throws IOException {
		return loadManager(Paths.get(path));
	}

	public boolean chatRoomHostServer;

	public boolean launchOnSystemLogIn, launchOnUserLogIn;

	public boolean shapeBackgroundRespondToMouseMovement = false;

	public int currentAnimationMode = 0;

	public boolean useTrayIcon = false;
	public boolean openProgramOnDoubleClickTrayIcon = true;

	public boolean calculatorUseOuterZoomAnimation = false;

	private void bootup() {

	}

	private void readObject(final ObjectInputStream is) throws IOException {
		if (!(is.readLong() == version))
			throw new RuntimeException("Outdated file.");
		try {
			is.defaultReadObject();
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void save(final File savePath) throws IOException {
		if (!savePath.exists()) {
			if (!savePath.getParentFile().isDirectory()) {
				if (savePath.getParentFile().exists())
					savePath.getParentFile().delete();
				savePath.getParentFile().mkdirs();
			}
		} else
			savePath.delete();
		savePath.createNewFile();

		final ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(savePath));
		os.writeObject(this);
		os.close();

	}

	private void writeObject(final ObjectOutputStream os) throws IOException {
		os.writeLong(version);
		os.defaultWriteObject();

	}
}
