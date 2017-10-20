package krow.pages;

import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.PickResult;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ScrollEvent.HorizontalTextScrollUnits;
import javafx.scene.input.ScrollEvent.VerticalTextScrollUnits;
import javafx.scene.layout.Pane;
import krow.scene.HorizontalScrollBox;
import kröw.core.Kröw;
import kröw.core.managers.WindowManager.Page;

public abstract class ScrollMenu extends Page {

	@FXML
	protected Pane pane;
	@FXML
	protected HorizontalScrollBox horizontalScroll;

	@Override
	public void initialize() {

		if (pane == null)
			throw new RuntimeException("Unspecified pane.");
		if (horizontalScroll == null)
			horizontalScroll = new HorizontalScrollBox();

		pane.setPrefSize(Kröw.scaleWidth(Kröw.getSystemProperties().getScreenWidth()),
				Kröw.scaleHeight(Kröw.getSystemProperties().getScreenHeight()));
		pane.setLayoutX(0);
		pane.setLayoutY(0);

		if (!pane.getChildren().contains(horizontalScroll))
			pane.getChildren().add(horizontalScroll);

		// Apply sizing to our container.
		horizontalScroll.setPrefWidth(Kröw.getSystemProperties().getScreenWidth());

		// Position our container.
		horizontalScroll.setLayoutX(0);
		horizontalScroll
				.setLayoutY(Kröw.getSystemProperties().getScreenHeight() / 2 - horizontalScroll.getPrefHeight() / 2);

		horizontalScroll.setOnKeyPressed(event -> {
			if (event.getCode().equals(KeyCode.LEFT))
				horizontalScroll.fireEvent(new ScrollEvent(ScrollEvent.SCROLL, 0, 0, 0, 0, event.isShiftDown(),
						event.isControlDown(), event.isAltDown(), event.isMetaDown(), true, false, 0, 1, 0, 1,
						HorizontalTextScrollUnits.NONE, 0, VerticalTextScrollUnits.NONE, 0, 0,
						new PickResult(horizontalScroll, 0, 0)));
			else if (event.getCode().equals(KeyCode.RIGHT))
				horizontalScroll.fireEvent(new ScrollEvent(ScrollEvent.SCROLL, 0, 0, 0, 0, event.isShiftDown(),
						event.isControlDown(), event.isAltDown(), event.isMetaDown(), true, false, 0, -1, 0, -1,
						HorizontalTextScrollUnits.NONE, 0, VerticalTextScrollUnits.NONE, 0, 0,
						new PickResult(horizontalScroll, 0, 0)));
		});

		// Put this in front of the verticalScroll container.
		horizontalScroll.toFront();
		loadDefaultImages();
	}

	protected void loadDefaultImages() {
	}

}
