package krow.fx.dialogues;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Dialogue<PT extends Parent> {
	protected final Stage stage = new Stage(StageStyle.TRANSPARENT);
	/**
	 * Both {@link #root} and {@link #pane} refer to the root {@link Parent} of the
	 * scene graph. The root parent is what is passed into this class's constructor,
	 * and is also obtainable from calling {@link Scene#getRoot()} on the
	 * {@link Scene} returned from {@link #getScene()}.
	 */
	protected final PT root, pane;

	public Dialogue(PT root) {
		this.root = pane = root;
		stage.setScene(new Scene(root));
	}

	protected final Scene getScene() {
		return stage.getScene();
	}

	protected void build() {
		getScene().setFill(Color.TRANSPARENT);
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
