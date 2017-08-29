package zeale.guis;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import krow.guis.GUIHelper;
import krow.guis.chatroom.ChatRoomMessage;
import kr�w.annotations.AutoLoad;
import kr�w.annotations.LoadTime;
import kr�w.app.api.connections.Client;
import kr�w.app.api.connections.ClientListener;
import kr�w.app.api.connections.Server;
import kr�w.core.Kr�w;
import kr�w.core.managers.WindowManager;
import kr�w.core.managers.WindowManager.Page;

public class ChatRoom extends WindowManager.Page {

	private static final Color NOTIFICATION_COLOR = Color.LIGHTBLUE, ERROR_COLOR = Color.FIREBRICK,
			SUCCESS_COLOR = Color.GREEN, WARNING_COLOR = Color.GOLD;

	private static final boolean canHostServer;

	static {
		boolean chs = true;
		try {
			Kr�w.addReflectionClass(ChatRoom.class);
		} catch (Exception e) {
			chs = false;
		}
		canHostServer = chs;
	}

	private final static double CHAT_PANE_PREF_WIDTH = 1277, CHAT_PANE_PREF_HEIGHT = 827, CHAT_PANE_LAYOUTX = 305,
			CHAT_PANE_LAYOUTY = 14;
	private final static double CHAT_BOX_PREF_WIDTH = 1920, CHAT_BOX_PREF_HEIGHT = 220, CHAT_BOX_LAYOUTX = 0,
			CHAT_BOX_LAYOUTY = 860;
	private final static double SEND_BUTTON_PREF_WIDTH = 55, SEND_BUTTON_PREF_HEIGHT = 24, SEND_BUTTON_LAYOUTX = 1851,
			SEND_BUTTON_LAYOUTY = 958;

	private static Client client;

	private static Server server;

	public static boolean isServerOpen() {
		return server != null;
	}

	@AutoLoad(LoadTime.PROGRAM_EXIT)
	public static void closeServer() {
		if (client != null) {
			client.closeConnection();
			client = null;
		}
		client = null;
		try {
			if (server != null)
				server.stop();
		} catch (IOException e) {
			e.printStackTrace();
		}
		server = null;
	}

	@FXML
	private TextFlow chatPane;

	@FXML
	private TextArea chatBox;
	@FXML
	private AnchorPane pane;

	@FXML
	private Button sendButton;
	private final ClientListener listener = object -> {
		Platform.runLater(() -> {

			if (object instanceof ChatRoomMessage) {
				new ChatRoomText((ChatRoomMessage) object).add(chatPane);
			} else

				chatPane.getChildren()
						.add(new Text("Message unreceived!" + (Kr�w.DEBUG_MODE ? object.toString() : "") + "\n"));
		});
	};

	private static class ChatRoomText {
		private ChatRoomMessage message;

		public ChatRoomText(ChatRoomMessage message) {
			this.message = message;
		}

		public void add(TextFlow pane) {
			Text name = new Text();
			if (message.getAuthor().isEmpty()) {
				name.setText("Unnamed");
				// TODO Ask server to send color from a queue
				name.setFill(ERROR_COLOR);
			} else {
				name.setText(message.getAuthor());
				name.setFill(Color.LIGHTGOLDENRODYELLOW);
			}
			Text splitter = new Text(" > ");
			Text message = new Text(this.message.getText() + "\n");
			message.setFill(Color.WHITE);
			splitter.setFill(Color.BLUE);

			pane.getChildren().addAll(name, splitter, message);

		}

	}

	private String user;

