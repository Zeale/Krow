package krow.guis;

import javafx.animation.FadeTransition;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Popup;
import javafx.util.Duration;
import kröw.core.Kröw;

public final class PopupHelper {

	private PopupHelper() {
	}

	public static void applyPopup(Node node, Popup popup) {
		Parent popupRoot = popup.getScene().getRoot();
		FadeTransition openTransition = new FadeTransition(Duration.millis(350), popupRoot),
				closeTransition = new FadeTransition(Duration.millis(350), popupRoot);
		openTransition.setToValue(1);
		closeTransition.setToValue(0);
		popupRoot.setOpacity(0);

		closeTransition.setOnFinished(event -> popup.hide());

		new Object() {

			private byte prevEvent = -1;

			{

				node.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
					prevEvent = 0;
					open(event);
				});

				node.addEventFilter(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {
						// Make sure we aren't leaving and going to this node.
						if (event.getPickResult().getIntersectedNode() == node)
							return;

						prevEvent = 1;
						close();
					}
				});

				popupRoot.addEventFilter(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {
						prevEvent = 2;
						open(event);
					}
				});

				popupRoot.addEventFilter(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {
						if (prevEvent == 0)
							return;
						prevEvent = 3;
						close();
					}
				});

			}

			private void open(MouseEvent event) {
				if (!popup.isShowing()) {
					popup.show(node, event.getSceneX(), event.getSceneY());
					popup.setX(event.getSceneX() - popup.getWidth() / 2);
					popup.setY(event.getSceneY() - popup.getHeight() - Kröw.scaleHeight(20));
				}
				openTransition.stop();
				closeTransition.stop();
				openTransition.setFromValue(openTransition.getNode().getOpacity());
				openTransition.play();
			}

			private void close() {
				openTransition.stop();
				closeTransition.stop();
				closeTransition.setFromValue(closeTransition.getNode().getOpacity());
				closeTransition.play();
			}
		};

	}

	private static final Color BASIC_POPUP_DEFAULT_BORDER_COLOR = new Color(0, 0, 0, 0.5);
	private static final Color BASIC_POPUP_DEFAULT_SHADOW_COLOR = new Color(0, 0, 0, 0.25);

	public static VBox buildBasicPopup(Node boundNode, Label... labels) {
		double defaultSize = new Label().getFont().getSize();
		Popup popup = new Popup();
		VBox box = new VBox(10);
		for (Label l : labels) {
			if (l.getFont().getSize() == defaultSize)
				l.setFont(
						Font.font(l.getFont().getFamily(), FontWeight.BOLD, FontPosture.REGULAR, Kröw.scaleHeight(18)));
			l.setTextFill(Color.WHITE);
			box.getChildren().add(l);
		}

		box.setBorder(new Border(new BorderStroke(BASIC_POPUP_DEFAULT_BORDER_COLOR, BorderStrokeStyle.SOLID,
				CornerRadii.EMPTY, new BorderWidths(2))));
		box.setBackground(new Background(new BackgroundFill(new Color(0, 0, 0, 0), CornerRadii.EMPTY, Insets.EMPTY)));
		box.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, BASIC_POPUP_DEFAULT_SHADOW_COLOR, 4.1, 0.4, 17, 21));

		popup.getScene().setRoot(box);

		applyPopup(boundNode, popup);
		return box;
	}

}
