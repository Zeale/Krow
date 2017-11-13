package krow.backgrounds;

import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * <h1 align="center">{@link BackgroundManager}</h1>
 * <h3 align="center">The superclass for all other
 * {@link BackgroundManager}s...</h3>
 * 
 * <br>
 * <p>
 * The {@link BackgroundManager} class provides many different methods to allow
 * external manipulation such as mouse interaction, fading in and out, and a few
 * other things. As of now, subclasses may choose wheter or not to implement
 * these methods.
 * 
 * @author Zeale
 *
 */
public abstract class BackgroundManager {

	private static final Color DEFAULT_COLOR = Color.BLACK;
	private static final double DEFAULT_ANIMATION_DURATION = 8;

	public abstract void show(Pane pane);

	private double animationDuration = DEFAULT_ANIMATION_DURATION;

	protected Pane currentPane;

	private Color startColor = DEFAULT_COLOR;

	private EventHandler<MouseEvent> mouseMovementHandler;

	private final ArrayList<Node> mouseDetectionNodes = new ArrayList<>();

	protected BackgroundManager() {
	}

	protected BackgroundManager(final Color startColor) {
		this();
		this.startColor = startColor;
	}

	protected BackgroundManager(final Color startColor, final Node... detectionNodes) {
		this();
		this.startColor = startColor;
		for (final Node n : detectionNodes)
			n.setOnMouseMoved(getMouseMovementHandler());
	}

	protected BackgroundManager(final Node... detectionNodes) {
		this();
		for (final Node n : detectionNodes)
			n.setOnMouseMoved(getMouseMovementHandler());
	}

	public void addMouseDetectionNodes(final Node... nodes) {
		for (final Node n : nodes) {
			n.setOnMouseMoved(mouseMovementHandler);
			mouseDetectionNodes.add(n);
		}
	}

	public abstract void disable();

	public void disableMouseInteraction() {
		currentPane.setOnMouseMoved(null);
	}

	public abstract void enable();

	public void enableMouseInteraction() {
		currentPane.setOnMouseMoved(getMouseMovementHandler());
	}

	public abstract void fadeIn();

	public abstract void fadeOut();

	/**
	 * @return the animationDuration
	 */
	public double getAnimationDuration() {
		return animationDuration;
	}

	/**
	 * @return the currentPane
	 */
	public Pane getCurrentPane() {
		return currentPane;
	}

	protected EventHandler<MouseEvent> getMouseMovementHandler() {
		return mouseMovementHandler;
	}

	public Color getStartColor() {
		return startColor;
	}

	public void removeMouseDetectionNodes(final Node... nodes) {
		for (final Node n : nodes)
			n.setOnMouseMoved(null);
	}

	/**
	 * @param animationDuration
	 *            the animationDuration to set
	 */
	public void setAnimationDuration(final double animationDuration) {
		this.animationDuration = animationDuration;
	}

	/**
	 * @param currentPane
	 *            the currentPane to set
	 */
	public void setCurrentPane(final Pane currentPane) {
		if (this.currentPane == currentPane)
			return;
		for (final Node n : mouseDetectionNodes)
			removeMouseDetectionNodes(n);
		if (this.currentPane != null)
			this.currentPane.setOnMouseMoved(null);
		this.currentPane = currentPane;
		currentPane.setOnMouseMoved(getMouseMovementHandler());
	}

	protected void setMouseMovementHandler(final EventHandler<MouseEvent> mouseMovementHandler) {
		this.mouseMovementHandler = mouseMovementHandler;
		currentPane.setOnMouseMoved(mouseMovementHandler);
	}

}