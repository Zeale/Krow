package kröw.gui.events;

import kröw.events.Event;
import kröw.gui.Application;
import kröw.gui.WindowManager;
import kröw.gui.WindowManager.Window;

public class PageChangeRequestedEvent extends Event {
	public final Window<? extends Application> oldWindow;
	public final Class<? extends Application> newPageClass;

	public PageChangeRequestedEvent(final Window<? extends Application> oldWindow,
			final Class<? extends Application> newPageClass) {
		this.oldWindow = oldWindow;
		this.newPageClass = newPageClass;
	}
}