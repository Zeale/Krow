package zeale.guis.chatroom;

import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.WeakHashMap;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import krow.guis.chatroom.ChatRoomMessage;
import krow.guis.chatroom.ChatRoomServer;
import krow.guis.chatroom.UserMessage;
import kröw.annotations.AutoLoad;
import kröw.annotations.LoadTime;
import kröw.connections.Client;
import kröw.connections.ClientListener;
import kröw.connections.Server;
import kröw.connections.messages.Message;
import kröw.connections.messages.ReplyMessage;
import kröw.gui.Application;

public abstract class ConsoleWindow extends Application {

	private static final short DEFAULT_SERVER_PORT = 25000;

	private static Client client;
	private static Server server;

	public Client getClient() {
		return client;
	}

	public Server getServer() {
		return server;
	}

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
		return server != null;
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

		boolean success = startServer(DEFAULT_SERVER_PORT);
		if (!success)
			return false;

		// If we're not already connected to a server, connect to the one we just made.
		if (!connected())
			connect(null);// null makes this computer connect to itself.
		return true;
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
		server = null;
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

	/**
	 * Called when an object <b>that isn't a {@link Message}</b> is received.
	 * 
	 * @param obj
	 *            The received object.
	 */
	protected void receivedObj(Serializable obj) {
	}

	/**
	 * Called when a {@link Message} of any type is received.
	 * 
	 * @param msg
	 *            The received {@link Message}.
	 */
	protected void receivedMsg(Message msg) {
		if (msg instanceof ReplyMessage)
			if (SENT_MESSAGES_MAP.containsKey(((ReplyMessage) msg).getId()))
				SENT_MESSAGES_MAP.get(((ReplyMessage) msg).getId()).receivedReply();
		if (msg instanceof UserMessage) {
			Text name = new Text(((UserMessage) msg).sender), arrow = new Text(" > "),
					text = new Text(((UserMessage) msg).getText());
			name.setFill(Color.RED);
			arrow.setFill(Color.WHITE);
			text.setFill(Color.BLUE);
			printNode(name);
			printNode(arrow);
			printNode(text);
			println();
		}
	}

	protected final void handleUserInput(String input) {
		input = input.trim();
		final boolean del = false;

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
			commandInput(cmd, args);
		} else {
			textInput(input);
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
	protected abstract void commandInput(String name, String... args);

	/**
	 * Called by <b>{@link #handleUserInput(String)}</b>. Handles a basic message
	 * sent by the user.
	 * 
	 * @param message
	 *            The text that the user submitted.
	 */
	protected abstract void textInput(String message);

	public void send(String text) {
		new ChatRoomText(text).send();
	}

	private static final WeakHashMap<UUID, ChatRoomText> SENT_MESSAGES_MAP = new WeakHashMap<>();

	/**
	 * An instance of this class should be created to give basic messages that the
	 * user sends to a server.
	 * 
	 * @author Zeale
	 *
	 */
	private class ChatRoomText {

		private UUID id = UUID.randomUUID();
		{
			SENT_MESSAGES_MAP.put(id, this);
		}

		private class ReplyText extends Text {
			{
				setOpacity(0.5);
			}

			public void receivedReply() {
				setOpacity(1);
			}

			public void noReply() {
				setStrikethrough(true);
			}
		}

		public void receivedReply() {
			message.receivedReply();
		}

		private ReplyText message = new ReplyText();
		private Text name = new Text(username), arrow = new Text(" > ");

		/**
		 * Prints each text node contained to the console with the correct coloring and
		 * sends a message to the server. Once a reply has been received, the message
		 * that was printed to the console will become fully opaque.
		 */
		public void send() {
			name.setFill(Color.RED);
			arrow.setFill(Color.WHITE);
			message.setFill(Color.LIGHTBLUE);
			printNode(name);
			printNode(arrow);
			printNode(message);
			println();
			rawSend(new ChatRoomMessage(message.getText(), name.getText(), id));
		}

		public ChatRoomText(String message) {
			if (!connected())
				throw new RuntimeException();
			this.message.setText(message);
		}

	}

	public boolean rawSend(Serializable object) {
		if (connected())
			try {
				getClient().sendObject(object);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		return false;
	}

}
