package wolf.mindset.events;

import wolf.mindset.MindsetObject;

/**
 * This event occurrs when a mindset is altered in some way. It stores the data
 * of the change, which is accessible through its |TODO|
 * 
 * @author Zeale
 *
 */
public class ChangeEvent extends Event {
	public final boolean isAddEvent;
	public final MindsetObject effectedObject;

	public ChangeEvent(boolean isAddEvent, MindsetObject effectedObject) {
		this.isAddEvent = isAddEvent;
		this.effectedObject = effectedObject;
	}

}
