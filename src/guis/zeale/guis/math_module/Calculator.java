package zeale.guis.math_module;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import kr�w.core.managers.WindowManager.Page;
import kr�w.libs.math.EquationParser;
import kr�w.libs.math.exceptions.EmptyEquationException;
import kr�w.libs.math.exceptions.IrregularCharacterException;
import kr�w.libs.math.exceptions.UnmatchedParenthesisException;

public class Calculator extends Page {

	@Override
	public String getWindowFile() {
		return "Calculator.fxml";
	}

	@FXML
	private TextField input;
	@FXML
	private Button done;

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
	}

}
