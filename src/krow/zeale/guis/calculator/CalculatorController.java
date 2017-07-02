package krow.zeale.guis.calculator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import kröw.zeale.math.EquationParser;
import kröw.zeale.math.exceptions.DuplicateDecimalException;
import kröw.zeale.math.exceptions.EmptyEquationException;
import kröw.zeale.math.exceptions.IrregularCharacterException;
import kröw.zeale.math.exceptions.UnmatchedParenthesisException;
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

	String getEquation() {
		return outputField.getText();
	}

	void concatEquation(String text) {
		setEquation(getEquation() + text);
	}

	void setEquation(String equation) {
		outputField.setStyle("-fx-text-fill: white;");
		outputField.setText(equation);
	}

	@FXML
	private void _event_close() {
		onClose.run();
	}

	private Runnable onClose;

	public void setOnClose(Runnable onClose) {
		this.onClose = onClose;
	}

	@FXML
	private void _event_clear() {
		outputField.clear();
	}

	@FXML
	private void _event_backspace() {
		String equation = getEquation();
		int length = equation.length();
		if (length <= 0)
			return;
		setEquation(equation.substring(0, length - 1));
	}

	@FXML
	private void _event_evaluate() {
		try {
			setEquation(String.valueOf(parser.evaluate(getEquation())));
		} catch (EmptyEquationException e) {
			setError("What do you want me to evaluate???...");
			if (Wolf.DEBUG_MODE)
				e.printStackTrace();
		} catch (UnmatchedParenthesisException e) {
			setError("There are an unequal amount of '(' and ')' characters...");
			if (Wolf.DEBUG_MODE)
				e.printStackTrace();
		} catch (DuplicateDecimalException e) {
			setError("There is a duplicate decimal in a number at position " + (e.getPosition() + 1)
					+ " of the equation.");
			if (Wolf.DEBUG_MODE)
				e.printStackTrace();
		} catch (IrregularCharacterException e) {
			setError("The character at position " + e.getPosition() + " was unexpected.");
			if (Wolf.DEBUG_MODE)
				e.printStackTrace();
		} catch (Exception e) {
			setError("Something went wrong...");
			if (Wolf.DEBUG_MODE)
				e.printStackTrace();
		}
	}

	private void setError(String text) {
		outputField.setStyle("-fx-text-fill: #C00;");
		outputField.setText(text);
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
	private void _event_numberClicked(ActionEvent e) {
		concatEquation(((Button) e.getSource()).getText());
	}

	@FXML
	private void _event_operatorClicked(ActionEvent e) {
		concatEquation(((Button) e.getSource()).getText());
	}

	@FXML
	private void _event_functionClicked(ActionEvent e) {
		concatEquation((String) ((Button) e.getSource()).getText() + "(");
	}

	void setParser(EquationParser parser) {
		this.parser = parser;
	}

	private EquationParser parser;

}
