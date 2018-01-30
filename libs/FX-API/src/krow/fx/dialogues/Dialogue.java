package krow.fx.dialogues;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Dialogue<PT extends Parent> {
	protected final Stage stage;
	/**
	 * Both {@link #root} and {@link #pane} refer to the root {@link Parent} of the
	 * scene graph. The root parent is what is passed into this class's constructor,
	 * and is also obtainable from calling {@link Scene#getRoot()} on the
	 * {@link Scene} returned from {@link #getScene()}.
	 */
	protected final PT root, pane;

	public Dialogue(PT root, Stage stage) {
		this.root = pane = root;
		this.stage = stage;
		stage.setScene(new Scene(root));
		stage.setOnCloseRequest(event -> event.consume());
		stage.setAlwaysOnTop(true);
		getScene().setFill(Color.TRANSPARENT);
	}

	protected final Scene getScene() {
		return stage.getScene();
	}

	protected void build() {
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
