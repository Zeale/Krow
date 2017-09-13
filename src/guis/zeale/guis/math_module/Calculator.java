package zeale.guis.math_module;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import kr�w.core.Kr�w;
import kr�w.core.managers.WindowManager.Page;
import kr�w.libs.math.EquationParser;
import kr�w.libs.math.exceptions.EmptyEquationException;
import kr�w.libs.math.exceptions.IrregularCharacterException;
import kr�w.libs.math.exceptions.UnmatchedParenthesisException;

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

		calcIO.setPrefSize(Kr�w.scaleWidth(954), Kr�w.scaleHeight(42));
		calcIO.setLayoutX(Kr�w.scaleWidth(485));
		calcIO.setLayoutY(Kr�w.scaleHeight(175));

		buttonTabPane.setPrefSize(Kr�w.scaleWidth(954), Kr�w.scaleHeight(478));
		buttonTabPane.setLayoutX(Kr�w.scaleWidth(485));
		buttonTabPane.setLayoutY(Kr�w.scaleHeight(248));

		/**********************************
		 **** INJECTED NODE PROPERTIES ****
		 **********************************/
		calcIO.setText(cachedText);
		solve.setOnAction(event -> evaluate());

		// This assures that regular buttons will automatically append their
		// visual content to the TEXT content.
		//
		// That will then copy its contents to text
		for (Node n : ((Pane) arithmeticTab.getContent()).getChildren())
			if (n instanceof Button) {
				Button b = (Button) n;
				if (b.getOnAction() == null)
					b.setOnAction(event -> appendText(b.getText()));
			}

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

	/***************************************************************************************
	 *********************************** INSTANCE METHODS **********************************
	 ***************************************************************************************/

	private void appendText(String text) {
		calcIO.appendText(text);
		calcIO.positionCaret(calcIO.getLength());
	}

}
