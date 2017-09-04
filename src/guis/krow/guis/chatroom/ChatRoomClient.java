package krow.guis.chatroom;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import kröw.program.api.connections.Client;

public class ChatRoomClient extends Client {

	private String name = "An unknown person";

	public ChatRoomClient(final Socket socket) throws IOException {
		super(socket);
	}

	public ChatRoomClient(final String hostname, final int port) throws UnknownHostException, IOException {
		super(hostname, port);
	}

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
	public final void setName(final String name) {
		this.name = name;
	}

}
