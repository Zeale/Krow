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
				double numb = Double.parseDouble(field.getText());
				numb += event.getDeltaY();
				field.setText("" + numb);
			});

			addContent(field);
		}

		private NumberPrompt(PromptDialogue<K, ? super Number> owner, K key, String description) {
			owner.super(key, description);
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
