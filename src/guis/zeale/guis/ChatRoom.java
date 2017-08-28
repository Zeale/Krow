package zeale.guis;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextFlow;
import kröw.core.managers.WindowManager;

public class ChatRoom extends WindowManager.Page {

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

}
