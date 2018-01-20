package krow.guis;

import java.util.ArrayList;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Popup;
import javafx.util.Duration;
import kröw.core.Kröw;
import kröw.gui.ApplicationManager;

public final class PopupHelper {

	public static class PopupWrapper<BT extends Pane> {
		public final BT box;
		public final Popup popup;

		private PopupWrapper(final BT box, final Popup popup) {
			this.box = box;
			this.popup = popup;
		}

	}

	public static final double DEFAULT_POPUP_VERTICAL_DISPLACEMENT = 0;

	private static ArrayList<Popup> popups = new ArrayList<>();

	static {
		ApplicationManager.addOnPageChangeRequested(event -> hideAllShowingRegisteredPopups());
	}

	private static final Color BASIC_POPUP_DEFAULT_BORDER_COLOR = new Color(0, 0, 0, 0.5);

	private static final Color BASIC_POPUP_DEFAULT_SHADOW_COLOR = new Color(0, 0, 0, 0.25);

	private static final Object LAST_POPUP_KEY = new Object();

	public static void applyHoverPopup(final Node node, final Popup popup) {
		final Parent popupRoot = popup.getScene().getRoot();
		final FadeTransition openTransition = new FadeTransition(Duration.millis(350), popupRoot),
				closeTransition = new FadeTransition(Duration.millis(350), popupRoot);
		openTransition.setToValue(1);
		closeTransition.setToValue(0);
		popupRoot.setOpacity(0);

		closeTransition.setOnFinished(event -> {
			popup.hide();
		});

		new Object() {

			private byte prevEvent = -1;

			{
				node.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
					prevEvent = 0;
					open(event);
				});

				node.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
					// Make sure we aren't leaving and going to this node.
					if (event.getPickResult().getIntersectedNode() == node)
						return;

					prevEvent = 1;
					close();
				});

				popupRoot.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
					prevEvent = 2;
					open(event);
				});

				popupRoot.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
					if (prevEvent == 0)
						return;
					prevEvent = 3;
					close();
				});

			}

			private void close() {
				openTransition.stop();
				closeTransition.stop();
				closeTransition.setFromValue(closeTransition.getNode().getOpacity());
				closeTransition.play();
			}

			private void open(final MouseEvent event) {

				if (node.getProperties().containsKey(LAST_POPUP_KEY)
						&& node.getProperties().get(LAST_POPUP_KEY) != popup
						&& ((Popup) node.getProperties().get(LAST_POPUP_KEY)).isShowing())
					return;
				node.getProperties().put(LAST_POPUP_KEY, popup);

				if (!popup.isShowing()) {
					popup.show(node, event.getSceneX(), event.getSceneY());
					popup.setX(event.getSceneX() - popup.getWidth() / 2);
					popup.setY(event.getSceneY() - popup.getHeight()
							- Kröw.scaleHeight(DEFAULT_POPUP_VERTICAL_DISPLACEMENT));
				}
				openTransition.stop();
				closeTransition.stop();
				openTransition.setFromValue(openTransition.getNode().getOpacity());
				openTransition.play();
			}
		};

	}

	public static void applyClickPopup(final Node node, final Popup popup, MouseButton button) {
		final Parent popupRoot = popup.getScene().getRoot();
		final FadeTransition openTransition = new FadeTransition(Duration.millis(350), popupRoot),
				closeTransition = new FadeTransition(Duration.millis(350), popupRoot);
		openTransition.setToValue(1);
		closeTransition.setToValue(0);
		popupRoot.setOpacity(0);

		closeTransition.setOnFinished(event -> {
			popup.hide();
		});

		new Object() {

			private byte prevEvent = -1;
			private long timeEntered;

			{

				node.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
					if (!popup.isShowing())
						return;
					prevEvent = 0;
					open(event);
				});

				node.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
					if (event.getButton().equals(button)) {
						event.consume();
						open(event);
					}
				});

				node.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
					// Make sure we aren't leaving and going to this node.
					final long time = System.currentTimeMillis();
					if (time - 300 <= timeEntered || event.getPickResult().getIntersectedNode() == node)
						return;

					prevEvent = 1;
					close();
				});

				popupRoot.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
					prevEvent = 2;
					timeEntered = System.currentTimeMillis();
					open(event);
				});

				popupRoot.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
					if (prevEvent == 0)
						return;
					prevEvent = 3;
					close();
				});

			}

			private void close() {
				popups.remove(popup);
				openTransition.stop();
				closeTransition.stop();
				closeTransition.setFromValue(closeTransition.getNode().getOpacity());
				closeTransition.play();
			}

			private void open(final MouseEvent event) {

				if (node.getProperties().containsKey(LAST_POPUP_KEY)) {
					final Popup popup2 = (Popup) node.getProperties().get(LAST_POPUP_KEY);
					if (popup2 != popup && popup2.isShowing())
						popup2.hide();
				}

				node.getProperties().put(LAST_POPUP_KEY, popup);

				popups.add(popup);

				if (!popup.isShowing()) {
					popup.show(node, event.getSceneX(), event.getSceneY());
					popup.setX(event.getSceneX() - popup.getWidth() / 2);
					popup.setY(event.getSceneY() - popup.getHeight()
							- Kröw.scaleHeight(DEFAULT_POPUP_VERTICAL_DISPLACEMENT));
				}
				openTransition.stop();
				closeTransition.stop();
				openTransition.setFromValue(openTransition.getNode().getOpacity());
				openTransition.play();
			}
		};

	}

	public static VBox addHoverPopup(final Node boundNode, final Color color, final String... labels) {
		final Label[] lbls = new Label[labels.length];
		for (int i = 0; i < labels.length; i++) {
			lbls[i] = new Label(labels[i]);
			lbls[i].setTextFill(color);
		}
		return addHoverPopup(boundNode, lbls);
	}

	public static VBox addHoverPopup(final Node boundNode, final Label... labels) {
		final PopupWrapper<VBox> wrapper = buildPopup(labels);
		applyHoverPopup(boundNode, wrapper.popup);
		return wrapper.box;
	}

	public static PopupWrapper<VBox> buildPopup(final Label... labels) {

		final double defaultSize = new Label().getFont().getSize();
		final Paint defaultTextFill = new Label().getTextFill();

		final Popup popup = new Popup();
		final VBox box = new VBox(10);
		for (final Label l : labels) {
			if (l.getFont().getSize() == defaultSize)
				l.setFont(
						Font.font(l.getFont().getFamily(), FontWeight.BOLD, FontPosture.REGULAR, Kröw.scaleHeight(18)));
			if (l.getTextFill().equals(defaultTextFill))
				l.setTextFill(Color.WHITE);
			box.getChildren().add(l);
		}

		box.setBorder(new Border(new BorderStroke(BASIC_POPUP_DEFAULT_BORDER_COLOR, BorderStrokeStyle.SOLID,
				CornerRadii.EMPTY, new BorderWidths(2))));
		box.setBackground(
				new Background(new BackgroundFill(new Color(0, 0, 0, 0.004), CornerRadii.EMPTY, Insets.EMPTY)));
		box.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, BASIC_POPUP_DEFAULT_SHADOW_COLOR, 4.1, 0.4, 17, 21));

		popup.getScene().setRoot(box);

		return new PopupWrapper<>(box, popup);

	}

	public static VBox addRightClickPopup(final Node boundNode, final Label... labels) {
		final PopupWrapper<VBox> wrapper = buildPopup(labels);
		applyClickPopup(boundNode, wrapper.popup, MouseButton.SECONDARY);
		return wrapper.box;
	}

	public static VBox addLeftClickPopup(final Node boundNode, final Label... labels) {
		final PopupWrapper<VBox> wrapper = buildPopup(labels);
		applyClickPopup(boundNode, wrapper.popup, MouseButton.PRIMARY);
		return wrapper.box;
	}

	public static void hideAllShowingRegisteredPopups() {
		for (final Popup popup : popups)
			popup.hide();
		popups.clear();
	}

	private PopupHelper() {
	}

}
