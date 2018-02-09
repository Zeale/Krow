package krow.fx.dialogues.promptdialogues;

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
					else if (!Character.isDigit(c))
						event.consume();

				}
			});

			field.setOnScroll(event -> {
				double numb = 0;
				try {

					numb = Double.parseDouble(field.getText());

					if (numb == 0)
						numb = 1;

					// getDeltaY is returned in "Pixels", so "one scroll" of my mousewheel will
					// cause this method to return 40.
					double increaseAmount = event.getDeltaY() / 40;

					// The greater numb is, the greater this should be. If numb is 1, this should be
					// about 0.8. If numb is 500, this should be about 40. In other words, this
					// number is based off of numb, and as numb grows greater, so does this number,
					// but this number grows at a slower rate.
					double increaseMultiplier = 0.8;

					numb += increaseAmount * increaseMultiplier * Math.abs(numb) * (increaseAmount > 0 ? 1 : -1);

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
