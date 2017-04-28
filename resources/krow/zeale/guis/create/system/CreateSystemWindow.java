package krow.zeale.guis.create.system;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import kröw.libs.System;
import kröw.zeale.v1.program.core.Kröw;
import kröw.zeale.v1.program.guis.Window;

/**
 * The Controller for the <code>CreateSystemWindow.fxml</code> {@link Scene}.
 *
 * @author Zeale
 *
 * @Internal This class is a part of this program's internal API and should not
 *           be used apart from it.
 *
 */
public class CreateSystemWindow extends Window {

	/**
	 * The user will press this {@link Button} when they are done making their
	 * {@link System}.
	 */
	@FXML
	private Button doneButton;
	/**
	 * The user will type the name of their {@link System} into this field.
	 */
	@FXML
	private TextField nameField;
	/**
	 * This {@link TextArea} takes the description of the user's {@link System}.
	 */
	@FXML
	private TextArea descriptionField;
	/**
	 * Allows the user to select the {@link Date} that their {@link System} was
	 * made.
	 */
	@FXML
	private DatePicker creationDatePicker;

	@FXML
	private MenuBar menuBar;

	/**
	 * Called when the user attempts to close the program.
	 */
	@FXML
	private void onClose() {
		Platform.exit();
	}

	/**
	 * Called when the user attempts to go to the previous {@link Scene}.
	 */
	@FXML
	private void onGoBack() {
		Window.setSceneToPreviousScene();
	}

	/**
	 * <p>
	 * Called when the user clicks the {@link #doneButton}.
	 * <p>
	 * This {@link Button} is where a {@link System} is made.
	 */
	@FXML
	private void onSystemCreated() {

		creationDatePicker.getValue();
		final kröw.libs.System syst = new kröw.libs.System(nameField.getText().isEmpty() ? "null" : nameField.getText(),
				descriptionField.getText().isEmpty() ? "null" : descriptionField.getText(),
				creationDatePicker.getValue() == null ? new Date()
						: java.util.Date.from(Instant.from(LocalDate.now(ZoneId.systemDefault()))));

		if (!Kröw.getDataManager().getSystems().contains(syst)) {
			Kröw.getDataManager().getSystems().add(syst);
			Window.setSceneToPreviousScene();
		} else
			java.lang.System.out.println("The law " + syst.getName() + " already exists!");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see kröw.zeale.v1.program.guis.Window#getWindowFile()
	 */
	@Override
	public String getWindowFile() {
		return "CreateSystemWindow.fxml";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see kröw.zeale.v1.program.guis.Window#initialize()
	 */
	@Override
	public void initialize() {
		Window.setPaneDraggableByNode(menuBar);
	};

}
