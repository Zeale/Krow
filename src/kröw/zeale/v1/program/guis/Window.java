package kröw.zeale.v1.program.guis;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import krow.zeale.guis.home.HomeWindow;
import kröw.libs.Construct;
import kröw.zeale.v1.program.core.Kröw;

public class Window extends Application {

	private double xOffset, yOffset;

	private static Stage stage;

	private static Scene previousScene;
	private static Window controller;

	private static Window previousController;

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

	public void initialize() {

	}

	@Override
	public void start(final Stage primaryStage) throws Exception {
		Window.stage = primaryStage;
		Window.setScene(HomeWindow.class, "Home.fxml");
		primaryStage.initStyle(StageStyle.UNDECORATED);
		primaryStage.setTitle(Kröw.NAME);
		primaryStage.show();
	}

	@Override
	public void stop() throws Exception {
		for (final Construct c : Kröw.INSTANCE.getConstructs()) {
			final File cf = new File(Kröw.CONSTRUCT_SAVE_DIRECTORY, c.getName() + ".const");
			Kröw.saveObject(c, cf);

		}
		super.stop();
	}

	public static Object getController() {
		return Window.controller;
	}

	public static Window getPreviousController() {
		return Window.previousController;
	}

	public static Scene getPreviousScene() {
		return Window.previousScene;
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
		final FXMLLoader loader = new FXMLLoader(cls.getResource(scene));
		Window.previousScene = Window.stage.getScene();
		Window.previousController = Window.controller;
		Window.stage.setScene(new Scene(loader.load()));
		Window.controller = loader.<Window>getController();
		if (Kröw.DEBUG_MODE) {
			System.out.println();
			System.out.println(Window.previousController == null ? "The previous controller's value is null."
					: "The previous controller's value is not null.");
			System.out.println(Window.controller == null ? "The current controller's value is null."
					: "The current controller's value is not null.");
		}
	}

	public static void setScene(final String scene) throws IOException {
		final FXMLLoader loader = new FXMLLoader(Window.class.getResource(scene));
		Window.previousScene = Window.stage.getScene();
		Window.previousController = Window.controller;
		Window.stage.setScene(new Scene(loader.load()));
		Window.controller = loader.<Window>getController();
		if (Kröw.DEBUG_MODE) {
			System.out.println();
			System.out.println(Window.previousController == null ? "The previous controller's value is null."
					: "The previous controller's value is not null.");
			System.out.println(Window.controller == null ? "The current controller's value is null."
					: "The current controller's value is not null.");
		}

	}

	public static void setSceneToPreviousScene() {
		final Scene tempScene = Window.getPreviousScene();
		Window.previousScene = Window.stage.getScene();
		Window.stage.setScene(tempScene);
		if (Window.previousController != null)
			Window.previousController.initialize();

		final Window tempController = Window.getPreviousController();
		Window.previousController = Window.controller;
		Window.controller = tempController;

		if (Kröw.DEBUG_MODE) {
			System.out.println();
			System.out.println(Window.previousController == null ? "The previous controller's value is null."
					: "The previous controller's value is not null.");
			System.out.println(Window.controller == null ? "The current controller's value is null."
					: "The current controller's value is not null.");
		}

	}
}
