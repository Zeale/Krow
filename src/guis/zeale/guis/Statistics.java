package zeale.guis;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import krow.guis.GUIHelper;
import kr�w.core.Kr�w;
import kr�w.core.managers.WindowManager;

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

		searchBar.setPrefWidth(Kr�w.scaleWidth(SEARCH_BAR_WIDTH));
		searchBar.setPrefHeight(Kr�w.scaleHeight(SEARCH_BAR_HEIGHT));
		searchBar.setLayoutX(Kr�w.scaleWidth(SEARCH_BAR_LAYOUT_X));
		searchBar.setLayoutY(Kr�w.scaleHeight(SEARCH_BAR_LAYOUT_Y));

		searchList.setPrefWidth(Kr�w.scaleWidth(SEARCH_LIST_WIDTH));
		searchList.setPrefHeight(Kr�w.scaleHeight(SEARCH_LIST_HEIGHT));
		searchList.setLayoutX(Kr�w.scaleWidth(SEARCH_LIST_LAYOUT_X));
		searchList.setLayoutY(Kr�w.scaleHeight(SEARCH_LIST_LAYOUT_Y));

		GUIHelper.applyShapeBackground(pane);
	}

	@Override
	public String getWindowFile() {
		return "Statistics.fxml";
	}

}
