package krow.fx.dialogues;

import java.util.Random;
import java.util.concurrent.ExecutionException;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class LoadBox extends Dialogue<AnchorPane> {

	private final ProgressBar loadingBar = new ProgressBar(0);
	private final ImageView splashscreenIcon = new ImageView();
	private final HBox bottomBox = new HBox(splashscreenIcon, loadingBar);
	private final Button continueButton = new Button("Continue...");
	private Task<Boolean> loader;
	private Runnable onDoneLoading;

	public void setOnDoneLoading(Runnable onDoneLoading) {
		this.onDoneLoading = onDoneLoading;
	}

	private final EventHandler<WorkerStateEvent> loadHandler = event -> {
		if (event.getSource().isRunning() || event.getEventType().equals(WorkerStateEvent.WORKER_STATE_SCHEDULED))
			return;
		// When done loading, we can show the continueButton ourselves.
		bottomBox.getChildren().set(0, continueButton);
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
		pane.getChildren().addAll(splashscreenIcon, bottomBox);
	}

	private Image getNewImage() {
		int rand = new Random().nextInt(13) + 1;
		if (rand <= 4)
			return new Image("/krow/resources/Kröw_hd.png");
		else if (rand <= 8)
			return new Image("/krow/resources/Settings.png");
		else if (rand <= 12)
			return new Image("/krow/resources/Testing.png");
		else
			return new Image("/krow/resources/graphics/github120px.png");
	}

	protected void build() {
		pane.setMinHeight(542);
		pane.setMaxHeight(542);
		pane.setMinWidth(512);
		pane.setMaxWidth(512);

		pane.setBackground(new Background((BackgroundFill) null));

		// NODES

		splashscreenIcon.setImage(getNewImage());
		splashscreenIcon.setPreserveRatio(true);
		splashscreenIcon.setFitWidth(512);
		splashscreenIcon.setOnMouseEntered(event -> splashscreenIcon.setEffect(new DropShadow()));
		splashscreenIcon.setOnMouseExited(event -> splashscreenIcon.setEffect(null));

		bottomBox.setAlignment(Pos.CENTER);
		AnchorPane.setTopAnchor(splashscreenIcon, 0d);
		AnchorPane.setBottomAnchor(bottomBox, 0d);
		AnchorPane.setLeftAnchor(bottomBox, 0d);
		AnchorPane.setRightAnchor(bottomBox, 0d);
		continueButton.setOnAction(event -> onDoneLoading.run());

		loadingBar.setMinWidth(512);
		continueButton.setMinWidth(512);
	}

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
