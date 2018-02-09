package krow.fx.dialogues.promptdialogues;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public final class PromptFactory {
	public static String promptString(String prompt, String hint) {
		Dialog<String> dialog = new Dialog<>();

		Text promptText = new Text(prompt);
		TextField field = new TextField();
		field.setPromptText(hint);
		Button doneButton = new Button("Continue");

		doneButton.setOnAction(event -> {
			String result = field.getText();
			dialog.setResult(result);
			dialog.close();
		});

		VBox wrapper = new VBox(5);
		wrapper.getChildren().addAll(promptText, field, doneButton);

		dialog.getDialogPane().setContent(wrapper);

		dialog.setResultConverter(param -> {
			if (!param.getButtonData().isDefaultButton())
				return null;
			return field.getText();
		});

		return dialog.showAndWait().orElse(null);

	}

	public static String promptString(String string) {
		return promptString(string, null);
	}

	public static class NumberPrompt<K> extends PromptDialogue<K, ? super Number>.Prompt<Number> {

		public void setValue(String value) {
			setValue(value);
		}

		@Override
		public void setValue(Number number) {
			field.setText("" + number);
		}

		private TextField field = new TextField();
		{
			field.setOnKeyTyped(event -> {
				for (char c : event.getCharacter().toCharArray()) {
					if (c == '.')
						if (field.getText().contains("."))
							event.consume();
						else
							;
					else if (c == '-') {
						// Cache the caret position.
						int carPos = field.getCaretPosition();

						if (field.getText().contains("-")) {
							// Remove the '-' in front.
							field.setText(field.getText().substring(1));
							field.positionCaret(carPos - 1);// A char was removed.
						} else {
							field.setText('-' + field.getText());
							field.positionCaret(carPos + 1);// A char was added.
						}

						event.consume();
					} else if (!Character.isDigit(c))
						event.consume();

				}
			});

			field.caretPositionProperty().addListener((ChangeListener<Number>) (observable, oldValue, newValue) -> {
				if (field.getText().contains("-") && newValue.intValue() < 1)
					field.positionCaret(1);
			});

			field.setOnScroll(event -> {
				double numb = 0;
				try {
					CALC_BLOCK: {
						numb = Double.parseDouble(field.getText());
						boolean increase = event.getDeltaY() > 0;

						// Increment or decrement from 0.
						if (numb >= -0.1 && numb <= 0.1) {
							numb = (int) numb + (increase ? 1 : -1);
							break CALC_BLOCK;
						}

						// If we're incrementing.
						if (increase) {
							// Handle -1 so we don't go into decimals. (e.g. -0.9, -0.8, -0.7, etc.)
							if (numb >= -1 && numb <= -0.1) {
								numb = (int) numb + 1;
								break CALC_BLOCK;
							}
							// Handle positive numbs.
							if (numb > 0)
								numb += Math.pow(10, Math.floor(Math.log10(Math.abs(numb))));
							// Handle negative numbs.
							else
								numb += Math.pow(10, Math.ceil(Math.log10(Math.abs(numb))) - 1);
						}
						// If we're decrementing...
						else {
							// Handle 1 so we don't go into decimals.
							if (numb <= 1 && numb >= 0.1) {
								numb = (int) numb - 1;
								break CALC_BLOCK;
							}
							if (numb < 0)
								numb -= Math.pow(10, Math.floor(Math.log10(Math.abs(numb))));
							else
								numb -= Math.pow(10, Math.ceil(Math.log10(Math.abs(numb))) - 1);
						}
					}

				} catch (Exception e) {
				} finally {
					field.setText("" + numb);
				}
			});

			addContent(field);
		}

		public NumberPrompt(PromptDialogue<K, ? super Number> owner, K key, String description) {
			owner.super(key, description);
		}

		public NumberPrompt(PromptDialogue<K, ? super Number> owner, K key, String description, Number defaultValue) {
			owner.super(key, description);
			setValue(defaultValue);
		}

		public NumberPrompt(PromptDialogue<K, ? super Number> owner, K key, String description, String defaultValue) {
			owner.super(key, description);
			setValue(defaultValue);
		}

		@Override
		public Number getValue() {
			return Double.parseDouble(field.getText());
		}

		@Override
		protected boolean verifyValue() {
			try {
				Double.parseDouble(field.getText());
			} catch (Exception e) {
				return false;
			}

			return true;
		}

	}

}
