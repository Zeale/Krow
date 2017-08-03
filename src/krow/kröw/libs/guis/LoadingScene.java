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

	/* Default Values */
	private static final Paint DEFAULT_SCENE_BACKGROUND = Color.TRANSPARENT;
	private final Label loadingLbl = new Label();
	private final ProgressIndicator progressIndicator = new ProgressIndicator();

	private BorderPane pane;

	{
		setFill(DEFAULT_SCENE_BACKGROUND);// Set the default bg.
		// It may be set to another value depending on the constructor
		// that is called.
	}

	{
		pane = (BorderPane) getRoot();
		pane.setCenter(loadingLbl);
		pane.setBottom(progressIndicator);
	}

	/* Constructors */
	public LoadingScene() {
		super(new BorderPane());
	}

	public LoadingScene(final double width, final double height) {
		super(new BorderPane(), width, height);
	}

	public LoadingScene(final double width, final double height, final boolean depthBuffer) {
		super(new BorderPane(), width, height, depthBuffer);
	}

	public LoadingScene(final double width, final double height, final boolean depthBuffer,
			final SceneAntialiasing antiAliasing) {
		super(new BorderPane(), width, height, depthBuffer, antiAliasing);
	}

	public LoadingScene(final double width, final double height, final Paint fill) {
		this(width, height);
		setFill(fill);
	}

	/* Set up our loading scene. */

	public LoadingScene(final Paint fill) {
		this();
		setFill(fill);
	}

	public void initialize() {
		pane.setStyle("-fx-background-color: #000000C4;");
		Window.getStage().getScene().setFill(Color.TRANSPARENT);
		Window.getStage().setFullScreen(true);
	}

}