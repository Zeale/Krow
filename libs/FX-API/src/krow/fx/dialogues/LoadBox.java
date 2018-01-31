package krow.fx.dialogues;

import java.util.concurrent.ExecutionException;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.ImageCursor;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public abstract class LoadBox extends Dialogue<AnchorPane> {

	private final ProgressBar loadingBar = new ProgressBar(0);
	private final ImageView splashscreenIcon = new ImageView();
	private final VBox flowBox = new VBox(splashscreenIcon, loadingBar);
	private final Button continueButton = new Button("Continue...");
	private Task<Boolean> loader;
	private Runnable onDoneLoading;

	private DoubleProperty size = new SimpleDoubleProperty(512);
	{
		size.addListener((ChangeListener<Number>) (observable, oldValue, newValue) -> resize(newValue.doubleValue()));
	}

	public void setOnDoneLoading(Runnable onDoneLoading) {
		this.onDoneLoading = onDoneLoading;
	}

	private final EventHandler<WorkerStateEvent> loadHandler = event -> {
		if (event.getSource().isRunning() || event.getEventType().equals(WorkerStateEvent.WORKER_STATE_SCHEDULED))
			return;
		// When done loading, we can show the continueButton ourselves.
		int pos = flowBox.getChildren().indexOf(loadingBar);
		flowBox.getChildren().set(pos, continueButton);
	};

	public void setLoader(Task<Boolean> loader) {
		if (this.loader != null)
			this.loader.removeEventHandler(WorkerStateEvent.ANY, loadHandler);
		this.loader = loader;
		loadingBar.progressProperty().bind(loader.progressProperty());
		loader.addEventHandler(WorkerStateEvent.ANY, loadHandler);
	}

	public void load() {
		loader.run();
	}

	public boolean getLoadResult() throws InterruptedException, ExecutionException {
		return loader.get();
	}

	{
		build();
	}

	public LoadBox(Stage loadStage) {
		super(new AnchorPane(), loadStage);
		pane.getChildren().addAll(flowBox);
	}

	protected abstract Image getNextImage();

	@Override
	protected void build() {

		resize(size.doubleValue());

		pane.setBackground(new Background((BackgroundFill) null));

		// NODES

		splashscreenIcon.setImage(getNextImage());
		splashscreenIcon.setPreserveRatio(true);

		// The mouse-enter, counterpart of the following call is made in #resize(double)
		// due to the dependency of the shadow's size on the size of the pane.
		splashscreenIcon.setOnMouseExited(event -> splashscreenIcon.setEffect(null));

		flowBox.setAlignment(Pos.CENTER);

		continueButton.setOnAction(event -> onDoneLoading.run());

		Image img = new Image("/krow/resources/Testing.png");
		loadingBar.setCursor(new ImageCursor(img, img.getWidth() / 2, img.getHeight() / 2));

		continueButton.setStyle(
				"-fx-background-color: darkgray; -fx-border-radius: 0px; -fx-border-style: solid; -fx-border-width: 3px; -fx-border-color: black; -fx-text-fill: black;");

	}

	protected void resize(double newSize) {

		// Add a bit to the height for the Button at the bottom.
		pane.setPrefHeight(newSize + 45d / 512 * newSize);// 30, scaled from 512 to newSize
		// Add a bit to the width for the edges of the image. This way, the image edges
		// can have drop shadows that won't get cut off by the edge of the window.
		pane.setPrefWidth(newSize * 1.5);// Defaults to 512 + 256

		flowBox.setPrefWidth(newSize);
		flowBox.setSpacing(5d / 512 * newSize);// 15 scaled from newSize

		// The excess space in pane totals to newSize/2. Since we want half of the
		// excess space on either side of this box...
		AnchorPane.setLeftAnchor(flowBox, newSize / 4);
		AnchorPane.setRightAnchor(flowBox, newSize / 4);

		splashscreenIcon.setFitWidth(newSize);

		loadingBar.setMinWidth(newSize);
		continueButton.setMinWidth(newSize);

		splashscreenIcon.setOnMouseEntered(
				event -> splashscreenIcon.setEffect(new DropShadow(35d / 512 * newSize, Color.BLACK)));

	}

	@Override
	public void show() {
		stage.show();
		stage.sizeToScene();
		stage.centerOnScreen();
		loader.run();
	}

	public void setProgress(double progress) {
		loadingBar.setProgress(progress);
	}

	public ProgressBar getLoadingBar() {
		return loadingBar;
	}

}
