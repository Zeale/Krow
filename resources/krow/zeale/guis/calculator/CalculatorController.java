package krow.zeale.guis.calculator;

import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;

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

	void setEquation(String equation) {
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
		setEquation(String.valueOf(parser.evaluate(getEquation())));
	}

	void setParser(Calculator.Parser parser) {
		this.parser = parser;
	}

	private Calculator.Parser parser;

}
