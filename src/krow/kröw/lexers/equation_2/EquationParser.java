package kröw.lexers.equation_2;

import kröw.lexers.equation_2.exceptions.ParserException;

public class EquationParser {
	public EquationParser() {
	}

	public Number parse(String equation) throws ParserException {
		new EquationTokenizer(equation).tokenize();
		return 0;
	}

}
