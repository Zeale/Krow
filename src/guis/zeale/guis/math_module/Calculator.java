package zeale.guis.math_module;

import java.text.DecimalFormat;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
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

	public static final StringProperty TEXT = new SimpleStringProperty();

	@Override
	public String getWindowFile() {
		return "Calculator.fxml";
	}

	@FXML
	private TextField input;
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
		done.setOnAction(event -> evaluate());

		TEXT.addListener(textListener);
		for (Node n : ((Pane) arithmeticTab.getContent()).getChildren())
			if (n instanceof Button) {
				Button b = (Button) n;
				if (b.getOnAction() == null)
					b.setOnAction(event -> TEXT.set((TEXT.get() == null ? "" : TEXT.get()) + b.getText()));
			}

	}

	private ChangeListener<String> textListener = (observable, oldValue, newValue) -> input.setText(newValue);

	@Override
	protected void onPageSwitched() {
		TEXT.removeListener(textListener);
	}

	@FXML
	private void evaluate() {
		try {
			TEXT.set(new DecimalFormat().format(new EquationParser().evaluate(TEXT.get())));
		} catch (EmptyEquationException | UnmatchedParenthesisException | IrregularCharacterException e) {
			e.printStackTrace();
		}
	}

}
