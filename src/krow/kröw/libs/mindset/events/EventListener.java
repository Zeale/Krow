package kröw.libs.mindset.events;

/**
 * @author Zeale
 *
 * @param <E>
 *            The Event type that will be listened to.
 */
public interface EventListener<E extends Event> {

	void eventOccurred(E event);

}
