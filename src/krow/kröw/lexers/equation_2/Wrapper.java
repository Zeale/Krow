package kröw.lexers.equation_2;

public enum Wrapper {
	PARENTHESIS('(', ')'), BRACKET('[', ']'), BRACE('{', '}'), ANGLE_BRACKET('<',
			'>'), PIPELINE('|'), FLOOR_WRAPPER((char) 8970, (char) 8971), CIELING_WRAPPER((char) 8968, (char) 8969);
	public static Wrapper getWrapper(final char c) {
		for (final Wrapper w : Wrapper.values())
			if (w.close == c || w.open == c)
				return w;
		return null;
	}

	public static Wrapper getWrapper(final char c, final boolean open) {
		for (final Wrapper w : Wrapper.values())
			if ((open ? w.open : w.close) == c)
				return w;
		return null;
	}

	public final char open, close;

	private Wrapper(final char both) {
		open = close = both;
	}

	private Wrapper(final char open, final char close) {
		this.open = open;
		this.close = close;
	}

}
