package kröw.libs.guis;

import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class LoadingScene extends Scene {

	/* Fields */

	private Label loadingLbl = new Label();
	private ProgressIndicator progressIndicator = new ProgressIndicator();
	private BorderPane pane;

	/* Default Values */
	private static final Paint DEFAULT_SCENE_BACKGROUND = Color.TRANSPARENT;

	{
		setFill(DEFAULT_SCENE_BACKGROUND);// Set the default bg.
		// It may be set to another value depending on the constructor
		// that is called.
	}

	/* Constructors */
	public LoadingScene() {
		super(new BorderPane());
	}

	public LoadingScene(Paint fill) {
		this();
		setFill(fill);
	}

	public LoadingScene(double width, double height) {
		super(new BorderPane(), width, height);
	}

	public LoadingScene(double width, double height, Paint fill) {
		this(width, height);
		setFill(fill);
	}

	public LoadingScene(double width, double height, boolean depthBuffer) {
		super(new BorderPane(), width, height, depthBuffer);
	}

	public LoadingScene(double width, double height, boolean depthBuffer, SceneAntialiasing antiAliasing) {
		super(new BorderPane(), width, height, depthBuffer, antiAliasing);
	}

	/* Set up our loading scene. */

	{
		pane = (BorderPane) getRoot();
		pane.setCenter(loadingLbl);
		pane.setBottom(progressIndicator);
	}

	public void initialize() {
		pane.setStyle("-fx-background-color: #000000C4;");
		Window.getStage().getScene().setFill(Color.TRANSPARENT);
		Window.getStage().setFullScreen(true);
	}

}