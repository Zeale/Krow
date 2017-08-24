package kröw.libs.mindset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kröw.app.api.collections.UniqueArrayList;
import kröw.libs.mindset.events.AddEvent;
import kröw.libs.mindset.events.ChangeEvent;
import kröw.libs.mindset.events.EventListener;
import kröw.libs.mindset.events.RemoveEvent;

public class ConstructMindset {

	private final UniqueArrayList<MindsetObject> saveQueue = new UniqueArrayList<>();

	private final UniqueArrayList<Law> laws = new UniqueArrayList<>();
	private final UniqueArrayList<Construct> constructs = new UniqueArrayList<>();
	private final UniqueArrayList<System> systems = new UniqueArrayList<>();

	private final List<EventListener<ChangeEvent>> changeEventListeners = new ArrayList<>();

	private final List<EventListener<AddEvent>> addEventListeners = new ArrayList<>();

	private final List<EventListener<RemoveEvent>> removeEventListeners = new ArrayList<>();

	public ConstructMindset() {

	}

	public void addAddEventListener(final EventListener<AddEvent> listener) {
		addEventListeners.add(listener);
	}

	public void addChangeEventListener(final EventListener<ChangeEvent> listener) {
		changeEventListeners.add(listener);
	}

	public void addRemoveEventListener(final EventListener<RemoveEvent> listener) {
		removeEventListeners.add(listener);
	}

	void attatch(final Construct object) throws ObjectAlreadyExistsException {

		if (!constructs.add(object)) {
			final List<Construct> list = new ArrayList<>(constructs);
			throw new ObjectAlreadyExistsException(object, list.get(list.indexOf(object)));
		}
		for (final EventListener<ChangeEvent> l : changeEventListeners)
			l.eventOccurred(new ChangeEvent(true, object));
		for (final EventListener<AddEvent> l : addEventListeners)
			l.eventOccurred(new AddEvent(object));
	}

	void attatch(final Law object) throws ObjectAlreadyExistsException {
		if (!laws.add(object)) {
			final List<Law> list = new ArrayList<>(laws);
			throw new ObjectAlreadyExistsException(object, list.get(list.indexOf(object)));
		}
		for (final EventListener<ChangeEvent> l : changeEventListeners)
			l.eventOccurred(new ChangeEvent(true, object));
		for (final EventListener<AddEvent> l : addEventListeners)
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
		for (final EventListener<ChangeEvent> l : changeEventListeners)
			l.eventOccurred(new ChangeEvent(true, object));
		for (final EventListener<AddEvent> l : addEventListeners)
			l.eventOccurred(new AddEvent(object));
	}

	void detatch(final MindsetObject object) {
		if (saveQueue.remove(object) | (laws.remove(object) || systems.remove(object) || constructs.remove(object))) {
			for (final EventListener<ChangeEvent> l : changeEventListeners)
				l.eventOccurred(new ChangeEvent(false, object));
			for (final EventListener<RemoveEvent> l : removeEventListeners)
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

}
