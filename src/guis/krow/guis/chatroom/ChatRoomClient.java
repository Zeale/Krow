package krow.guis.chatroom;

import java.io.IOException;
import java.net.UnknownHostException;

import kröw.app.api.connections.Client;

public class ChatRoomClient extends Client {

	public ChatRoomClient(String hostname, int port) throws UnknownHostException, IOException {
		super(hostname, port);
	}

	private String name;

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public final void setName(String name) {
		this.name = name;
	}

}
