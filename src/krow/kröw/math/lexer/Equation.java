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
		double val = get(0).getOperator().operate(get(0).value, get(1).value);
		for (int i = 1; i < size(); i++) {
			val = get(i).getOperator().operate(val, get(++i).value);
		}

		return val;

	}

}
