package krow.fx.dialogues;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Dialogue {
	protected final Stage stage = new Stage(StageStyle.TRANSPARENT);
	protected final Scene scene;

	public Dialogue(Parent root) {
		this.scene = new Scene(root);
		build();
	}

	protected void build() {
		scene.setFill(Color.TRANSPARENT);
		stage.setScene(scene);
	}

	public void close() {
		stage.close();
	}

	public void hide() {
		stage.hide();
	}

	public void show() {
		stage.show();
		stage.sizeToScene();
		stage.centerOnScreen();
	}

}
