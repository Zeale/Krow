package krow.zeale.guis.calculator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import kröw.zeale.math.EquationParser;
import kröw.zeale.math.exceptions.EmptyEquationException;
import kröw.zeale.math.exceptions.UnmatchedParenthesisException;

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
	private void _event_evaluate() {
		try {
			setEquation(String.valueOf(parser.evaluate(getEquation())));
		} catch (EmptyEquationException e) {
			setError("What do you want me to evaluate???...");
		} catch (UnmatchedParenthesisException e) {
			setError("There are an unequal amount of '(' and ')' characters...");
		} catch (Exception e) {
			setError("Something went wrong...");
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
