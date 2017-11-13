package zeale.guis;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import kröw.core.managers.WindowManager.Page;

public class BackgroundModule extends Page {

	@FXML
	private AnchorPane root, visualizerPane;

	@Override
	public String getWindowFile() {
		return "BackgroundModule.fxml";
	}

}
