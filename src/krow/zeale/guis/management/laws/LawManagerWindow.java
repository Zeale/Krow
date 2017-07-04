package krow.zeale.guis.management.laws;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import kröw.zeale.core.Kröw;
import wolf.mindset.Law;
import wolf.mindset.ObjectAlreadyExistsException;
import wolf.mindset.tables.TableViewable;
import wolf.zeale.collections.ObservableListWrapper;
import wolf.zeale.guis.Window;

/**
 * This class is the Controller for the <code>LawManager.fxml</code> file.
 *
 * @author Zeale
 * @Internal This is an internal class and is not meant to be used externally.
 *
 */
public class LawManagerWindow extends Window {
	/**
	 * The {@link MenuBar} at the top of the {@link Window}.
	 */
	@FXML
	private MenuBar menubar;

	/**
	 * The table that displays {@link Law}s.
	 */
	@FXML
	private TableView<Law> laws;
	/**
	 * <p>
	 * {@link #nameTable} - The {@link TableColumn} that displays the name of
	 * each {@link Law}.
	 * <p>
	 * {@link #ruleTable} - The {@link TableColumn} that displays the rule of
	 * each {@link Law}.
	 * <p>
	 * {@link #descriptionTable} - The {@link TableColumn} that displays the
	 * description of each {@link Law}.
	 * <p>
	 * {@link #dateTable} - The {@link TableColumn} that displays the date and
	 * time that each {@link Law} was created.
	 */
	@FXML
	private TableColumn<Law, String> nameTable, ruleTable, descriptionTable, dateTable;

	/**
	 * The {@link TextField} where the user can edit the name of a selected
	 * {@link Law}.
	 */
	@FXML
	private TextField editNameField;
	/**
	 * <p>
	 * {@link #editDescriptionField} - The {@link TextArea} where the user can
	 * edit the description of a {@link Law}.
	 * <p>
	 * {@link #editRuleField} - The {@link TextArea} where the user can edit the
	 * rule of a {@link Law}.
	 */
	@FXML
	private TextArea editDescriptionField, editRuleField;
	/**
	 * The {@link DatePicker} where the user can edit the time at which a
	 * {@link Law} was created.
	 */
	@FXML
	private DatePicker creationDatePicker;
	/**
	 * <p>
	 * {@link #editLawDoneButton} - This {@link Button} is clicked when the user
	 * is done editing a {@link Law}.
	 * <p>
	 * {@link #deleteLawButton} - This {@link Button} is clicked when the user
	 * attempts to delete a {@link Law}.
	 */
	@FXML
	private Button editLawDoneButton, deleteLawButton;

	/**
	 * The {@link Law} that is currently being edited.
	 */
	private Law lawBeingEdited;

	/*
	 * (non-Javadoc)
	 *
	 * @see kröw.zeale.v1.program.guis.Window#getWindowFile()
	 */
	@Override
	public String getWindowFile() {
		return "LawManger.fxml";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see kröw.zeale.v1.program.guis.Window#initialize()
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void initialize() {
		super.initialize();

		Window.setPaneDraggableByNode(menubar);

		/*
		 * More deprecated methods with a catch block. The {@link
		 * HomeWindow#initialize()} method contains an answer to "why?" and the
		 * printed "error" of what happens when the NoSuchMethodError is thrown.
		 */
		try {
			nameTable.impl_setReorderable(false);
			ruleTable.impl_setReorderable(false);
			descriptionTable.impl_setReorderable(false);
			dateTable.impl_setReorderable(false);
		} catch (final NoSuchMethodError e) {
		}

		laws.setItems(new ObservableListWrapper<>(Kröw.CONSTRUCT_MINDSET.getLaws()));

		nameTable.setCellValueFactory(new TableViewable.TableViewCellValueFactory<>("Name"));
		dateTable.setCellValueFactory(new TableViewable.TableViewCellValueFactory<>("Creation Date"));
		descriptionTable.setCellValueFactory(new TableViewable.TableViewCellValueFactory<>("Description"));
		ruleTable.setCellValueFactory(new TableViewable.TableViewCellValueFactory<>("Rules"));

	}

	/**
	 * Called when the user attempts to delete a {@link Law}.
	 */
	@FXML
	private void onDeleteLaw() {
		lawBeingEdited.delete();
		Window.spawnLabelAtMousePos("Deleted the law, " + lawBeingEdited.getName() + ", successfully", Color.GREEN);
		laws.setItems(new ObservableListWrapper<>(Kröw.CONSTRUCT_MINDSET.getLaws()));
		onDoneEditingLaw();
	}

	/**
	 * Called when the user finishes editing a {@link Law}. Specifically, when
	 * the user clicks on the {@link #editLawDoneButton}.
	 */
	@FXML
	private void onDoneEditingLaw() {
		final String description = editDescriptionField.getText();
		lawBeingEdited.setDescription(description.isEmpty() ? "null" : description);
		final String rule = editRuleField.getText();
		lawBeingEdited.setRule(rule.isEmpty() ? "null" : rule);
		final LocalDate creationDate = creationDatePicker.getValue();
		lawBeingEdited.setCreationDate(creationDate == null ? new Date()
				: java.util.Date.from(Instant.from(creationDate.atStartOfDay(ZoneId.systemDefault()))));
		final String currentName = lawBeingEdited.getName();
		final String newName = editNameField.getText();
		try {
			if (!currentName.equals(newName.isEmpty() ? "null" : newName))
				lawBeingEdited.setName(newName.isEmpty() ? "null" : newName);
		} catch (final ObjectAlreadyExistsException e) {
			System.err.println("A Law with this name already exists.");
			Window.spawnLabelAtMousePos("A Law with the name, " + newName + ", already exists.", Color.RED);
			return;
		}

		Window.spawnLabelAtMousePos("Edited the Law, " + currentName + ", successfully", Color.GREEN);

		editLawDoneButton.setVisible(false);
		editDescriptionField.setVisible(false);
		creationDatePicker.setVisible(false);
		editRuleField.setVisible(false);
		editNameField.setVisible(false);
		deleteLawButton.setVisible(false);
		editRuleField.setVisible(false);

		laws.setVisible(true);
		laws.refresh();
	}

	/**
	 * Called when the user attempts to return to the previously showing
	 * {@link Scene}.
	 */
	@FXML
	private void onGoBackRequested() {
		Window.setSceneToPreviousScene();
	}

	/**
	 * Called when the user clicks anywhere in the {@link #laws}
	 * {@link TableView}.
	 */
	@FXML
	private void onLawsTableClicked() {
		lawBeingEdited = Kröw.CONSTRUCT_MINDSET.getLawsUnmodifiable()
				.get(laws.getFocusModel().getFocusedCell().getRow());

		editNameField.setText(lawBeingEdited.getName());
		editDescriptionField.setText(lawBeingEdited.getDescription());
		editRuleField.setText(lawBeingEdited.getRule());
		creationDatePicker
				.setValue(lawBeingEdited.getCreationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

		laws.setVisible(false);

		editDescriptionField.setVisible(true);
		creationDatePicker.setVisible(true);
		editDescriptionField.setVisible(true);
		editNameField.setVisible(true);
		editLawDoneButton.setVisible(true);
		deleteLawButton.setVisible(true);
		editRuleField.setVisible(true);

	}

}
