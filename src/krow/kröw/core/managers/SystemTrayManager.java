package kröw.core.managers;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;

import kröw.core.BootMethod;

public final class SystemTrayManager {

	private static SystemTray systemTray;

	public boolean isSystemTrayAvailable() {
		return SystemTray.isSupported();
	}

	public SystemTray getSystemTray() {
		return systemTray;
	}

	@BootMethod
	private static void loadSystemTray() {
		if (SystemTray.isSupported()) {
			systemTray = SystemTray.getSystemTray();
			PopupMenu popupMenu = new PopupMenu("Kröw");
			MenuItem open = new MenuItem("Open"), close = new MenuItem("close");
			open.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					WindowManager.getStage().show();
				}
			});

			close.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					WindowManager.getStage().close();
				}
			});
			popupMenu.add(open);
			TrayIcon icon = new TrayIcon(
					new ImageIcon(SystemTray.class.getResource("/krow/resources/Kröw_hd.png")).getImage(), "Kröw",
					popupMenu);
			icon.setImageAutoSize(true);

			try {
				systemTray.add(icon);
			} catch (AWTException e1) {
				e1.printStackTrace();
			}
		}
	}

}
