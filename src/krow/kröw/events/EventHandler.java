package kr�w.events;

public interface EventHandler<ET extends Event> {
	void handle(ET event);
}
