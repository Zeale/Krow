package zeale.guis;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import kröw.core.Kröw;
import kröw.libs.guis.Window;

public class Settings extends Window {

	@FXML
	private Label labelSettings;

	@Override
	public String getWindowFile() {
		return "Settings.fxml";
	}

	@Override
	public void initialize() {
		Window.getStage().getScene().setFill(Color.TRANSPARENT);
		Window.getStage().setFullScreen(true);
		labelSettings.setLayoutX((double) 265 / 1920 * Kröw.SCREEN_WIDTH);
		labelSettings.setLayoutY((double) 160 / 1080 * Kröw.SCREEN_HEIGHT);
	}

}