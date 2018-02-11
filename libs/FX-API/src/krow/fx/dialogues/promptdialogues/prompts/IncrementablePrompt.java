package krow.fx.dialogues.promptdialogues.prompts;

import javafx.scene.input.ScrollEvent;
import krow.fx.dialogues.promptdialogues.PromptDialogue;

// All other prompt classes should be changed to match this one in that it extends a PromptDialogue whose first type parameter can be a super class of K. If Prompt#getKey() is ever changed from private to public, or event to protected, this will allow prompts to return more specific instances of their keys, since their type parameter can be more specific.
// Subclasses of Prompt can NOT extend PromptDialogue<?, ? super V>.Prompt<V> to simply "drop their key type parameter," because their constructors won't work. They won't be able to ask for a PromptDialogue of the exact types wrapping the Prompt that they extended if they don't cache what the key type is. Still, Prompt subclasses probably shouldn't force the dialogue they are added into to take keys exactly matching their key.
public abstract class IncrementablePrompt<K, V> extends PromptDialogue<? super K, ? super V>.TextFieldPrompt<V> {

	{
		field.setOnScroll(event -> {
			if (event.getDeltaY() > 0)
				increment(event.getDeltaY() / 40);
			else if (event.getDeltaY() < 0)
				decrement(-event.getDeltaY() / 40);
		});
	}

	public IncrementablePrompt(PromptDialogue<? super K, ? super V> owner, K key, String description) {
		owner.super(key, description);
	}

	public IncrementablePrompt(PromptDialogue<? super K, ? super V> owner, K key, String description, V defaultValue) {
		owner.super(key, description, defaultValue);
	}

	/**
	 * Called when the user scrolls up by one unit. This is not the same value as
	 * that retrieved from a {@link ScrollEvent#getDeltaY()} call, since that is
	 * measured in pixels. This is measured in how many "notches" that the user
	 * turned the mouse wheel.
	 * 
	 * @param amount
	 *            The amount of times the user scrolled the mouse wheel. This method
	 *            will be called as a result of a scroll event and will be passed
	 *            the value, <code>{@link ScrollEvent#getDeltaY()} / 40</code> for
	 *            this <b><code>amount</code></b> parameter.
	 */
	protected abstract void increment(double amount);

	/**
	 * Called when the user scrolls down by one unit. This is not the same value as
	 * that retrieved from a {@link ScrollEvent#getDeltaY()} call, since that is
	 * measured in pixels. This is measured in how many "notches" that the user
	 * turned the mouse wheel.
	 * 
	 * @param amount
	 *            <p>
	 *            The amount of times the user scrolled the mouse wheel. This method
	 *            will be called as a result of a scroll event and will be passed
	 *            the value, <code>- {@link ScrollEvent#getDeltaY()} / 40</code> for
	 *            this <strong><code>amount</code></strong> parameter.
	 *            <p>
	 *            <b>Take note of the negative in front of the value:</b> Since
	 *            {@link ScrollEvent#getDeltaY()} is inherently negative when the
	 *            user scrolls down, this method gets called so that
	 *            <b><code>amount</code></b> is assured to be positive.
	 */
	protected abstract void decrement(double amount);

}
