package kröw.lexers.equation_2.tokens;

import java.util.HashMap;

public class VariableToken extends Token {

	private HashMap<String, Double> variables = new HashMap<>();

	public VariableToken(String value) {
		super(value, Type.VARIABLE);
	}

	@Override
	public Double getValue() {
		return variables.get(super.getValue());
	}

}
