package krow.fx.dialogues.promptdialogues;

import java.util.HashMap;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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
 * @param <V>
 *            The type of Values stored in the returned map. If the returned map
 *            will store Doubles, this should be {@link Double}. If the returned
 *            map will store Integers, this should be {@link Integer}. If the
 *            returned map will store both Doubles and Integers as values,
 *            &lt;V&gt; should be the most immediate superclass of the two, so
 *            {@link Number}. This may often be {@link Object}.
 */
public class PromptDialogue<K, V> extends Dialog<Map<K, V>> {

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

	private ObservableList<Prompt<? extends V>> basicPrompts = FXCollections.observableArrayList();

	{

		// Add doneButton and scrollPort to the fullContent.
		fullContent.getChildren().addAll(scrollPort, doneButton);

		// Set this Dialog's content as fullContent.
		getDialogPane().setContent(fullContent);

		doneButton.setOnAction(event -> {
			Map<K, V> values = new HashMap<>();

			boolean requiredPromptsFilled = true;
			for (Prompt<? extends V> p : basicPrompts) {
				if (p.isRequired() && !p.verifyValue()) {
					p.highlightDescription(Color.FIREBRICK);
					requiredPromptsFilled = false;
				} else
					p.removeDescriptionHighlight();

				// Only continue on filling the map if we know we're gonna return it. If not all
				// the required values were filled, then we're not gonna hide the prompt or
				// return this map.
				if (requiredPromptsFilled)
					values.put(p.key, p.getValue());
			}

			// If they haven't filled out required prompts then don't close the prompt;
			// we're not done here.
			if (!requiredPromptsFilled)
				return;

			setResult(values);

			// This will return the showAndWait() call.
			close();
		});

		// Stylize and space out the dialogue.
		promptWrapper.setFillWidth(true);
		promptWrapper.setPrefWidth(500);
		promptWrapper.setAlignment(Pos.TOP_CENTER);
	}

	private void addPrompt(Prompt<? extends V> prompt) {
		if (basicPrompts.contains(prompt))
			return;
		basicPrompts.add(prompt);
		promptWrapper.getChildren().add(prompt);
	}

	private void removePrompt(Prompt<? extends V> p) {
		while (basicPrompts.contains(p))
			basicPrompts.remove(p);
		while (promptWrapper.getChildren().contains(p))
			promptWrapper.getChildren().remove(p);
	}

	/**
	 * A superclass for Prompts used by this {@link PromptDialogue}. When a
	 * {@link Prompt} is created, it automatically adds itself to its parent
	 * {@link PromptDialogue} [defined in this class].
	 * 
	 * @author Zeale
	 *
	 * @param <PV>
	 *            <p>
	 *            The return value of this Prompt.
	 *            <p>
	 *            This does NOT have to match the return type of the parent
	 *            {@link PromptDialogue}. For example, a {@link PromptDialogue} that
	 *            has a &lt;V&gt; of {@link Number} can have two prompts.
	 *            <ul>
	 *            <li>The first prompt can have a &lt;PV&gt; of {@link Double},</li>
	 *            <li>and the second prompt can have a &lt;PV&gt; of
	 *            {@link Integer}.</li>
	 *            <ul>
	 *            <p>
	 *            Simply put &lt;PV&gt; must be a subtype of &lt;V&gt; or must be
	 *            the same as &lt;V>.
	 */
	public abstract class Prompt<PV extends V> extends VBox {

		private void highlightDescription(Color color) {
			fieldDescription.setFill(color);
			fieldDescription.setStrokeWidth(2.0);
		}

		private void removeDescriptionHighlight() {
			fieldDescription.setFill(Color.BLACK);
			fieldDescription.setStrokeWidth(1.0);
		}

		private K key;
		private Text fieldDescription = new Text();
		private boolean required;

		public final void setDescription(String text) {
			fieldDescription.setText(text);
		}

		public final void setRequired(boolean required) {
			this.required = required;
		}

		/**
		 * <p>
		 * This method is called by a {@link PromptDialogue} when the user pushes the
		 * <code>continue</code> button on each of the dialogue's prompts that are
		 * {@link #required}. This method should return <code>true</code> if this Prompt
		 * has a value that is acceptable.
		 * <p>
		 * If this method returns false when it is called by the PromptDialogue, then
		 * the dialogue will remain hidden and this Prompt's description will become
		 * red.
		 * 
		 * @return <code>true</code> if the value is acceptable, <code>false</code>
		 *         otherwise.
		 */
		protected abstract boolean verifyValue();

		public final boolean isRequired() {
			return required;
		}

		protected abstract PV getValue();

		public Prompt(K key, String description) {
			setKey(key);
			setDescription(description);
		}

		public final void setKey(K key) {
			this.key = key;
		}

		{
			addPrompt(this);
			getChildren().addAll(fieldDescription);
		}

		protected final void addContent(Node content) {
			getChildren().add(content);
		}

		protected final void removeContent(Node content) {
			getChildren().remove(content);
		}

		/**
		 * Cannot be undone.
		 */
		public final void remove() {
			removePrompt(this);
		}
	}

	public class BasicPrompt extends PromptDialogue<K, ? super String>.Prompt<String> {

		private TextField field = new TextField();
		{
			addContent(field);
		}

		// This will become abstract, to follow the change described right above the
		// class declaration. Next commit :D

		public void setHint(String text) {
			field.setPromptText(text);
		}

		public BasicPrompt(K key, String description) {
			super(key, description);
		}

		public BasicPrompt(K key, String description, String hint) {
			this(key, description);
			setHint(hint);
		}

		public BasicPrompt(K key, String description, String hint, String defaultValue) {
			this(key, description);
			setHint(hint);
		}

		public void setValue(String value) {
			field.setText(value);
		}

		@Override
		protected String getValue() {
			return field.getText();
		}

		@Override
		protected boolean verifyValue() {
			return !getValue().isEmpty();
		}

	}

	public PromptDialogue() {
	}

	public PromptDialogue(String title) {
		setTitle(title);
	}
}
