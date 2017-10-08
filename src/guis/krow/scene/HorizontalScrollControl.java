package krow.scene;

import javafx.animation.TranslateTransition;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import kröw.core.Kröw;

public class HorizontalScrollControl extends HBox {

	private static final long SLIDE_ANIMATION_DURATION = 1000;

	private double displacement = 0;

	private final EventHandler<ScrollEvent> onScroll = new EventHandler<ScrollEvent>() {

		@Override
		public void handle(ScrollEvent event) {
			// The amount of images to scroll.
			int amount = event.getDeltaY() / event.getMultiplierY() > 0 ? 1 : -1;

			displacement += amount * SINGLE_JUMP_DISTANCE;

			for (Node n : getChildren()) {
				TranslateTransition slider = getSlider(n);
				slider.stop();
				slider.setFromX(n.getTranslateX());
				slider.setByX(displacement - n.getTranslateX());
				slider.play();
			}
		}
	};

	private static enum PropertyKeys {
		SLIDER;
	}

	public static int NODE_WIDTH = Kröw.scaleWidth(100), NODE_HEIGHT = Kröw.scaleHeight(100),
			NODE_SPACING = (int) ((double) NODE_WIDTH / 2);

	private final double SINGLE_JUMP_DISTANCE = NODE_WIDTH + NODE_SPACING;

	public HorizontalScrollControl() {
		// TODO Auto-generated constructor stub
	}

	{
		getChildren().addListener(new ListChangeListener<Node>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Node> c) {
				while (c.next()) {
					if (c.wasAdded())
						for (Node n : c.getAddedSubList()) {
							TranslateTransition slider = new TranslateTransition();
							n.getProperties().put(PropertyKeys.SLIDER, slider);
						}
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
