package kröw.libs.collections;

import java.util.ArrayList;
import java.util.Collection;

public class UniqueArrayList<E> extends ArrayList<E> {

	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public UniqueArrayList() {
	}

	public UniqueArrayList(final Collection<? extends E> c) {
		super(c);
	}

	@SafeVarargs
	public UniqueArrayList(final E... elements) {
		for (final E e : elements)
			add(e);
	}

	public UniqueArrayList(final int initialCapacity) {
		super(initialCapacity);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.ArrayList#add(java.lang.Object)
	 */
	@Override
	public boolean add(final E e) {
		if (!contains(e))
			return super.add(e);
		else
			return false;
	}

	@Override
	public void add(final int index, final E element) {
		if (!contains(element))
			super.add(index, element);
	}

	@Override
	public boolean addAll(final Collection<? extends E> c) {
		return addAll(size() - 1, c);
	}

	@Override
	public boolean addAll(final int index, final Collection<? extends E> c) {
		final ArrayList<E> list = new ArrayList<>(c);
		list.removeAll(this);
		return super.addAll(index, list);
	}

	@Override
	public E set(final int index, final E element) {
		if (get(index) == element)
			return element;
		else if (contains(element))
			return null;
		return super.set(index, element);
	}

}
