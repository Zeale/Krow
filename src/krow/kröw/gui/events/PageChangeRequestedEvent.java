package kröw.gui.events;

import kröw.events.Event;
import kröw.gui.Application;
import kröw.gui.ApplicationManager;
import kröw.gui.ApplicationManager.Frame;

public class PageChangeRequestedEvent extends Event {
	public final Frame<? extends Application> oldWindow;
	public final Class<? extends Application> newPageClass;

	public PageChangeRequestedEvent(final Frame<? extends Application> oldWindow,
			final Class<? extends Application> newPageClass) {
		this.oldWindow = oldWindow;
		this.newPageClass = newPageClass;
	}
}