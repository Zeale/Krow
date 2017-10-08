package krow.scene;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import kröw.core.Kröw;
import kröw.core.managers.WindowManager.Page;

public abstract class ScrollMenu extends Page {
	protected class Item {

		public final Node image;
		public final TranslateTransition transition;
		public double goal;

		public Item(final Node node, final TranslateTransition transition) {
			this.image = node;
			this.transition = transition;
		}

	}

	protected final static long SLIDE_ANIMATION_DURATION = 1000;

	public static int IMAGE_WIDTH = (int) ((double) 100 / 1920 * Kröw.getSystemProperties().getScreenWidth()),
			IMAGE_HEIGHT = (int) ((double) 100 / 1080 * Kröw.getSystemProperties().getScreenHeight()),
			IMAGE_SPACING = IMAGE_WIDTH / 2;

	@FXML
	protected Pane pane;

	@FXML
	protected HBox horizontalScroll;
	@FXML
	protected VBox verticalScroll;

	protected final ObservableList<Item> views = FXCollections.observableArrayList();

	private short totalShift = (short) (views.size() - 6);

	public void addImage(final ImageView image) {
		final TranslateTransition transition = new TranslateTransition(Duration.millis(SLIDE_ANIMATION_DURATION),
				image);
		image.setFitHeight(IMAGE_HEIGHT);
		image.setFitWidth(IMAGE_WIDTH);
		views.add(new Item(image, transition));
	}

	public void addShape(Shape shape) {
		final TranslateTransition transition = new TranslateTransition(Duration.millis(SLIDE_ANIMATION_DURATION),
				shape);
		views.add(new Item(shape, transition));
	}

	public void addImage(final int index, final ImageView imageView) {

		final TranslateTransition transition = new TranslateTransition(Duration.millis(SLIDE_ANIMATION_DURATION),
				imageView);
		imageView.setFitHeight(IMAGE_HEIGHT);
		imageView.setFitWidth(IMAGE_WIDTH);
		views.add(index, new Item(imageView, transition));
	}

	public void animate(final boolean forward) {

		if (forward)
			for (final Item i : views) {
				i.goal++;
				i.transition.stop();
				i.transition.setToX(i.goal * (IMAGE_SPACING + IMAGE_WIDTH));
				i.transition.play();
			}

		else
			for (final Item i : views) {
				i.goal--;
				i.transition.stop();
				i.transition.setToX(i.goal * (IMAGE_SPACING + IMAGE_WIDTH));
				i.transition.play();
			}

	}

	public List<Node> clearImages() {
		final ArrayList<Node> items = new ArrayList<>();
		for (final Item i : views)
			items.add(i.image);
		views.clear();
		return items;
	}

	@Override
	public void initialize() {

		if (verticalScroll == null)
			verticalScroll = new VBox();
		if (horizontalScroll == null)
			horizontalScroll = new HBox();
		if (pane == null)
			throw new RuntimeException("Unspecified pane.");

		pane.setPrefSize(Kröw.scaleWidth(Kröw.getSystemProperties().getScreenWidth()),
				Kröw.scaleHeight(Kröw.getSystemProperties().getScreenHeight()));
		pane.setLayoutX(0);
		pane.setLayoutY(0);

		if (!pane.getChildren().contains(verticalScroll))
			pane.getChildren().add(verticalScroll);
		if (!pane.getChildren().contains(horizontalScroll))
			pane.getChildren().add(horizontalScroll);

		// TODO Actually implement a vertical bar.
		verticalScroll.setVisible(false);

		// Make sure the views list works in sync with the horizontalScroll's
		// child list.
		views.addListener((ListChangeListener<Item>) c -> {
			horizontalScroll.getChildren().clear();
			for (final Item i1 : views)
				horizontalScroll.getChildren().add(i1.image);
			// If there are less than six images, we use this to position
			// those images. This adds in dummy images for positioning.
			for (int i2 = 7 - horizontalScroll.getChildren().size(); i2 > 0; i2--) {
				final ImageView iv = new ImageView();
				iv.setFitHeight(IMAGE_HEIGHT);
				iv.setFitWidth(IMAGE_WIDTH);
				horizontalScroll.getChildren().add(0, iv);
			}
		});

		// Set up the default shift location.
		totalShift = (short) (views.size() - 6);

		// Apply sizing to our containers.
		horizontalScroll.setPrefWidth(Kröw.getSystemProperties().getScreenWidth());
		horizontalScroll.setPrefHeight(IMAGE_HEIGHT);
		verticalScroll.setPrefHeight(Kröw.getSystemProperties().getScreenHeight());
		verticalScroll.setPrefWidth(IMAGE_WIDTH);

		// Position our containers.
		horizontalScroll.setLayoutX(0);
		horizontalScroll
				.setLayoutY(Kröw.getSystemProperties().getScreenHeight() / 2 - horizontalScroll.getPrefHeight() / 2);
		verticalScroll.setLayoutY(0);
		verticalScroll.setLayoutX(Kröw.getSystemProperties().getScreenWidth() / 2 - verticalScroll.getPrefWidth() / 2);

		// The spacing between each image.
		horizontalScroll.setSpacing(IMAGE_SPACING);

		// Set what happens when the user scrolls to a different image.
		horizontalScroll.setOnScroll(event -> {
			final int amount = event.getDeltaY() / event.getMultiplierY() > 0 ? 1 : -1;
			slideImages(amount);
		});
		horizontalScroll.setOnKeyPressed(event -> {
			if (event.getCode().equals(KeyCode.LEFT))
				slideImages(1);
			else if (event.getCode().equals(KeyCode.RIGHT))
				slideImages(-1);
		});

		// Put this in front of the verticalScroll container.
		horizontalScroll.toFront();
		loadDefaultImages();
	}

	protected void loadDefaultImages() {
	}

	public boolean removeImage(final ImageView imageView) {
		for (int i = 0; i < views.size(); i++)
			if (views.get(i).image == imageView) {
				views.remove(i);
				return true;
			}
		return false;
	}

	public void removeImage(final int index) {
		views.remove(index);
	}

	protected void slideImages(int amount) {
		if (amount + totalShift > views.size()) {
			totalShift = (short) views.size();
			return;// Omit below loop; it's useless.
		} else if (amount + totalShift < 1) {
			totalShift = 1;
			return;// Omit below loop; it's useless.
		} else
			totalShift += amount;

		for (; amount != 0; amount -= amount > 0 ? 1 : -1)
			animate(amount > 0);
	}
}
