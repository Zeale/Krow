package kröw.app.api.connections;

import java.io.Serializable;

public class Message implements Serializable {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	public final Object text;

	public Message(final Object text) {
		this.text = text;
	}

	public Object getText() {
		return text;
	}

}
