package zeale.guis.math_app;

import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import krow.guis.GUIHelper;
import krow.pages.ScrollMenu;
import kröw.gui.ApplicationManager;
import zeale.guis.math_app.controllers.Calculator;

public class MathApp extends ScrollMenu {

	/**
	 * <p>
	 * <b>THIS ICON REMAINS HERE ONLY FOR VISUAL PURPOSES FOR DEVELOPERS. IT SHOULD
	 * NOT BE INCLUDED IN THE DISTRIBUTED PROGRAM.</b>
	 * <p>
	 * Copyright of this icon does not belong to us. This icon is solely used to
	 * visualize what a calculator icon would look like when placed in this scroll
	 * menu.
	 */
	public static final Image CALCULATOR_ICON = new Image("/krow/resources/graphics/math-app/calculator-icon.png");

	@Override
	public void initialize() {
		super.initialize();

		pane.getStyleClass().add("background");
		horizontalScroll.setId("HorizontalScroll");
		horizontalScroll.setEffect(new Reflection());

		GUIHelper.addDefaultSettings(GUIHelper.buildMenu(pane));
		GUIHelper.applyShapeBackground(pane);
	}

	@Override
	protected void loadDefaultImages() {
		final ImageView calculator = new ImageView(CALCULATOR_ICON),
				statistics = new ImageView("/krow/resources/Statistics.png");
		calculator.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			ApplicationManager.setScene(Calculator.class.getResource("Calculator.fxml"));
		});
		calculator.setPickOnBounds(true);

		statistics.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
			// TODO Add statistics page
			// WindowManager.setScene(Statistics.class);
		});

		horizontalScroll.getChildren().addAll(calculator, statistics);

	}

}
