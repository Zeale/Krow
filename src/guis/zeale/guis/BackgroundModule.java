package zeale.guis;

import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.layout.AnchorPane;
import krow.guis.GUIHelper;
import krow.guis.GUIHelper.MenuBox;
import kröw.core.managers.WindowManager.Page;

public class BackgroundModule extends Page {

	@FXML
	private AnchorPane root, visualizerPane;
	@FXML
	private Accordion backgroundMenu;
	@FXML
	private MenuBox sideMenu;

	@Override
	public void initialize() {
		sideMenu = GUIHelper.buildMenu(root);
		GUIHelper.addDefaultSettings(sideMenu);
		
	}

	@FXML
	private void showMenu() {
		if(sideMenu.isVisible()){
			sideMenu.setVisible(false);
			backgroundMenu.setVisible(true);
		}else{
			sideMenu.setVisible(true);
			backgroundMenu.setVisible(false);
		}
	}

	@Override
	public String getWindowFile() {
		return "BackgroundModule.fxml";
	}

}
