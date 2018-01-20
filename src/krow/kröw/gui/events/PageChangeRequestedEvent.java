package kr�w.gui.events;

import kr�w.events.Event;
import kr�w.gui.Application;
import kr�w.gui.WindowManager;
import kr�w.gui.WindowManager.Window;

public class PageChangeRequestedEvent extends Event {
	public final Window<? extends Application> oldWindow;
	public final Class<? extends Application> newPageClass;

	public PageChangeRequestedEvent(final Window<? extends Application> oldWindow,
			final Class<? extends Application> newPageClass) {
		this.oldWindow = oldWindow;
		this.newPageClass = newPageClass;
	}
}