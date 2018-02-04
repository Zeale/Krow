package zeale.windowbuilder.api;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.stage.Stage;

public class TrayIconManager {
	private final TrayIcon icon;

	public boolean isTrayIconAvailable() {
		return trayIconAvailable;
	}

	public void hideIcon() {
		if (isTrayIconAvailable())
			SystemTray.getSystemTray().remove(icon);
	}

	public TrayIconManager(Stage owner) {
		if (isTrayIconAvailable())
			icon.addActionListener(e -> Platform.runLater(() -> owner.show()));
	}

	private boolean trayIconAvailable;

	{
		TrayIcon icon = null;
		try {
			icon = new TrayIcon(ImageIO.read(getClass().getResourceAsStream("/krow/resources/Testing.png")));
			icon.setToolTip("WindowBuilder");
			icon.setImageAutoSize(true);
			try {
				SystemTray.getSystemTray().add(icon);
			} catch (AWTException e1) {
				trayIconAvailable = false;
			}
		} catch (IOException e) {
			trayIconAvailable = false;
		}
		this.icon = icon;

	}
}
