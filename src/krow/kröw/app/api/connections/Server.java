package kröw.app.api.connections;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

import kröw.core.Kröw;

public abstract class Server {
	private final ServerSocket socket;

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

	protected abstract void acceptConnection(final Socket connection);

	public void allowIncomingConnections() {
		final boolean accept = this.accept;
		this.accept = true;
		if (!accept)
			acceptThread.start();
	}

	public void blockIncomingConnections() {
		accept = false;

	}

	protected abstract List<Client> getAllConnections();

	public void stop() throws IOException {
		blockIncomingConnections();
		running = false;
		socket.close();
		for (final Object o : getAllConnections().toArray()) {
			Client c = (Client) o;
			c.sendEndConnectionMsg();
			c.closeConnection();
		}
	}

}
