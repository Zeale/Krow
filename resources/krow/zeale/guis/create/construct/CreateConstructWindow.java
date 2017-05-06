package krow.zeale.guis.create.construct;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import krow.zeale.guis.home.HomeWindow;
import kröw.zeale.v1.program.core.Kröw;
import wolf.mindset.Construct;
import wolf.mindset.ObjectAlreadyExistsException;
import wolf.zeale.guis.Window;

/**
 * <p>
 * This class is the Controller of the <code>CreateConstructWindow.fxml</code>
 * document. It includes injected fields and some methods that are called when
 * the user triggers an event in the window.
 * <p>
 * This class, being a controller, must extend {@link Window}.
 *
 * @author Zeale
 *
 * @Internal This class is a part of this program's internal API and should not
 *           be used apart from it.
 *
 */
public class CreateConstructWindow extends Window {

	/**
	 * <p>
	 * A {@link CheckBox} indicating the gender of the {@link Construct} that
	 * will be created. See {@link Construct#getGender()} for more details.
	 * <p>
	 * <code>true</code> or <code>checked</code> indicates female,
	 * <code>false</code> or <code>unchecked</code> indicates male.
	 *
	 * @FXML This field is injected.
	 */
	@FXML
	private CheckBox gender;

	/**
	 * <p>
	 * A {@link CheckBox} indicating whether or not this {@link Construct} is
	 * alive.
	 * <p>
	 * <code>true</code> or <code>checked</code> for alive, <code>false</code>
	 * or <code>unchecked</code> otherwise.
	 */
	@FXML
	private CheckBox alive;

	/**
	 * A {@link TextField} where the user enters in the name of a
	 * {@link Construct}.
	 */
	@FXML
	private TextField nameField;
	/**
	 * A {@link TextArea} where the user enters in a description of their
	 * {@link Construct}.
	 */
	@FXML
	private TextArea descriptionField;

	/**
	 * The {@link MenuBar} at the top of the {@link Window}.
	 */
	@FXML
	private MenuBar menuBar;

	/**
	 * Called when the user attempts to close the {@link Window} via the Close
	 * button in the {@link #menuBar}.
	 */
	@FXML
	private void close() {
		Platform.exit();
	}

	/**
	 * Called when the user clicks the "done" button in the GUI, to signify that
	 * they are finished creating their {@link Construct}.
	 */
	@FXML
	private void done() {
		final String name = nameField.getText(), description = descriptionField.getText();
		Construct construct;
		try {
			construct = new Construct(name, description, gender.isSelected(), alive.isSelected());
		} catch (final ObjectAlreadyExistsException e) {
			System.err.println("The Construct, " + name + ", already exists!");
			return;
		}
		Kröw.constructs.add(construct);
		goBack();

	}

	/**
	 * Called when the user attempts to go back to the previous {@link Window}
	 * by clicking the "back" button in the {@link #menuBar}.
	 */
	@FXML
	private void goBack() {
		Window.setSceneToPreviousScene();
	}

	/**
	 * Called when the user attempts to go to the {@link HomeWindow} by clicking
	 * the "home" button in the {@link #menuBar}.
	 */
	@FXML
	private void home() {
		try {
			Window.setScene(HomeWindow.class);
		} catch (InstantiationException | IllegalAccessException | IOException e) {
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see kröw.zeale.v1.program.guis.Window#getWindowFile()
	 */
	@Override
	public String getWindowFile() {
		return "CreateConstructWindow.fxml";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see kröw.zeale.v1.program.guis.Window#initialize()
	 */
	@Override
	public void initialize() {
		Window.setPaneDraggableByNode(menuBar);

	}
}
