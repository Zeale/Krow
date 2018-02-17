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
import kröw.gui.ApplicationManager;
import zeale.guis.Statistics;
import zeale.guis.chatroom.ChatRoom;
import zeale.guis.chatroom.ConsoleWindow;

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

	private boolean chatRoomHostServer;
	private boolean launchOnSystemLogIn, launchOnUserLogIn;
	private boolean shapeBackgroundRespondToMouseMovement = false;

	private int currentAnimationMode = 0;
	private int globalProgramBackground = 0;
	// I'm ignoring the versioning of this file for now. Once the Protection API
	// works, I can discard this class as a whole.
	private int statsAppUpdateSpeed = 1000;

	private boolean useTrayIcon = false;
	private boolean openProgramOnDoubleClickTrayIcon = true;

	public boolean calculatorUseOuterZoomAnimation = false;

	private void bootup() {

	}

	public int getStatsAppUpdateSpeed() {
		return statsAppUpdateSpeed;
	}

	public void setStatsAppUpdateSpeed(int statsAppUpdateSpeed) {
		this.statsAppUpdateSpeed = statsAppUpdateSpeed;
		switch (statsAppUpdateSpeed) {

		case 0:
		default:
			Statistics.AutoUpdatingStatistic.setTimeout(300000);
			break;
		case 1:
			Statistics.AutoUpdatingStatistic.setTimeout(60000);
			break;
		case 2:
			Statistics.AutoUpdatingStatistic.setTimeout(30000);
			break;
		case 3:
			Statistics.AutoUpdatingStatistic.setTimeout(10000);
			break;
		case 4:
			Statistics.AutoUpdatingStatistic.setTimeout(500);
			break;
		case 5:
			Statistics.AutoUpdatingStatistic.setTimeout(1000);
			break;
		case 6:
			// From one millisec to a year or so.
			Statistics.AutoUpdatingStatistic.setTimeout((long) (Math.random() * (1000 * 60 * 6024 * 365 + 1)) + 1);
			break;
		case 7:
			Statistics.AutoUpdatingStatistic.setTimeout(40);
			break;
		case 8:
			Statistics.AutoUpdatingStatistic.setTimeout(10);
			break;
		case 9:
			Statistics.AutoUpdatingStatistic.setTimeout(1);
			break;

		}
	}

	/**
	 * @return the currentAnimationMode
	 */
	public final int getCurrentAnimationMode() {
		return currentAnimationMode;
	}

	/**
	 * @return the chatRoomHostServer
	 */
	public final boolean isChatRoomHostServer() {
		return chatRoomHostServer;
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

	/**
	 * @return the openProgramOnDoubleClickTrayIcon
	 */
	public final boolean isOpenProgramOnDoubleClickTrayIcon() {
		return openProgramOnDoubleClickTrayIcon;
	}

	public boolean isShapeBackgroundRespondToMouseMovement() {
		return shapeBackgroundRespondToMouseMovement;
	}

	/**
	 * @return the useTrayIcon
	 */
	public final boolean isUseTrayIcon() {
		return useTrayIcon;
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
	 * @param chatRoomHostServer
	 *            the chatRoomHostServer to set
	 */
	public final void setChatRoomHostServer(final boolean chatRoomHostServer) {
		final boolean temp = this.chatRoomHostServer;
		this.chatRoomHostServer = chatRoomHostServer;

		if (chatRoomHostServer == temp)
			return;
		// TODO Fix
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

	/**
	 * @return the globalProgramBackground
	 */
	public int getGlobalProgramBackground() {
		return globalProgramBackground;
	}

	/**
	 * @param globalProgramBackground
	 *            An integer from 0 to 2, inclusive, representing the program's
	 *            background. 0 is dark gray (default), 1 is moderately transparent,
	 *            and 2 is wholly transparent. When 2 is enabled, the user can click
	 *            behind the program. Any invalid value for this parameter will be
	 *            set to the closest valid value.
	 */
	public void setGlobalProgramBackground(int globalProgramBackground) {
		if (globalProgramBackground > 2)
			globalProgramBackground = 2;
		if (globalProgramBackground < 0)
			globalProgramBackground = 0;
		this.globalProgramBackground = globalProgramBackground;
		ApplicationManager.getStage().getScene()
				.setFill(globalProgramBackground == 0 ? Kröw.SOLID_BACKGROUND
						: globalProgramBackground == 1 ? Kröw.MODERATELY_TRANSPARENT_BACKGROUND
								: Kröw.COMPLETELY_TRANSPARENT_BACKGROUND);
		if (globalProgramBackground == 2)
			ApplicationManager.getStage().setAlwaysOnTop(true);
		else
			ApplicationManager.getStage().setAlwaysOnTop(false);
	}

	/**
	 * @param openProgramOnDoubleClickTrayIcon
	 *            the openProgramOnDoubleClickTrayIcon to set
	 */
	public final void setOpenProgramOnDoubleClickTrayIcon(final boolean openProgramOnDoubleClickTrayIcon) {
		this.openProgramOnDoubleClickTrayIcon = openProgramOnDoubleClickTrayIcon;
		if (openProgramOnDoubleClickTrayIcon)
			Kröw.getSystemTrayManager().addActionListener();
		else
			Kröw.getSystemTrayManager().removeActionListener();
	}

	public void setShapeBackgroundRespondToMouseMovement(final boolean shapeBackgroundRespondToMouseMovement) {
		this.shapeBackgroundRespondToMouseMovement = shapeBackgroundRespondToMouseMovement;
	}

	/**
	 * @param useTrayIcon
	 *            the useTrayIcon to set
	 */
	public final void setUseTrayIcon(final boolean useTrayIcon) {
		this.useTrayIcon = useTrayIcon;
		if (useTrayIcon)
			Kröw.getSystemTrayManager().showIcon();
		else
			Kröw.getSystemTrayManager().hideIcon();
	}

	private void writeObject(final ObjectOutputStream os) throws IOException {
		os.writeLong(version);
		os.defaultWriteObject();

	}
}
