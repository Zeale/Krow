package zeale.guis;

import java.net.Socket;
import java.util.Stack;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import krow.guis.GUIHelper;
import kröw.core.managers.WindowManager;

public class ChatRoom extends WindowManager.Page {

	private Stack<String> messageQueue = new Stack<>();
	private Socket socket;

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

		GUIHelper.addDefaultSettings(GUIHelper.buildMenu(pane));
		GUIHelper.applyShapeBackground(pane, chatPane, chatBox);
	}

	private void sendMessage(String message) {
		chatBox.setText("");
		chatPane.getChildren().add(new Label(message));
		// TODO Implement
	}

}
