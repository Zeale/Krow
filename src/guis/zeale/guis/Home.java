
package zeale.guis;

import java.io.IOException;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import krow.guis.GUIHelper;
import krow.guis.PopupHelper;
import krow.guis.ShapeFactory;
import krow.pages.ScrollMenu;
import kröw.core.Kröw;
import kröw.gui.Application;
import kröw.gui.ApplicationManager;
import kröw.gui.exceptions.NotSwitchableException;
import zeale.guis.developer_page.DeveloperApp;
import zeale.guis.math_app.MathApp;
import zeale.guis.math_app.controllers.Calculator;

public class Home extends ScrollMenu {

	private static final Image CHAT_ROOM_MENU_ICON = new Image("krow/resources/ChatRoomIcon_hd.png");

	@Override
	public boolean canSwitchPage(final Class<? extends Application> newSceneClass) {
		if (newSceneClass.equals(Home.class))
			return false;
		return true;
	}

	@Override
	public String getWindowFile() {
		return "Home.fxml";
	}

	@FXML
	@Override
	public void initialize() {

		super.initialize();

		// Styles. Yaaa...
		horizontalScroll.setStyle(
				"-fx-background-color:  linear-gradient(to right, #00000020 0%, #000000A8 45%, #000000A8 55%, #00000020 100%);");

		final EventHandler<KeyEvent> keyHandler = event -> {
			if (event.getCode() == KeyCode.D && event.isShiftDown() && event.isControlDown())
				try {
					ApplicationManager.setScene(DeveloperApp.class);
					event.consume();
				} catch (InstantiationException | IllegalAccessException | IOException | NotSwitchableException e) {
					e.printStackTrace();
				}
		};

		horizontalScroll.setFocusTraversable(true);
		horizontalScroll.setOnKeyPressed(keyHandler);

		GUIHelper.addDefaultSettings(GUIHelper.buildMenu(pane));
		applyDefaultBackground(pane);

	}

	@Override
	protected final void loadDefaultImages() {
		// Now to add the default images to our horizontal scroll container.
		final ImageView chatRoom = new ImageView(CHAT_ROOM_MENU_ICON);
		chatRoom.setOnMouseClicked(event -> {
			try {
				ApplicationManager.setScene(ChatRoom.class);
			} catch (InstantiationException | IllegalAccessException | IOException | NotSwitchableException e1) {
				e1.printStackTrace();
			}
		});
		chatRoom.setPickOnBounds(true);

		final ImageView krow = new ImageView(new Image(Kröw.IMAGE_KRÖW_LOCATION));
		// This code assures that clicking in a transparent portion of the image
		// will still cause a click to be registered by the event handler.
		// Event handler
		krow.setOnMouseClicked(event -> {
			try {
				ApplicationManager.setScene(Tools.class);
			} catch (InstantiationException | IllegalAccessException | IOException
					| NotSwitchableException e) {
				e.printStackTrace();
			}
		});

		final ImageView settings = new ImageView("krow/resources/Settings.png");
		settings.setOnMouseClicked(event -> {
			try {
				ApplicationManager.setScene(Settings.class);
			} catch (InstantiationException | IllegalAccessException | IOException e) {
				e.printStackTrace();
			} catch (final NotSwitchableException e) {
				e.printStackTrace();
			}
		});

		final ImageView statistics = new ImageView("krow/resources/Statistics.png");
		statistics.setOnMouseClicked(event -> {
			try {
				ApplicationManager.setScene(Statistics.class);
			} catch (InstantiationException | IllegalAccessException | IOException | NotSwitchableException e) {
				e.printStackTrace();
			}
		});
		statistics.setPickOnBounds(true);

		final ImageView mathApp = new ImageView(MathApp.CALCULATOR_ICON);
		mathApp.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			try {
				ApplicationManager.setScene(Calculator.class);
			} catch (InstantiationException | IllegalAccessException | IOException | NotSwitchableException e) {
				e.printStackTrace();
			}
		});
		mathApp.setPickOnBounds(true);

		final Shape backgroundShape = ShapeFactory.buildRegularShape(Kröw.scaleHeight(100),
				(int) (Math.random() * 5 + 3));
		backgroundShape.setOnMouseClicked(event -> ApplicationManager.spawnLabelAtMousePos("WIP", Color.FIREBRICK));
		backgroundShape.setPickOnBounds(true);
		backgroundShape.setFill(Color.TRANSPARENT);
		backgroundShape.setStroke(Color.WHITE);
		PopupHelper.addHoverPopup(backgroundShape, Color.FIREBRICK, "Work In Progress");
		// TODO: Add a right click popup with a screensaver option.
		backgroundShape.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() == MouseButton.PRIMARY)
				try {
					ApplicationManager.setScene(BackgroundApp.class);
				} catch (InstantiationException | IllegalAccessException | IOException | NotSwitchableException e) {
					e.printStackTrace();
				}

		});

		horizontalScroll.getChildren().addAll(settings, krow, chatRoom, statistics, mathApp, backgroundShape);
		horizontalScroll.selectCenter();

		PopupHelper.addHoverPopup(settings, GUIHelper.makeBoldLabel("Settings App", 18));
		PopupHelper.addHoverPopup(krow, GUIHelper.makeBoldLabel("Tools App", 18));
		PopupHelper.addHoverPopup(chatRoom, GUIHelper.makeBoldLabel("Chat Room App", 18));
		PopupHelper.addHoverPopup(statistics, GUIHelper.makeBoldLabel("Statistics App", 18));
		PopupHelper.addHoverPopup(mathApp, GUIHelper.makeBoldLabel("Math App", 18));

		final Label mathAppCalculatorLbl = new Label("Calculator"),
				mathAppStatisticsLbl = new Label("Statistics");
		mathAppStatisticsLbl.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton().equals(MouseButton.PRIMARY))
				try {
					ApplicationManager.setScene(Calculator.class).getController().enableStatsMode();
				} catch (InstantiationException | IllegalAccessException | IOException | NotSwitchableException e) {
					e.printStackTrace();
				}
		});

		mathAppCalculatorLbl.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton().equals(MouseButton.PRIMARY))
				try {
					ApplicationManager.setScene(Calculator.class);
				} catch (InstantiationException | IllegalAccessException | IOException | NotSwitchableException e) {
					e.printStackTrace();
				}
		});

		PopupHelper.addRightClickPopup(mathApp, mathAppCalculatorLbl, mathAppStatisticsLbl);

	}

}