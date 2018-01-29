package krow.fx.dialogues;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginBox extends Dialogue<VBox> {
	private final TextField passwordField = new TextField(), usernameField = new TextField();
	private final Button continueButton = new Button("Continue");

	private EventHandler<LoginEvent> loginHandler;

	public EventHandler<LoginEvent> getLoginHandler() {
		return loginHandler;
	}

	public LoginBox() {
		super(new VBox(10));
	}

	public void setLoginHandler(EventHandler<LoginEvent> loginHandler) {
		this.loginHandler = loginHandler;
	}

	public void build() {
		super.build();
		pane.getChildren().addAll(usernameField, passwordField, continueButton);
		continueButton.setOnAction(
				event -> loginHandler.handle(new LoginEvent(usernameField.getText(), passwordField.getText(), event)));
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
