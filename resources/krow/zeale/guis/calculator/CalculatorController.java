package krow.zeale.guis.calculator;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * The controller for the calculator window.
 * 
 * @author Zeale
 *
 */
public class CalculatorController {
	@FXML
	private TextField outputField;

	String getEquation() {
		return outputField.getText();
	}

	void setEquation(String equation) {
		outputField.setText(equation);
	}
}
