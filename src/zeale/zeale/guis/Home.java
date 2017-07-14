package zeale.guis;

import java.util.ArrayList;
import java.util.Random;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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

	private ArrayList<Item> views = new ArrayList<Home.Item>(10);
	private short totalShift = (short) (views.size() - 6);

	private static final Image CONSTRUCT_MENU_ICON = new Image("krow/resources/ConstructIcon_hd.png");

	@FXML
	@Override
	public void initialize() {

		// TODO Actually implement a vertical bar.
		verticalScroll.setVisible(false);

		horizontalScroll.setStyle(
				"-fx-background-color:  linear-gradient(to right, #00000020 0%, #000000A8 45%, #000000A8 55%, #00000020 100%);");

		for (int i = 0; i < 10; i++) {
			ImageView img = new ImageView(new Random().nextBoolean()
					? new Random().nextBoolean() ? Kröw.IMAGE_DARK_CROW : Kröw.IMAGE_LIGHT_CROW
					: new Random().nextBoolean() ? Kröw.IMAGE_KRÖW : CONSTRUCT_MENU_ICON);

			TranslateTransition transition = new TranslateTransition(Duration.seconds(1), img);

			Item item = new Item(img, transition);

			views.add(item);
		}
		totalShift = (short) (views.size() - 6);

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

				double amount = event.getDeltaY() / event.getMultiplierY() > 0 ? 1 : -1;
				if (amount + totalShift > views.size()) {
					amount = views.size() - totalShift;
					totalShift = (short) views.size();
					return;// Omit below loop; it's useless.
				} else if (amount + totalShift < 1) {
					totalShift = 1;
					return;// Omit below loop; it's useless.
				} else {
					totalShift += amount;
				}
				for (; amount != 0; amount -= amount > 0 ? 1 : -1)
					animate(amount > 0);
			}
		});

		horizontalScroll.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ESCAPE)
					Platform.exit();
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

	public void animate(boolean forward) {
		if (forward)
			for (Item i : views) {
				i.goal++;
				i.transition.stop();
				i.transition.setToX(i.goal * (IMAGE_SPACING + IMAGE_WIDTH));
				i.transition.play();
			}

		else
			for (Item i : views) {
				i.goal--;
				i.transition.stop();
				i.transition.setToX(i.goal * (IMAGE_SPACING + IMAGE_WIDTH));
				i.transition.play();
			}

	}

	public Home() {
	}

}
