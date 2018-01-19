package zeale.guis;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Stack;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import krow.guis.GUIHelper;
import krow.guis.chatroom.ChatRoomServer;
import krow.guis.chatroom.messages.ChatRoomMessage;
import krow.guis.chatroom.messages.CommandMessage;
import kröw.annotations.AutoLoad;
import kröw.annotations.LoadTime;
import kröw.callables.VarArgsTask;
import kröw.connections.Client;
import kröw.connections.FullClientListener;
import kröw.connections.Server;
import kröw.connections.messages.Message;
import kröw.core.Kröw;
import kröw.core.managers.WindowManager;
import kröw.core.managers.WindowManager.App;

public class ChatRoom extends WindowManager.App {

	private static class ChatRoomText {
		private final ChatRoomMessage message;

		public ChatRoomText(final ChatRoomMessage message) {
			this.message = message;
		}

		public void add(final TextFlow pane) {
			final Text name = new Text();
			if (message.getAuthor().isEmpty()) {
				name.setText("Unnamed");
				// TODO Ask server to send color from a queue
				name.setFill(ERROR_COLOR);
			} else {
				name.setText(message.getAuthor());
				name.setFill(Color.LIGHTGOLDENRODYELLOW);
			}
			final Text splitter = new Text(" > ");
			final Text message = new Text(this.message.getText() + "\n");
			message.setFill(Color.WHITE);
			splitter.setFill(Color.BLUE);

			pane.getChildren().addAll(name, splitter, message);

		}

	}

	private static final Color NOTIFICATION_COLOR = Color.LIGHTBLUE, ERROR_COLOR = Color.FIREBRICK,
			SUCCESS_COLOR = Color.GREEN, WARNING_COLOR = Color.GOLD;

	static {
		Kröw.addReflectionClass(ChatRoom.class);
	}

	private final static double CHAT_PANE_PREF_WIDTH = 1277, CHAT_PANE_PREF_HEIGHT = 827, CHAT_PANE_LAYOUTX = 305,
			CHAT_PANE_LAYOUTY = 14;
	private final static double CHAT_BOX_PREF_WIDTH = 1920, CHAT_BOX_PREF_HEIGHT = 220, CHAT_BOX_LAYOUTX = 0,
			CHAT_BOX_LAYOUTY = 860;
	private final static double SEND_BUTTON_PREF_WIDTH = 55, SEND_BUTTON_PREF_HEIGHT = 24, SEND_BUTTON_LAYOUTX = 1851,
			SEND_BUTTON_LAYOUTY = 958;

	private static Client client;

	private static Server server;

	private static final Object TEXT_FONT_SIZE_KEY = new Object(), TEXT_FONT_FAMILY_KEY = new Object();

