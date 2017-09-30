package kröw.lexers.equation_2.tokens;

public class NumberToken extends Token {
	private final double value;

	/**
	 * @return the value
	 */
	public final double getValue() {
		return value;
	}

	public NumberToken(double value) {
		this.value = value;
	}

	@Override
	public void setNext(Token next) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T extends Token> void setPrevious(T previous) {
		// TODO Auto-generated method stub
		
	}

}
