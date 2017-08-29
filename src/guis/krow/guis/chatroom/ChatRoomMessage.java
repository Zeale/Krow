package krow.guis.chatroom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import kröw.app.api.connections.Message;

public class ChatRoomMessage extends Message {
	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	public ChatRoomMessage(String text, String author, long timeSent) {
		super(text);
		this.author = author;
		this.timeSent = timeSent;
	}

	public ChatRoomMessage(String text, String author, long timeSent, String... recipients) {
		super(text);
		this.author = author;
		this.timeSent = timeSent;
		for (String s : recipients)
			this.recipients.add(s);
	}

	private String author;
	private long timeSent;

	/**
	 * @return the author
	 */
	public final String getAuthor() {
		return author;
	}

	public Date getTimeSent() {
		return new Date(timeSent);
	}

	private ArrayList<String> recipients;

	public List<String> getRecipients() {
		return Collections.unmodifiableList(recipients);
	}
}
