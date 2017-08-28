package zeale.guis;

import java.io.IOException;
import java.util.Stack;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import krow.guis.GUIHelper;
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

	private Stack<String> messageQueue = new Stack<>();

	private static Client client;
	private static Server server;

	private Thread sendThread = new Thread(new Runnable() {

		@Override
		public void run() {
			while (!messageQueue.isEmpty()) {

			}
		}
	});

	@Override
	public String getWindowFile() {
		return "ChatRoom.fxml";
	}

	@FXML
	private TextFlow chatPane;
	@FXML
	private TextArea chatBox;

	@FXML
	private AnchorPane pane;
	@FXML
	private Button sendButton;

	private void emptyMessageWarning() {
		WindowManager.spawnLabelAtMousePos("Empty message...", Color.FIREBRICK);
	}

	private void sendingMessageNotification() {
		WindowManager.spawnLabelAtMousePos("Sending...", Color.GREEN);
	}

	@Override
	public void initialize() {

		chatPane.setPrefSize(Kröw.scaleWidth(CHAT_PANE_PREF_WIDTH), Kröw.scaleHeight(CHAT_PANE_PREF_HEIGHT));
		chatPane.setLayoutX(Kröw.scaleWidth(CHAT_PANE_LAYOUTX));
		chatPane.setLayoutY(Kröw.scaleHeight(CHAT_PANE_LAYOUTY));

		System.out.println(Kröw.scaleHeight(CHAT_BOX_LAYOUTY));
		System.out.println(CHAT_BOX_LAYOUTY);
		System.out.println(Kröw.getSystemProperties().getScreenHeight());

		chatBox.setPrefSize(Kröw.scaleWidth(CHAT_BOX_PREF_WIDTH), Kröw.scaleHeight(CHAT_BOX_PREF_HEIGHT));
		chatBox.setLayoutX(Kröw.scaleWidth(CHAT_BOX_LAYOUTX));
		chatBox.setLayoutY(Kröw.scaleHeight(CHAT_BOX_LAYOUTY));

		sendButton.setPrefSize(Kröw.scaleWidth(SEND_BUTTON_PREF_WIDTH), Kröw.scaleHeight(SEND_BUTTON_PREF_HEIGHT));
		sendButton.setLayoutX(Kröw.scaleWidth(SEND_BUTTON_LAYOUTX));
		sendButton.setLayoutY(Kröw.scaleHeight(SEND_BUTTON_LAYOUTY));

		sendButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (!chatBox.getText().isEmpty()) {
					sendMessage(chatBox.getText());
					sendingMessageNotification();
				} else
					emptyMessageWarning();
			}
		});

		chatBox.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
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
			}
		});

		chatPane.getChildren().addListener(new ListChangeListener<Node>() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see javafx.collections.ListChangeListener#onChanged(javafx.
			 * collections.ListChangeListener.Change)
			 */
			@Override
			public void onChanged(Change<? extends Node> c) {
				while (c.next())
					if (c.wasAdded())
						for (Node n : c.getAddedSubList())
							if (n instanceof Text) {

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

		} catch (IOException e) {
			e.printStackTrace();
		}

		GUIHelper.addDefaultSettings(GUIHelper.buildMenu(pane));
		GUIHelper.applyShapeBackground(pane, chatPane, chatBox);
	}

	private void sendMessage(String message) {
		chatBox.setText("");
		try {
			client.sendMessage(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private ClientListener listener = new ClientListener() {

		@Override
		public void objectReceived(Object object) {
			System.out.println("Client: Received object");
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					Text t = new Text(object.toString());
					t.setFill(Color.WHITE);
					chatPane.getChildren().add(t);
				}
			});
		}
	};

	@Override
	public boolean canSwitchScenes(Class<? extends Page> newSceneClass) {
		client.removeListener(listener);
		client.closeConnection();
		if (server != null)
			server.stop();
		return true;
	}

}
