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
import kr�w.core.Kr�w;
import kr�w.core.managers.WindowManager.Page;

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
		background.setPrefWidth(Kr�w.getSystemProperties().getScreenWidth());
		background.setPrefHeight(Kr�w.getSystemProperties().getScreenHeight());

		menu.setLayoutX(0);
		menu.setLayoutY(0);
		menu.setPrefWidth(Kr�w.getSystemProperties().getScreenWidth());
		menu.setPrefHeight(Kr�w.getSystemProperties().getScreenHeight());

		bgVidPlayer.setLayoutX(0);
		bgVidPlayer.setLayoutY(0);
		bgVidPlayer.setFitWidth(Kr�w.getSystemProperties().getScreenWidth());
		bgVidPlayer.setFitHeight(Kr�w.getSystemProperties().getScreenHeight());

		showRandomBGVideo();

		addMenuItem(new MenuItem("Games", new ImageView("/krow/resources/games.png"), event -> {
			// TODO Auto-generated method stub
		}));

	}

	public class MenuItem extends Button {

		private static final int GRAPHIC_TEXT_SPACING = 20;

		{
			setWrapText(true);
			setPrefSize(Kr�w.scaleWidth(MENUITEM_WIDTH), Kr�w.scaleHeight(MENUITEM_HEIGHT));
			setGraphicTextGap(Kr�w.scaleHeight(GRAPHIC_TEXT_SPACING));
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
