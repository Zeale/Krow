package kröw.zeale.math.exceptions;

public class DuplicateDecimalException extends IrregularCharacterException {
	private static final long serialVersionUID = 1L;

	public DuplicateDecimalException(String equation, int position) {
		super('.', position, equation);
	}

	public DuplicateDecimalException(String message, String equation, int position) {
		super(message, '.', position, equation);
	}
}
