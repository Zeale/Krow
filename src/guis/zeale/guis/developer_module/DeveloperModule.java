package zeale.guis.developer_module;

import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.PickResult;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ScrollEvent.HorizontalTextScrollUnits;
import javafx.scene.input.ScrollEvent.VerticalTextScrollUnits;
import javafx.scene.layout.Pane;
import krow.guis.GUIHelper;
import krow.scene.HorizontalMultiScrollBox;
import kröw.core.Kröw;
import kröw.core.managers.WindowManager.Page;

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

		// Position our container.
		scroll.setLayoutX(0);
		scroll.setLayoutY(Kröw.getSystemProperties().getScreenHeight() / 2 - scroll.getPrefHeight() / 2);

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

		GUIHelper.addDefaultSettings(GUIHelper.buildMenu(root));
		GUIHelper.applyShapeBackground(root);

		loadDefaultItems();
	}

	private void loadDefaultItems() {

	}

}
