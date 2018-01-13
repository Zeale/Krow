package zeale.guis.developer_module;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import kr�w.core.managers.WindowManager.Page;

public class TestingModule extends Page {

	private @FXML Accordion accordion;
	private @FXML BorderPane root;
	private @FXML VBox toolbox;

	private void addTool(Button button) {
		toolbox.getChildren().add(button);
	}

	public TestingModule() {
	}

	@Override
	public void initialize() {
		root.setLeft(null);
		root.setCenter(accordion);
		toolbox.setSpacing(20);

		// Add testing tools

	}

	@Override
	public String getWindowFile() {
		return "TestingModule.fxml";
	}

	private void show(Node n) {
		root.setCenter(n);
		root.setLeft(accordion);
	}

}
