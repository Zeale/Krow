package kröw.zeale.math.exceptions;

public class IrregularCharacterException extends Exception {

	private static final long serialVersionUID = 1L;
	private final char character;
	private final int position;
	private final String equation;

	public IrregularCharacterException(char character, int position, String equation) {
		this.character = character;
		this.position = position;
		this.equation = equation;
	}

	public IrregularCharacterException(String message, char character, int position, String equation) {
		super(message);
		this.character = character;
		this.position = position;
		this.equation = equation;
	}

	/**
	 * @return the character
	 */
	public final char getCharacter() {
		return character;
	}

	/**
	 * @return the position
	 */
	public final int getPosition() {
		return position;
	}

	/**
	 * @return the equation
	 */
	public final String getEquation() {
		return equation;
	}

}
