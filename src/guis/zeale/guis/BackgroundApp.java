package zeale.guis;

import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import krow.backgrounds.ImageBackground;
import krow.backgrounds.ShapeBackground;
import krow.guis.GUIHelper;
import krow.guis.GUIHelper.MenuBox;
import kröw.gui.Application;

public class BackgroundApp extends Application {

	@FXML
	private AnchorPane root, visualizerPane;
	@FXML
	private Accordion backgroundMenu;
	@FXML
	private MenuBox sideMenu;

	@Override
	public void initialize() {

		sideMenu = GUIHelper.buildMenu(root);
		GUIHelper.addDefaultSettings(sideMenu);
		sideMenu.getParentWrapper().setVisible(false);

		applyDefaultBackground(visualizerPane);

		Rectangle clipping = new Rectangle();

		visualizerPane.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
			clipping.setWidth(newValue.getWidth());
			clipping.setHeight(newValue.getHeight());
		});

		clipping.setMouseTransparent(true);

		visualizerPane.setClip(clipping);

	}

	@FXML
	private void showMenu() {
		if (sideMenu.getParentWrapper().isVisible()) {
			sideMenu.getParentWrapper().setVisible(false);
			backgroundMenu.setVisible(true);
		} else {
			sideMenu.getParentWrapper().setVisible(true);
			backgroundMenu.setVisible(false);
		}
	}

	@FXML
	private void rainbowPreset() {
		getDefaultBackground().dispose();
		ShapeBackground sb = new ShapeBackground();
		sb.addRandomShapes(50);
		sb.setColorAnimations(true, ShapeBackground.ColorAnimation.rainbow(5, false, true));
		sb.playColorAnimations();
		sb.show(visualizerPane);
		setDefaultBackground(sb);
	}

	@FXML
	private void cookiePlusBackground() {
		getDefaultBackground().dispose();
		ImageBackground background = new ImageBackground(() -> {
			ImageView view = new ImageView(new Image("/krow/resources/graphics/cookie+256px.png"));
			view.setFitHeight(75);
			view.setFitWidth(75);
			view.setEffect(new DropShadow());
			return view;
		});
		setDefaultBackground(background);
		getDefaultBackground().show(visualizerPane);
		background.setImageCount(40);
	}

	@FXML
	private void cookieBackground() {
		getDefaultBackground().dispose();
		ImageBackground background = new ImageBackground(() -> {
			ImageView view = new ImageView(new Image("/krow/resources/graphics/cookie256px.png"));
			view.setFitHeight(75);
			view.setFitWidth(75);
			view.setEffect(new DropShadow());
			return view;
		});
		setDefaultBackground(background);
		getDefaultBackground().show(visualizerPane);
		background.setImageCount(40);
	}

}
