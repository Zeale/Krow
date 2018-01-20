package kröw.gui.events;

import kröw.events.Event;
import kröw.gui.Application;
import kröw.gui.WindowManager;
import kröw.gui.WindowManager.Window;

public class PageChangedEvent extends Event {

	public final Window<? extends Application> oldWindow, newWindow;

	public PageChangedEvent(final Window<? extends Application> currentPage, final Window<? extends Application> window) {
		oldWindow = currentPage;
		newWindow = window;
	}
}