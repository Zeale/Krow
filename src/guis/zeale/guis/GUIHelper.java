package zeale.guis;

import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import kröw.core.Kröw;

class GUIHelper {

	private static final Interpolator MENU_BUTTON_TRANSITION_INTERPOLATOR = Interpolator.EASE_BOTH;

	private static final Duration MENU_BUTTON_ANIMATION_DURATION = Duration.seconds(0.8);

	private static final double MENU_BUTTON_RECTANGLE_WIDTH = 25, MENU_BUTTON_RECTANGLE_HEIGHT = 5,
			MENU_BUTTON_RECTANGLE_X = Kröw.SCREEN_WIDTH - (double) 50 / 1920 * Kröw.SCREEN_WIDTH,
			MENU_BUTTON_Y = (double) 40 / 1920 * Kröw.SCREEN_HEIGHT,
			MENU_BUTTON_RECTANGLE_SPACING = (double) 20 / 1920 * Kröw.SCREEN_HEIGHT;

	private static final byte MENU_BAR_ANIMATION_ROTATION_COUNT = 1;

	private static final Color MENU_BUTTON_START_COLOR = Color.BLACK, MENU_BUTTON_END_COLOR = Color.WHITE;

	public static void buildCloseButton(Pane pane) {

		Shape menubarTop = new Rectangle(MENU_BUTTON_RECTANGLE_WIDTH, MENU_BUTTON_RECTANGLE_HEIGHT),
				menubarBottom = new Rectangle(MENU_BUTTON_RECTANGLE_WIDTH, MENU_BUTTON_RECTANGLE_HEIGHT);
		menubarTop.setLayoutX(MENU_BUTTON_RECTANGLE_X);
		menubarBottom.setLayoutX(MENU_BUTTON_RECTANGLE_X);
		menubarTop.setLayoutY(MENU_BUTTON_Y);
		menubarBottom.setLayoutY(MENU_BUTTON_RECTANGLE_SPACING + MENU_BUTTON_Y);

		menubarTop.setStroke(MENU_BUTTON_START_COLOR);
		menubarTop.setFill(MENU_BUTTON_START_COLOR);
		menubarBottom.setStroke(MENU_BUTTON_START_COLOR);
		menubarBottom.setFill(MENU_BUTTON_START_COLOR);

		FillTransition topFill = new FillTransition(MENU_BUTTON_ANIMATION_DURATION, menubarTop),
				bottomFill = new FillTransition(MENU_BUTTON_ANIMATION_DURATION, menubarBottom);
		topFill.setInterpolator(MENU_BUTTON_TRANSITION_INTERPOLATOR);
		bottomFill.setInterpolator(MENU_BUTTON_TRANSITION_INTERPOLATOR);

		RotateTransition topRot = new RotateTransition(MENU_BUTTON_ANIMATION_DURATION, menubarTop),
				bottomRot = new RotateTransition(MENU_BUTTON_ANIMATION_DURATION, menubarBottom);
		topRot.setInterpolator(MENU_BUTTON_TRANSITION_INTERPOLATOR);
		bottomRot.setInterpolator(MENU_BUTTON_TRANSITION_INTERPOLATOR);

		TranslateTransition topTrans = new TranslateTransition(MENU_BUTTON_ANIMATION_DURATION, menubarTop),
				bottomTrans = new TranslateTransition(MENU_BUTTON_ANIMATION_DURATION, menubarBottom);

		AnchorPane cover = new AnchorPane();

		cover.setPrefSize(MENU_BUTTON_RECTANGLE_WIDTH * 2 + MENU_BUTTON_RECTANGLE_SPACING,
				MENU_BUTTON_RECTANGLE_WIDTH * 2 + MENU_BUTTON_RECTANGLE_SPACING);
		cover.setLayoutX(Kröw.SCREEN_WIDTH - cover.getPrefWidth());
		cover.setLayoutY(0);

		cover.setOnMouseEntered(event -> {
			topFill.stop();
			bottomFill.stop();
			topRot.stop();
			bottomRot.stop();
			topTrans.stop();
			bottomTrans.stop();

			topFill.setFromValue((Color) menubarTop.getFill());
			bottomFill.setFromValue((Color) menubarBottom.getFill());
			topFill.setToValue(MENU_BUTTON_END_COLOR);
			bottomFill.setToValue(MENU_BUTTON_END_COLOR);

			topRot.setFromAngle(menubarTop.getRotate());
			bottomRot.setFromAngle(menubarBottom.getRotate());
			double rotationCount = 45 + MENU_BAR_ANIMATION_ROTATION_COUNT * 180;
			topRot.setToAngle(rotationCount);
			bottomRot.setToAngle(-rotationCount);

			topTrans.setFromY(menubarTop.getTranslateY());
			bottomTrans.setFromY(menubarBottom.getTranslateY());
			topTrans.setToY(MENU_BUTTON_RECTANGLE_SPACING / 2);
			bottomTrans.setToY(-MENU_BUTTON_RECTANGLE_SPACING / 2);

			topFill.play();
			bottomFill.play();
			topRot.play();
			bottomRot.play();
			topTrans.play();
			bottomTrans.play();
		});
		cover.setOnMouseExited(event -> {
			topFill.stop();
			bottomFill.stop();
			topRot.stop();
			bottomRot.stop();
			topTrans.stop();
			bottomTrans.stop();

			topFill.setFromValue((Color) menubarTop.getFill());
			bottomFill.setFromValue((Color) menubarBottom.getFill());
			topFill.setToValue(MENU_BUTTON_START_COLOR);
			bottomFill.setToValue(MENU_BUTTON_START_COLOR);

			topRot.setFromAngle(menubarTop.getRotate());
			bottomRot.setFromAngle(menubarBottom.getRotate());
			topRot.setToAngle(0);
			bottomRot.setToAngle(0);

			topTrans.setFromY(menubarTop.getTranslateY());
			bottomTrans.setFromY(menubarBottom.getTranslateY());
			topTrans.setToY(0);
			bottomTrans.setToY(0);

			topFill.play();
			bottomFill.play();
			topRot.play();
			bottomRot.play();
			topTrans.play();
			bottomTrans.play();

		});

		pane.getChildren().add(menubarTop);
		pane.getChildren().add(menubarBottom);
		pane.getChildren().add(cover);

	}

}
