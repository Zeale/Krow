package krow.guis.calculator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import kröw.math.EquationParser;
import kröw.math.exceptions.DuplicateDecimalException;
import kröw.math.exceptions.EmptyEquationException;
import kröw.math.exceptions.IrregularCharacterException;
import kröw.math.exceptions.UnmatchedParenthesisException;
import wolf.zeale.Wolf;

/**
 * The controller for the calculator window.
 *
 * @author Zeale
 *
 */
public class CalculatorController {

	@FXML
	MenuBar menuBar;

	@FXML
	private TextField outputField;

	private Runnable onClose;

	private EquationParser parser;

	@FXML
	private void _event_backspace() {
		final String equation = getEquation();
		final int length = equation.length();
		if (length <= 0)
			return;
		setEquation(equation.substring(0, length - 1));
	}

	@FXML
	private void _event_clear() {
		outputField.clear();
	}

	@FXML
	private void _event_close() {
		onClose.run();
	}

	@FXML
	private void _event_cutDecimal() {
		try {
			setEquation("" + (int) Double.parseDouble(getEquation()));
		} catch (final NumberFormatException e) {
			setError("The text couldn't be parsed as a number...");
		}
	}

	@FXML
	private void _event_evaluate() {
		try {
			setEquation(String.valueOf(parser.evaluate(getEquation())));
		} catch (final EmptyEquationException e) {
			setError("What do you want me to evaluate???...");
			if (Wolf.DEBUG_MODE)
				e.printStackTrace();
		} catch (final UnmatchedParenthesisException e) {
			setError("There are an unequal amount of '(' and ')' characters...");
			if (Wolf.DEBUG_MODE)
				e.printStackTrace();
		} catch (final DuplicateDecimalException e) {
			setError("There is a duplicate decimal in a number at position " + (e.getPosition() + 1)
					+ " of the equation.");
			if (Wolf.DEBUG_MODE)
				e.printStackTrace();
		} catch (final IrregularCharacterException e) {
			setError("The character at position " + e.getPosition() + " was unexpected.");
			if (Wolf.DEBUG_MODE)
				e.printStackTrace();
		} catch (final Exception e) {
			setError("Something went wrong...");
			if (Wolf.DEBUG_MODE)
				e.printStackTrace();
		}
	}

	@FXML
	private void _event_functionClicked(final ActionEvent e) {
		concatEquation(((Button) e.getSource()).getText() + "(");
	}

	/**
	 * <p>
	 * Called when a number {@link Button} on the GUI is clicked.
	 * <p>
	 * This method concats the text of the {@link Button} to the
	 * {@link #outputField}
	 * <p>
	 * I know that this method is looked down upon, (i.e., having all 10 buttons
	 * use the same method), but I'm <i>not</i> adding ten different methods for
	 * the numerical buttons alone. That will cause <b><i>mayhem</i></b>, as
	 * I've seen from experience. Internationalization and other stuff can be
	 * dealt with later. Besides, I'd much rather have the parser support
	 * different characters than have the GUI convert all characters to whatever
	 * necessary, (assuming a conversion must be implemented for numbers...).
	 *
	 * @param e
	 *            The {@link ActionEvent} used to get the source {@link Button}
	 *            and its text.
	 */
	@FXML
	private void _event_numberClicked(final ActionEvent e) {
		concatEquation(((Button) e.getSource()).getText());
	}

	@FXML
	private void _event_operatorClicked(final ActionEvent e) {
		concatEquation(((Button) e.getSource()).getText());
	}

	void concatEquation(final String text) {
		setEquation(getEquation() + text);
	}

	String getEquation() {
		return outputField.getText();
	}

	void setEquation(final String equation) {
		outputField.setStyle("-fx-text-fill: white;");
		outputField.setText(equation);
	}

	private void setError(final String text) {
		outputField.setStyle("-fx-text-fill: #C00;");
		outputField.setText(text);
	}

	public void setOnClose(final Runnable onClose) {
		this.onClose = onClose;
	}

	void setParser(final EquationParser parser) {
		this.parser = parser;
	}

}
