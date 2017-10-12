package krow.guis.chatroom.messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import kröw.connections.TextMessage;

public class ChatRoomMessage extends TextMessage {
	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	private final String author;

	private final long timeSent;

	private ArrayList<String> recipients;

	public ChatRoomMessage(final String text, final String author, final long timeSent) {
		super(text);
		this.author = author;
		this.timeSent = timeSent;
	}

	public ChatRoomMessage(final String text, final String author, final long timeSent, final String... recipients) {
		super(text);
		this.author = author;
		this.timeSent = timeSent;
		this.recipients = new ArrayList<>();
		for (final String s : recipients)
			this.recipients.add(s);
	}

	/**
	 * @return the author
	 */
	public final String getAuthor() {
		return author;
	}

	public List<String> getRecipients() {
		return Collections.unmodifiableList(recipients);
	}

	public Date getTimeSent() {
		return new Date(timeSent);
	}
}
