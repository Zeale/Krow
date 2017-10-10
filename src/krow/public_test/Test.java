package public_test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import krow.scene.MultiScrollBox;
import kröw.core.Kröw;

public class Test extends Application {
	public static void main(final String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Make our scroll control
		MultiScrollBox menu = new MultiScrollBox();
		// Set up stage stuff
		primaryStage.setScene(new Scene(menu));

		primaryStage.setFullScreen(true);
		// Add a bg because why not?
		menu.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, null)));
		// Add this to make sure that something shows up if the image (added
		// above) doesn't work
		menu.getChildren().add(new Button("Test"));

		// Build a random node.
		ImageView krow = new ImageView(Kröw.IMAGE_KRÖW);
		krow.setFitHeight(100);
		krow.setFitWidth(100);

		// Add a menu to this MultiScrollMenu. Multi Scroll Menus can have
		// vertical scroll menus in them as well as regular nodes.
		menu.new Menu().getMenuItemList().add(krow);
		// Scrolling on the krow image will move it vertically as well as move
		// all the other elements horizontally.

		menu.setLayoutX(250);
		menu.setLayoutY(250);

		// Show the screen.
		primaryStage.show();

	}

}
