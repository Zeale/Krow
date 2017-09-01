package zeale.guis.games;

import java.util.Random;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import kröw.core.Kröw;
import kröw.core.managers.WindowManager.Page;

public class GameWindow extends Page {
	
	@FXML
	private static final String MEDIA_FOLDER = "";

	@FXML
	private AnchorPane bgpane, coverpane;

	@FXML
	private MediaView bg;

	@Override
	public String getWindowFile() {
		return "GameWindow.fxml";
	}

	@Override
	public void initialize() {
		bgpane.setPrefWidth(Kröw.getSystemProperties().getScreenWidth());
		bgpane.setPrefHeight(Kröw.getSystemProperties().getScreenHeight());
		bgpane.setLayoutX(0);
		bgpane.setLayoutY(0);

		coverpane.setPrefWidth(Kröw.getSystemProperties().getScreenWidth());
		coverpane.setPrefHeight(Kröw.getSystemProperties().getScreenHeight());
		coverpane.setLayoutX(0);
		coverpane.setLayoutY(0);

		bg.setFitWidth(Kröw.getSystemProperties().getScreenWidth());
		bg.setFitHeight(Kröw.getSystemProperties().getScreenHeight());
		bg.setLayoutX(0);
		bg.setLayoutY(0);

		bg.setMediaPlayer(new MediaPlayer(
				new Media(MEDIA_FOLDER + new Random().nextInt(2) + ".mp4")));
	}

}
