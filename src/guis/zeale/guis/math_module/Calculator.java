package zeale.guis.math_module;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
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

	@FXML
	private TextField calcIO;
	@FXML
	private Button done;
	@FXML
	private AnchorPane pane;
	@FXML
	private TabPane buttonTabPane;
	@FXML
	private Tab arithmeticTab, functionsTab;

	@Override
	public void initialize() {

		calcIO.setText(cachedText);
		done.setOnAction(event -> evaluate());

		// This assures that regular buttons will automatically append their
		// visual content to the TEXT content.
		//
		// That will then copy its contents to text
		for (Node n : ((Pane) arithmeticTab.getContent()).getChildren())
			if (n instanceof Button) {
				Button b = (Button) n;
				if (b.getOnAction() == null)
					b.setOnAction(event -> calcIO.appendText(b.getText()));
			}

	}

	@Override
	protected void onPageSwitched() {
		cachedText = calcIO.getText();
	}

	@FXML
	private void evaluate() {
		try {
			calcIO.setText("" + new EquationParser().evaluate(calcIO.getText()));
		} catch (EmptyEquationException | UnmatchedParenthesisException | IrregularCharacterException e) {
			e.printStackTrace();
		}
	}

}
