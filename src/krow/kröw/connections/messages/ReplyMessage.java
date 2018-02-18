package kröw.connections.messages;

import java.util.UUID;

public class ReplyMessage extends Message {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	private final UUID id;

	public ReplyMessage(UUID id) {
		this.id = id;
	}

	public UUID getId() {
		return id;
	}

}
