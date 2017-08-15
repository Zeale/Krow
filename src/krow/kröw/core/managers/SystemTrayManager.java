package kröw.core.managers;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;

import kröw.annotations.AutoLoad;
import kröw.annotations.LoadTime;

public final class SystemTrayManager {

	private static TrayIcon icon;
	private static PopupMenu popup;

	public boolean isSystemTrayAvailable() {
		return SystemTray.isSupported();
	}

	@AutoLoad(LoadTime.PROGRAM_ENTER)
	private void loadSystemTrayIcon() {
		if (isSystemTrayAvailable()) {
			popup = new PopupMenu("Kröw");
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
			popup.add(open);
			icon = new TrayIcon(new ImageIcon(SystemTray.class.getResource("/krow/resources/Kröw_hd.png")).getImage(),
					"Kröw", popup);
			icon.setImageAutoSize(true);

			try {
				SystemTray.getSystemTray().add(icon);
			} catch (AWTException e1) {
				e1.printStackTrace();
			}
		}
	}

	@AutoLoad(LoadTime.PROGRAM_EXIT)
	private void closeSystemTrayIcon() {
		SystemTray.getSystemTray().remove(icon);
	}

}
