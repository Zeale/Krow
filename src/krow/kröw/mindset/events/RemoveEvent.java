package kröw.mindset.events;

import kröw.mindset.MindsetObject;

/**
 * This event occurs when an object is removed from a mindset.
 *
 * @author Zeale
 *
 */
@Deprecated
public class RemoveEvent extends Event {
	public final MindsetObject objectRemoved;

	public RemoveEvent(final MindsetObject objectRemoved) {
		super();
		this.objectRemoved = objectRemoved;
	}

}
