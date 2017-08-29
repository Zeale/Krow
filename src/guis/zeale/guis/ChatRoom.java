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
import kr�w.app.api.connections.Client;
import kr�w.app.api.connections.ClientListener;
import kr�w.app.api.connections.Server;
import kr�w.core.Kr�w;
import kr�w.core.managers.WindowManager;
import kr�w.core.managers.WindowManager.Page;

public class ChatRoom extends WindowManager.Page {

	private final static double CHAT_PANE_PREF_WIDTH = 1277, CHAT_PANE_PREF_HEIGHT = 827, CHAT_PANE_LAYOUTX = 305,
			CHAT_PANE_LAYOUTY = 14;
	private final static double CHAT_BOX_PREF_WIDTH = 1920, CHAT_BOX_PREF_HEIGHT = 220, CHAT_BOX_LAYOUTX = 0,
			CHAT_BOX_LAYOUTY = 860;
	private final static double SEND_BUTTON_PREF_WIDTH = 55, SEND_BUTTON_PREF_HEIGHT = 24, SEND_BUTTON_LAYOUTX = 1851,
			SEND_BUTTON_LAYOUTY = 958;

	private static Client client;

	private static Server server;
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
			server.stop();
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
			if (server == null)
				server = new Server(25565);
			if (client == null) {
				client = new Client("dusttoash.org", 25565);
				client.addListener(listener);
			}

		} catch (final IOException e) {
			e.printStackTrace();
		}

		GUIHelper.addDefaultSettings(GUIHelper.buildMenu(pane));
		GUIHelper.applyShapeBackground(pane, chatPane, chatBox);
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

	public void printTextToConsole(String text, Color color) {
		Text t = new Text(text + "\n");
		t.setFill(color);
		chatPane.getChildren().add(t);
	}

	public void printTextToConsole(String text) {
		printTextToConsole(text, Color.WHITE);
	}

	private void parseCommand(String cmd, String[] args) {
		if (cmd.equalsIgnoreCase("setname") || cmd.equalsIgnoreCase("set-name"))
			if (args == null || args.length == 0 || args[0].trim().isEmpty())
				printTextToConsole("Command usage: /setname (name)", Color.RED);
			else {
				user = args[0];
				printTextToConsole("Your name has been changed to: " + user, Color.AQUA);
			}
		else
			WindowManager.spawnLabelAtMousePos("Unknown Command", Color.FIREBRICK);

	}

	private static final Object TEXT_FONT_SIZE_KEY = new Object(), TEXT_FONT_FAMILY_KEY = new Object();

	private void sendingMessageNotification() {
		WindowManager.spawnLabelAtMousePos("Sending...", Color.GREEN);
	}

	private void sendMessage(final String message) {
		try {
			client.sendMessage(new ChatRoomMessage(message, user == null ? "" : user, new Date().getTime()));
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
