package kröw.app.api.connections;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class Message implements Serializable {
	public static enum DataKey {
		RECIPIENTS, AUTHORS, MAIN_RECIPIENT, MAIN_AUTHOR, OTHER_DATA, BREAK_CONNECTION;

		@Override
		public String toString() {
			return name();
		}

	}

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	public static Message breakConnectionMesage() {
		final Message m = new Message("break-connection;");
		m.getData().put(DataKey.BREAK_CONNECTION, true);
		return m;
	}

	public final String text;

	private long timeSent;

	private final HashMap<DataKey, Object> data = new HashMap<>(7);

	public Message(final String text) {
		this.text = text;
	}

	public Message(final String text, final Date timeSent) {
		this.text = text;
		this.timeSent = timeSent.getTime();
	}

	public HashMap<DataKey, Object> getData() {
		return data;
	}

	public Date getTimeSent() {
		return new Date(timeSent);
	}

}
