package wolf.mindset.events;

import wolf.mindset.MindsetObject;

/**
 * This event occurs when an object is added to a mindset.
 * 
 * @author Zeale
 *
 */
public class AddEvent extends Event {
	public final MindsetObject objectAdded;

	public AddEvent(MindsetObject objectAdded) {
		this.objectAdded = objectAdded;
	}

}
