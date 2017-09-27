package kröw.lexers.equation.exceptions;

public class DuplicateDecimalException extends IrregularCharacterException {
	private static final long serialVersionUID = 1L;

	public DuplicateDecimalException(final String equation, final int position) {
		super('.', position, equation);
	}

	public DuplicateDecimalException(final String message, final String equation, final int position) {
		super(message, '.', position, equation);
	}
}
