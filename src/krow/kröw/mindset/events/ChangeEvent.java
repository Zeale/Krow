package kröw.mindset.events;

import kröw.mindset.MindsetObject;

/**
 * This event occurrs when a mindset is altered in some way. It stores the data
 * of the change, which is accessible through its |TODO|
 *
 * @author Zeale
 *
 */
@Deprecated
public class ChangeEvent extends Event {
	public final boolean isAddEvent;
	public final MindsetObject effectedObject;

	public ChangeEvent(final boolean isAddEvent, final MindsetObject effectedObject) {
		this.isAddEvent = isAddEvent;
		this.effectedObject = effectedObject;
	}

}
