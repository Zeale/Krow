package krow.fx.dialogues;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class LoginBox extends Dialogue<VBox> {
	private final TextField passwordField = new TextField(), usernameField = new TextField();
	private final Button continueButton = new Button("Continue");

	private EventHandler<LoginEvent> loginHandler;

	public EventHandler<LoginEvent> getLoginHandler() {
		return loginHandler;
	}

	public LoginBox(Stage stage) {
		super(new VBox(10), stage);
	}

	public void setLoginHandler(EventHandler<LoginEvent> loginHandler) {
		this.loginHandler = loginHandler;
	}

	@Override
	public void build() {
		super.build();
		pane.getChildren().addAll(usernameField, passwordField, continueButton);
		continueButton.setOnAction(
				event -> loginHandler.handle(new LoginEvent(usernameField.getText(), passwordField.getText(), event)));

		pane.setBackground(new Background(new BackgroundFill(new Color(0.23, 0.23, 0.23, 1), null, null)));
		pane.setPadding(new Insets(25));

		for (Node n : pane.getChildren())
			n.setStyle(
					"-fx-background-color: darkgray; -fx-border-radius: 0px; -fx-border-style: solid; -fx-border-width: 3px; -fx-border-color:white; -fx-text-fill: white;");
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

	@Override
	public void hide() {
		stage.hide();
	}

	@Override
	public void close() {
		stage.close();
	}

}
