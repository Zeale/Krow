package wolf.mindset.events;

import wolf.mindset.MindsetObject;

/**
 * This event occurs when an object is removed from a mindset.
 *
 * @author Zeale
 *
 */
public class RemoveEvent extends Event {
	public final MindsetObject objectRemoved;

	public RemoveEvent(final MindsetObject objectRemoved) {
		super();
		this.objectRemoved = objectRemoved;
	}

}
