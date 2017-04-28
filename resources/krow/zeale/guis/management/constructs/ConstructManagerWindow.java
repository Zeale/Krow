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
import kröw.libs.Construct;
import kröw.libs.MindsetObject;
import kröw.zeale.v1.program.core.Kröw;
import kröw.zeale.v1.program.guis.Window;

/**
 * This class is the Controller for the <code>ConstructManager.fxml</code> file.
 *
 * @author Zeale
 *
 * @Internal This class is part of this program's internal API. It is not
 *           intended for external use.
 *
 */
public class ConstructManagerWindow extends Window {
	/**
	 * The {@link Window}'s {@link MenuBar} (at the top of the {@link Window}).
	 */
	@FXML
	private MenuBar menubar;

	/**
	 * The {@link TableView} of the user's {@link Construct}s.
	 */
	@FXML
	private TableView<Construct> constructs;
	/**
	 * <p>
	 * {@link #nameTable} - The {@link TableColumn} that renders
	 * {@link Construct} names.
	 * <p>
	 * {@link #genderTable} - The {@link TableColumn} that renders
	 * {@link Construct} genders.
	 * <p>
	 * {@link #descriptionTable} - The {@link TableColumn} that renders
	 * descriptions of {@link Construct}s.
	 * <p>
	 * {@link #lifeTable} - The {@link TableColumn} that render whether or not a
	 * given {@link Construct} is alive.
	 *
	 */
	@FXML
	private TableColumn<Construct, String> nameTable, genderTable, descriptionTable, lifeTable;

	/**
	 * <p>
	 * {@link #genderPieChart} - The {@link PieChart} that renders information
	 * about the general {@link Construct}s' genders.
	 * <p>
	 * {@link #lifePieChart} - The {@link PieChart} that renders information
	 * about the number of living {@link Construct}s in comparison to the number
	 * of dead {@link Construct}s.
	 */
	@FXML
	private PieChart genderPieChart, lifePieChart;

	/**
	 * The {@link TextField} that shows up when the user attempts to edit a
	 * {@link Construct} which changes the {@link Construct}'s name.
	 */
	@FXML
	private TextField editNameField;
	/**
	 * The {@link TextArea} used for editing a {@link Construct}'s description.
	 */
	@FXML
	private TextArea editDescriptionField;
	/**
	 * <p>
	 * {@link #editLifeField} - The {@link CheckBox} used to change whether or
	 * not a {@link Construct} is alive.
	 * <p>
	 * {@link #editGenderField} - The {@link CheckBox} used to change a
	 * {@link Construct}'s gender.
	 */
	@FXML
	private CheckBox editLifeField, editGenderField;
	/**
	 * <p>
	 * {@link #editConstructDoneButton} - The {@link Button} used to finalize
	 * the editing of a {@link Construct}.
	 * <p>
	 * {@link #deleteConstructButton} - The {@link Button} used to delete a
	 * {@link Construct}.
	 */
	@FXML
	private Button editConstructDoneButton, deleteConstructButton;

	/**
	 * The {@link Construct} that is currently being edited by the user.
	 */
	private Construct constructBeingEdited;

	/**
	 * Called when a {@link Construct} in the {@link #constructs} table is
	 * clicked.
	 */
	@FXML
	private void onConstructsTableClicked() {
		constructBeingEdited = Kröw.getDataManager().getConstructs()
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

	/**
	 * Called when the user attempts to delete a {@link Construct}.
	 */
	@FXML
	private void onDeleteConstruct() {
		if (!Kröw.getDataManager().getConstructs().remove(constructBeingEdited))
			System.err.println("The construct " + constructBeingEdited.getName()
					+ " could not be removed from the Construct list.....");
		constructBeingEdited.delete();
		onDoneEditingConstruct();
	}

	/**
	 * Called when the user finishes editing a {@link Construct}.
	 */
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

	/**
	 * Called when the user requests to go back to the previously showing
	 * {@link Window}.
	 */
	@FXML
	private void onGoBackRequested() {
		Window.setSceneToPreviousScene();
	}

	/**
	 * <p>
	 * .A private method used to refresh the date in the {@link #constructs}
	 * table, the {@link #lifePieChart}, and the {@link #genderPieChart} of this
	 * class.
	 * <p>
	 * The moment this method is called, the various displays of data in this
	 * {@link Window} will update themselves to accommodate for any changes made
	 * to the program's current list of {@link Construct}s.
	 */
	private void refreshData() {
		final ArrayList<Construct> constructs = new ArrayList<>(
				Kröw.getDataManager().getConstructs().getConstructList());
		Kröw.getDataManager().getConstructs().clear();
		Kröw.getDataManager().getConstructs().addAll(constructs);

		ObservableList<PieChart.Data> list = FXCollections.observableArrayList(
				new PieChart.Data("Males", Kröw.getDataManager().getConstructs().getMaleConstructs().size()),
				new PieChart.Data("Females", Kröw.getDataManager().getConstructs().getFemaleConstructs().size()));
		genderPieChart.setData(list);
		list = FXCollections.observableArrayList(
				new PieChart.Data("Living", Kröw.getDataManager().getConstructs().getLivingConstructs().size()),
				new PieChart.Data("Dead", Kröw.getDataManager().getConstructs().getDeadConstructs().size()));
		lifePieChart.setData(list);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see kröw.zeale.v1.program.guis.Window#getWindowFile()
	 */
	@Override
	public String getWindowFile() {
		return "ConstructManager.fxml";
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
		constructs.setItems(Kröw.getDataManager().getConstructs().getConstructList());

		nameTable.setCellValueFactory(new MindsetObject.MindsetObjectTableViewCellValueFactory<>("Name"));
		genderTable.setCellValueFactory(new MindsetObject.MindsetObjectTableViewCellValueFactory<>("Gender"));
		descriptionTable.setCellValueFactory(new MindsetObject.MindsetObjectTableViewCellValueFactory<>("Description"));
		lifeTable.setCellValueFactory(new MindsetObject.MindsetObjectTableViewCellValueFactory<>("Living"));

	}

}
