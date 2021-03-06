package zeale.guis.developer_page;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.PickResult;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ScrollEvent.HorizontalTextScrollUnits;
import javafx.scene.input.ScrollEvent.VerticalTextScrollUnits;
import javafx.scene.layout.Pane;
import krow.fx.scene.HorizontalMultiScrollBox;
import krow.fx.scene.HorizontalScrollBox;
import krow.guis.GUIHelper;
import kr�w.core.Kr�w;
import kr�w.gui.Application;
import kr�w.gui.ApplicationManager;
import zeale.guis.Home;
import zeale.guis.schedule_app.ScheduleApp;
import zeale.windowbuilder.api.WindowBuilder;

public class DeveloperApp extends Application {

	private final static Image TESTING_ICON = new Image("krow/resources/Testing.png");

	@FXML
	private Pane root;
	@FXML
	private HorizontalMultiScrollBox scroll;

	@Override
	public void initialize() {
		if (root == null)
			throw new RuntimeException("Unspecified pane.");
		if (scroll == null)
			scroll = new HorizontalMultiScrollBox();

		root.setPrefSize(Kr�w.scaleWidth(Kr�w.getSystemProperties().getScreenWidth()),
				Kr�w.scaleHeight(Kr�w.getSystemProperties().getScreenHeight()));
		root.setLayoutX(0);
		root.setLayoutY(0);

		if (!root.getChildren().contains(scroll))
			root.getChildren().add(scroll);

		// Apply sizing to our container.
		scroll.setPrefWidth(Kr�w.getSystemProperties().getScreenWidth());
		scroll.setForceHeight(HorizontalScrollBox.DEFAULT_NODE_HEIGHT);
		scroll.setForceWidth(Kr�w.getSystemProperties().getScreenWidth());

		// Position our container.

		scroll.setLayoutX(0);
		scroll.setLayoutY(Kr�w.getSystemProperties().getScreenHeight() / 2 - scroll.getForceHeight() / 2);

		scroll.setOnKeyPressed(event -> {
			if (event.getCode().equals(KeyCode.LEFT))
				scroll.fireEvent(new ScrollEvent(ScrollEvent.SCROLL, 0, 0, 0, 0, event.isShiftDown(),
						event.isControlDown(), event.isAltDown(), event.isMetaDown(), true, false, 0, 1, 0, 1,
						HorizontalTextScrollUnits.NONE, 0, VerticalTextScrollUnits.NONE, 0, 0,
						new PickResult(scroll, 0, 0)));
			else if (event.getCode().equals(KeyCode.RIGHT))
				scroll.fireEvent(new ScrollEvent(ScrollEvent.SCROLL, 0, 0, 0, 0, event.isShiftDown(),
						event.isControlDown(), event.isAltDown(), event.isMetaDown(), true, false, 0, -1, 0, -1,
						HorizontalTextScrollUnits.NONE, 0, VerticalTextScrollUnits.NONE, 0, 0,
						new PickResult(scroll, 0, 0)));
		});

		// Put this in front of the verticalScroll container.
		scroll.toFront();

		scroll.setStyle(
				"-fx-background-color:  linear-gradient(to right, #00000020 0%, #000000A8 45%, #000000A8 55%, #00000020 100%);");

		scroll.setFocusTraversable(true);

		final EventHandler<KeyEvent> keyHandler = event -> {
			if (event.getCode() == KeyCode.D && event.isShiftDown() && event.isControlDown()) {
				ApplicationManager.setScene(Home.class.getResource("Home.fxml"));
				event.consume();
			}
		};

		scroll.addEventFilter(KeyEvent.KEY_PRESSED, keyHandler);

		GUIHelper.addDefaultSettings(GUIHelper.buildMenu(root));
		GUIHelper.applyShapeBackground(root);

		loadDefaultItems();
	}

	private void loadDefaultItems() {
		final ImageView console = new ImageView("/krow/resources/graphics/developer-app/Console.png");
		console.setOnMouseClicked(event -> {
			ApplicationManager.setScene(ConsoleApp.class.getResource("ConsoleApp.fxml"));
		});

		final ImageView scheduler = new ImageView("/krow/resources/graphics/schedule-app/Schedule.png");
		scheduler.setOnMouseClicked(event -> {
			ApplicationManager.setScene(ScheduleApp.class.getResource("ScheduleApp.fxml"));
		});
		scheduler.setPickOnBounds(true);

		ImageView testing = new ImageView(TESTING_ICON);
		testing.setOnMouseClicked(event -> {
			ApplicationManager.setScene(TestingApp.class.getResource("TestingApp.fxml"));
		});
		testing.setEffect(new DropShadow());

		ImageView windowBuilder = new ImageView("/krow/resources/graphics/developer-app/Console.png");
		windowBuilder.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				if (DeveloperApp.windowBuilder == null)
					DeveloperApp.windowBuilder = new WindowBuilder();

				DeveloperApp.windowBuilder.show();
				DeveloperApp.windowBuilder.requestFocus();
			}
		});

		scroll.getChildren().addAll(console, scheduler, testing, windowBuilder);

		scroll.centerNodes();
	}

	private static WindowBuilder windowBuilder;

}
