package zeale.guis;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import kröw.core.Kröw;
import wolf.zeale.guis.Window;

public class Home extends Window {

	private static int IMAGE_WIDTH = (int) ((double) 100 / 1920 * Kröw.SCREEN_WIDTH),
			IMAGE_HEIGHT = (int) ((double) 100 / 1080 * Kröw.SCREEN_HEIGHT), IMAGE_SPACING = IMAGE_WIDTH / 2;

	@Override
	public String getWindowFile() {
		return "Home.fxml";
	}

	@FXML
	private HBox horizontalScroll;
	@FXML
	private VBox verticalScroll;

	private ObservableList<Item> views = FXCollections.observableArrayList();
	private short totalShift = (short) (views.size() - 6);

	private static final Image CONSTRUCT_MENU_ICON = new Image("krow/resources/ConstructIcon_hd.png");

	@FXML
	@Override
	public void initialize() {

		// TODO Actually implement a vertical bar.
		verticalScroll.setVisible(false);

		// Make sure the views list works in sync with the horizontalScroll's
		// child list.
		views.addListener(new ListChangeListener<Item>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Item> c) {
				horizontalScroll.getChildren().clear();
				for (Item i : views)
					horizontalScroll.getChildren().add(i.image);
				// If there are less than six images, we use this to position
				// those images. This adds in dummy images for positioning.
				for (int i = 7 - horizontalScroll.getChildren().size(); i > 0; i--) {
					ImageView iv = new ImageView();
					iv.setFitHeight(IMAGE_HEIGHT);
					iv.setFitWidth(IMAGE_WIDTH);
					horizontalScroll.getChildren().add(0, iv);
				}
			}
		});

		// Styles. Yaaa...
		horizontalScroll.setStyle(
				"-fx-background-color:  linear-gradient(to right, #00000020 0%, #000000A8 45%, #000000A8 55%, #00000020 100%);");

		// Set up the default shift location.
		totalShift = (short) (views.size() - 6);

		// Apply sizing to our containers.
		horizontalScroll.setPrefWidth(Kröw.SCREEN_WIDTH);
		horizontalScroll.setPrefHeight(IMAGE_HEIGHT);
		verticalScroll.setPrefHeight(Kröw.SCREEN_HEIGHT);
		verticalScroll.setPrefWidth(IMAGE_WIDTH);

		// Position our containers.
		horizontalScroll.setLayoutX(0);
		horizontalScroll.setLayoutY(Kröw.SCREEN_HEIGHT / 2 - horizontalScroll.getPrefHeight() / 2);
		verticalScroll.setLayoutY(0);
		verticalScroll.setLayoutX(Kröw.SCREEN_WIDTH / 2 - verticalScroll.getPrefWidth() / 2);

		// The spacing between each image.
		horizontalScroll.setSpacing(IMAGE_SPACING);

		// Set what happens when the user scrolls to a different image.
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

		// Exit the program via the escape key.
		horizontalScroll.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ESCAPE)
					Platform.exit();
			}
		});

		// Put this in front of the verticalScroll container.
		horizontalScroll.toFront();

		// TODO Fix all this stuff later.

		// Now to add the default images to our horizontal scroll container.
		ImageView constructs = new ImageView(CONSTRUCT_MENU_ICON);
		constructs.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// TODO Open the construct management window once we've made it.
				// For now, we'll print some crap.
				System.out.println("Clicked C");

				// If you tried to click the image and received no console
				// output,
				// then do keep in mind that JavaFX will only register clicks
				// that are located at a non-transparent area of the image.

			}
		});

		ImageView krow = new ImageView(Kröw.IMAGE_KRÖW);
		// This code assures that clicking in a transparent portion of the image
		// will still cause a click to be registered by the event handler.
		krow.setPickOnBounds(true);
		// Event handler
		krow.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				System.out.println("Clicked Krow");
			}
		});

		// Testing the dummy positioning images.
		addImage(krow);

		addImage(constructs);

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

	public List<ImageView> clearImages() {
		ArrayList<ImageView> items = new ArrayList<>();
		for (Item i : views)
			items.add(i.image);
		views.clear();
		return items;
	}

	public void addImage(ImageView image) {
		TranslateTransition transition = new TranslateTransition(Duration.seconds(1), image);
		image.setFitHeight(IMAGE_HEIGHT);
		image.setFitWidth(IMAGE_WIDTH);
		views.add(new Item(image, transition));
	}

	public void removeImage(int index) {
		views.remove(index);
	}

	public boolean removeImage(ImageView imageView) {
		for (int i = 0; i < views.size(); i++)
			if (views.get(i).image == imageView) {
				views.remove(i);
				return true;
			}
		return false;
	}

	public void addImage(int index, ImageView imageView) {

		TranslateTransition transition = new TranslateTransition(Duration.seconds(1), imageView);
		imageView.setFitHeight(IMAGE_HEIGHT);
		imageView.setFitWidth(IMAGE_WIDTH);
		views.add(index, new Item(imageView, transition));
	}

	public Home() {
	}

}
