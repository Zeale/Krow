package krow.scene;

import javafx.animation.TranslateTransition;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import kr�w.core.Kr�w;

public class HorizontalScrollControl extends HBox {

	private static final long SLIDE_ANIMATION_DURATION = 1000;

	private double displacement = 0;

	private static enum PropertyKeys {
		SLIDER;
	}

	public static int NODE_WIDTH = Kr�w.scaleWidth(100), NODE_HEIGHT = Kr�w.scaleHeight(100),
			NODE_SPACING = (int) ((double) NODE_WIDTH / 2);

	private final double SINGLE_JUMP_DISTANCE = NODE_WIDTH + NODE_SPACING;

	public HorizontalScrollControl() {
		// TODO Auto-generated constructor stub
	}
}
