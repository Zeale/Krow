package krow.scene.calculator;

import javafx.scene.Node;
import javafx.scene.control.Button;

public class CalculatorButton extends Button {

	private String output;

	public CalculatorButton(final String text) {
		super(text);
	}

	public CalculatorButton(final String text, final Node graphic) {
		super(text, graphic);
	}

	public CalculatorButton(final String text, final Node graphic, final String output) {
		super(text, graphic);
		this.output = output;
	}

	public CalculatorButton(final String text, final String output) {
		super(text);
		this.output = output;
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
	public final void setOutput(final String output) {
		this.output = output;
	}

}
