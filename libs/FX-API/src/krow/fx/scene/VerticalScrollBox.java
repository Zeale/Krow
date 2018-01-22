package krow.fx.scene;

import javafx.animation.TranslateTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

public class VerticalScrollBox extends VBox {
	private static enum PropertyKeys {
		SLIDER;
	}

	private static final long SLIDE_ANIMATION_DURATION = 1000;

	public static int DEFAULT_NODE_WIDTH = 100, DEFAULT_NODE_HEIGHT = 100,
			DEFAULT_NODE_SPACING = (int) ((double) DEFAULT_NODE_WIDTH / 2);

	{
		setSpacing(DEFAULT_NODE_SPACING);
	}

	private IntegerProperty nodeWidth = new SimpleIntegerProperty(DEFAULT_NODE_WIDTH),
			nodeHeight = new SimpleIntegerProperty(DEFAULT_NODE_HEIGHT);

	private double jumpDistance = getNodeWidth() + getSpacing();

	{
		nodeWidthProperty().addListener((ChangeListener<Number>) (observable, oldValue,
				newValue) -> jumpDistance = newValue.doubleValue() + getSpacing());
		spacingProperty().addListener((ChangeListener<Number>) (observable, oldValue,
				newValue) -> jumpDistance = newValue.doubleValue() + getNodeWidth());
	}

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

	private final EventHandler<ScrollEvent> onScroll = event -> {
		// The amount of images to scroll.
		final int amount = event.getDeltaY() / event.getMultiplierY() > 0 ? 1 : -1;

		displacement += amount * jumpDistance;
		final double max = (getChildren().size() - 1) * jumpDistance, min = 0;
		if (displacement > max)
			displacement = max;
		else if (displacement < min)
			displacement = min;

		for (final Node n : getChildren()) {
			final TranslateTransition slider = getSlider(n);
			slider.stop();
			slider.setFromY(n.getTranslateY());
			slider.setByY(displacement - n.getTranslateY() - shift);
			slider.play();
		}

		event.consume();
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
							((ImageView) n).setFitHeight(getNodeHeight());
							((ImageView) n).setFitWidth(getNodeWidth());
						}

					}
		});

		addEventHandler(ScrollEvent.SCROLL, onScroll);
		setSpacing(getSpacing());

		setStyle(
				"-fx-background-color:  linear-gradient(to top, #00000020 0%, #000000A8 45%, #000000A8 55%, #00000020 100%);");

	}

	public VerticalScrollBox() {
	}

	public void centerNodes() {
		for (final Node n : getChildren()) {
			n.getTransforms().clear();
			n.getTransforms().add(new Translate(0, (getForceHeight() - getNodeHeight()) / 2
					- (getChildren().size() - 1) * (getNodeHeight() + getSpacing())));
			n.setTranslateY(getChildren().size() / 2 * (getNodeHeight() + getSpacing()));
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
		setDisplacement(jumpDistance * (getChildren().size() / 2));
	}

	protected void setDisplacement(final double displacement) {
		this.displacement = displacement;
		for (final Node n : getChildren())
			n.setTranslateY(displacement);
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

	public final IntegerProperty nodeWidthProperty() {
		return this.nodeWidth;
	}

	public final int getNodeWidth() {
		return this.nodeWidthProperty().get();
	}

	public final void setNodeWidth(final int nodeWidth) {
		this.nodeWidthProperty().set(nodeWidth);
	}

	public final IntegerProperty nodeHeightProperty() {
		return this.nodeHeight;
	}

	public final int getNodeHeight() {
		return this.nodeHeightProperty().get();
	}

	public final void setNodeHeight(final int nodeHeight) {
		this.nodeHeightProperty().set(nodeHeight);
	}

}
