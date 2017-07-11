package krow.eggs;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import wolf.zeale.guis.Window;

public class InstalledApps extends Window {

	@FXML
	private AnchorPane pane;

	public void initialize() {
		// A fancy little object that we use to keep track of child nodes
		// pressing keys...

		// Try un-commenting the System.out.prinln()s and then running the
		// program to see how java handles key presses.
		new Object() {

			{
				/* Handle the AnchorPane */
				pane.<KeyEvent>addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

					@Override
					public void handle(KeyEvent event) {
						// System.out.println("I/My children were clicked");

						/* Check if child was clicked. If so, ignore event. */
						if (childClicked) {
							childClicked = false;
							return;
						}
						if (KeyEvent.KEY_PRESSED.equals(event.getEventType()) && event.getCode() == KeyCode.ESCAPE) {
							Window.setSceneToPreviousScene();
						}
					}
				});
				/* Handle the pane's nodes */
				for (Node n : pane.getChildren()) {// For each node...
					n.<KeyEvent>addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

						@Override
						public void handle(KeyEvent event) {
							if (KeyEvent.KEY_PRESSED.equals(event.getEventType())
									&& event.getCode() == KeyCode.ESCAPE) {
								// If escape was clicked here, we need to notify
								// the pane (aka this node's parent).

								// We do this by setting the wrapper object's
								// field to true.

								// That way the parent will know that this call
								// was meant for its child, and will then ignore
								// the call.
								childClicked = true;

								// We set the field to true even if shift wasn't
								// down, otherwise the parent AnchorPane will
								// think the escape key event was for it.

								if (event.isShiftDown()) {
									Window.setSceneToPreviousScene();
								}
							}
						}
					});
				}
			}

			/**
			 * <p>
			 * A boolean which shows whether or not a key event was meant for
			 * the AnchorPane's children.
			 * <p>
			 * If this is <code>true</code>, then the parent will ignore the
			 * incoming key event, as it will know that the event was actually
			 * triggered by one of its children.
			 */
			private boolean childClicked;

		};

	}

	@Override
	public String getWindowFile() {
		return "InstalledApps.fxml";
	}

}
