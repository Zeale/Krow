package zeale.guis;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import kröw.core.Kröw;



class GUIHelper {
	public static void buildCloseButton(ImageView closeButton){
		closeButton.setImage(CLOSE_BUTTON_IMAGE);
		closeButton.setLayoutX(Kröw.SCREEN_WIDTH - (double) 80 / 1920 * Kröw.SCREEN_WIDTH);
		closeButton.setLayoutY((double) 80 / 1920 * Kröw.SCREEN_HEIGHT);
		closeButton.setFitWidth((double) 64 / 1920 * Kröw.SCREEN_WIDTH);
		closeButton.setFitHeight((double) 64 / 1920 * Kröw.SCREEN_HEIGHT);
		
		closeButton.setOnMousePressed(event -> Platform.exit());
	}

	public static final Image CLOSE_BUTTON_IMAGE = new Image("krow/resources/Close.png");
}