	@Override
	public boolean canSwitchScenes(final Class<? extends Page> newSceneClass) {
		client.removeListener(listener);
		client.closeConnection();
		if (server != null)
			try {
				server.stop();
			} catch (IOException e) {
				e.printStackTrace();
			}
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

		chatPane.setPrefSize(Kr�w.scaleWidth(CHAT_PANE_PREF_WIDTH), Kr�w.scaleHeight(CHAT_PANE_PREF_HEIGHT));
		chatPane.setLayoutX(Kr�w.scaleWidth(CHAT_PANE_LAYOUTX));
		chatPane.setLayoutY(Kr�w.scaleHeight(CHAT_PANE_LAYOUTY));

		chatBox.setPrefSize(Kr�w.scaleWidth(CHAT_BOX_PREF_WIDTH), Kr�w.scaleHeight(CHAT_BOX_PREF_HEIGHT));
		chatBox.setLayoutX(Kr�w.scaleWidth(CHAT_BOX_LAYOUTX));
		chatBox.setLayoutY(Kr�w.scaleHeight(CHAT_BOX_LAYOUTY));

		sendButton.setPrefSize(Kr�w.scaleWidth(SEND_BUTTON_PREF_WIDTH), Kr�w.scaleHeight(SEND_BUTTON_PREF_HEIGHT));
		sendButton.setLayoutX(Kr�w.scaleWidth(SEND_BUTTON_LAYOUTX));
		sendButton.setLayoutY(Kr�w.scaleHeight(SEND_BUTTON_LAYOUTY));

		sendButton.setOnAction(event -> {
			if (!chatBox.getText().isEmpty()) {
				parseInput(chatBox.getText());
			} else
				emptyMessageWarning();
		});

		chatPane.getChildren().addListener(new ListChangeListener<Node>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Node> c) {
				while (c.next())
					if (c.wasAdded())
						for (Node n : c.getAddedSubList())
							if (n instanceof Text) {
								Text t = (Text) n;
								t.setFont(Font.font(16));
								if (t.getProperties().containsKey(TEXT_FONT_FAMILY_KEY))
									t.setFont(Font.font((String) t.getProperties().get(TEXT_FONT_FAMILY_KEY)));
								if (t.getProperties().containsKey(TEXT_FONT_SIZE_KEY))
									t.setFont(Font.font(t.getFont().getFamily(),
											(double) t.getProperties().get(TEXT_FONT_SIZE_KEY)));
							}

			}
		});

		chatBox.setOnKeyPressed(event -> {
			if (event.getCode().equals(KeyCode.ENTER)) {
				if (event.isShiftDown() ^ event.isControlDown())
					chatBox.appendText("\n");
				if (!event.isShiftDown()) {
					event.consume();
					if (!chatBox.getText().isEmpty()) {
						parseInput(chatBox.getText());
					} else
						emptyMessageWarning();
				}
			}
		});

		try {
			// Commented out for export
			if (canCreateServer() && Kr�w.getProgramSettings().isChatRoomHostServer()) {
				println("Starting a server and connecting to it...", NOTIFICATION_COLOR);
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

	public boolean createServer(int port) throws IOException {
		if (!canCreateServer())
			return false;
		server = new Server(port);
		client = new Client("localhost", port);
		client.addListener(listener);
		return true;
	}

	public boolean createServer() throws IOException {
		return createServer(25000);
	}

	public boolean canCreateServer() {
		if (server != null || !canHostServer || client != null)
			return false;
		return true;
	}

	public static boolean isClientConnected() {
		return client != null;
	}

	public void parseInput(String input) {

		if (input.startsWith("/")) {
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
		}
		chatBox.setText("");
	}

	public void printToConsole(String text, Color color) {
		Text t = new Text(text);
		t.setFill(color);
		chatPane.getChildren().add(t);
	}

	public void printLineToConsole(String text) {
		printLineToConsole(text, Color.WHITE);
	}

	public void println(String text) {
		printLineToConsole(text);
	}

	public void println(String text, Color color) {
		printLineToConsole(text, color);
	}

	public void println() {
		printLineToConsole("");
	}

	public void print(String text) {
		printToConsole(text);
	}

	public void print(String text, Color color) {
		printToConsole(text, color);
	}

	public void printToConsole(String text) {
		printLineToConsole(text, Color.WHITE);
	}

	public void printLineToConsole(String line, Color color) {
		printToConsole(line + "\n", color);
	}

	private void parseCommand(String cmd, String[] args) {

		if (cmd.startsWith("/")) {
			String message = cmd.substring(1);
			for (String s : args)
				message += " " + s;
			sendMessage(message);
			sendingMessageNotification();
		}

		if (cmd.equalsIgnoreCase("setname") || cmd.equalsIgnoreCase("set-name"))
			if (args == null || args.length == 0 || args[0].trim().isEmpty())
				printLineToConsole("Command usage: /setname (name)", Color.RED);
			else {
				user = args[0];
				printLineToConsole("Your name has been changed to: " + user, Color.AQUA);
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
				} catch (IOException e) {
					println("The server could not be created! (Perhaps the port number is taken :(  )", ERROR_COLOR);
					e.printStackTrace();
				} catch (NumberFormatException e) {
					println("the port number must not exceed 65535 or precede 1.", ERROR_COLOR);
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
				String[] strings = args[0].split(":");
				if (strings.length < 2) {
					try {
						println("Attempting to connect using the default port...", NOTIFICATION_COLOR);
						setClient(args[0], 25000);

						print("Connected to server ", SUCCESS_COLOR);
						print(args[0] + ":" + 25000, Color.DARKGREEN);
						print(" successfully.", SUCCESS_COLOR);
					} catch (IOException e) {
						print("Couldn't connect to ", ERROR_COLOR);
						print(args[0], Color.BLUEVIOLET);
						println(".", ERROR_COLOR);
					}
				} else {
					try {
						println("Attempting to connect...", NOTIFICATION_COLOR);
						setClient(strings[0], strings[1]);
						print("Connected to server ", SUCCESS_COLOR);
						print(strings[0] + ":" + strings[1], Color.DARKGREEN);
						print(" successfully.", SUCCESS_COLOR);
					} catch (IOException e) {
						println("An error ocurred while connecting to the server.", ERROR_COLOR);
					} catch (NumberFormatException e) {
						print("Could not parse the numerical value of ", ERROR_COLOR);
						print(strings[1], Color.BLUEVIOLET);
						println(".", ERROR_COLOR);
					}
				}
			} else

				try {
					setClient(args[0], args[1]);
				} catch (IOException e) {
					println("An error ocurred while connecting to the server.", ERROR_COLOR);
				} catch (NumberFormatException e) {
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
					client.closeConnection();
					client = null;
				} catch (Exception e) {
					println("Failed to close the connection...", ERROR_COLOR);
					return;
				}
				println("The connection was closed successfully...", SUCCESS_COLOR);
			}
		} else
			WindowManager.spawnLabelAtMousePos("Unknown Command", ERROR_COLOR);

	}

	private boolean setClient(String hostname, int port) throws UnknownHostException, IOException {
		if (client != null)
			return false;
		client = new Client(hostname, port);
		client.addListener(listener);
		return true;
	}

	private boolean setClient(String hostname, String port)
			throws NumberFormatException, UnknownHostException, IOException {
		if (client != null)
			return false;
		client = new Client(hostname, Short.parseShort(port));
		client.addListener(listener);
		return true;
	}

	private static final Object TEXT_FONT_SIZE_KEY = new Object(), TEXT_FONT_FAMILY_KEY = new Object();

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

}
