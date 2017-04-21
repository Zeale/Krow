package krow.zeale.guis.create.system;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import kröw.zeale.v1.program.core.Kröw;
import kröw.zeale.v1.program.guis.Window;

public class CreateSystemWindow extends Window {

	@FXML
	private Button doneButton, switchVisibilityButton;
	@FXML
	private TextField nameField;
	@FXML
	private TextArea descriptionField;
	@FXML
	private DatePicker creationDatePicker;

	@FXML
	private void onClose() {
		Platform.exit();
	}

	@FXML
	private void onGoBack() {
		Window.setSceneToPreviousScene();
	}

	@FXML
	private void onSystemCreated() {

		final kröw.libs.System law = new kröw.libs.System(nameField.getText().isEmpty() ? "null" : nameField.getText(),
				descriptionField.getText().isEmpty() ? "null" : descriptionField.getText(),
				creationDatePicker.getValue() == null ? new Date()
						: java.util.Date.from(
								Instant.from(creationDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()))));

		if (!Kröw.getDataManager().getSystems().contains(law)) {
			Kröw.getDataManager().getSystems().add(law);
			Window.setSceneToPreviousScene();
		} else
			System.out.println("The law " + law.getName() + " already exists!");
	}

	@Override
	public String getWindowFile() {
		return "CreateSystemWindow.fxml";
	}

}
