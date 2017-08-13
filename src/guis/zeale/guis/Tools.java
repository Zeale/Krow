package zeale.guis;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import kröw.core.managers.WindowManager.Page;

public class Tools extends Page {

	@FXML
	private Pane pane, subPane;

	@Override
	public String getWindowFile() {
		return "Tools.fxml";
	}

	@Override
	public void initialize() {
		GUIHelper.addDefaultSettings(GUIHelper.buildCloseButton(pane));
	}

}
