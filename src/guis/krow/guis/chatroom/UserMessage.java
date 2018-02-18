package krow.guis.chatroom;

import kr�w.connections.messages.TextMessage;

public class UserMessage extends TextMessage {

	public final String sender;

	public UserMessage(String text, String sender) {
		super(text);
		this.sender = sender;
	}

}
