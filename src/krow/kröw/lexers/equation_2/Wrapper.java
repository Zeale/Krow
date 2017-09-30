package kröw.lexers.equation_2;

public enum Wrapper {
	PARENTHESIS('(', ')'), BRACKET('[', ']'), BRACE('{', '}'), ANGLE_BRACKET('<',
			'>'), PIPELINE('|'), FLOOR_WRAPPER((char) 8970, (char) 8971), CIELING_WRAPPER((char) 8968, (char) 8969);
	public final char open, close;

	private Wrapper(char open, char close) {
		this.open = open;
		this.close = close;
	}

	private Wrapper(char both) {
		open = close = both;
	}

}
