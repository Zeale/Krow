package zeale.guis;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import krow.guis.GUIHelper;
import kröw.core.Kröw;
import kröw.core.managers.WindowManager;

public class Statistics extends WindowManager.Page {

	@FXML
	private AnchorPane pane;

	@FXML
	private TextField searchBar;

	@FXML
	private ListView<Object> searchList;

	private static final double SEARCH_BAR_WIDTH = 1241, SEARCH_BAR_HEIGHT = 44;
	private static final double SEARCH_BAR_LAYOUT_X = 361, SEARCH_BAR_LAYOUT_Y = 24;

	private static final double SEARCH_LIST_WIDTH = 1918, SEARCH_LIST_HEIGHT = 941;
	private static final double SEARCH_LIST_LAYOUT_X = 22, SEARCH_LIST_LAYOUT_Y = 118;

	@Override
	public void initialize() {
		GUIHelper.addDefaultSettings(GUIHelper.buildMenu(pane));

		searchBar.setPrefWidth(Kröw.scaleWidth(SEARCH_BAR_WIDTH));
		searchBar.setPrefHeight(Kröw.scaleHeight(SEARCH_BAR_HEIGHT));
		searchBar.setLayoutX(Kröw.scaleWidth(SEARCH_BAR_LAYOUT_X));
		searchBar.setLayoutY(Kröw.scaleHeight(SEARCH_BAR_LAYOUT_Y));

		searchList.setPrefWidth(Kröw.scaleWidth(SEARCH_LIST_WIDTH));
		searchList.setPrefHeight(Kröw.scaleHeight(SEARCH_LIST_HEIGHT));
		searchList.setLayoutX(Kröw.scaleWidth(SEARCH_LIST_LAYOUT_X));
		searchList.setLayoutY(Kröw.scaleHeight(SEARCH_LIST_LAYOUT_Y));

		GUIHelper.applyShapeBackground(pane);
	}

	@Override
	public String getWindowFile() {
		return "Statistics.fxml";
	}

}
