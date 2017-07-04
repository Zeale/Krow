package krow.zeale.guis.calculator;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import kröw.zeale.math.EquationParser;
import kröw.zeale.math.exceptions.EmptyEquationException;
import kröw.zeale.math.exceptions.IrregularCharacterException;
import kröw.zeale.math.exceptions.UnmatchedParenthesisException;
import wolf.zeale.guis.Window;

public class Calculator {

	private final Stage stage = new Stage();
	private final CalculatorController controller;
	private final EquationParser parser = new EquationParser();

	public Calculator() {
		final FXMLLoader loader = new FXMLLoader(Calculator.class.getResource("Calculator.fxml"));
		try {
			stage.setScene(new Scene(loader.load()));
			stage.initStyle(StageStyle.UNDECORATED);
		} catch (final IOException e) {
			// This block should never run. Just like in the FileManager class.
		}
		controller = loader.<CalculatorController>getController();
		controller.setOnClose(this::hide);
		controller.setParser(parser);
		Window.setPaneDraggableByNode(stage, stage.getScene().getRoot());
		Window.setPaneDraggableByNode(stage, controller.menuBar);
	}

	public double calculate()
			throws EmptyEquationException, UnmatchedParenthesisException, IrregularCharacterException {
		final double result = parser.evaluate(controller.getEquation());
		controller.setEquation(Double.toString(result));
		return result;
	}

	public final double getHeight() {
		return stage.getHeight();
	}

	public final String getTitle() {
		return stage.getTitle();
	}

	public final double getWidth() {
		return stage.getWidth();
	}

	public final double getX() {
		return stage.getX();
	}

	public final double getY() {
		return stage.getY();
	}

	public void hide() {
		stage.hide();
	}

	public final boolean isFocused() {
		return stage.isFocused();
	}

	public final boolean isFullScreen() {
		return stage.isFullScreen();
	}

	public final boolean isIconified() {
		return stage.isIconified();
	}

	public final boolean isMaximized() {
		return stage.isMaximized();
	}

	public final boolean isShowing() {
		return stage.isShowing();
	}

	public final void setFullScreen(final boolean value) {
		stage.setFullScreen(value);
	}

	public final void setIconified(final boolean value) {
		stage.setIconified(value);
	}

	public final void setMaximized(final boolean value) {
		stage.setMaximized(value);
	}

	public final void setTitle(final String value) {
		stage.setTitle(value);
	}

	public final void setX(final double value) {
		stage.setX(value);
	}

	public final void setY(final double value) {
		stage.setY(value);
	}

	public final void show() {
		stage.show();
	}

}
