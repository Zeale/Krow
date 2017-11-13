package krow.guis.chatroom;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import krow.guis.chatroom.messages.ChatRoomMessage;
import krow.guis.chatroom.messages.CommandMessage;
import kröw.connections.Client;
import kröw.connections.FullClientListener;
import kröw.connections.Server;
import kröw.connections.messages.Message;
import kröw.core.Kröw;

public class ChatRoomServer extends Server {

	protected List<ChatRoomClient> connections = new ArrayList<>();

	public ChatRoomServer() throws IOException {
		super();
	}

	public ChatRoomServer(final int port) throws IOException {
		super(port);
	}

	@Override
	protected void acceptConnection(final Socket connection) {

		try {
			final ChatRoomClient client = new ChatRoomClient(connection);
			if (Kröw.DEBUG_MODE)
				System.out.println("Server: Made server-client: " + client);
			connections.add(client);

			client.addListener(new FullClientListener() {

				@Override
				public void connectionClosed() {
					connections.remove(client);
				}

				@Override
				public void connectionEstablished() {

				}

				@Override
				public void connectionLost() {
					connections.remove(client);
				}

				@Override
				public void objectReceived(final Object object) {
					if (Kröw.DEBUG_MODE)
						System.out.println("Server: Object received");

					try {

						if (Client.isEndConnectionMessage((Message) object))
							for (final Client c : connections)
								c.sendObject(new ChatRoomMessage("A user has left the chatroom...", "Server",
										new Date().getTime()));

						if (object instanceof CommandMessage) {
							final CommandMessage cm = (CommandMessage) object;
							if (cm.getCommand().equalsIgnoreCase("setname")
									|| cm.getCommand().equalsIgnoreCase("set-name") && cm.getArgs() != null
											&& cm.getArgs().length > 0) {
								final String prevName = client.getName();
								client.setName(cm.getArgs()[0]);
								for (final Client cl : connections)
									if (cl != client)
										cl.sendMessage(new ChatRoomMessage(
												prevName + " has changed their name to " + client.getName() + ".",
												"Server", new Date().getTime()));
							}
							return;
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

					} catch (final Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (final IOException e) {
			e.printStackTrace();
			return;
		}

	}

	@Override
	protected List<ChatRoomClient> getAllConnections() {
		return connections;
	}

}
