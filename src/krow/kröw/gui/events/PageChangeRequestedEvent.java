package kr�w.gui.events;

import kr�w.events.Event;
import kr�w.gui.Application;
import kr�w.gui.ApplicationManager;
import kr�w.gui.ApplicationManager.Frame;

public class PageChangeRequestedEvent extends Event {
	public final Frame<? extends Application> oldWindow;
	public final Class<? extends Application> newPageClass;

	public PageChangeRequestedEvent(final Frame<? extends Application> oldWindow,
			final Class<? extends Application> newPageClass) {
		this.oldWindow = oldWindow;
		this.newPageClass = newPageClass;
	}
}