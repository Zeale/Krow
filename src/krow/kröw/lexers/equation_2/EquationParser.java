package kröw.lexers.equation_2;

import java.util.ArrayList;

import kröw.lexers.equation_2.exceptions.ParserException;
import kröw.lexers.equation_2.tokens.Token;

public class EquationParser {
	public EquationParser() {
	}

	public Number parse(String equation) throws ParserException {
		ArrayList<Token> tokens = new EquationTokenizer(equation).tokenize();
		int i = -1;
		while (i < tokens.size()) {
			i++;
		}
		return 0;
	}

}
