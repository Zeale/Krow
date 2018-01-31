package kröw.gui.events;

import kröw.events.Event;
import kröw.gui.Application;
import kröw.gui.ApplicationManager.Frame;

public class PageChangedEvent extends Event {

	public final Frame<? extends Application> oldWindow, newWindow;

	public PageChangedEvent(final Frame<? extends Application> currentPage, final Frame<? extends Application> window) {
		oldWindow = currentPage;
		newWindow = window;
	}
}