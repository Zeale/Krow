package kr�w.gui.events;

import kr�w.events.Event;
import kr�w.gui.Application;
import kr�w.gui.WindowManager;
import kr�w.gui.WindowManager.Window;

public class PageChangedEvent extends Event {

	public final Window<? extends Application> oldWindow, newWindow;

	public PageChangedEvent(final Window<? extends Application> currentPage, final Window<? extends Application> window) {
		oldWindow = currentPage;
		newWindow = window;
	}
}