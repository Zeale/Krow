package zeale.guis.math_module;

import java.io.IOException;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import krow.guis.GUIHelper;
import krow.scene.ScrollMenu;
import kröw.core.managers.WindowManager;
import kröw.core.managers.WindowManager.NotSwitchableException;

public class MathModule extends ScrollMenu {

	@Override
	public String getWindowFile() {
		return "MathModule.fxml";
	}

	/**
	 * <p>
	 * <b>THIS ICON REMAINS HERE ONLY FOR VISUAL PURPOSES FOR DEVELOPERS. IT
	 * SHOULD NOT BE INCLUDED IN THE DISTRIBUTED PROGRAM.</b>
	 * <p>
	 * Copyright of this icon does not belong to us. This icon is solely used to
	 * visualize what a calculator icon would look like when placed in this
	 * scroll menu.
	 */
	public static final Image CALCULATOR_ICON = new Image("/krow/resources/graphics/math-module/calculator-icon.png");// TODO
																														// Change

	@Override
	protected void loadDefaultImages() {
		ImageView calculator = new ImageView(CALCULATOR_ICON);
		calculator.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				try {
					WindowManager.setScene(Calculator.class);
				} catch (InstantiationException | IllegalAccessException | IOException | NotSwitchableException e) {
					e.printStackTrace();
				}
			}
		});
		addImage(calculator);
	}

	@Override
	public void initialize() {
		super.initialize();

		pane.getStyleClass().add("background");
		horizontalScroll.setId("HorizontalScroll");
		horizontalScroll.setEffect(new Reflection());

		GUIHelper.addDefaultSettings(GUIHelper.buildMenu(pane));
		GUIHelper.applyShapeBackground(pane);
	}

}
