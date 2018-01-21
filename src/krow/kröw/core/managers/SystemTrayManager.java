package kröw.core.managers;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.paint.Color;
import javafx.stage.WindowEvent;
import kröw.annotations.AutoLoad;
import kröw.annotations.LoadTime;
import kröw.core.Kröw;
import kröw.gui.ApplicationManager;

public final class SystemTrayManager {

	private static TrayIcon icon;
	private static PopupMenu popup;

	private static ActionListener openOnActionListener = e -> Platform.runLater(() -> {

		ApplicationManager.getStage().show();
		ApplicationManager.getStage().setIconified(false);
		ApplicationManager.getStage().toFront();

	});

	public void addActionListener() {
		icon.addActionListener(openOnActionListener);
	}

	@AutoLoad(LoadTime.PROGRAM_EXIT)
	private void closeSystemTrayIcon() {
		SystemTray.getSystemTray().remove(icon);
	}

	public void displayMessage(final String caption, final String message, final MessageType messageType) {
		icon.displayMessage(caption, message, messageType);
	}

	public boolean hideIcon() {
		if (isIconShowing()) {
			SystemTray.getSystemTray().remove(icon);
			return true;
		}
		return false;
	}

	public boolean isIconShowing() {
		for (final TrayIcon ti : SystemTray.getSystemTray().getTrayIcons())
			if (ti == icon)
				return true;
		return false;
	}

	public boolean isSystemTrayAvailable() {
		return SystemTray.isSupported();
	}

	@AutoLoad(LoadTime.PROGRAM_ENTER)
	private void loadSystemTrayIcon() {
		if (isSystemTrayAvailable()) {

			final Runnable show = () -> {
				ApplicationManager.getStage().show();
				ApplicationManager.getStage().setIconified(false);
				ApplicationManager.getStage().toFront();
			}, hideProgram = () -> {
				ApplicationManager.getStage().hide();
				if (!(isIconShowing() || showIcon())) {
					ApplicationManager.getStage().show();
					ApplicationManager.spawnLabelAtMousePos("Failed to show icon...", Color.FIREBRICK);
				}
			};

			popup = new PopupMenu("Kröw");
			final MenuItem open = new MenuItem("Open"), exit = new MenuItem("Exit"), hide = new MenuItem("Hide");
			open.addActionListener(e -> Platform.runLater(show));

			exit.addActionListener(e -> Kröw.exit());

			hide.addActionListener(e -> Platform.runLater(hideProgram));
			popup.add(open);
			popup.add(exit);
			popup.add(hide);
			Platform.runLater(() -> {
				final EventHandler<WindowEvent> previousHidingHandler = ApplicationManager.getStage().getOnHiding();
				ApplicationManager.getStage().setOnHiding(event -> {
					if (previousHidingHandler != null)
						previousHidingHandler.handle(event);
					open.setLabel("Show");
				});

				final EventHandler<WindowEvent> previousShowingHandler = ApplicationManager.getStage().getOnShowing();
				ApplicationManager.getStage().setOnShowing(event -> {
					if (previousShowingHandler != null)
						previousShowingHandler.handle(event);
					open.setLabel("Open");
				});
			});

			icon = new TrayIcon(new ImageIcon(SystemTray.class.getResource("/krow/resources/Kröw_hd.png")).getImage(),
					"Kröw", popup);
			icon.setImageAutoSize(true);

			if (Kröw.getProgramSettings().isOpenProgramOnDoubleClickTrayIcon())
				addActionListener();

			if (Kröw.getProgramSettings().isUseTrayIcon())
				showIcon();
		}
	}

	public void removeActionListener() {
		icon.removeActionListener(openOnActionListener);
	}

	public boolean showIcon() {
		assert !isIconShowing();
		if (isIconShowing())
			return false;
		try {
			SystemTray.getSystemTray().add(icon);
		} catch (final AWTException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
