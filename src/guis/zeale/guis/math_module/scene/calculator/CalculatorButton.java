package zeale.guis.math_module.scene.calculator;

import javafx.scene.Node;
import javafx.scene.control.Button;

public class CalculatorButton extends Button {

	private String output;

	public CalculatorButton(String text, Node graphic) {
		super(text, graphic);
	}

	public CalculatorButton(String text) {
		super(text);
	}

	/**
	 * @return the output
	 */
	public final String getOutput() {
		return output;
	}

	/**
	 * @param output
	 *            the output to set
	 */
	public final void setOutput(String output) {
		this.output = output;
	}

	public CalculatorButton(String text, String output) {
		super(text);
		this.output = output;
	}

	public CalculatorButton(String text, Node graphic, String output) {
		super(text, graphic);
		this.output = output;
	}

}
