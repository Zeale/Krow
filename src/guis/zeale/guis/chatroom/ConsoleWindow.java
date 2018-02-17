package zeale.guis.chatroom;

import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import krow.guis.chatroom.ChatRoomServer;
import kröw.annotations.AutoLoad;
import kröw.annotations.LoadTime;
import kröw.connections.Client;
import kröw.connections.ClientListener;
import kröw.connections.Server;
import kröw.connections.messages.Message;
import kröw.gui.Application;

public abstract class ConsoleWindow extends Application {

	private static final short DEFAULT_SERVER_PORT = 25000;

	private static Client client;
	private static Server server;

	@AutoLoad(LoadTime.PROGRAM_EXIT)
	private static void stopServer_Impl() {
		if (server != null)
			try {
				server.stop();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	private final ClientListener listener = object -> {
		if (object instanceof Message)
			receivedMsg((Message) object);
		else
			receivedObj((Serializable) object);
	};

	/**
	 * Checks if there is a server being hosted. If there is, {@link #server} will
	 * not be null.
	 * 
	 * @return <code>true</code> if there is a server being hosted.
	 *         <code>false</code> otherwise.
	 */
	public boolean hostingServer() {
		return server == null;
	}

	/**
	 * Checks if there is a connection to a server. If there is, {@link #client}
	 * will not be null.
	 * 
	 * @return Returns <code>true</code> if there is an active client.
	 *         <code>false</code> otherwise.
	 */
	public boolean connected() {
		return client != null;
	}

	public boolean connect(String hostname, short port) throws UnknownHostException, IOException {
		if (connected())
			return false;
		client = new Client(hostname, port);
		client.addListener(listener);
		return true;
	}

	public boolean connect(String hostname) throws UnknownHostException, IOException {
		return connect(hostname, DEFAULT_SERVER_PORT);
	}

	public boolean startServer() throws IOException {
		return startServer(DEFAULT_SERVER_PORT);
	}

	public boolean startServer(int port) throws IOException {
		if (hostingServer())
			return false;
		server = new ChatRoomServer(port);
		return true;
	}

	public boolean stopServer() throws IOException {
		if (!hostingServer())
			return false;
		server.stop();
		return true;
	}

	/**
	 * Attempts to disconnect the client and set its field to null.
	 * 
	 * @return <code>true</code> if there was a client connected. <code>false</code>
	 *         otherwise.
	 */
	public boolean disconnect() {
		if (!connected())
			return false;
		client.closeConnection();
		client = null;
		return true;
	}

	protected String username;

	@FXML
	protected TextFlow chatPane;
	@FXML
	protected TextArea chatBox;
	@FXML
	protected ScrollPane chatPaneWrapper;

	@FXML
	protected AnchorPane pane;

	@FXML
	protected Button sendButton;

	protected void println(final String text) {
		println(text, Color.WHITE);
	}

	protected void println(final String line, final Color color) {
		print(line + "\n", color);
	}

	protected void println() {
		println("");
	}

	protected void printNode(final Node node) {
		chatPane.getChildren().add(node);
	}

	protected void print(final String text) {
		print(text, Color.WHITE);
	}

	protected void print(final String text, final Color color) {
		final Text t = new Text(text);
		t.setFill(color);
		chatPane.getChildren().add(t);
	}

	protected void receivedObj(Serializable obj) {
	}

	protected void receivedMsg(Message msg) {
	}

	protected final void handleUserInput(String input) {
		input = input.trim();
		final boolean del = false;
		handleInput(input);

		if (input == null || input.isEmpty())
			return;

		if (input.startsWith("/")) {

			String temp = "";
			for (final char c : input.toCharArray())
				if (c == ' ')
					if (del)
						continue;
					else
						temp += c;
				else if (c == '\n')
					continue;
				else
					temp += c;
			input = temp;

			String cmd;
			String[] args;
			if (input.contains(" ")) {
				cmd = input.substring(1, input.indexOf(" "));
				input = input.substring(input.indexOf(" ") + 1);
				args = input.split(" ");
			} else {
				cmd = input.substring(1);
				args = null;
			}
			handleCommand(cmd, args);
		} else {
			handleMessage(input);
		}
		chatBox.setText("");
	}

	/**
	 * Called by <b>{@link #handleUserInput(String)}</b>. This method will handle a
	 * command and its arguments however its subclass desires.
	 * 
	 * @param name
	 *            The command's name. For example,
	 * 
	 *            <pre>
	 * <code>/test arg1 arg2 arg3</code>
	 *            </pre>
	 * 
	 *            would give the name "test".
	 * @param args
	 *            Each argument of the command.
	 */
	protected abstract void handleCommand(String name, String... args);

	/**
	 * Called by <b>{@link #handleUserInput(String)}</b>. Handles a basic message
	 * sent by the user.
	 * 
	 * @param message
	 *            The text that the user submitted.
	 */
	protected abstract void handleMessage(String message);

	/**
	 * <p>
	 * This method is called before {@link #handleCommand(String, String...)} or
	 * {@link #handleMessage(String)}, whichever gets called. It is also called
	 * once, each time either one of those methods are called.
	 * <p>
	 * This method can be overridden to provide custom functionality when the user
	 * submits some form of text. The text is <b>first {@link String#trim()
	 * trimmed}</b> so that there is no leading or trailing whitespace.
	 * <p>
	 * If the user submits a command, this method is called then
	 * {@link #handleCommand(String, String...)} is called. If the user submits a
	 * regular piece of text, this method is called then
	 * {@link #handleMessage(String)} is called.
	 * <p>
	 * The {@link #chatBox} is not cleared until this method and
	 * {@link #handleCommand(String, String...)}/{@link #handleMessage(String)} are
	 * called.
	 * 
	 * @param rawInput
	 *            The user's input, trimmed, but edited in no other way.
	 */
	protected void handleInput(String rawInput) {
	}
}
