package zeale.guis;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import krow.guis.GUIHelper;
import kröw.core.managers.WindowManager;

public class Statistics extends WindowManager.Page {

	@FXML
	private AnchorPane wrapper;
	@FXML
	private TabPane pane;

	@Override
	public void initialize() {
		GUIHelper.addDefaultSettings(GUIHelper.buildMenu(wrapper));

		// TODO Fill out

		GUIHelper.applyShapeBackground(wrapper);
	}

	@Override
	public String getWindowFile() {
		return "Statistics.fxml";
	}

}
