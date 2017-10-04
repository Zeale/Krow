package krow.guis;

import javafx.animation.Animation;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import kröw.core.managers.WindowManager;

public class Popup extends javafx.stage.Popup {

	private Animation openingAnimation, closingAnimation;

	private EventHandler<MouseEvent> mouseEntered = (event -> open()), mouseClicked = event -> {
		if (event.isSecondaryButtonDown())
			open();
	};

	public Popup(Animation openingAnimation, Animation closingAnimation) {
		this.openingAnimation = openingAnimation;
		this.closingAnimation = closingAnimation;
	}

	public Popup(Animation openingAnimation) {
		this.openingAnimation = openingAnimation;
	}

	public Popup(Node node, boolean hover) {
		bindNode(node, hover);
	}

	public void bindNode(Node node, boolean hover) {
		if (hover)
			node.removeEventHandler(MouseEvent.MOUSE_ENTERED, mouseEntered);
		else
			node.removeEventHandler(MouseEvent.MOUSE_CLICKED, mouseClicked);

		bindeNode(node, hover ? MouseEvent.MOUSE_ENTERED : MouseEvent.MOUSE_CLICKED,
				hover ? mouseEntered : mouseClicked);
	}

	public void bindNode(Node node) {
		bindNode(node, true);
	}

	public <T extends Event> void bindeNode(Node node, EventType<T> type, EventHandler<T> handler) {
		node.removeEventHandler(type, handler);
		node.addEventHandler(type, handler);
	}

	public Popup(Animation openingAnimation, Animation closingAnimation, Node node) {
		this.openingAnimation = openingAnimation;
		this.closingAnimation = closingAnimation;
		bindNode(node);
	}

	public Popup() {
	}

	public void setRoot(Parent value) {
		getScene().setRoot(value);
	}

	{
		addEventHandler(MouseEvent.MOUSE_EXITED, event -> close());
		setRoot(new AnchorPane());
	}

	public Parent getRoot() {
		return getScene().getRoot();
	}

	/**
	 * @return the openingAnimation
	 */
	public final Animation getOpeningAnimation() {
		return openingAnimation;
	}

	/**
	 * @param openingAnimation
	 *            the openingAnimation to set
	 */
	public final void setOpeningAnimation(Animation openingAnimation) {
		this.openingAnimation = openingAnimation;
	}

	/**
	 * @return the closingAnimation
	 */
	public final Animation getClosingAnimation() {
		return closingAnimation;
	}

	/**
	 * @param closingAnimation
	 *            the closingAnimation to set
	 */
	public final void setClosingAnimation(Animation closingAnimation) {
		this.closingAnimation = closingAnimation;
	}

	public void open() {
		if (openingAnimation != null)
			openingAnimation.play();
		else
			show(WindowManager.getStage());
	}

	public void close() {
		if (closingAnimation != null)
			closingAnimation.play();
		else
			hide();
	}

}
