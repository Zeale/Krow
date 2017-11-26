package krow.backgrounds;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class ImageBackground extends Background {

	private final ImageViewFactory imageFactory;
	private final Image image;

	public ImageBackground(ImageViewFactory factory) {
		imageFactory = factory;
		image = null;
	}

	public ImageBackground(Image image) {
		this.image = image;
		imageFactory = null;
	}

	private List<ImageView> trackedImageViews = new LinkedList<>();

	@Override
	public void show(Pane pane) {
		// TODO This should add ImageViews on a check-if-added basis. Removing
		// all ImageViews and adding them back could be laggier.
		pane.getChildren().removeAll(trackedImageViews);
		pane.getChildren().addAll(trackedImageViews);
		for (ImageView iv : trackedImageViews) {
			iv.setVisible(true);
		}
	}

	@Override
	public void disable() {
		// TODO Auto-generated method stub

	}

	@Override
	public void enable() {
		// TODO Auto-generated method stub

	}

	@Override
	public void fadeIn() {
		// TODO Auto-generated method stub

	}

	@Override
	public void fadeOut() {
		// TODO Auto-generated method stub

	}

	@FunctionalInterface
	public static interface ImageViewFactory {
		ImageView make(Image image);
	}

	public static final ImageViewFactory DEFAULT_IMAGEVIEW_FACTORY = image -> new ImageView(image);

}
