package zeale.guis;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import kröw.libs.guis.Window;

public class Tools extends Window {

	@FXML
	private Pane pane, subPane;

	@Override
	public String getWindowFile() {
		return "Tools.fxml";
	}

	@Override
	public String getWindowName() {
		return "Tools";
	}

	@Override
	public void initialize() {
		GUIHelper.addDefaultSettings(GUIHelper.buildCloseButton(pane));
	}

}
