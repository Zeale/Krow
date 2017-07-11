package wolf.mindset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wolf.mindset.events.AddEvent;
import wolf.mindset.events.ChangeEvent;
import wolf.mindset.events.EventListener;
import wolf.mindset.events.RemoveEvent;
import wolf.zeale.collections.UniqueArrayList;

public class ConstructMindset {

	private final UniqueArrayList<MindsetObject> saveQueue = new UniqueArrayList<>();

	private final UniqueArrayList<Law> laws = new UniqueArrayList<>();
	private final UniqueArrayList<Construct> constructs = new UniqueArrayList<>();
	private final UniqueArrayList<System> systems = new UniqueArrayList<>();

	public ConstructMindset() {

	}

	void attatch(final Construct object) throws ObjectAlreadyExistsException {

		if (!constructs.add(object)) {
			final List<Construct> list = new ArrayList<>(constructs);
			throw new ObjectAlreadyExistsException(object, list.get(list.indexOf(object)));
		}
		for (EventListener<ChangeEvent> l : changeEventListeners)
			l.eventOccurred(new ChangeEvent(true, object));
		for (EventListener<AddEvent> l : addEventListeners)
			l.eventOccurred(new AddEvent(object));
	}

	void attatch(final Law object) throws ObjectAlreadyExistsException {
		if (!laws.add(object)) {
			final List<Law> list = new ArrayList<>(laws);
			throw new ObjectAlreadyExistsException(object, list.get(list.indexOf(object)));
		}
		for (EventListener<ChangeEvent> l : changeEventListeners)
			l.eventOccurred(new ChangeEvent(true, object));
		for (EventListener<AddEvent> l : addEventListeners)
			l.eventOccurred(new AddEvent(object));
	}

	void attatch(final MindsetObject object) throws ObjectAlreadyExistsException {
		if (object instanceof Construct)
			attatch((Construct) object);
		else if (object instanceof Law)
			attatch((Law) object);
		else if (object instanceof System)
			attatch((System) object);
	}

	void attatch(final System object) throws ObjectAlreadyExistsException {
		if (!systems.add(object)) {
			final List<System> list = new ArrayList<>(systems);
			throw new ObjectAlreadyExistsException(object, list.get(list.indexOf(object)));

		}
		for (EventListener<ChangeEvent> l : changeEventListeners)
			l.eventOccurred(new ChangeEvent(true, object));
		for (EventListener<AddEvent> l : addEventListeners)
			l.eventOccurred(new AddEvent(object));
	}

	void detatch(final MindsetObject object) {
		if (saveQueue.remove(object) | (laws.remove(object) || systems.remove(object) || constructs.remove(object))) {
			for (EventListener<ChangeEvent> l : changeEventListeners)
				l.eventOccurred(new ChangeEvent(false, object));
			for (EventListener<RemoveEvent> l : removeEventListeners)
				l.eventOccurred(new RemoveEvent(object));
		}
	}

	public List<MindsetObject> getAllObjects() {
		final List<MindsetObject> list = new ArrayList<>(laws);
		for (final MindsetObject o : constructs)
			list.add(o);
		for (final MindsetObject o : systems)
			list.add(o);
		return list;
	}

	public UniqueArrayList<Construct> getConstructs() {
		return constructs;
	}

	public List<Construct> getConstructsUnmodifiable() {
		return Collections.unmodifiableList(constructs);
	}

	public UniqueArrayList<Law> getLaws() {
		return laws;
	}

	public List<Law> getLawsUnmodifiable() {
		return Collections.unmodifiableList(laws);
	}

	public UniqueArrayList<MindsetObject> getSaveQueue() {
		return saveQueue;
	}

	public List<MindsetObject> getSaveQueueUnmodifiable() {
		return Collections.unmodifiableList(saveQueue);
	}

	public UniqueArrayList<System> getSystems() {
		return systems;
	}

	public List<System> getSystemsUnmodifiable() {
		return Collections.unmodifiableList(systems);
	}

	private final List<EventListener<ChangeEvent>> changeEventListeners = new ArrayList<>();
	private final List<EventListener<AddEvent>> addEventListeners = new ArrayList<>();
	private final List<EventListener<RemoveEvent>> removeEventListeners = new ArrayList<>();

	public void addChangeEventListener(EventListener<ChangeEvent> listener) {
		changeEventListeners.add(listener);
	}

	public void addRemoveEventListener(EventListener<RemoveEvent> listener) {
		removeEventListeners.add(listener);
	}

	public void addAddEventListener(EventListener<AddEvent> listener) {
		addEventListeners.add(listener);
	}

}
