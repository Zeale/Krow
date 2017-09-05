package zeale.guis.gamehub;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
	private FlowPane menu;

	private static final double MENUITEM_WIDTH = 350, MENUITEM_HEIGHT = 350;

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

		menu.setLayoutX(0);
		menu.setLayoutY(0);
		menu.setPrefWidth(Kröw.getSystemProperties().getScreenWidth());
		menu.setPrefHeight(Kröw.getSystemProperties().getScreenHeight());

		bgVidPlayer.setLayoutX(0);
		bgVidPlayer.setLayoutY(0);
		bgVidPlayer.setFitWidth(Kröw.getSystemProperties().getScreenWidth());
		bgVidPlayer.setFitHeight(Kröw.getSystemProperties().getScreenHeight());

		showRandomBGVideo();

		addMenuItem(new MenuItem("Games", new ImageView("/krow/resources/games.png"), event -> {
			// TODO Auto-generated method stub
		}));

	}

	public class MenuItem extends Button {

		private static final int GRAPHIC_TEXT_SPACING = 20;

		{
			setWrapText(true);
			setPrefSize(Kröw.scaleWidth(MENUITEM_WIDTH), Kröw.scaleHeight(MENUITEM_HEIGHT));
			setGraphicTextGap(Kröw.scaleHeight(GRAPHIC_TEXT_SPACING));
		}

		public MenuItem(String text, ImageView graphic, EventHandler<MouseEvent> onClicked) {
			super(text, graphic);

			addEventHandler(MouseEvent.MOUSE_PRESSED, onClicked);
		}

	}

	public void addMenuItem(MenuItem menuItem) {
		menu.getChildren().add(menuItem);
	}

	private void showRandomBGVideo() {
		// TODO Display a random video in the background.
	}

}
