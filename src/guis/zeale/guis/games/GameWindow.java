package zeale.guis.games;

import java.util.Random;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import kr�w.core.Kr�w;
import kr�w.core.managers.WindowManager.Page;

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
		bgpane.setPrefWidth(Kr�w.getSystemProperties().getScreenWidth());
		bgpane.setPrefHeight(Kr�w.getSystemProperties().getScreenHeight());
		bgpane.setLayoutX(0);
		bgpane.setLayoutY(0);

		coverpane.setPrefWidth(Kr�w.getSystemProperties().getScreenWidth());
		coverpane.setPrefHeight(Kr�w.getSystemProperties().getScreenHeight());
		coverpane.setLayoutX(0);
		coverpane.setLayoutY(0);

		bg.setFitWidth(Kr�w.getSystemProperties().getScreenWidth());
		bg.setFitHeight(Kr�w.getSystemProperties().getScreenHeight());
		bg.setLayoutX(0);
		bg.setLayoutY(0);

		bg.setMediaPlayer(new MediaPlayer(
				new Media(MEDIA_FOLDER + new Random().nextInt(2) + ".mp4")));
	}

}
