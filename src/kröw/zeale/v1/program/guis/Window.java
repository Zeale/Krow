package kröw.zeale.v1.program.guis;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import krow.zeale.guis.home.HomeWindow;
import kröw.libs.MindsetObject;
import kröw.zeale.v1.program.core.Kröw;

public class Window extends Application {

	private double xOffset, yOffset;

	public Window() {
	}

	private static Stage stage;

	private static Scene previousScene;

	public static final Image LIGHT_CROW, DARK_CROW;

	static {
		Image dark = null, light = null;
		try {
			dark = new Image("/krow/zeale/DarkKröw.png");
		} catch (final IllegalArgumentException e) {
			System.err.println("The Dark Crow icon could not be loaded. Only the Light Crow Icon will be available.");

		}

		try {
			light = new Image("krow/zeale/LightKröw.png");
		} catch (final IllegalArgumentException e) {
			if (dark == null)
				System.err.println(
						"The Light Crow icon could not be loaded either! The icons will be set to the default coffee mug.");
			else
				System.err
						.println("The Light Crow icon could not be loaded. Only the Dark Crow icon will be available.");
		}

		DARK_CROW = dark;
		LIGHT_CROW = light;
	}

	private static Window controller;

	private static Window previousController;

	public static Object getController() {
		return Window.controller;
	}

	public static Window getPreviousController() {
		return Window.previousController;
	}

	public static Scene getPreviousScene() {
		return Window.previousScene;
	}

	public static Stage getStage() {
		return Window.stage;
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
			System.out.println(Window.previousController == null ? "The previous controller of the window is null."
					: "The previous controller of the window is not null.");
			System.out.println(Window.controller == null ? "The current controller of the window is null."
					: "The current controller of the window is not null.");
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
			System.out.println(Window.previousController == null ? "The previous controller of the window is null."
					: "The previous controller of the window is not null.");
			System.out.println(Window.controller == null ? "The current controller of the window is null."
					: "The current controller of the window is not null.");
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
			System.out.println(Window.previousController == null ? "The previous controller of the window is null."
					: "The previous controller of the window is not null.");
			System.out.println(Window.controller == null ? "The current controller of the window is null."
					: "The current controller of the window is not null.");
		}

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
		if (Window.DARK_CROW != null)
			primaryStage.getIcons().add(Window.DARK_CROW);
		else if (Window.LIGHT_CROW != null)
			primaryStage.getIcons().add(Window.LIGHT_CROW);
		primaryStage.show();
	}

	@Override
	public void stop() throws Exception {
		MindsetObject.saveObjects();
		super.stop();
	}

}
