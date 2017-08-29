package kröw.app.api.connections;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import kröw.core.Kröw;

public class Client {

	private final ArrayList<ClientListener> listeners = new ArrayList<>();

	private boolean connectionClosed;

	private final Socket socket;

	private final ObjectInputStream objIn;

	private final ObjectOutputStream objOut;
	private Thread outputThread = new Thread(new Runnable() {

		@Override
		public void run() {
			int socketExceptionCount = 0;
			while (!connectionClosed && listeners.size() > 0) {

				Serializable obj;
				try {
					if (socket.isClosed())
						closeConnection();
					obj = (Serializable) objIn.readObject();
					if (Kröw.DEBUG_MODE)
						System.out.println("Client: Received object in class, calling listeners");

					if (obj instanceof CloseMessage) {
						for (ClientListener cl : listeners)
							if (cl instanceof FullClientListener)
								((FullClientListener) cl).connectionClosed();
						closeConnection();
						return;
					}
					for (final ClientListener cl : listeners)
						cl.objectReceived(obj);
				} catch (EOFException e) {
					closeConnection();
					for (ClientListener cl : listeners)
						if (cl instanceof FullClientListener)
							((FullClientListener) cl).connectionLost();
					return;
				} catch (final SocketException e) {
					socketExceptionCount++;
					pause();
					if (socketExceptionCount > 3)
						closeConnection();
				} catch (ClassNotFoundException | IOException e1) {
					e1.printStackTrace();
				}

				try {
					Thread.sleep(10);
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}

			outputThread = new Thread(this);

			return;
		}
	});

	public Client(final Socket socket) throws IOException {
		this.socket = socket;

		objOut = new ObjectOutputStream(socket.getOutputStream());
		objIn = new ObjectInputStream(socket.getInputStream());

	}

	public Client(final String hostname, final int port) throws UnknownHostException, IOException {
		socket = new Socket(hostname, port);

		objOut = new ObjectOutputStream(socket.getOutputStream());
		objIn = new ObjectInputStream(socket.getInputStream());

	}

	public void addListener(final ClientListener listener) {
		listeners.add(listener);
		if (!outputThread.isAlive())// Start thread; it kills itself if there
									// are no listeners.
			outputThread.start();
	}

	public void closeConnection() {

		// TODO Send end connection msg

		connectionClosed = true;
		for (final ClientListener cl : listeners)
			if (cl instanceof FullClientListener)
				((FullClientListener) cl).connectionClosed();
		try {
			objIn.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		try {
			objOut.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void pause() {
		try {
			Thread.sleep(1000);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void removeListener(final ClientListener listener) {
		listeners.remove(listener);
	}

	public void sendMessage(final String message) throws IOException {
		sendObject(message);
	}

	public void sendMessage(Message message) throws IOException {
		sendObject(message);
	}

	public void sendCloseMsg() throws IOException {
		sendObject(new CloseMessage());
	}

	private static final class CloseMessage extends Message {

		/**
		 * SUID
		 */
		private static final long serialVersionUID = 1L;

		public CloseMessage() {
			super("end");
		}

	}

	public void sendObject(final Serializable object) throws IOException {
		if (Kröw.DEBUG_MODE)
			System.out.println("Client: Sending an object object");
		objOut.writeObject(object);
		objOut.flush();
	}

}
