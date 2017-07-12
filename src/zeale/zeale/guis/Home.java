package zeale.guis;

import java.util.Random;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import kröw.core.Kröw;
import wolf.zeale.guis.Window;

public class Home extends Window {

	private static final ImageView BLANK_IMAGE = new ImageView();

	private static int IMAGE_WIDTH = (int) ((double) 100 / 1920 * Kröw.SCREEN_WIDTH),
			IMAGE_HEIGHT = (int) ((double) 100 / 1080 * Kröw.SCREEN_HEIGHT), IMAGE_SPACING = IMAGE_WIDTH / 2;

	static {
		BLANK_IMAGE.setFitHeight(IMAGE_HEIGHT);
		BLANK_IMAGE.setFitWidth(IMAGE_WIDTH);
	}

	@Override
	public String getWindowFile() {
		return "Home.fxml";
	}

	@FXML
	private HBox horizontalScroll;
	@FXML
	private VBox verticalScroll;

	private Item[] views = new Item[14];

	@FXML
	@Override
	public void initialize() {

		// TODO Actually implement a vertical bar.
		verticalScroll.setVisible(false);

		horizontalScroll.setStyle(
				"-fx-background-color:  linear-gradient(to right, #00000020 0%, #000000A8 45%, #000000A8 55%, #00000020 100%);");

		for (byte i = 0; i < 14; i++) {
			ImageView img = new ImageView(new Random().nextBoolean()
					? new Random().nextBoolean() ? Kröw.IMAGE_DARK_CROW : Kröw.IMAGE_LIGHT_CROW : Kröw.IMAGE_KRÖW);

			TranslateTransition transition = new TranslateTransition(Duration.seconds(1), img);

			Item item = new Item(img, transition);

			views[i] = item;

			transition.setOnFinished(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					// TODO Check if gone off screen
					item.goal -= IMAGE_WIDTH + IMAGE_SPACING;
					if (item.goal == 0)
						return;// Wait for scroll
					if (item.goal > 0)
						transition.setByX(IMAGE_WIDTH + IMAGE_SPACING);
					else
						transition.setByX(-(IMAGE_WIDTH + IMAGE_SPACING));
				}
			});

		}

		horizontalScroll.setPrefWidth(Kröw.SCREEN_WIDTH);
		horizontalScroll.setPrefHeight(IMAGE_HEIGHT);
		verticalScroll.setPrefHeight(Kröw.SCREEN_HEIGHT);
		verticalScroll.setPrefWidth(IMAGE_WIDTH);

		horizontalScroll.setLayoutX(0);
		horizontalScroll.setLayoutY(Kröw.SCREEN_HEIGHT / 2 - horizontalScroll.getPrefHeight() / 2);
		verticalScroll.setLayoutY(0);
		verticalScroll.setLayoutX(Kröw.SCREEN_WIDTH / 2 - verticalScroll.getPrefWidth() / 2);

		for (Item v : views) {
			v.image.setFitWidth(IMAGE_WIDTH);
			v.image.setFitHeight(IMAGE_HEIGHT);
			horizontalScroll.getChildren().add(v.image);
		}

		horizontalScroll.setSpacing(IMAGE_SPACING);

		horizontalScroll.setOnScroll(new EventHandler<ScrollEvent>() {

			@Override
			public void handle(ScrollEvent event) {
				double amount = event.getDeltaY() / event.getMultiplierY();
				if (amount == 0)
					return;
				boolean forward = amount > 0;
				// TODO Check if we'll go off the screen.
				for (Item i : views) {
					i.goal += amount * (IMAGE_SPACING + IMAGE_WIDTH);
					if (forward)
						i.transition.setByX(IMAGE_WIDTH + IMAGE_SPACING);
					else
						i.transition.setByX(-(IMAGE_WIDTH + IMAGE_SPACING));
					i.transition.play();
				}
			}
		});
		horizontalScroll.toFront();
	}

	private class Item {
		public final ImageView image;
		public final TranslateTransition transition;
		public double goal;

		public Item(ImageView image, TranslateTransition transition) {
			this.image = image;
			this.transition = transition;
		}

	}

	public Home() {
	}

}
