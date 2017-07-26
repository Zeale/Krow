package zeale.guis;

import javafx.scene.paint.Color;
import kröw.libs.guis.Window;

public class Settings extends Window {

	@Override
	public String getWindowFile() {
		return "Settings.fxml";
	}

	@Override
	public void initialize() {
		Window.getStage().getScene().setFill(Color.TRANSPARENT);
		Window.getStage().setFullScreen(true);
	}

}