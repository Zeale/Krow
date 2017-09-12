package zeale.guis.math_module;

import java.text.DecimalFormat;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
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
	private Tab arithmetic, functions;

	@Override
	public void initialize() {
		done.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					input.setText("" + new EquationParser().evaluate(input.getText()));
				} catch (EmptyEquationException | UnmatchedParenthesisException | IrregularCharacterException e) {
					e.printStackTrace();
				}
			}

		});

		TEXT.addListener(textListener);

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
