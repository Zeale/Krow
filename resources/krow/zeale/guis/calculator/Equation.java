package krow.zeale.guis.calculator;

import java.util.ArrayList;
import java.util.Collection;

import krow.zeale.guis.calculator.Parser.Element;
import krow.zeale.guis.calculator.exceptions.EmptyEquationException;
import krow.zeale.guis.calculator.exceptions.UnmatchedParenthesisException;

class Equation extends ArrayList<Object> {
	/**
	 * SVUID
	 */
	private static final long serialVersionUID = 1L;
	private boolean started;

	@Override
	public boolean add(Object e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(int index, Object element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends Object> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int index, Collection<? extends Object> c) {
		throw new UnsupportedOperationException();
	}

	public void start(Parser.Element element) {
		if (started)
			throw new UnsupportedOperationException();
		super.add(element);
		started = true;
	}

	public void add(Parser.Operation operation, Parser.Element element) {
		if (!started)
			throw new UnsupportedOperationException();
		super.add(operation);
		super.add(element);
	}

	public double evaluate() throws EmptyEquationException, UnmatchedParenthesisException {

		for (byte precedence = 3; precedence > -1; precedence--)
			for (int i = 2; i < size() && i > 0; i += 2)
				if (((Parser.Operation) get(--i)).getPrecedence() == precedence) {
					popin(new Element.Number(((Parser.Operation) remove(i))
							.evaluate(((Parser.Element) remove(--i)).evaluate(), ((Parser.Element) remove(i)).evaluate())),
							i);
					i -= 2;
				} else
					i++;
		return ((Parser.Element) get(0)).evaluate();
	}

	private void popin(Parser.Element element, int location) {
		super.add(location, element);
	}

	private void popin(Parser.Operation operation, int location) {
		super.add(location, operation);
	}
}