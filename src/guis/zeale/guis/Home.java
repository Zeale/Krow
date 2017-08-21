
package zeale.guis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import krow.guis.GUIHelper;
import kröw.core.Kröw;
import kröw.core.managers.WindowManager;
import kröw.core.managers.WindowManager.NotSwitchableException;
import kröw.core.managers.WindowManager.Page;

public class Home extends Page {
	private class Item {

		public final ImageView image;
		public final TranslateTransition transition;
		public double goal;

		public Item(final ImageView image, final TranslateTransition transition) {
			this.image = image;
			this.transition = transition;
		}

	}

	private final static long SLIDE_ANIMATION_DURATION = 1000;

	private static int IMAGE_WIDTH = (int) ((double) 100 / 1920 * Kröw.getSystemProperties().getScreenWidth()),
			IMAGE_HEIGHT = (int) ((double) 100 / 1080 * Kröw.getSystemProperties().getScreenHeight()),
			IMAGE_SPACING = IMAGE_WIDTH / 2;

	private static final Image CONSTRUCT_MENU_ICON = new Image("krow/resources/ConstructIcon_hd.png");

	@FXML
	private AnchorPane pane;
	@FXML
	private HBox horizontalScroll;

	@FXML
	private VBox verticalScroll;
	private final ObservableList<Item> views = FXCollections.observableArrayList();

	private short totalShift = (short) (views.size() - 6);

	public void addImage(final ImageView image) {
		final TranslateTransition transition = new TranslateTransition(Duration.millis(SLIDE_ANIMATION_DURATION),
				image);
		image.setFitHeight(IMAGE_HEIGHT);
		image.setFitWidth(IMAGE_WIDTH);
		views.add(new Item(image, transition));
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

	@Override
	public boolean canSwitchScenes(final Class<? extends Page> newSceneClass) {
		if (newSceneClass.equals(Home.class))
			return false;
		return true;
	}

	public List<ImageView> clearImages() {
		final ArrayList<ImageView> items = new ArrayList<>();
		for (final Item i : views)
			items.add(i.image);
		views.clear();
		return items;
	}

	@Override
	public String getWindowFile() {
		return "Home.fxml";
	}

	@FXML
	@Override
	public void initialize() {

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

		// Styles. Yaaa...
		horizontalScroll.setStyle(
				"-fx-background-color:  linear-gradient(to right, #00000020 0%, #000000A8 45%, #000000A8 55%, #00000020 100%);");

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

		GUIHelper.addDefaultSettings(GUIHelper.buildMenu(pane));
		GUIHelper.applyShapeBackground(pane);
	}

	private final void loadDefaultImages() {
		// Now to add the default images to our horizontal scroll container.
		final ImageView constructs = new ImageView(CONSTRUCT_MENU_ICON);
		constructs.setOnMouseClicked(event -> WindowManager.spawnLabelAtMousePos("WIP", Color.WHITE));
		constructs.setPickOnBounds(true);

		final ImageView krow = new ImageView(Kröw.IMAGE_KRÖW);
		// This code assures that clicking in a transparent portion of the image
		// will still cause a click to be registered by the event handler.
		// Event handler
		krow.setOnMouseClicked(event -> {
			try {
				WindowManager.setScene(Tools.class);
			} catch (InstantiationException | IllegalAccessException | IOException
					| WindowManager.NotSwitchableException e) {
				e.printStackTrace();
			}
		});

		final ImageView settings = new ImageView("krow/resources/Settings.png");
		settings.setOnMouseClicked(event -> {
			try {
				WindowManager.setScene(Settings.class);
			} catch (InstantiationException | IllegalAccessException | IOException e) {
				e.printStackTrace();
			} catch (final WindowManager.NotSwitchableException e) {
				e.printStackTrace();
			}
		});

		ImageView statistics = new ImageView("krow/resources/Statistics.png");
		statistics.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				System.out.println("Potato");
				try {
					WindowManager.setScene(Statistics.class);
				} catch (InstantiationException | IllegalAccessException | IOException | NotSwitchableException e) {
					e.printStackTrace();
				}
			}
		});
		statistics.setPickOnBounds(true);

		addImage(settings);
		addImage(krow);
		addImage(constructs);
		addImage(statistics);
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

	private void slideImages(int amount) {
		if (amount + totalShift > views.size()) {
			amount = views.size() - totalShift;
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