package kröw.math.lexer;

/**
 * Temporarily empty class for development purposes.
 * 
 * @author Zeale
 *
 */
public class Term {

	public final double value;
	private final Operator operator;

	public Term(double value, Operator operator) {
		this.value = value;
		this.operator = operator;
	}

	public Operator getOperator() {
		return operator;
	}

}
