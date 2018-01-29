package krow.fx.dialogues;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoginBox {
	private final Stage stage = new Stage(StageStyle.TRANSPARENT);
	private final VBox pane = new VBox(10);
	private final Scene scene = new Scene(pane);
	private final TextField passwordField = new TextField(), usernameField = new TextField();
	private final Button continueButton = new Button("Continue");

	private EventHandler<LoginEvent> loginHandler;

	public EventHandler<LoginEvent> getLoginHandler() {
		return loginHandler;
	}

	public void setLoginHandler(EventHandler<LoginEvent> loginHandler) {
		this.loginHandler = loginHandler;
	}

	{
		build();
	}

	private void build() {
		stage.setScene(scene);
		scene.setFill(Color.TRANSPARENT);
		pane.getChildren().addAll(usernameField, passwordField, continueButton);
		continueButton.setOnAction(
				event -> loginHandler.handle(new LoginEvent(usernameField.getText(), passwordField.getText(), event)));
	}

	public void show() {
		stage.show();
		stage.sizeToScene();
		stage.centerOnScreen();
	}

	public class LoginEvent extends Event {

		/**
		 * SUID
		 */
		private static final long serialVersionUID = 1L;
		public final ActionEvent cause;
		public final String username, password;

		private LoginEvent(String username, String password, ActionEvent cause) {
			super(ANY);
			this.username = username;
			this.password = password;
			this.cause = cause;
		}

	}

	public void hide() {
		stage.hide();
	}

	public void close() {
		stage.close();
	}

}
