package kröw.app.api.connections;

import java.util.Date;
import java.util.HashMap;

public class Message {
	public final String text;
	private Date timeSent;

	private HashMap<DataKey, Object> data = new HashMap<>(7);

	public Message(String text, Date timeSent) {
		this.text = text;
		this.timeSent = timeSent;
	}

	public Message(String text) {
		this.text = text;
	}

	public HashMap<DataKey, Object> getData() {
		return data;
	}

	public static enum DataKey {
		RECIPIENTS, AUTHORS, MAIN_RECIPIENT, MAIN_AUTHOR, OTHER_DATA,;

		@Override
		public String toString() {
			return name();
		}

	}

}
