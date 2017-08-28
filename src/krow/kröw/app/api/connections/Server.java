package kröw.app.api.connections;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import kröw.core.Kröw;

public class Server {
	private final ServerSocket socket;

	private final ArrayList<Client> connections = new ArrayList<>();

	public Server(int port) throws IOException {
		this.socket = new ServerSocket(port);
		acceptThread.start();
	}

	public Server() throws IOException {
		socket = new ServerSocket(0);
	}

	private boolean accept = true, running = true;

	private Thread acceptThread = new Thread(new Runnable() {

		@Override
		public void run() {
			while (accept && running) {
				try {
					Socket connection = socket.accept();
					System.out.println("Accepting connection");
					new Thread(new Runnable() {

						@Override
						public void run() {
							acceptConnection(connection);
						}
					}).start();
				} catch (IOException e) {
					System.err.println("Failed to accept an incoming connection.");
				}
			}
			acceptThread = new Thread(this);
		}
	});

	protected void acceptConnection(Socket connection) {
		try {
			Client client = new Client(connection);
			if (Kröw.DEBUG_MODE)
				System.out.println("Server: Made server-client: " + client);
			connections.add(client);

			client.addListener(new ClientListener() {

				@Override
				public void objectReceived(Object object) {

					if (Kröw.DEBUG_MODE)
						System.out.println("Server: Object received");
					for (Client cl : connections)
						// if (cl != client)
						try {
							if (Kröw.DEBUG_MODE)
								System.out.println(
										"Server: Sending object to clients via server-client... (Ignore upcoming client send msgs...)");
							cl.sendObject((Serializable) object);
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	public void blockIncomingConnections() {
		accept = false;

	}

	public void allowIncomingConnections() {
		boolean accept = this.accept;
		this.accept = true;
		if (!accept)
			acceptThread.start();
	}

	public void stop() {
		blockIncomingConnections();
		running = false;
		for (Client c : connections)
			c.closeConnection();
	}

}
