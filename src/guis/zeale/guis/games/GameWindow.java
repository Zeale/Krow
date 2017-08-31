package zeale.guis.games;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import kröw.core.managers.WindowManager.Page;

public class GameWindow extends Page {

	@FXML
	private AnchorPane bgpane, coverpane;

	@Override
	public String getWindowFile() {
		return "GameWindow.fxml";
	}

}
