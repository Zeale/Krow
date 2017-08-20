package kr�w.core.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

import kr�w.core.Kr�w;

public class ProgramSettings implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final long version = 0;

	public static final File DEFAULT_FILE_PATH = new File(Kr�w.MANAGER_DIRECTORY, "ProgramSettings.kmgr");

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

	private boolean launchOnSystemLogIn;

	private boolean launchOnUserLogIn;

	private boolean shapeBackgroundRespondToMouseMovement = false;
	private int currentAnimationMode = 0;

	private boolean useTrayIcon = false;
	private boolean openProgramOnDoubleClickTrayIcon = true;
	
	

	/**
	 * @return the useTrayIcon
	 */
	public final boolean isUseTrayIcon() {
		return useTrayIcon;
	}

	/**
	 * @param useTrayIcon the useTrayIcon to set
	 */
	public final void setUseTrayIcon(boolean useTrayIcon) {
		this.useTrayIcon = useTrayIcon;
	}

	/**
	 * @return the openProgramOnDoubleClickTrayIcon
	 */
	public final boolean isOpenProgramOnDoubleClickTrayIcon() {
		return openProgramOnDoubleClickTrayIcon;
	}

	/**
	 * @param openProgramOnDoubleClickTrayIcon the openProgramOnDoubleClickTrayIcon to set
	 */
	public final void setOpenProgramOnDoubleClickTrayIcon(boolean openProgramOnDoubleClickTrayIcon) {
		this.openProgramOnDoubleClickTrayIcon = openProgramOnDoubleClickTrayIcon;
	}

	private void bootup() {

	}

	/**
	 * @return the currentAnimationMode
	 */
	public final int getCurrentAnimationMode() {
		return currentAnimationMode;
	}

	/**
	 * @return the launchOnSystemLogIn
	 */
	public final boolean isLaunchOnSystemLogIn() {
		return launchOnSystemLogIn;
	}

	/**
	 * @return the launchOnUserLogIn
	 */
	public final boolean isLaunchOnUserLogIn() {
		return launchOnUserLogIn;
	}

	public boolean isShapeBackgroundRespondToMouseMovement() {
		return shapeBackgroundRespondToMouseMovement;
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

	/**
	 * @param currentAnimationMode
	 *            the currentAnimationMode to set
	 */
	public final void setCurrentAnimationMode(final int currentAnimationMode) {
		this.currentAnimationMode = currentAnimationMode;
	}

	/**
	 * @param launchOnSystemLogIn
	 *            the launchOnSystemLogIn to set
	 */
	public final void setLaunchOnSystemLogIn(final boolean launchOnSystemLogIn) {
		this.launchOnSystemLogIn = launchOnSystemLogIn;
	}

	/**
	 * @param launchOnUserLogIn
	 *            the launchOnUserLogIn to set
	 */
	public final void setLaunchOnUserLogIn(final boolean launchOnUserLogIn) {
		this.launchOnUserLogIn = launchOnUserLogIn;
	}

	public void setShapeBackgroundRespondToMouseMovement(final boolean shapeBackgroundRespondToMouseMovement) {
		this.shapeBackgroundRespondToMouseMovement = shapeBackgroundRespondToMouseMovement;
	}

	private void writeObject(final ObjectOutputStream os) throws IOException {
		os.writeLong(version);
		os.defaultWriteObject();

	}
}