	@AutoLoad(LoadTime.PROGRAM_EXIT)
	public static void closeServer() {
		try {
			if (server != null)
				server.stop();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		server = null;
	}

	public static boolean isClientConnected() {
		return client != null;
	}

	public static boolean isServerOpen() {
		return server != null;
	}

	private final Stack<String> history = new Stack<>();

	@FXML
	private TextFlow chatPane;
	@FXML
	private TextArea chatBox;

	@FXML
	private AnchorPane pane;

	@FXML
	private Button sendButton;

	private final FullClientListener listener = new FullClientListener() {

		@Override
		public void connectionClosed() {
			client = null;
		}

		@Override
		public void connectionEstablished() {

		}

		@Override
		public void connectionLost() {
			client = null;
		}

		@Override
		public void objectReceived(final Object object) {
			Platform.runLater(() -> {

				if (object instanceof ChatRoomMessage)
					new ChatRoomText((ChatRoomMessage) object).add(chatPane);
				else

					chatPane.getChildren().add(
							new Text("Message unreceived!   " + (Kröw.DEBUG_MODE ? object.toString() : "") + "\n"));
			});
		}
	};

	private String user;

	public boolean canCreateServer() {
		return server == null;
	}

	@Override
	public boolean canSwitchPage(final Class<? extends App> newSceneClass) {
		return true;
	}

	public boolean createServer() throws IOException {
		return createServer(25000);
	}

	public boolean createServer(final int port) throws IOException {
		if (!canCreateServer())
			return false;
		server = new ChatRoomServer(port);
		if (client == null)
			setClient("localhost", port);
		return true;
	}

	private void emptyMessageWarning() {
		WindowManager.spawnLabelAtMousePos("Empty message...", ERROR_COLOR);
	}

	@Override
	public String getWindowFile() {
		return "ChatRoom.fxml";
	}

	@Override
	public void initialize() {

		chatPane.setPrefSize(Kröw.scaleWidth(CHAT_PANE_PREF_WIDTH), Kröw.scaleHeight(CHAT_PANE_PREF_HEIGHT));
		chatPane.setLayoutX(Kröw.scaleWidth(CHAT_PANE_LAYOUTX));
		chatPane.setLayoutY(Kröw.scaleHeight(CHAT_PANE_LAYOUTY));

		chatBox.setPrefSize(Kröw.scaleWidth(CHAT_BOX_PREF_WIDTH), Kröw.scaleHeight(CHAT_BOX_PREF_HEIGHT));
		chatBox.setLayoutX(Kröw.scaleWidth(CHAT_BOX_LAYOUTX));
		chatBox.setLayoutY(Kröw.scaleHeight(CHAT_BOX_LAYOUTY));

		sendButton.setPrefSize(Kröw.scaleWidth(SEND_BUTTON_PREF_WIDTH), Kröw.scaleHeight(SEND_BUTTON_PREF_HEIGHT));
		sendButton.setLayoutX(Kröw.scaleWidth(SEND_BUTTON_LAYOUTX));
		sendButton.setLayoutY(Kröw.scaleHeight(SEND_BUTTON_LAYOUTY));

		sendButton.setOnAction(event -> {
			if (!chatBox.getText().isEmpty())
				parseInput(chatBox.getText());
			else
				emptyMessageWarning();
		});

		chatPane.getChildren().addListener((ListChangeListener<Node>) c -> {
			while (c.next())
				if (c.wasAdded())
					for (final Node n : c.getAddedSubList())
						if (n instanceof Text) {
							final Text t = (Text) n;
							t.setFont(Font.font(16));
							if (t.getProperties().containsKey(TEXT_FONT_FAMILY_KEY))
								t.setFont(Font.font((String) t.getProperties().get(TEXT_FONT_FAMILY_KEY)));
							if (t.getProperties().containsKey(TEXT_FONT_SIZE_KEY))
								t.setFont(Font.font(t.getFont().getFamily(),
										(double) t.getProperties().get(TEXT_FONT_SIZE_KEY)));
						}

		});

		chatBox.setOnKeyPressed(new EventHandler<KeyEvent>() {

			private final SimpleIntegerProperty historyPos = new SimpleIntegerProperty();
			private final ChangeListener<Number> listener = new ChangeListener<Number>() {

				@Override
				public void changed(final ObservableValue<? extends Number> observable, final Number oldValue,
						final Number newValue) {

					if (oldValue.intValue() == newValue.intValue() || history.isEmpty())
						return;

					historyPos.removeListener(this);

					// When subtracting 0 from array's size, you get the
					// array's size.
					// Getting a value at arr.get(arr.size()) throws an
					// exception.
					if (historyPos.get() <= 0)
						historyPos.set(1);

					if (historyPos.get() > history.size())
						historyPos.set(history.size());

					if (historyPos.get() == 1 && !(oldValue.intValue() > 1) && !chatBox.getText().isEmpty()
							&& !chatBox.getText().equals(history.peek()))
						history.push(chatBox.getText());
					chatBox.setText(history.get(history.size() - historyPos.get()));
					chatBox.positionCaret(chatBox.getText().length());

					historyPos.addListener(this);
				}
			};
			{
				historyPos.addListener(listener);
			}

			@Override
			public void handle(final KeyEvent event) {
				if (event.getCode().equals(KeyCode.UP) && event.isShiftDown()) {

					event.consume();

					historyPos.set(historyPos.get() + 1);

					return;
				} else if (event.getCode().equals(KeyCode.DOWN) && event.isShiftDown()) {
					event.consume();
					if (historyPos.get() <= 1) {
						chatBox.setText("");
						return;
					}
					historyPos.set(historyPos.get() - 1);
					return;
				} else if (event.getCode().equals(KeyCode.ENTER)) {
					if (event.isShiftDown() ^ event.isControlDown())
						chatBox.appendText("\n");
					if (!event.isShiftDown()) {
						event.consume();
						if (!chatBox.getText().isEmpty())
							parseInput(chatBox.getText());
						else
							emptyMessageWarning();
					}
				} else if (event.getCode().equals(KeyCode.LEFT)) {
					if (event.isShiftDown()) {
						if (event.isControlDown())
							chatBox.selectPositionCaret(0);
						else if (event.isAltDown())
							chatBox.selectPreviousWord();
						else
							chatBox.positionCaret(0);
						event.consume();
					}
				} else if (event.getCode().equals(KeyCode.RIGHT)) {
					if (event.isShiftDown()) {
						if (event.isControlDown())
							chatBox.selectPositionCaret(chatBox.getText().length());
						else if (event.isAltDown())
							chatBox.selectEndOfNextWord();
						else
							chatBox.positionCaret(chatBox.getText().length());
						event.consume();
					}

				} else if (event.getCode().equals(KeyCode.D))
					if (event.isAltDown() && event.isControlDown() && event.isShiftDown())
						chatPane.getChildren().clear();
					else if (event.isAltDown() && event.isControlDown())
						chatBox.setText("");
					else if (event.isControlDown()) {
						if (chatBox.getText().isEmpty())
							return;

						while (chatBox.deleteNextChar())
							;
						event.consume();

					} else if (event.isAltDown()) {
						if (chatBox.getText().isEmpty())
							return;
						while (chatBox.deletePreviousChar())
							;
						event.consume();
					}
				if (event.getText().length() == 1) {
					historyPos.removeListener(listener);
					historyPos.set(0);
					historyPos.addListener(listener);
				}

			}
		});

		try {
			if (canCreateServer() && Kröw.getProgramSettings().isChatRoomHostServer()) {
				println("Starting a server...", NOTIFICATION_COLOR);
				createServer();
				println("Created a server successfully!", SUCCESS_COLOR);
			} else {
				print("Please use ", ERROR_COLOR);
				print("/connect ", Color.RED);
				println("to connect to a server.", ERROR_COLOR);
			}

		} catch (final IOException e) {
			e.printStackTrace();
		}

		GUIHelper.addDefaultSettings(GUIHelper.buildMenu(pane));
		GUIHelper.applyShapeBackground(pane, chatPane, chatBox);
	}

	@Override
	protected void onPageSwitched() {
		if (client != null) {
			client.removeListener(listener);
			client.closeConnection();
		}
		if (server != null)
			try {
				server.stop();
			} catch (final IOException e) {
				e.printStackTrace();
			}
	}

	private void parseCommand(String cmd, final String[] args) {
		final CommandMessage msg = new CommandMessage(cmd, args);

		if (cmd.startsWith("/")) {
			if (args != null)
				for (final String s : args)
					cmd += " " + s;
			sendMessage(cmd);
			sendingMessageNotification();
			return;
		}

		if (cmd.equalsIgnoreCase("setname") || cmd.equalsIgnoreCase("set-name"))
			if (args == null || args.length == 0 || args[0].trim().isEmpty())
				printLineToConsole("Command usage: /setname (name)", Color.RED);
			else {
				user = args[0];
				printLineToConsole("Your name has been changed to: " + user, Color.AQUA);
				send(msg);
			}
		else if (cmd.equalsIgnoreCase("start-server") || cmd.equalsIgnoreCase("startserver")) {
			if (!canCreateServer()) {
				println("The server already exists!", ERROR_COLOR);
				print("Use ", ERROR_COLOR);
				print("/stop-server ", Color.RED);
				println("to stop it.", ERROR_COLOR);
			} else
				try {
					if (args != null && args.length > 0)
						createServer(Short.parseShort(args[0]));
					else
						createServer();
					println("The server was created successfully!", SUCCESS_COLOR);
				} catch (final IOException e) {
					println("The server could not be created! (Perhaps the port number is taken :(  )", ERROR_COLOR);
					e.printStackTrace();
				} catch (final NumberFormatException e) {
					println("The port number must not exceed 65535 or precede 1. It also must be a valid port number.",
							ERROR_COLOR);
				}

		} else if (cmd.equalsIgnoreCase("stopserver") || cmd.equalsIgnoreCase("stop-server")) {
			if (isServerOpen()) {
				closeServer();
				println("The server is being closed...", SUCCESS_COLOR);
			} else {
				println("The server wasn't open...", ERROR_COLOR);
				print("Use ", ERROR_COLOR);
				print("/start-server ", Color.RED);
				println("to start it.", ERROR_COLOR);
			}

		} else if (cmd.equalsIgnoreCase("help")) {

			// This removes redundant calls to multiple print commands.
			// (Especially printing " - " repeatedly.)
			//
			// Instead we can call this Task's execute method with the name of
			// the command to show in the help menu, and the description of the
			// command.
			final VarArgsTask<String> showHelp = params -> {
				if (params.length < 2)
					try {
						throw new Exception("Invalid args");
					} catch (final Exception e) {
						e.printStackTrace();
						return;
					}
				print(params[0], Color.CRIMSON);
				print(" - ");
				println(params[1], Color.DEEPSKYBLUE);
			};

			println("Showing pg. 1 for help.", Color.BISQUE);
			println("{Necessary Information} - Whatever is inside braces must be given by the user when entering the command.",
					Color.MEDIUMPURPLE);
			println("[Unnecessary Information] - Whatever is inside brackets does not need to be given by the user when entering the command.",
					Color.MEDIUMPURPLE);
			println("(Data Type) - Whatever follows parentheses must be of the type specified inside the parentheses.",
					Color.MEDIUMPURPLE);

			println();
			println();
			println();

			showHelp.execute("set-name {name}", "Set's your name. This method takes effect across chats.");
			showHelp.execute("start-server [(Integer) port]", "Starts a server if one has not already been started.");
			showHelp.execute("stop-server", "Stops the running server... If it's running, that is.");
			showHelp.execute("help [(Integer) page number]", "Shows command help [at the specified page.]");
			showHelp.execute("connect {(Text) server address} [(Integer) port]",
					"Connects to the specified server if you're not already connected to one. The port is optional, and defaults to 25000.");
			showHelp.execute("disconnect", "Disconnects from a server, if you are connected to one.");
			showHelp.execute("clear-screen", "Clears the screen; removes all the messages being displayed.");

			send(msg);
			return;
		} else if (cmd.equalsIgnoreCase("connect")) {
			if (args == null || args.length == 0 || args.length > 2) {
				println("Usage: /connect (address) [port]", ERROR_COLOR);
				return;
			}
			if (client != null) {
				println("You are already connected to a server...", ERROR_COLOR);
				return;
			}
			if (args.length == 1) {
				final String[] strings = args[0].split(":");
				if (strings.length < 2)
					try {
						println("Attempting to connect using the default port...", NOTIFICATION_COLOR);
						setClient(args[0], 25000);

						print("Connected to server ", SUCCESS_COLOR);
						print(args[0] + ":" + 25000, Color.DARKGREEN);
						println(" successfully.", SUCCESS_COLOR);
					} catch (final IOException e) {
						print("Couldn't connect to ", ERROR_COLOR);
						print(args[0], Color.BLUEVIOLET);
						println(".", ERROR_COLOR);
					}
				else
					try {
						println("Attempting to connect...", NOTIFICATION_COLOR);
						setClient(strings[0], strings[1]);
						print("Connected to server ", SUCCESS_COLOR);
						print(strings[0] + ":" + strings[1], Color.DARKGREEN);
						println(" successfully.", SUCCESS_COLOR);
					} catch (final IOException e) {
						println("An error ocurred while connecting to the server.", ERROR_COLOR);
					} catch (final NumberFormatException e) {
						print("Could not parse the numerical value of ", ERROR_COLOR);
						print(strings[1], Color.BLUEVIOLET);
						println(".", ERROR_COLOR);
					}
			} else

				try {
					println("Attempting to connect...", NOTIFICATION_COLOR);
					setClient(args[0], args[1]);
					print("Connected to server ", SUCCESS_COLOR);
					print(args[0] + ":" + args[1], Color.DARKGREEN);
					println(" successfully.", SUCCESS_COLOR);
				} catch (final IOException e) {
					println("An error ocurred while connecting to the server.", ERROR_COLOR);
				} catch (final NumberFormatException e) {
					print("Could not parse the numerical value of ", ERROR_COLOR);
					print(args[1], Color.BLUEVIOLET);
					println(".", ERROR_COLOR);
				}

		} else if (cmd.equalsIgnoreCase("disconnect")) {
			if (client == null) {
				println("You are not connected to a server.", WARNING_COLOR);
				if (server != null)
					println("\tYou are hosting one though...", WARNING_COLOR);
			} else {
				println("Attempting to disconnect...", NOTIFICATION_COLOR);
				if (server != null)
					println("\tYou're still hosting a server though...", WARNING_COLOR);
				try {
					send(msg);
					client.closeConnection();
					client = null;
				} catch (final Exception e) {
					println("Failed to close the connection...", ERROR_COLOR);
					return;
				}
				println("The connection was closed successfully...", SUCCESS_COLOR);
			}
		} else if (cmd.equalsIgnoreCase("cls") || cmd.equalsIgnoreCase("clear-screen")) {
			chatPane.getChildren().clear();
		} else
			WindowManager.spawnLabelAtMousePos("Unknown Command", ERROR_COLOR);

	}

	public void parseInput(String input) {
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

			if (history.isEmpty() || !input.equals(history.peek().trim()))
				history.add(chatBox.getText());

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
			parseCommand(cmd, args);
		} else {
			sendMessage(input);
			sendingMessageNotification();
			if (history.isEmpty() || !input.equals(history.peek()))
				history.add(chatBox.getText());
		}
		chatBox.setText("");
	}

