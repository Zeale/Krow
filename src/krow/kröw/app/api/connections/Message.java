package kröw.app.api.connections;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class Message implements Serializable {
	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;
	public final String text;
	private long timeSent;

	public Date getTimeSent() {
		return new Date(timeSent);
	}

	private HashMap<DataKey, Object> data = new HashMap<>(7);

	public Message(String text, Date timeSent) {
		this.text = text;
		this.timeSent = timeSent.getTime();
	}

	public Message(String text) {
		this.text = text;
	}

	public HashMap<DataKey, Object> getData() {
		return data;
	}

	public static enum DataKey {
		RECIPIENTS, AUTHORS, MAIN_RECIPIENT, MAIN_AUTHOR, OTHER_DATA, BREAK_CONNECTION;

		@Override
		public String toString() {
			return name();
		}

	}

	public static Message breakConnectionMesage() {
		Message m = new Message("break-connection;");
		m.getData().put(DataKey.BREAK_CONNECTION, true);
		return m;
	}

}
