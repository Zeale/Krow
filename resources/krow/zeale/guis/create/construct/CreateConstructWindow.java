package krow.zeale.guis.create.construct;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import kröw.libs.Construct;
import kröw.zeale.v1.program.core.Kröw;
import kröw.zeale.v1.program.guis.Window;

public class CreateConstructWindow extends Window {
	@FXML
	private CheckBox gender;
	@FXML
	private CheckBox alive;

	@FXML
	private TextField nameField;
	@FXML
	private TextArea descriptionField;

	@FXML
	private MenuBar menuBar;

	@FXML
	private void done() {
		String name = nameField.getText(), description = descriptionField.getText();
		if (name.isEmpty())
			name = "null";
		if (description.isEmpty())
			description = "null";
		final Construct construct = new Construct(name, description, gender.isSelected(), alive.isSelected());
		if (!Kröw.INSTANCE.getConstructs().contains(construct)) {
			Kröw.INSTANCE.getConstructs().add(construct);
			goBack();
		} else
			System.err.println("The Construct " + construct.getName() + " already exists!");

	}

	@FXML
	private void goBack() {
		Window.setSceneToPreviousScene();
	}

	@Override
	public void initialize() {
		Window.setPaneDraggableByNode(menuBar);

	}
}
