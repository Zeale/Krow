package kr�w.gui.events;

import kr�w.events.Event;
import kr�w.gui.Application;
import kr�w.gui.ApplicationManager.Frame;

public class PageChangedEvent extends Event {

	public final Frame<? extends Application> oldWindow, newWindow;

	public PageChangedEvent(final Frame<? extends Application> currentPage, final Frame<? extends Application> window) {
		oldWindow = currentPage;
		newWindow = window;
	}
}