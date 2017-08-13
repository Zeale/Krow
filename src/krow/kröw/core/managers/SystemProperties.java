package kröw.core.managers;

import java.awt.Dimension;
import java.awt.Toolkit;

public class SystemProperties {

	private final double screenHeight;
	private final double screenWidth;
	private final double screenDotsPerInch;
	private final boolean isDPIOversized;
	private String osName;

	{
		final Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();

		screenHeight = screenDimensions.getHeight();
		screenWidth = screenDimensions.getWidth();
		screenDotsPerInch = Toolkit.getDefaultToolkit().getScreenResolution();
		isDPIOversized = screenDotsPerInch > 96.1;
		osName = System.getProperty("os.name");
	}

	public boolean isWindows() {
		return osName.toLowerCase().startsWith("Win");
	}

	public boolean isApple() {
		return osName.toLowerCase().startsWith("mac");
	}

	/**
	 * @return the screenHeight
	 */
	public final double getScreenHeight() {
		return screenHeight;
	}

	/**
	 * @return the screenWidth
	 */
	public final double getScreenWidth() {
		return screenWidth;
	}

	/**
	 * @return the screenDotsPerInch
	 */
	public final double getScreenDotsPerInch() {
		return screenDotsPerInch;
	}

	/**
	 * @return the isDPIOversized
	 */
	public final boolean isDPIOversized() {
		return isDPIOversized;
	}

}
