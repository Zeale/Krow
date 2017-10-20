package kröw.collections;

import java.util.List;

import javafx.collections.ModifiableObservableListBase;

public class ObservableListWrapper<E> extends ModifiableObservableListBase<E> {

	private final List<E> backer;

	public ObservableListWrapper(final List<E> backerList) {
		backer = backerList;
	}

	@Override
	protected void doAdd(final int index, final E element) {
		beginChange();
		try {
			backer.add(index, element);
			nextAdd(index, index + 1);
		} finally {
			endChange();
		}
	}

	@Override
	protected E doRemove(final int index) {
		beginChange();
		try {
			final E t = backer.remove(index);
			nextRemove(index, t);
			return t;
		} finally {
			endChange();
		}
	}

	@Override
	protected E doSet(final int index, final E element) {
		beginChange();
		try {
			final E t = backer.set(index, element);
			nextSet(index, t);
			return t;
		} finally {
			endChange();
		}
	}

	@Override
	public E get(final int index) {
		return backer.get(index);
	}

	@Override
	public int size() {
		return backer.size();
	}

}
