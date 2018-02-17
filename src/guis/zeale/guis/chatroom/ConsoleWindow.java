package zeale.guis.chatroom;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import kröw.gui.Application;

public class ConsoleWindow extends Application {
	@FXML
	protected TextFlow chatPane;
	@FXML
	protected TextArea chatBox;

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

	protected void sendTextMessage(String text) {

	}
}
