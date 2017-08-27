package kröw.app.api.connections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

	private final String hostname;

	public String getHostname() {
		return hostname;
	}

	private boolean connectionClosed;

	private final Socket socket;

	private final ObjectInputStream objIn;
	private final ObjectOutputStream objOut;

	private Thread outputThread = new Thread(new Runnable() {

		@Override
		public void run() {
			while (!connectionClosed) {

				Serializable ser;
				try {
					while ((ser = (Serializable) objIn.readObject()) != null) {
						// Notify listeners...
					}
				} catch (ClassNotFoundException | IOException e1) {
					e1.printStackTrace();
				}

				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			outputThread = new Thread(this);

			return;
		}
	});

	private Client(String hostname, short port) throws UnknownHostException, IOException {
		this.hostname = hostname;

		socket = new Socket(hostname, port);

		objIn = new ObjectInputStream(socket.getInputStream());
		objOut = new ObjectOutputStream(socket.getOutputStream());
	}

	public void sendMessage(String message) throws IOException {
		objOut.writeObject(message);
	}

	public void sendObject(Serializable object) throws IOException {
		objOut.writeObject(object);
	}

	public void closeConnection() {
		connectionClosed = true;
	}

}
