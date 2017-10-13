package krow.scene;

import javafx.animation.TranslateTransition;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import kröw.core.Kröw;

public class HorizontalScrollBox extends HBox {

	private double forceWidth = -1, forceHeight = -1;

	@Override
	protected void setHeight(double value) {
		if (forceHeight >= 0)
			super.setHeight(forceHeight);
		else
			super.setHeight(value);
	}

	@Override
	protected void setWidth(double value) {
		if (forceWidth >= 0)
			super.setWidth(forceWidth);
		else
			super.setWidth(value);
	}

	/**
	 * @return the forceWidth
	 */
	public final double getForceWidth() {
		return forceWidth;
	}

	/**
	 * @param forceWidth
	 *            the forceWidth to set
	 */
	public final void setForceWidth(double forceWidth) {
		this.forceWidth = forceWidth;
	}

	/**
	 * @return the forceHeight
	 */
	public final double getForceHeight() {
		return forceHeight;
	}

	/**
	 * @param forceHeight
	 *            the forceHeight to set
	 */
	public final void setForceHeight(double forceHeight) {
		this.forceHeight = forceHeight;
	}

	private static final long SLIDE_ANIMATION_DURATION = 1000;

	private double displacement = 0;

	public static int NODE_WIDTH = Kröw.scaleWidth(100), NODE_HEIGHT = Kröw.scaleHeight(100),
			NODE_SPACING = (int) ((double) NODE_WIDTH / 2);

	private final double SINGLE_JUMP_DISTANCE = NODE_WIDTH + NODE_SPACING;

	private final EventHandler<ScrollEvent> onScroll = event -> {
		// The amount of images to scroll.
		int amount = event.getDeltaY() / event.getMultiplierY() > 0 ? 1 : -1;

		displacement += amount * SINGLE_JUMP_DISTANCE;
		double max = (getChildren().size() - 1) * SINGLE_JUMP_DISTANCE, min = 0;
		if (displacement > max)
			displacement = max;
		else if (displacement < min)
			displacement = min;

		for (Node n : getChildren()) {
			TranslateTransition slider = getSlider(n);
			slider.stop();
			slider.setFromX(n.getTranslateX());
			slider.setByX(displacement - n.getTranslateX());
			slider.play();
			event.consume();
		}
	};

	private static enum PropertyKeys {
		SLIDER;
	}

	public HorizontalScrollBox() {
	}

	{
		getChildren().addListener((ListChangeListener<Node>) c -> {
			while (c.next()) {
				if (c.wasAdded())
					for (Node n : c.getAddedSubList()) {
						TranslateTransition slider = new TranslateTransition();
						n.getProperties().put(PropertyKeys.SLIDER, slider);
						slider.setDuration(Duration.millis(SLIDE_ANIMATION_DURATION));
						slider.setNode(n);

						n.setTranslateX(displacement);

						if (n instanceof ImageView) {
							((ImageView) n).setFitHeight(NODE_HEIGHT);
							((ImageView) n).setFitWidth(NODE_WIDTH);
						}

					}
			}
		});

		addEventHandler(ScrollEvent.SCROLL, onScroll);

		setSpacing(NODE_SPACING);

		setStyle(
				"-fx-background-color:  linear-gradient(to right, #00000020 0%, #000000A8 45%, #000000A8 55%, #00000020 100%);");

	}

	protected void setDisplacement(double displacement) {
		this.displacement = displacement;
		for (Node n : getChildren())
			n.setTranslateX(displacement);
	}

	public void center() {
		setDisplacement(SINGLE_JUMP_DISTANCE * (getChildren().size() / 2));
	}

	/**
	 * Convenience method for obtaining a Node's slider.
	 * 
	 * @param node
	 *            The node to obtain the slider from.
	 * @return The slider obtained from the node.
	 */
	private static TranslateTransition getSlider(Node node) {
		return (TranslateTransition) node.getProperties().get(PropertyKeys.SLIDER);
	}

}
