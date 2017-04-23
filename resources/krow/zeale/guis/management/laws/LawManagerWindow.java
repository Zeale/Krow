package krow.zeale.guis.management.laws;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import kröw.libs.Law;
import kröw.libs.MindsetObject;
import kröw.zeale.v1.program.core.Kröw;
import kröw.zeale.v1.program.guis.Window;

public class LawManagerWindow extends Window {
	@FXML
	private MenuBar menubar;

	@FXML
	private TableView<Law> laws;
	@FXML
	private TableColumn<Law, String> nameTable, ruleTable, descriptionTable, dateTable;

	@FXML
	private TextField editNameField;
	@FXML
	private TextArea editDescriptionField, editRuleField;
	@FXML
	private DatePicker creationDatePicker;
	@FXML
	private Button editLawDoneButton, deleteLawButton;

	private Law lawBeingEdited;

	@FXML
	private void onDeleteConstruct() {
		if (!Kröw.getDataManager().getLaws().remove(lawBeingEdited))
			System.err.println("The law " + lawBeingEdited.getName()
					+ " could not be deleted from RAM. It should be deleted from the filesystem though.");
		lawBeingEdited.delete();
		onDoneEditingLaw();
	}

	@FXML
	private void onDoneEditingLaw() {
		lawBeingEdited
				.setDescription(editDescriptionField.getText().isEmpty() ? "null" : editDescriptionField.getText());
		lawBeingEdited.setName(editNameField.getText().isEmpty() ? "null" : editNameField.getText());
		lawBeingEdited.setRule(editRuleField.getText().isEmpty() ? "null" : editRuleField.getText());
		lawBeingEdited.setCreationDate(creationDatePicker.getValue() == null ? new Date()
				: java.util.Date
						.from(Instant.from(creationDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()))));

		editLawDoneButton.setVisible(false);
		editDescriptionField.setVisible(false);
		creationDatePicker.setVisible(false);
		editRuleField.setVisible(false);
		editNameField.setVisible(false);
		deleteLawButton.setVisible(false);
		editRuleField.setVisible(false);

		refreshData();

		laws.setVisible(true);
	}

	@FXML
	private void onGoBackRequested() {
		Window.setSceneToPreviousScene();
	}

	@FXML
	private void onLawsTableClicked() {
		lawBeingEdited = Kröw.getDataManager().getLaws().get(laws.getFocusModel().getFocusedCell().getRow());
		laws.setVisible(false);

		editNameField.setText(lawBeingEdited.getName());
		editDescriptionField.setText(lawBeingEdited.getDescription());
		editRuleField.setText(lawBeingEdited.getRule());
		creationDatePicker
				.setValue(lawBeingEdited.getCreationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

		editDescriptionField.setVisible(true);
		creationDatePicker.setVisible(true);
		editDescriptionField.setVisible(true);
		editNameField.setVisible(true);
		editLawDoneButton.setVisible(true);
		deleteLawButton.setVisible(true);
		editRuleField.setVisible(true);

	}

	private void refreshData() {
		final ArrayList<Law> laws = new ArrayList<>(Kröw.getDataManager().getLaws().getLawList());
		Kröw.getDataManager().getLaws().clear();
		Kröw.getDataManager().getLaws().addAll(laws);
	}

	@Override
	public String getWindowFile() {
		return "LawManger.fxml";
	}

	@SuppressWarnings("deprecation")
	@Override
	public void initialize() {
		super.initialize();

		Window.setPaneDraggableByNode(menubar);

		/*
		 * More deprecated methods with a catch block. The {@link
		 * HomeWindow#initialize()} method contains a description and the
		 * printed "error" of what happens when the NoSuchMethodError is thrown.
		 */
		try {
			nameTable.impl_setReorderable(false);
			ruleTable.impl_setReorderable(false);
			descriptionTable.impl_setReorderable(false);
			dateTable.impl_setReorderable(false);
		} catch (final NoSuchMethodError e) {
		}

		refreshData();
		laws.setItems(Kröw.getDataManager().getLaws().getLawList());

		nameTable.setCellValueFactory(new MindsetObject.MindsetObjectTableViewCellValueFactory<>("Name"));
		dateTable.setCellValueFactory(new MindsetObject.MindsetObjectTableViewCellValueFactory<>("Creation Date"));
		descriptionTable.setCellValueFactory(new MindsetObject.MindsetObjectTableViewCellValueFactory<>("Description"));
		ruleTable.setCellValueFactory(new MindsetObject.MindsetObjectTableViewCellValueFactory<>("Rules"));

	}

}
