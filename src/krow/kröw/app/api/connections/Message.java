package kröw.app.api.connections;

import java.io.Serializable;

public class Message implements Serializable {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	public final String text;

	public Message(final String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

}
