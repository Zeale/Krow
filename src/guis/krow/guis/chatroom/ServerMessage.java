package krow.guis.chatroom;

import java.util.Date;

import kröw.connections.messages.TextMessage;

public class ServerMessage extends TextMessage {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;
	public final long timeSent;

	public ServerMessage(String text) {
		super(text);
		timeSent = new Date().getTime();
	}

}
