package zeale.guis.gamehub;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.media.MediaView;
import kröw.core.Kröw;
import kröw.core.managers.WindowManager.Page;

public class GameHub extends Page {

	@FXML
	private AnchorPane background;
	@FXML
	private MediaView bgVidPlayer;
	@FXML
	private FlowPane content;

	@Override
	public String getWindowFile() {
		return "GameHub.fxml";
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		background.setLayoutX(0);
		background.setLayoutY(0);
		background.setPrefWidth(Kröw.getSystemProperties().getScreenWidth());
		background.setPrefHeight(Kröw.getSystemProperties().getScreenHeight());

		content.setLayoutX(0);
		content.setLayoutY(0);
		content.setPrefWidth(Kröw.getSystemProperties().getScreenWidth());
		content.setPrefHeight(Kröw.getSystemProperties().getScreenHeight());

		bgVidPlayer.setLayoutX(0);
		bgVidPlayer.setLayoutY(0);
		bgVidPlayer.setFitWidth(Kröw.getSystemProperties().getScreenWidth());
		bgVidPlayer.setFitHeight(Kröw.getSystemProperties().getScreenHeight());

		showRandomBGVideo();

	}

	private void showRandomBGVideo() {
		// TODO Display a random video in the background.
	}

}