	public void print(final String text) {
		printToConsole(text);
	}

	public void print(final String text, final Color color) {
		printToConsole(text, color);
	}

	public void printLineToConsole(final String text) {
		printLineToConsole(text, Color.WHITE);
	}

	public void printLineToConsole(final String line, final Color color) {
		printToConsole(line + "\n", color);
	}

	public void println() {
		printLineToConsole("");
	}

	public void println(final String text) {
		printLineToConsole(text);
	}

	public void println(final String text, final Color color) {
		printLineToConsole(text, color);
	}

	public void printNode(final Node node) {
		chatPane.getChildren().add(node);
	}

	public void printToConsole(final String text) {
		printToConsole(text, Color.WHITE);
	}

	public void printToConsole(final String text, final Color color) {
		final Text t = new Text(text);
		t.setFill(color);
		chatPane.getChildren().add(t);
	}

	private void send(final Message message) {
		if (client != null)
			try {
				client.sendMessage(message);
			} catch (final IOException e) {
				e.printStackTrace();
			}
	}

	private void sendingMessageNotification() {
		WindowManager.spawnLabelAtMousePos("Sending...", SUCCESS_COLOR);
	}

	private void sendMessage(final String message) {
		try {
			if (client != null)
				client.sendMessage(new ChatRoomMessage(message, user == null ? "" : user, new Date().getTime()));
			else
				println("You can't send messages without being connected to a server! See /help for details...",
						ERROR_COLOR);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private boolean setClient(final String hostname, final int port) throws UnknownHostException, IOException {
		if (client != null)
			return false;
		client = new Client(hostname, port);
		client.addListener(listener);
		return true;
	}

	private boolean setClient(final String hostname, final String port)
			throws NumberFormatException, UnknownHostException, IOException {
		if (client != null)
			return false;
		client = new Client(hostname, Short.parseShort(port));
		client.addListener(listener);
		return true;
	}

}
