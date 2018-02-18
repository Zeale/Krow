package krow.guis.chatroom;

import java.util.UUID;

import kröw.connections.messages.TextMessage;

// This class should be replied to with a ReplyMessage once it is received, so that the client knows that the server received the msg.
public class ChatRoomMessage extends TextMessage {
	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;
	public final String username;
	public final UUID id;

	public ChatRoomMessage(String text, String username, UUID id) {
		super(text);
		this.username = username;
		this.id = id;
	}

}
