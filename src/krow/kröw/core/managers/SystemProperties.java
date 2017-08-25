package kröw.core.managers;

import java.awt.Toolkit;
import java.util.List;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public final class SystemProperties {

	private String osName;

	public int getScreenCount() {
		return Screen.getScreens().size();
	}

	/**
	 * If the current screen is not the primary screen, this will return -2.
	 * 
	 * @return The dots-per-inch of the current screen (or -2 if unknown).
	 */
	public final double getScreenDotsPerInch() {
		// System.out.println(getCurrentScreen().getDpi());
		// ??? 0.0 ???
		if (!Screen.getPrimary().equals(getCurrentScreen()))
			return -2;
		return Toolkit.getDefaultToolkit().getScreenResolution();
	}

	public Screen getCurrentScreen() {

		List<Screen> screens = Screen.getScreensForRectangle(WindowManager.getStage().getX(),
				WindowManager.getStage().getY(), 1, 1);
		if (Double.isNaN(WindowManager.getStage().getX()) || Double.isNaN(WindowManager.getStage().getY())
				|| screens.isEmpty())
			return Screen.getScreensForRectangle(1, 1, 1, 1).get(0);
		return screens.get(0);
	}

	public Rectangle2D getCurrentScreenBounds() {
		return getCurrentScreen().getBounds();
	}

	/**
	 * @return the screenHeight
	 */
	public final double getScreenHeight() {
		return getCurrentScreenBounds().getHeight();
	}

	/**
	 * @return the screenWidth
	 */
	public final double getScreenWidth() {
		return getCurrentScreenBounds().getWidth();
	}

	public boolean isApple() {
		return osName.toLowerCase().startsWith("mac");
	}

	/**
	 * @return the isDPIOversized
	 */
	public final boolean isDPIOversized() {
		return getScreenDotsPerInch() > 96.1;
	}

	public boolean isWindows() {
		return osName.toLowerCase().startsWith("win");
	}

}
