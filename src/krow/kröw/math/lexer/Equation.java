package kröw.math.lexer;

import java.util.ArrayList;

public class Equation extends ArrayList<Term> {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	public double evaluate() {
		// TODO if(isEmpty())throw new EmptyEquationException();
		if (size() == 1)
			return get(0).value;

		Term t = get(0);
		double val = t.value;
		int pos = 0;
		while (t.getOperator() != Operator.END_LINE) {
			//
			val = t.getOperator().operate(val, get(++pos).value);
			t = get(pos);
		}

		return val;

	}

}
