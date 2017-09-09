package zeale.guis.math_module;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import krow.scene.ScrollMenu;

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
		addImage(new ImageView(CALCULATOR_ICON));
	}

}
