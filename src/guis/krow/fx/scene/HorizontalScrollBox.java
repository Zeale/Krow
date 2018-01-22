package krow.fx.scene;

import javafx.animation.TranslateTransition;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
import kröw.core.Kröw;

public class HorizontalScrollBox extends HBox {

	private static enum PropertyKeys {
		SLIDER;
	}

	private static final long SLIDE_ANIMATION_DURATION = 1000;

	public static int NODE_WIDTH = Kröw.scaleWidth(100), NODE_HEIGHT = Kröw.scaleHeight(100),
			NODE_SPACING = (int) ((double) NODE_WIDTH / 2);

	/**
	 * Convenience method for obtaining a Node's slider.
	 *
	 * @param node
	 *            The node to obtain the slider from.
	 * @return The slider obtained from the node.
	 */
	private static TranslateTransition getSlider(final Node node) {
		return (TranslateTransition) node.getProperties().get(PropertyKeys.SLIDER);
	}

	private double forceWidth = -1, forceHeight = -1;

	private double displacement = 0;

	private final double shift = 0;

	private final double SINGLE_JUMP_DISTANCE = NODE_WIDTH + NODE_SPACING;

	private final EventHandler<ScrollEvent> onScroll = event -> {
		// The amount of images to scroll.
		final int amount = event.getDeltaY() / event.getMultiplierY() > 0 ? 1 : -1;

		displacement += amount * SINGLE_JUMP_DISTANCE;
		final double max = (getChildren().size() - 1) * SINGLE_JUMP_DISTANCE, min = 0;
		if (displacement > max)
			displacement = max;
		else if (displacement < min)
			displacement = min;

		for (final Node n : getChildren()) {
			final TranslateTransition slider = getSlider(n);
			slider.stop();
			slider.setFromX(n.getTranslateX());
			slider.setByX(displacement - n.getTranslateX() - shift);
			slider.play();
			event.consume();
		}
	};

	{
		getChildren().addListener((ListChangeListener<Node>) c -> {
			while (c.next())
				if (c.wasAdded())
					for (final Node n : c.getAddedSubList()) {
						final TranslateTransition slider = new TranslateTransition();
						n.getProperties().put(PropertyKeys.SLIDER, slider);
						slider.setDuration(Duration.millis(SLIDE_ANIMATION_DURATION));
						slider.setNode(n);

						n.setTranslateX(displacement);

						if (n instanceof ImageView) {
							((ImageView) n).setFitHeight(NODE_HEIGHT);
							((ImageView) n).setFitWidth(NODE_WIDTH);
						}

					}
		});

		addEventHandler(ScrollEvent.SCROLL, onScroll);

		setSpacing(NODE_SPACING);

		setStyle(
				"-fx-background-color:  linear-gradient(to right, #00000020 0%, #000000A8 45%, #000000A8 55%, #00000020 100%);");

	}

	public HorizontalScrollBox() {
	}

	public void centerNodes() {
		for (final Node n : getChildren()) {
			n.getTransforms().clear();
			n.getTransforms().add(new Translate(
					(getForceWidth() - NODE_WIDTH) / 2 - (getChildren().size() - 1) * (NODE_WIDTH + NODE_SPACING), 0));
			n.setTranslateX(getChildren().size() / 2 * (NODE_WIDTH + NODE_SPACING));
		}

	}

	/**
	 * @return the forceHeight
	 */
	public final double getForceHeight() {
		return forceHeight;
	}

	/**
	 * @return the forceWidth
	 */
	public final double getForceWidth() {
		return forceWidth;
	}

	public void selectCenter() {
		setDisplacement(SINGLE_JUMP_DISTANCE * (getChildren().size() / 2));
	}

	protected void setDisplacement(final double displacement) {
		this.displacement = displacement;
		for (final Node n : getChildren())
			n.setTranslateX(displacement);
	}

	/**
	 * @param forceHeight
	 *            the forceHeight to set
	 */
	public final void setForceHeight(final double forceHeight) {
		this.forceHeight = forceHeight;
	}

	/**
	 * @param forceWidth
	 *            the forceWidth to set
	 */
	public final void setForceWidth(final double forceWidth) {
		this.forceWidth = forceWidth;
	}

	@Override
	protected void setHeight(final double value) {
		if (forceHeight >= 0)
			super.setHeight(forceHeight);
		else
			super.setHeight(value);
	}

	@Override
	protected void setWidth(final double value) {
		if (forceWidth >= 0)
			super.setWidth(forceWidth);
		else
			super.setWidth(value);
	}

}
