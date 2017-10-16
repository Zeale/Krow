package krow.pages;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.text.TextFlow;
import kröw.core.managers.WindowManager.Page;

public abstract class TextPanel extends Page {

	@FXML
	protected TextFlow console;
	@FXML
	protected TextArea input;

	public TextPanel() {
	}

}
