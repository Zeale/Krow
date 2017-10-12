package kröw.connections.messages;

public class TextMessage extends Message {
	private final String text;

	public TextMessage(String text) {
		this.text = text;
	}

	/**
	 * @return the text
	 */
	public final String getText() {
		return text;
	}

}
