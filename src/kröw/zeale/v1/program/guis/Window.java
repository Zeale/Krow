package kröw.zeale.v1.program.guis;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import krow.zeale.guis.home.HomeWindow;
import kröw.zeale.v1.program.core.Kröw;

public class Window extends Application {

	private double xOffset, yOffset;

	private static Stage stage;

	public Window() {
	}

	public Stage getStage() {
		return Window.stage;
	}

	public final double getxOffset() {
		return xOffset;
	}

	public final double getyOffset() {
		return yOffset;
	}

	@Override
	public void start(final Stage primaryStage) throws Exception {
		Window.stage = primaryStage;
		Window.stage.setScene(new Scene(FXMLLoader.load(HomeWindow.class.getResource("Home.fxml"))));
		primaryStage.initStyle(StageStyle.UNDECORATED);
		primaryStage.setTitle(Kröw.NAME);
		primaryStage.show();
	}

	@Override
	public void stop() throws Exception {
		// TODO Save everything then close
		super.stop();
	}

	public static void setPaneDraggableByNode(final Node node) {
		new Object() {

			private double xOffset, yOffset;

			{
				node.setOnMousePressed(event -> {
					xOffset = Window.stage.getX() - event.getScreenX();
					yOffset = Window.stage.getY() - event.getScreenY();
				});

				node.setOnMouseDragged(event -> {
					Window.stage.setX(event.getScreenX() + xOffset);
					Window.stage.setY(event.getScreenY() + yOffset);
				});
			}

		};
	}

	public static void setScene(final Class<?> cls, final String scene) throws IOException {
		Window.stage.setScene(new Scene(FXMLLoader.load(cls.getResource(scene))));
	}

	public static void setScene(final Scene scene) {
		Window.stage.setScene(scene);
	}

	public static void setScene(final String scene) throws IOException {
		Window.stage.setScene(new Scene(FXMLLoader.load(Window.class.getResource(scene))));
	}
}
