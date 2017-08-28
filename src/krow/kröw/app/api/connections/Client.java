package kröw.app.api.connections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import kröw.core.Kröw;

public class Client {

	private ArrayList<ClientListener> listeners = new ArrayList<>();

	public void addListener(ClientListener listener) {
		listeners.add(listener);
		if (!outputThread.isAlive())// Start thread; it kills itself if there
									// are no listeners.
			outputThread.start();
	}

	private boolean connectionClosed;

	private final Socket socket;

	private final ObjectInputStream objIn;
	private final ObjectOutputStream objOut;

	public void removeListener(ClientListener listener) {
		listeners.remove(listener);
	}

	private Thread outputThread = new Thread(new Runnable() {

		@Override
		public void run() {
			while (!connectionClosed && listeners.size() > 0) {

				Serializable obj;
				try {
					obj = (Serializable) objIn.readObject();
					if (Kröw.DEBUG_MODE)
						System.out.println("Client: Received object in class, calling listeners");
					for (ClientListener cl : listeners)
						cl.objectReceived(obj);
				} catch (StreamCorruptedException e) {

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

	public Client(String hostname, int port) throws UnknownHostException, IOException {
		socket = new Socket(hostname, port);

		objOut = new ObjectOutputStream(socket.getOutputStream());
		objIn = new ObjectInputStream(socket.getInputStream());

	}

	public Client(Socket socket) throws IOException {
		this.socket = socket;

		objOut = new ObjectOutputStream(socket.getOutputStream());
		objIn = new ObjectInputStream(socket.getInputStream());

	}

	public void sendMessage(String message) throws IOException {
		sendObject(message);
	}

	public void sendObject(Serializable object) throws IOException {
		if (Kröw.DEBUG_MODE)
			System.out.println("Client: Sending an object object");
		objOut.writeObject(object);
		objOut.flush();
	}

	public void closeConnection() {

		try {
			sendObject(Message.breakConnectionMesage());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		connectionClosed = true;
		for (ClientListener cl : listeners)
			if (cl instanceof FullClientListener)
				((FullClientListener) cl).connectionClosed();
		try {
			objIn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			objOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
