package krow.guis.chatroom;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kröw.app.api.connections.Client;
import kröw.app.api.connections.FullClientListener;
import kröw.app.api.connections.Message;
import kröw.app.api.connections.Server;
import kröw.core.Kröw;

public class ChatRoomServer extends Server {

	protected List<Client> connections = new ArrayList<>();

	public ChatRoomServer() throws IOException {
		super();
	}

	public ChatRoomServer(int port) throws IOException {
		super(port);
	}

	@Override
	protected void acceptConnection(Socket connection) {

		try {
			final Client client = new Client(connection);
			if (Kröw.DEBUG_MODE)
				System.out.println("Server: Made server-client: " + client);
			connections.add(client);

			client.addListener(new FullClientListener() {

				@Override
				public void objectReceived(Object object) {
					if (Kröw.DEBUG_MODE)
						System.out.println("Server: Object received");

					try {

						if (Client.isEndConnectionMessage((Message) object))
							for (Client c : connections) {
								c.sendObject(new ChatRoomMessage("A user has left the chatroom...", "Server",
										new Date().getTime()));
							}

						for (final Client cl : connections)
							// if (cl != client)
							try {
								if (Kröw.DEBUG_MODE)
									System.out.println(
											"Server: Sending object to clients via server-client... (Ignore upcoming client send msgs...)");
								cl.sendObject((Serializable) object);
							} catch (final IOException e) {
								e.printStackTrace();
							}

					} catch (IOException e) {
					}
				}

				@Override
				public void connectionLost() {
					connections.remove(client);
				}

				@Override
				public void connectionEstablished() {

				}

				@Override
				public void connectionClosed() {
					connections.remove(client);
				}
			});
		} catch (final IOException e) {
			e.printStackTrace();
			return;
		}

	}

	@Override
	protected List<Client> getAllConnections() {
		return connections;
	}

}
