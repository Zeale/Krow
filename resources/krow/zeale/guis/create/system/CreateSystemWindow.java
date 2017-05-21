package krow.zeale.guis.create.system;

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
import javafx.scene.paint.Color;
import kröw.zeale.v1.program.core.Kröw;
import wolf.mindset.ObjectAlreadyExistsException;
import wolf.mindset.System;
import wolf.zeale.guis.Window;

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

		final String name = nameField.getText().isEmpty() ? "null" : nameField.getText();
		try {
			final String text = descriptionField.getText();
			final LocalDate value = creationDatePicker.getValue();
			new wolf.mindset.System(name, text.isEmpty() ? "null" : text,
					value == null ? new Date() : Date.from(value.atStartOfDay(ZoneId.systemDefault()).toInstant()))
							.getMindsetModel().attatch(Kröw.CONSTRUCT_MINDSET);
		} catch (final ObjectAlreadyExistsException e) {
			java.lang.System.err.println("A System with this name already exists.");
			Window.spawnLabelAtMousePos("The System, " + name + ", already exists...", Color.RED);
			return;
		}

		Window.spawnLabelAtMousePos("Added the System, " + name + ", successfully", Color.GREEN);
		Window.setSceneToPreviousScene();
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
