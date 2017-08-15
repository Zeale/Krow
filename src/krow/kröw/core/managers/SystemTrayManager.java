package kröw.core.managers;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.paint.Color;
import javafx.stage.WindowEvent;
import kröw.annotations.AutoLoad;
import kröw.annotations.LoadTime;
import kröw.core.Kröw;

public final class SystemTrayManager {

	private static TrayIcon icon;
	private static PopupMenu popup;

	public boolean isSystemTrayAvailable() {
		return SystemTray.isSupported();
	}

	@AutoLoad(LoadTime.PROGRAM_ENTER)
	private void loadSystemTrayIcon() {
		if (isSystemTrayAvailable()) {

			final Runnable show = new Runnable() {

				@Override
				public void run() {
					WindowManager.getStage().show();
					WindowManager.getStage().setIconified(false);
					WindowManager.getStage().toFront();
				}
			}, hideProgram = new Runnable() {

				@Override
				public void run() {
					WindowManager.getStage().hide();
					if (!(isIconShowing() || showIcon())) {
						WindowManager.getStage().show();
						WindowManager.spawnLabelAtMousePos("Failed to show icon...", Color.FIREBRICK);
					}
				}
			};

			popup = new PopupMenu("Kröw");
			MenuItem open = new MenuItem("Open"), exit = new MenuItem("Exit"), hide = new MenuItem("Hide");
			open.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Platform.runLater(show);
				}
			});

			exit.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Kröw.exit();
				}
			});

			hide.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Platform.runLater(hideProgram);
				}
			});
			popup.add(open);
			popup.add(exit);
			popup.add(hide);
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					final EventHandler<WindowEvent> previousHidingHandler = WindowManager.getStage().getOnHiding();
					WindowManager.getStage().setOnHiding(new EventHandler<WindowEvent>() {

						@Override
						public void handle(WindowEvent event) {
							if (previousHidingHandler != null)
								previousHidingHandler.handle(event);
							open.setLabel("Show");
						}
					});

					EventHandler<WindowEvent> previousShowingHandler = WindowManager.getStage().getOnShowing();
					WindowManager.getStage().setOnShowing(new EventHandler<WindowEvent>() {

						@Override
						public void handle(WindowEvent event) {
							if (previousShowingHandler != null)
								previousShowingHandler.handle(event);
							open.setLabel("Open");
						}
					});
				}
			});

			icon = new TrayIcon(new ImageIcon(SystemTray.class.getResource("/krow/resources/Kröw_hd.png")).getImage(),
					"Kröw", popup);
			icon.setImageAutoSize(true);

			showIcon();
		}
	}

	@AutoLoad(LoadTime.PROGRAM_EXIT)
	private void closeSystemTrayIcon() {
		SystemTray.getSystemTray().remove(icon);
	}

	public void displayMessage(String caption, String message, MessageType messageType) {
		icon.displayMessage(caption, message, messageType);
	}

	public boolean hideIcon() {
		if (isIconShowing()) {
			SystemTray.getSystemTray().remove(icon);
			return true;
		}
		return false;
	}

	public boolean showIcon() {
		assert !isIconShowing();
		if (isIconShowing())
			return false;
		try {
			SystemTray.getSystemTray().add(icon);
		} catch (AWTException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean isIconShowing() {
		for (TrayIcon ti : SystemTray.getSystemTray().getTrayIcons())
			if (ti == icon)
				return true;
		return false;
	}
}
