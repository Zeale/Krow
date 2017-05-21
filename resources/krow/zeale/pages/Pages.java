package krow.zeale.pages;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import krow.zeale.guis.home.HomeWindow;
import wolf.mindset.MindsetObject;
import wolf.zeale.Wolf;
import wolf.zeale.guis.Window;

public class Pages extends Window {

	@FXML
	private FlowPane centerPane;
	@FXML
	private FlowPane leftPane, rightPane;

	@FXML
	private MenuBar menuBar;

	private static MindsetObject object;

	public static void openNavigationPage() {

	}

	public static void openPage(final MindsetObject object) {
		Pages.object = object;
		try {
			Window.setScene(Pages.class, "ObjectPage.fxml");
		} catch (final IOException e) {
			e.printStackTrace();
		}

	}

	@FXML
	private void goToNormalWindow() {
		try {
			Window.setScene(HomeWindow.class, "Home.fxml");
		} catch (final IOException e) {
			if (Wolf.DEBUG_MODE)
				e.printStackTrace();
		}
	}

	@FXML
	private void onCloseRequested() {
		Platform.exit();
	}

	@Override
	public String getWindowFile() {
		return "ObjectPage.fxml";
	}

	@Override
	public void initialize() {
		final Text title = new Text(Pages.object.getName());
		title.setStyle("-fx-fill:red; -fx-font-size:30pt; -fx-font-weight:bold;");
		title.setY(50);

		centerPane.getChildren().add(title);
		Window.setPaneDraggableByNode(menuBar);

	}
}
