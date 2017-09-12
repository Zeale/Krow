package zeale.guis.math_module.scene.calculator;

import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import zeale.guis.math_module.Calculator;

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

	{
		setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				outputProperty.set(outputProperty.get() + output);
			}
		});

		getStyleClass().add("pop-button");
	}

	private StringProperty outputProperty = Calculator.TEXT;

	/**
	 * @return the outputProperty
	 */
	public final StringProperty getOutputProperty() {
		return outputProperty;
	}

	/**
	 * @param outputProperty the outputProperty to set
	 */
	public final void setOutputProperty(StringProperty outputProperty) {
		this.outputProperty = outputProperty;
	}

}
