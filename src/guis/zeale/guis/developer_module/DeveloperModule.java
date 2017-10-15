package zeale.guis.developer_module;

import java.io.IOException;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.PickResult;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ScrollEvent.HorizontalTextScrollUnits;
import javafx.scene.input.ScrollEvent.VerticalTextScrollUnits;
import javafx.scene.layout.Pane;
import krow.guis.GUIHelper;
import krow.scene.HorizontalMultiScrollBox;
import krow.scene.HorizontalMultiScrollBox.Menu;
import krow.scene.HorizontalScrollBox;
import krow.scene.VerticalScrollBox;
import kröw.core.Kröw;
import kröw.core.managers.WindowManager;
import kröw.core.managers.WindowManager.NotSwitchableException;
import kröw.core.managers.WindowManager.Page;
import zeale.guis.Home;

public class DeveloperModule extends Page {

	@FXML
	private Pane root;
	@FXML
	private HorizontalMultiScrollBox scroll;

	@Override
	public String getWindowFile() {
		return "DeveloperModule.fxml";
	}

	@Override
	public void initialize() {
		if (root == null)
			throw new RuntimeException("Unspecified pane.");
		if (scroll == null)
			scroll = new HorizontalMultiScrollBox();

		root.setPrefSize(Kröw.scaleWidth(Kröw.getSystemProperties().getScreenWidth()),
				Kröw.scaleHeight(Kröw.getSystemProperties().getScreenHeight()));
		root.setLayoutX(0);
		root.setLayoutY(0);

		if (!root.getChildren().contains(scroll))
			root.getChildren().add(scroll);

		// Apply sizing to our container.
		scroll.setPrefWidth(Kröw.getSystemProperties().getScreenWidth());
		scroll.setForceHeight(HorizontalScrollBox.NODE_HEIGHT);
		scroll.setForceWidth(Kröw.getSystemProperties().getScreenWidth());

		// Position our container.

		scroll.setLayoutX(0);
		scroll.setLayoutY(Kröw.getSystemProperties().getScreenHeight() / 2 - scroll.getForceHeight() / 2);

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

		EventHandler<KeyEvent> keyHandler = event -> {
			if (event.getCode() == KeyCode.D && event.isShiftDown() && event.isControlDown())
				try {
					WindowManager.setScene(Home.class);
					event.consume();
				} catch (InstantiationException | IllegalAccessException | IOException | NotSwitchableException e) {
					e.printStackTrace();
				}
		};

		scroll.addEventFilter(KeyEvent.KEY_PRESSED, keyHandler);

		GUIHelper.addDefaultSettings(GUIHelper.buildMenu(root));
		GUIHelper.applyShapeBackground(root);

		loadDefaultItems();
	}

	private void loadDefaultItems() {
		scroll.getChildren().add(new ImageView("krow/resources/Settings.png"));
		Menu m = scroll.new Menu();
		m.getMenuBox().setForceHeight(Kröw.getSystemProperties().getScreenHeight());
		m.getMenuBox();
		m.getMenuBox().setForceWidth(VerticalScrollBox.NODE_WIDTH);
		m.getMenuBox().setTranslateY(-scroll.getLayoutY());
		m.getMenuItemList().add(new ImageView("krow/resources/Settings.png"));
		m.getMenuItemList().add(new ImageView("krow/resources/Settings.png"));
		m.getMenuItemList().add(new ImageView("krow/resources/Settings.png"));
		scroll.centerNodes();
	}

}
