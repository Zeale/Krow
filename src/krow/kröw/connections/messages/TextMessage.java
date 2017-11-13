package kröw.connections.messages;

public class TextMessage extends Message {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private final String text;

	public TextMessage(final String text) {
		this.text = text;
	}

	/**
	 * @return the text
	 */
	public final String getText() {
		return text;
	}

}
