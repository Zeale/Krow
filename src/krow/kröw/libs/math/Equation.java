package kröw.libs.math;

import java.util.ArrayList;
import java.util.Collection;

import kröw.libs.math.exceptions.EmptyEquationException;
import kröw.libs.math.exceptions.IrregularCharacterException;
import kröw.libs.math.exceptions.UnmatchedParenthesisException;

class Equation extends ArrayList<Object> {
	/**
	 * SVUID
	 */
	private static final long serialVersionUID = 1L;
	private boolean started;

	@Override
	public void add(final int index, final Object element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean add(final Object e) {
		throw new UnsupportedOperationException();
	}

	public void add(final Operation operation, final Element element) {
		if (!started)
			throw new UnsupportedOperationException();
		super.add(operation);
		super.add(element);
	}

	@Override
	public boolean addAll(final Collection<? extends Object> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(final int index, final Collection<? extends Object> c) {
		throw new UnsupportedOperationException();
	}

	public double evaluate() throws EmptyEquationException, UnmatchedParenthesisException, IrregularCharacterException {

		for (byte precedence = 3; precedence > -1; precedence--)
			for (int i = 2; i < size() && i > 0; i += 2)
				if (((Operation) get(--i)).getPrecedence() == precedence) {
					popin(new Number(((Operation) remove(i)).evaluate(((Element) remove(--i)).evaluate(),
							((Element) remove(i)).evaluate())), i);
					i -= 2;
				} else
					i++;
		return ((Element) get(0)).evaluate();
	}

	private void popin(final Element element, final int location) {
		super.add(location, element);
	}

	private void popin(final Operation operation, final int location) {
		super.add(location, operation);
	}

	public void start(final Element element) {
		if (started)
			throw new UnsupportedOperationException();
		super.add(element);
		started = true;
	}
}