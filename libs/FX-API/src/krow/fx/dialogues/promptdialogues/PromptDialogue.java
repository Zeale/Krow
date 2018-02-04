package krow.fx.dialogues.promptdialogues;

import java.util.HashMap;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * <p>
 * Opens a prompt dialogue with multiple {@link TextField}s for the user to
 * submit values. When building this dialogue, the programmer can add fields for
 * the user to submit data to. Each field will have a Key, and a description.
 * The Key is of the type of the type parameter K, and the description is a
 * description of what the user should type into the text field. The programmer
 * may also supply an <i>optional</i> String called the <code>hint</code>. The
 * hint is the gray PromptText that shows up in the {@link TextField}. See
 * {@link TextField#promptTextProperty()}.
 * <p>
 * The prompt is shown in a <code>showAndWait</code> manner, meaning that the
 * call to show the {@link PromptDialogue} will block until the user exits the
 * dialogue.
 * 
 * @author Zeale
 *
 * @param <K>
 *            The type of the Key that will be used to obtain a text field's
 *            value in the returned map.
 */
public class PromptDialogue<K> extends Dialog<Map<K, String>> {

	// Will contain the scrollPort and the doneButton.
	private VBox fullContent = new VBox(20);
	// Will contain each individual prompt.
	private VBox promptWrapper = new VBox(15);
	// Will contain the promptWrapper, allowing the user to scroll down if there are
	// an exceedingly large amount of prompts. :)
	private ScrollPane scrollPort = new ScrollPane(promptWrapper);

	// The button that the user will push when they are ready to submit all the
	// values.
	private Button doneButton = new Button("Continue");

	private ObservableList<Prompt> prompts = FXCollections.observableArrayList();

	{

		// Add doneButton and scrollPort to the fullContent.
		fullContent.getChildren().addAll(scrollPort, doneButton);

		// Set this Dialog's content as fullContent.
		getDialogPane().setContent(fullContent);

		doneButton.setOnAction(event -> {
			Map<K, String> values = new HashMap<>();
			for (Prompt p : prompts) {
				values.put(p.key, p.getValue());
			}

			setResult(values);

			// This will return the showAndWait() call.
			close();
		});

		// Stylize and space out the dialogue.
		promptWrapper.setFillWidth(true);
		promptWrapper.setPrefWidth(500);
		promptWrapper.setAlignment(Pos.TOP_CENTER);
	}

	private void addPrompt(Prompt p) {
		if (prompts.contains(p))
			return;
		prompts.add(p);
		promptWrapper.getChildren().add(p);
	}

	private void removePrompt(Prompt p) {
		while (prompts.contains(p))
			prompts.remove(p);
		while (promptWrapper.getChildren().contains(p))
			promptWrapper.getChildren().remove(p);
	}

	// This will include its own Type Parameter and probably be a standalone class
	// in the next update. Along with that, the PromptDialogue<K> class will be
	// changed to PromptDialogue<K, V>. This way, users will be able to submit
	// Prompt objects whose returned value (V) will either be, or be a subclass of,
	// the PromptDialogue's returned Map value (V).
	public class Prompt extends VBox {

		private K key;
		private TextField field = new TextField();
		private Text fieldDescription = new Text();

		public void setDescription(String text) {
			fieldDescription.setText(text);
		}

		// This will become abstract, to follow the change described right above the
		// class declaration. Next commit :D
		public String getValue() {
			return field.getText();
		}

		public void setHint(String text) {
			field.setPromptText(text);
		}

		public void setKey(K key) {
			this.key = key;
		}

		public Prompt(K key, String description) {
			setKey(key);
			setDescription(description);
		}

		public Prompt(K key, String description, String hint) {
			this(key, description);
			setHint(hint);
		}

		{
			addPrompt(this);
			getChildren().addAll(fieldDescription, field);
		}

		/**
		 * Cannot be undone.
		 */
		public void remove() {
			removePrompt(this);
		}

	}

	public PromptDialogue() {
	}

	public PromptDialogue(String title) {
		setTitle(title);
	}
}
