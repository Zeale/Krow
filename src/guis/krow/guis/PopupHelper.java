package krow.guis;

import javafx.animation.FadeTransition;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
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

}
