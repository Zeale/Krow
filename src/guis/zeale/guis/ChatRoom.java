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
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import krow.guis.GUIHelper;
import krow.guis.chatroom.ChatRoomMessage;
import kröw.app.api.connections.Client;
import kröw.app.api.connections.ClientListener;
import kröw.app.api.connections.Server;
import kröw.core.Kröw;
import kröw.core.managers.WindowManager;
import kröw.core.managers.WindowManager.Page;

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
			Text t;

			if (object instanceof ChatRoomMessage) {
				t = new ChatRoomText((ChatRoomMessage) object);
			} else
				t = new Text("Message unreceived!" + (Kröw.DEBUG_MODE ? object.toString() : "") + "\n");

			chatPane.getChildren().add(t);
		});
	};

	private static class ChatRoomText extends Text {
		private ChatRoomMessage message;

		public ChatRoomText(ChatRoomMessage message) {
			this.message = message;
			setText(message.getAuthor() + " > " + message.getText() + "\n");
		}

		{
			setFill(Color.WHITE);
		}

		public ChatRoomMessage getMessage() {
			return message;
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
				sendMessage(chatBox.getText());
				sendingMessageNotification();
			} else
				emptyMessageWarning();
		});

		chatBox.setOnKeyPressed(event -> {
			if (event.getCode().equals(KeyCode.ENTER)) {
				if (event.isShiftDown() ^ event.isControlDown())
					chatBox.appendText("\n");
				if (!event.isShiftDown()) {
					event.consume();
					if (!chatBox.getText().isEmpty()) {
						sendMessage(chatBox.getText());
						sendingMessageNotification();
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

	private void sendingMessageNotification() {
		WindowManager.spawnLabelAtMousePos("Sending...", Color.GREEN);
	}

	private void sendMessage(final String message) {
		chatBox.setText("");
		try {
			client.sendMessage(new ChatRoomMessage(message, user == null ? "Unnamed" : user, new Date().getTime()));
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
