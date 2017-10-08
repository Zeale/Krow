package public_test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import krow.scene.VerticalScrollBox;

public class Test extends Application {
	public static void main(final String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Make our scroll control
		VerticalScrollBox vsb = new VerticalScrollBox();
		// Add a child
		vsb.getChildren().add(new ImageView(new Image("/krow/resources/ChatRoomIcon_hd.png")));
		// Set up stage stuff
		primaryStage.setScene(new Scene(vsb));
		// Testing if a bug is caused by fullscreen or not. This line can be
		// removed.
		primaryStage.setFullScreen(true);
		// Add a bg because why not?
		vsb.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, null)));
		// Add this to make sure that something shows up if the image (added
		// above) doesn't work
		vsb.getChildren().add(new Button("Test"));
		// Show the screen.
		primaryStage.show();

	}

}
