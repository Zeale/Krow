package zeale.guis.math_module;

import java.util.List;

import javafx.animation.ScaleTransition;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import kröw.core.Kröw;
import kröw.core.managers.WindowManager.Page;
import kröw.libs.math.EquationParser;
import kröw.libs.math.exceptions.EmptyEquationException;
import kröw.libs.math.exceptions.IrregularCharacterException;
import kröw.libs.math.exceptions.UnmatchedParenthesisException;

public class Calculator extends Page {

	/**
	 * This text controls what is inside this {@link Page}'s {@link #calcIO}
	 * field.
	 */
	private static String cachedText;

	@Override
	public String getWindowFile() {
		return "Calculator.fxml";
	}

	@Override
	protected void onPageSwitched() {
		cachedText = calcIO.getText();
	}

	/*******************************************************************************************
	 *********************************** DEFINING INJECTED NODES *******************************
	 *******************************************************************************************/

	/* Basic Nodes */
	@FXML
	private TextField calcIO;
	@FXML
	private Button solve;
	@FXML
	private AnchorPane pane;
	@FXML
	private TabPane buttonTabPane;
	@FXML
	private Tab arithmeticTab, functionsTab;

	/*****************************************************************************************
	 *********************************** INITIALIZATION METHOD *******************************
	 *****************************************************************************************/

	@Override
	public void initialize() {

		/******************************
		 **** INJECTED NODE SIZING ****
		 ******************************/
		pane.setPrefSize(Kröw.scaleWidth(1920), Kröw.scaleHeight(1080));

		calcIO.setPrefSize(Kröw.scaleWidth(954), Kröw.scaleHeight(42));
		calcIO.setLayoutX(Kröw.scaleWidth(485));
		calcIO.setLayoutY(Kröw.scaleHeight(175));

		buttonTabPane.setPrefSize(Kröw.scaleWidth(954), Kröw.scaleHeight(478));
		buttonTabPane.setLayoutX(Kröw.scaleWidth(485));
		buttonTabPane.setLayoutY(Kröw.scaleHeight(248));

		/**********************************
		 **** INJECTED NODE PROPERTIES ****
		 **********************************/
		calcIO.setText(cachedText);
		solve.setOnAction(event -> evaluate());

		// This loop assures that regular buttons will automatically append
		// their
		// visual content to the TEXT content.
		//
		// That will then copy its contents to text
		for (Node n : ((Pane) arithmeticTab.getContent()).getChildren()) {
			n.setLayoutX(Kröw.scaleWidth(n.getLayoutX()));
			n.setLayoutY(Kröw.scaleWidth(n.getLayoutY()));
			if (n instanceof Region) {
				Region r = (Region) n;
				r.setPrefSize(Kröw.scaleWidth(r.getPrefWidth()), Kröw.scaleHeight(r.getPrefHeight()));
				if (n instanceof Button) {
					Button b = (Button) n;
					if (b.getOnAction() == null)
						b.setOnAction(event -> appendText(b.getText()));
				}
			}
		}

		if (Kröw.getProgramSettings().calculatorUseOuterZoomAnimation)
			addHoverAnimation(1.02, pane.getChildren());
		addHoverAnimation(1.1, ((AnchorPane) arithmeticTab.getContent()).getChildren());

	}

	/*************************************************************************************
	 *********************************** EVENT METHODS ***********************************
	 *************************************************************************************/

	@FXML
	private void evaluate() {
		try {
			calcIO.setText("" + new EquationParser().evaluate(calcIO.getText()));
		} catch (EmptyEquationException | UnmatchedParenthesisException | IrregularCharacterException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void cube() {
		appendText("^3");
	}

	@FXML
	private void square() {
		appendText("^2");
	}

	@FXML
	private void sqrt() {
		appendText((char) 8730 + "(");
	}

	/**************************************************************************************
	 *********************************** HELPER METHODS ***********************************
	 **************************************************************************************/

	private void addHoverAnimation(double size, Node... nodes) {
		for (Node n : nodes) {

			ScaleTransition buttonTabPaneScaleTransition = new ScaleTransition(Duration.seconds(0.1));
			buttonTabPaneScaleTransition.setNode(n);

			n.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<Event>() {

				@Override
				public void handle(Event event) {
					buttonTabPaneScaleTransition.stop();
					buttonTabPaneScaleTransition.setToX(size);
					buttonTabPaneScaleTransition.setToY(size);
					buttonTabPaneScaleTransition.play();
				}
			});
			n.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<Event>() {

				@Override
				public void handle(Event event) {
					buttonTabPaneScaleTransition.stop();
					buttonTabPaneScaleTransition.setToX(1);
					buttonTabPaneScaleTransition.setToY(1);
					buttonTabPaneScaleTransition.play();
				}
			});

		}
	}

	private void addHoverAnimation(double size, List<Node> nodes) {
		for (Node n : nodes) {

			ScaleTransition buttonTabPaneScaleTransition = new ScaleTransition(Duration.seconds(0.1));
			buttonTabPaneScaleTransition.setNode(n);

			n.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<Event>() {

				@Override
				public void handle(Event event) {
					buttonTabPaneScaleTransition.stop();
					buttonTabPaneScaleTransition.setToX(size);
					buttonTabPaneScaleTransition.setToY(size);
					buttonTabPaneScaleTransition.play();
				}
			});
			n.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<Event>() {

				@Override
				public void handle(Event event) {
					buttonTabPaneScaleTransition.stop();
					buttonTabPaneScaleTransition.setToX(1);
					buttonTabPaneScaleTransition.setToY(1);
					buttonTabPaneScaleTransition.play();
				}
			});

		}
	}

	/***************************************************************************************
	 *********************************** INSTANCE METHODS **********************************
	 ***************************************************************************************/

	private void appendText(String text) {
		calcIO.appendText(text);
		calcIO.positionCaret(calcIO.getLength());
	}

}
