package zeale.guis.games;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import kr�w.core.managers.WindowManager.Page;

public class GameWindow extends Page {

	@FXML
	private AnchorPane bgpane, coverpane;

	@Override
	public String getWindowFile() {
		return "GameWindow.fxml";
	}

}
