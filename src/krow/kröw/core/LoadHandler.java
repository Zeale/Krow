package kröw.core;

import java.util.Random;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoadHandler {

	private ProgressBar loadingBar = new ProgressBar(0);
	private ImageView splashscreenIcon = new ImageView();
	private TextField promptBox = new TextField();
	private Button submitPromptButton;
	private HBox bottomBox = new HBox(promptBox, submitPromptButton);
	private AnchorPane pane = new AnchorPane(splashscreenIcon, bottomBox);
	private Scene scene = new Scene(pane);
	private final Stage primaryStage;
	private Button continueButton = new Button("Continue...");
	private Stage stage = new Stage(StageStyle.TRANSPARENT);

	{
		build();
	}

	public LoadHandler(Stage primaryStage) {
		this.primaryStage = primaryStage;
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

	private void build() {
		stage.setOnCloseRequest(event -> event.consume());
		stage.setScene(scene);
		scene.setFill(Color.TRANSPARENT);

		pane.setMinHeight(542);
		pane.setMaxHeight(542);
		pane.setMinWidth(512);
		pane.setMaxWidth(512);

		scene.setFill(Color.TRANSPARENT);
		pane.setBackground(new Background((BackgroundFill) null));

		stage.setAlwaysOnTop(true);

		// NODES

		splashscreenIcon.setImage(getNewImage());
		splashscreenIcon.setPreserveRatio(true);
		splashscreenIcon.setFitWidth(512);

		bottomBox.setAlignment(Pos.CENTER);
		AnchorPane.setTopAnchor(splashscreenIcon, 0d);
		AnchorPane.setBottomAnchor(bottomBox, 0d);
		AnchorPane.setLeftAnchor(bottomBox, 0d);
		AnchorPane.setRightAnchor(bottomBox, 0d);

		Text failureText = new Text("Failed to show main window. Press done again to try again.");
		continueButton.setOnAction(event -> {
			primaryStage.show();
			if (!primaryStage.isShowing())
				if (!pane.getChildren().contains(failureText))
					pane.getChildren().add(1, failureText);
				else
					;
			else
				stage.close();
		});

		loadingBar.setMinWidth(512);
		continueButton.setMinWidth(512);

		submitPromptButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
			}
		});

	}

	public void show() {
		stage.show();
		stage.sizeToScene();
		stage.centerOnScreen();
	}

	public void doneLoading() {
		bottomBox.getChildren().set(0, continueButton);
	}

	public void setProgress(double progress) {
		loadingBar.setProgress(progress);
	}

	public ProgressBar getLoadingBar() {
		return loadingBar;
	}

}
