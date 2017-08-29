package zeale.guis;

import java.io.IOException;
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
import kröw.annotations.AutoLoad;
import kröw.annotations.LoadTime;
import kröw.app.api.connections.Client;
import kröw.app.api.connections.ClientListener;
import kröw.app.api.connections.Server;
import kröw.core.Kröw;
import kröw.core.managers.WindowManager;
import kröw.core.managers.WindowManager.Page;

public class ChatRoom extends WindowManager.Page {

	private static final boolean canHostServer;

	static {
		boolean chs = true;
		try {
			Kröw.addReflectionClass(ChatRoom.class);
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
						.add(new Text("Message unreceived!" + (Kröw.DEBUG_MODE ? object.toString() : "") + "\n"));
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
				name.setFill(Color.FIREBRICK);
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
		WindowManager.spawnLabelAtMousePos("Empty message...", Color.FIREBRICK);
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
			if (canCreateServer() && Kröw.getProgramSettings().isChatRoomHostServer()) {
				createServer();
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
				println("The server already exists!", Color.FIREBRICK);
				print("Use ", Color.FIREBRICK);
				print("/stop-server ", Color.RED);
				println("to stop it.", Color.FIREBRICK);
			} else
				try {
					if (args != null && args.length > 0)
						createServer(Short.parseShort(args[0]));
					else
						createServer();
					println("The server was created successfully!", Color.GREEN);
				} catch (IOException e) {
					println("The server could not be created! (Perhaps the port number is taken :(  )",
							Color.FIREBRICK);
					e.printStackTrace();
				} catch (NumberFormatException e) {
					println("the port number must not exceed 65535 or precede 1.", Color.FIREBRICK);
				}

		} else if (cmd.equalsIgnoreCase("stopserver") || cmd.equalsIgnoreCase("stop-server")) {
			if (isServerOpen()) {
				closeServer();
				println("The server is being closed...", Color.GREEN);
			} else {
				println("The server wasn't open...", Color.FIREBRICK);
				print("Use ", Color.FIREBRICK);
				print("/start-server ", Color.RED);
				println("to start it.", Color.FIREBRICK);
			}

		} else if (cmd.equalsIgnoreCase("help")) {

		} else if (cmd.equalsIgnoreCase("connect")) {
			if (args == null || args.length == 0)
				println("Usage: /connect (address) [port]", Color.FIREBRICK);
		} else
			WindowManager.spawnLabelAtMousePos("Unknown Command", Color.FIREBRICK);

	}

	private static final Object TEXT_FONT_SIZE_KEY = new Object(), TEXT_FONT_FAMILY_KEY = new Object();

	private void sendingMessageNotification() {
		WindowManager.spawnLabelAtMousePos("Sending...", Color.GREEN);
	}

	private void sendMessage(final String message) {
		try {
			if (client != null)
				client.sendMessage(new ChatRoomMessage(message, user == null ? "" : user, new Date().getTime()));
			else
				println("You can't send messages without being connected to a server! See /help for details...",
						Color.FIREBRICK);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
