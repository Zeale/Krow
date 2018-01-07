package kröw.math.lexer.exceptions;

public class InvalidFunctionNameException extends RuntimeException {

	public InvalidFunctionNameException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InvalidFunctionNameException(String term) {
		super("The character after \"" + term + "\" made the function name invalid.");
		// TODO Auto-generated constructor stub
	}

}
