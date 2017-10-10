package krow.scene;

import javafx.animation.TranslateTransition;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class VerticalScrollBox extends VBox {

	private static final long SLIDE_ANIMATION_DURATION = 1000;

	public static int NODE_WIDTH = 100, NODE_HEIGHT = 100, NODE_SPACING = (int) ((double) NODE_HEIGHT / 2);

	private final double SINGLE_JUMP_DISTANCE = NODE_HEIGHT + NODE_SPACING;

	private double displacement = 0;

	private final EventHandler<ScrollEvent> onScroll = event -> {
		// The amount of images to scroll.
		int amount = event.getDeltaY() / event.getMultiplierY() > 0 ? 1 : -1;

		displacement += amount * SINGLE_JUMP_DISTANCE;

		for (Node n : getChildren()) {
			TranslateTransition slider = getSlider(n);
			slider.stop();
			slider.setFromY(n.getTranslateY());
			slider.setByY(displacement - n.getTranslateY());
			slider.play();
		}

		event.consume();
	};

	private static enum PropertyKeys {
		SLIDER;
	}

	public VerticalScrollBox() {
		// TODO Auto-generated constructor stub
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
					}
			}
		});

		addEventHandler(ScrollEvent.SCROLL, onScroll);

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
