package kr�w.mindset.events;

import kr�w.mindset.MindsetObject;

/**
 * This event occurs when an object is added to a mindset.
 *
 * @author Zeale
 *
 */
@Deprecated
public class AddEvent extends Event {
	public final MindsetObject objectAdded;

	public AddEvent(final MindsetObject objectAdded) {
		this.objectAdded = objectAdded;
	}

}
