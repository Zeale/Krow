package kröw.app.api.connections;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import kröw.core.Kröw;

public class Server {
	private final ServerSocket socket;

	private final ArrayList<Client> connections = new ArrayList<>();

	private boolean accept = true, running = true;

	private Thread acceptThread = new Thread(new Runnable() {

		@Override
		public void run() {
			while (accept && running)
				try {
					final Socket connection = socket.accept();
					if (Kröw.DEBUG_MODE)
						System.out.println("Server: Accepting connection");
					new Thread(() -> acceptConnection(connection)).start();
				} catch (SocketException e) {
					// Server terminated
				} catch (final IOException e) {
					System.err.println("Failed to accept an incoming connection.");
				}
			acceptThread = new Thread(this);
		}
	});

	public Server() throws IOException {
		socket = new ServerSocket(0);
	}

	public Server(final int port) throws IOException {
		socket = new ServerSocket(port);
		acceptThread.start();
	}

	protected void acceptConnection(final Socket connection) {
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

	public void allowIncomingConnections() {
		final boolean accept = this.accept;
		this.accept = true;
		if (!accept)
			acceptThread.start();
	}

	public void blockIncomingConnections() {
		accept = false;

	}

	public void stop() throws IOException {
		blockIncomingConnections();
		running = false;
		socket.close();
		for (final Object o : connections.toArray()) {
			Client c = (Client) o;
			c.sendCloseMsg();
			c.closeConnection();
		}
	}

}
