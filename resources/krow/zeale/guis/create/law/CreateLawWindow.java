package krow.zeale.guis.create.law;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import krow.zeale.guis.home.HomeWindow;
import kröw.libs.Law;
import kröw.zeale.v1.program.core.Kröw;
import kröw.zeale.v1.program.guis.Window;

public class CreateLawWindow extends Window {

	@FXML
	private Button doneButton, switchVisibilityButton;
	@FXML
	private TextField nameField;
	@FXML
	private TextArea descriptionField, ruleField;
	@FXML
	private DatePicker creationDatePicker;

	@FXML
	private void home() {
		try {
			Window.setScene(HomeWindow.class);
		} catch (InstantiationException | IllegalAccessException | IOException e) {
		}
	}

	@FXML
	private void onClose() {
		Platform.exit();
	}

	@FXML
	private void onGoBack() {
		Window.setSceneToPreviousScene();
	}

	@FXML
	private void onLawCreated() {

		final Law law = new Law(nameField.getText().isEmpty() ? "null" : nameField.getText(),
				descriptionField.getText().isEmpty() ? "null" : descriptionField.getText(),
				ruleField.getText().isEmpty() ? "null" : ruleField.getText(),
				creationDatePicker.getValue() == null ? new Date()
						: java.util.Date.from(
								Instant.from(creationDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()))));

		if (!Kröw.getDataManager().getLaws().contains(law)) {
			Kröw.getDataManager().getLaws().add(law);
			Window.setSceneToPreviousScene();
		} else
			System.out.println("The law " + law.getName() + " already exists!");
	}

	@FXML
	private void onWriteRuleRequested() {
		if (descriptionField.isVisible())
			switchVisibilityButton.setText("Description");
		descriptionField.setVisible(!descriptionField.isVisible());
		ruleField.setVisible(!ruleField.isVisible());

	}

	@Override
	public String getWindowFile() {
		return "CreateLawWindow.fxml";
	}

}
