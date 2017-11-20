package zeale.guis;

import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import krow.guis.GUIHelper;
import krow.guis.GUIHelper.MenuBox;
import kr�w.core.managers.WindowManager.Page;

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
		sideMenu.getParentWrapper().setVisible(false);

		applyDefaultBackground(visualizerPane);

		Rectangle clipping = new Rectangle();

		visualizerPane.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
			clipping.setWidth(newValue.getWidth());
			clipping.setHeight(newValue.getHeight());
		});

		clipping.setMouseTransparent(true);

		visualizerPane.setClip(clipping);

	}

	@FXML
	private void showMenu() {
		if (sideMenu.getParentWrapper().isVisible()) {
			sideMenu.getParentWrapper().setVisible(false);
			backgroundMenu.setVisible(true);
		} else {
			sideMenu.getParentWrapper().setVisible(true);
			backgroundMenu.setVisible(false);
		}
	}

	@Override
	public String getWindowFile() {
		return "BackgroundModule.fxml";
	}

}