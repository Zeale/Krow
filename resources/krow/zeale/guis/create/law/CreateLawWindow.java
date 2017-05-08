package krow.zeale.guis.create.law;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import krow.zeale.guis.home.HomeWindow;
import kröw.zeale.v1.program.core.Kröw;
import wolf.mindset.Construct;
import wolf.mindset.Law;
import wolf.mindset.ObjectAlreadyExistsException;
import wolf.zeale.guis.Window;

/**
 * This class is the Controller of the <code>CreateLawWindow.fxml</code> file.
 *
 * @author Zeale
 *
 * @Internal This class is part of this program's internal API and should not be
 *           used apart from it.
 *
 */
public class CreateLawWindow extends Window {

	/**
	 * The {@link MenuBar} at the top of the {@link Window}.
	 */
	@FXML
	private MenuBar menuBar;

	/**
	 * <p>
	 * {@link #doneButton} - The button that is clicked when the user is
	 * finished making their {@link Construct}. This button calls the
	 * {@link #onLawCreated()} method.
	 * <p>
	 * {@link #switchVisibilityButton} - This button switches the GUI from
	 * showing the {@link #descriptionField} to showing the {@link #ruleField}
	 * and back when clicked. It calls the {@link #onWriteRuleRequested()}
	 * method.
	 */
	@FXML
	private Button doneButton, switchVisibilityButton;
	/**
	 * The {@link TextField} where the user enters their {@link Law}'s name.
	 */
	@FXML
	private TextField nameField;
	/**
	 * <p>
	 * {@link #descriptionField} - The {@link TextArea} where the user will
	 * input the description of their {@link Law}.
	 * <p>
	 * {@link #ruleField} - The {@link TextArea} where the user will input the
	 * rule that their law instates.
	 */
	@FXML
	private TextArea descriptionField, ruleField;
	/**
	 * The {@link DatePicker} where the user will enter the {@link Date} that
	 * their {@link Law} was made.
	 */
	@FXML
	private DatePicker creationDatePicker;

	/**
	 * This method is called when the user attempts to go to the
	 * {@link HomeWindow}.
	 */
	@FXML
	private void home() {
		try {
			Window.setScene(HomeWindow.class);
		} catch (InstantiationException | IllegalAccessException | IOException e) {
		}
	}

	/**
	 * This method is called when the user attempts to close the program.
	 */
	@FXML
	private void onClose() {
		Platform.exit();
	}

	/**
	 * This method is called when the user attempts to go back to the previous
	 * {@link Window}.
	 */
	@FXML
	private void onGoBack() {
		Window.setSceneToPreviousScene();
	}

	/**
	 * This method is called when the user presses the {@link #doneButton}. This
	 * method creates a {@link Law} object with the specified information and
	 * adds it to the {@link List} of stored {@link Law}s.
	 */
	@FXML
	private void onLawCreated() {

		Law law;
		try {
			law = new Law(nameField.getText().isEmpty() ? "null" : nameField.getText(),
					descriptionField.getText().isEmpty() ? "null" : descriptionField.getText(),
					ruleField.getText().isEmpty() ? "null" : ruleField.getText(), creationDatePicker.getValue() == null
							? new Date() : java.util.Date.from(Instant.from(LocalDate.now(ZoneId.systemDefault()))));
		} catch (final ObjectAlreadyExistsException e) {
			System.err.println(
					"The Law, " + (nameField.getText().isEmpty() ? "null" : nameField.getText()) + ", already exists.");
			Window.spawnLabelAtMousePos("The Law, " + (nameField.getText().isEmpty() ? "null" : nameField.getText())
					+ ", already exists...", Color.RED);
			return;
		}

		Kröw.laws.add(law);
		Window.spawnLabelAtMousePos("Added the Law, " + law.getName() + ", successfully", Color.GREEN);
		Window.setSceneToPreviousScene();
	}

	/**
	 * This method is called when the user presses the
	 * {@link #switchVisibilityButton}. It switches the visibility of the
	 * {@link #descriptionField} and the {@link #ruleField}.
	 */
	@FXML
	private void onWriteRuleRequested() {
		if (descriptionField.isVisible())
			switchVisibilityButton.setText("Description");
		descriptionField.setVisible(!descriptionField.isVisible());
		ruleField.setVisible(!ruleField.isVisible());

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see kröw.zeale.v1.program.guis.Window#getWindowFile()
	 */
	@Override
	public String getWindowFile() {
		return "CreateLawWindow.fxml";
	}

	@Override
	public void initialize() {
		Window.setPaneDraggableByNode(menuBar);
	}

}
