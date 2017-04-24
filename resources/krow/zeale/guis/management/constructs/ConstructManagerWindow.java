package krow.zeale.guis.management.constructs;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import kr�w.libs.Construct;
import kr�w.libs.MindsetObject;
import kr�w.zeale.v1.program.core.Kr�w;
import kr�w.zeale.v1.program.guis.Window;

public class ConstructManagerWindow extends Window {
	@FXML
	private MenuBar menubar;

	@FXML
	private TableView<Construct> constructs;
	@FXML
	private TableColumn<Construct, String> nameTable, genderTable, descriptionTable, lifeTable;

	@FXML
	private PieChart genderPieChart, lifePieChart;

	@FXML
	private TextField editNameField;
	@FXML
	private TextArea editDescriptionField;
	@FXML
	private CheckBox editLifeField, editGenderField;
	@FXML
	private Button editConstructDoneButton, deleteConstructButton;

	private Construct constructBeingEdited;

	@FXML
	private void onConstructsTableClicked() {
		constructBeingEdited = Kr�w.getDataManager().getConstructs()
				.get(constructs.getFocusModel().getFocusedCell().getRow());
		constructs.setVisible(false);

		editNameField.setText(constructBeingEdited.getName());
		editDescriptionField.setText(constructBeingEdited.getDescription());
		editGenderField.setSelected(constructBeingEdited.getGender());
		editLifeField.setSelected(constructBeingEdited.isAlive());

		editDescriptionField.setVisible(true);
		editGenderField.setVisible(true);
		editLifeField.setVisible(true);
		editNameField.setVisible(true);
		editConstructDoneButton.setVisible(true);
		deleteConstructButton.setVisible(true);

	}

	@FXML
	private void onDeleteConstruct() {
		if (!Kr�w.getDataManager().getConstructs().remove(constructBeingEdited))
			System.err.println("The construct " + constructBeingEdited.getName()
					+ " could not be removed from the Construct list.....");
		constructBeingEdited.delete();
		onDoneEditingConstruct();
	}

	@FXML
	private void onDoneEditingConstruct() {
		constructBeingEdited.setAlive(editLifeField.isSelected());
		constructBeingEdited.setDescription(editDescriptionField.getText());
		constructBeingEdited.setName(editNameField.getText());
		constructBeingEdited.setGender(editGenderField.isSelected());

		editConstructDoneButton.setVisible(false);
		editDescriptionField.setVisible(false);
		editGenderField.setVisible(false);
		editLifeField.setVisible(false);
		editNameField.setVisible(false);
		deleteConstructButton.setVisible(false);

		refreshData();

		constructs.setVisible(true);
	}

	@FXML
	private void onGoBackRequested() {
		Window.setSceneToPreviousScene();
	}

	private void refreshData() {
		final ArrayList<Construct> constructs = new ArrayList<>(
				Kr�w.getDataManager().getConstructs().getConstructList());
		Kr�w.getDataManager().getConstructs().clear();
		Kr�w.getDataManager().getConstructs().addAll(constructs);

		ObservableList<PieChart.Data> list = FXCollections.observableArrayList(
				new PieChart.Data("Males", Kr�w.getDataManager().getConstructs().getMaleConstructs().size()),
				new PieChart.Data("Females", Kr�w.getDataManager().getConstructs().getFemaleConstructs().size()));
		genderPieChart.setData(list);
		list = FXCollections.observableArrayList(
				new PieChart.Data("Living", Kr�w.getDataManager().getConstructs().getLivingConstructs().size()),
				new PieChart.Data("Dead", Kr�w.getDataManager().getConstructs().getDeadConstructs().size()));
		lifePieChart.setData(list);
	}

	@Override
	public String getWindowFile() {
		return "ConstructManager.fxml";
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
			genderTable.impl_setReorderable(false);
			descriptionTable.impl_setReorderable(false);
			lifeTable.impl_setReorderable(false);
		} catch (final NoSuchMethodError e) {
		}

		refreshData();
		constructs.setItems(Kr�w.getDataManager().getConstructs().getConstructList());

		nameTable.setCellValueFactory(new MindsetObject.MindsetObjectTableViewCellValueFactory<>("Name"));
		genderTable.setCellValueFactory(new MindsetObject.MindsetObjectTableViewCellValueFactory<>("Gender"));
		descriptionTable.setCellValueFactory(new MindsetObject.MindsetObjectTableViewCellValueFactory<>("Description"));
		lifeTable.setCellValueFactory(new MindsetObject.MindsetObjectTableViewCellValueFactory<>("Living"));

	}

}
