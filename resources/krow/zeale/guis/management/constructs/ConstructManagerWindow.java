package krow.zeale.guis.management.constructs;

import java.text.DecimalFormat;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import kröw.zeale.v1.program.core.Kröw;
import wolf.mindset.Construct;
import wolf.mindset.Construct.Mark;
import wolf.mindset.MindsetObject.MarkAlreadyExistsException;
import wolf.mindset.ObjectAlreadyExistsException;
import wolf.mindset.tables.TableViewable;
import wolf.zeale.Wolf;
import wolf.zeale.collections.ObservableListWrapper;
import wolf.zeale.guis.Window;

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

	@FXML
	private AnchorPane editMarkPane;

	@FXML
	private TableView<Construct.Mark> markTable;

	@FXML
	private CheckBox edit_gBox, edit_lBox;

	@FXML
	private Button edit_markDone, edit_deleteMarkButton;

	@FXML
	private TextField edit_markName;
	@FXML
	private TextArea edit_markDesc;
	@FXML
	private TabPane managePane;
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

	private Construct.Mark markBeingEdited;
	@FXML
	private CheckBox create_lBox, create_gBox;
	@FXML
	private TextField create_markName;
	@FXML
	private TextArea create_markDesc;

	@FXML
	private TableColumn<Construct.Mark, String> markNameColumn, markDescriptionColumn, markGenderColumn, markLifeColumn;

	private void clearMarkMakerEditor() {
		create_gBox.setSelected(false);
		create_lBox.setSelected(false);
		create_markDesc.setText("");
		create_markName.setText("");
	}

	@FXML
	private void createMark() {
		try {
			constructBeingEdited.makeMark(create_markDesc.getText().isEmpty() ? "null" : create_markDesc.getText(),
					create_gBox.isSelected(), create_lBox.isSelected(),
					create_markName.getText().isEmpty() ? "null" : create_markName.getText());
		} catch (final MarkAlreadyExistsException e) {
			Window.spawnLabelAtMousePos("That Mark already exists...", Color.RED);
			return;
		}
		Window.spawnLabelAtMousePos("Made the Mark, "
				+ (create_markDesc.getText().isEmpty() ? "null" : create_markDesc.getText()) + " successfully",
				Color.GREEN);
		updateMarks();
		clearMarkMakerEditor();
		managePane.getSelectionModel().select(1);
	}

	@FXML
	private void deleteMark() {
		constructBeingEdited.getMarks().remove(markBeingEdited);
		editMarkPane.setVisible(false);
		markTable.setVisible(true);
		markBeingEdited = null;
		updateMarks();
	}

	@FXML
	private void doneEditingMark() {

		try {
			markBeingEdited.setMark(edit_markName.getText());
		} catch (final MarkAlreadyExistsException e) {
			System.out.println("A Mark with the name, " + edit_markName.getText() + ", already exists...");
			Window.spawnLabelAtMousePos("A Mark with the name, " + edit_markName.getText() + ", already exists...",
					Color.RED);
			return;
		}

		markBeingEdited.setAlive(edit_lBox.isSelected());
		markBeingEdited.setGender(edit_gBox.isSelected());
		markBeingEdited.setDescription(edit_markDesc.getText());

		markBeingEdited = null;
		Window.spawnLabelAtMousePos("Edited the Mark, " + edit_markName.getText() + ", successfully", Color.GREEN);
		updateMarks();

		editMarkPane.setVisible(false);
		markTable.setVisible(true);

	}

	@FXML
	private void goBackToConstructTable() {
		clearMarkMakerEditor();
		managePane.setVisible(false);
		constructs.setVisible(true);
		genderPieChart.setTitle("Gender");
		lifePieChart.setTitle("Life");
		updateConstructs();

	}

	@FXML
	private void goBackToMarkTable() {
		clearMarkMakerEditor();
		markTable.setVisible(true);
		editMarkPane.setVisible(false);
	}

	@FXML
	private void markTableClicked() {
		final int mrk = markTable.getFocusModel().getFocusedCell().getRow();
		if (mrk < 0) {
			if (Wolf.DEBUG_MODE)
				System.out.println("There are no Marks in this Construct for you to click.");
			return;
		}
		markBeingEdited = constructBeingEdited.getMarks().get(mrk);
		markTable.setVisible(false);
		editMarkPane.setVisible(true);
		edit_markDesc.setText(markBeingEdited.getDescription());
		edit_markName.setText(markBeingEdited.getMark());
		edit_gBox.setSelected(markBeingEdited.getRawGender());
		edit_lBox.setSelected(markBeingEdited.isAlive());
	}

	@FXML
	private void onClearMarkMakerEditor() {
		clearMarkMakerEditor();
		Window.spawnLabelAtMousePos("Cleared GUI successfully", Color.GOLD);
	}

	/**
	 * Called when a {@link Construct} in the {@link #constructs} table is
	 * clicked.
	 */
	@FXML
	private void onConstructsTableClicked() {
		final int cnstrct = constructs.getFocusModel().getFocusedCell().getRow();
		if (cnstrct < 0) {
			if (Wolf.DEBUG_MODE)
				System.out.println("There are no Constructs for you to select...");
			return;
		}
		constructBeingEdited = Kröw.CONSTRUCT_MINDSET.getConstructsUnmodifiable().get(cnstrct);

		editNameField.setText(constructBeingEdited.getName());
		editDescriptionField.setText(constructBeingEdited.getDescription());
		editGenderField.setSelected(constructBeingEdited.getGender());
		editLifeField.setSelected(constructBeingEdited.isAlive());
		markTable.setItems(new ObservableListWrapper<>(constructBeingEdited.getMarks()));

		Window.spawnLabelAtMousePos("Editing Construct, \"" + constructBeingEdited.getName() + "\".", Color.GOLD);

		constructs.setVisible(false);
		managePane.setVisible(true);

		updateMarks();

		genderPieChart.setTitle("Mark Genders");

		lifePieChart.setTitle("Mark Life");

	}

	/**
	 * Called when the user attempts to delete a {@link Construct}.
	 */
	@FXML
	private void onDeleteConstruct() {
		constructBeingEdited.delete();
		Window.spawnLabelAtMousePos("Deleted the Construct, " + constructBeingEdited.getName() + ".", Color.GOLD);
		constructs.setItems(new ObservableListWrapper<>(Kröw.CONSTRUCT_MINDSET.getConstructs()));
		onDoneEditingConstruct();
	}

	/**
	 * Called when the user finishes editing a {@link Construct}.
	 */
	@FXML
	private void onDoneEditingConstruct() {
		constructBeingEdited.setAlive(editLifeField.isSelected());
		constructBeingEdited.setDescription(editDescriptionField.getText());
		constructBeingEdited.setGender(editGenderField.isSelected());
		try {
			if (!constructBeingEdited.getName()
					.equals(editNameField.getText().isEmpty() ? "null" : editNameField.getText()))
				constructBeingEdited.setName(editNameField.getText());
		} catch (final ObjectAlreadyExistsException e) {
			System.err.println("A Construct with this name already exists.");
			Window.spawnLabelAtMousePos(
					"A Construct with the name, " + constructBeingEdited.getName() + ", already exists.", Color.RED);
			return;
		}

		managePane.setVisible(false);
		Window.spawnLabelAtMousePos("Successfully modified Construct, " + constructBeingEdited.getName() + ".",
				Color.GREEN);
		updateConstructs();

		constructs.setVisible(true);

		constructBeingEdited = null;
	}

	/**
	 * Called when the user requests to go back to the previously showing
	 * {@link Window}.
	 */
	@FXML
	private void onGoBackRequested() {
		if (!constructs.isVisible())
			goBackToConstructTable();
		else
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
	private void updateConstructs() {

		final DecimalFormat dc = new DecimalFormat("0.##");

		final int maleConstructCount = Kröw.getMaleConstructs().size();
		final int femaleConstructCount = Kröw.getFemaleConstructs().size();
		ObservableList<PieChart.Data> list = FXCollections
				.observableArrayList(
						new PieChart.Data("Males ("
								+ dc.format(
										(double) maleConstructCount / (maleConstructCount + femaleConstructCount) * 100)
								+ "%)", maleConstructCount),
						new PieChart.Data(
								"Females (" + dc.format((double) femaleConstructCount
										/ (maleConstructCount + femaleConstructCount) * 100) + "%)",
								femaleConstructCount));
		genderPieChart.setData(list);
		final int livingConstructCount = Kröw.getLivingConstructs().size();
		final int deadConstructCount = Kröw.getDeadConstructs().size();
		list = FXCollections.observableArrayList(
				new PieChart.Data("Living ("
						+ dc.format((double) livingConstructCount / (livingConstructCount + deadConstructCount) * 100)
						+ "%)", livingConstructCount),
				new PieChart.Data(
						"Dead (" + dc.format(
								(double) deadConstructCount / (livingConstructCount + deadConstructCount) * 100) + "%)",
						deadConstructCount));
		lifePieChart.setData(list);

		constructs.refresh();
	}

	private void updateMarks() {
		if (constructBeingEdited != null)
			markTable.setItems(new ObservableListWrapper<>(constructBeingEdited.getMarks()));
		markTable.refresh();

		int dead = 0, living = 0, male = 0, female = 0;

		for (final Mark mk : constructBeingEdited.getMarks()) {
			if (mk.isAlive())
				living++;
			else
				dead++;
			if (mk.getRawGender())
				female++;
			else
				male++;
		}

		final DecimalFormat dc = new DecimalFormat("0.##");

		ObservableList<PieChart.Data> list = FXCollections.observableArrayList(
				new PieChart.Data("Male Mks (" + dc.format((double) male / (male + female) * 100) + "%)", male),
				new PieChart.Data("Female Mks (" + dc.format((double) female / (female + male) * 100) + "%)", female));
		genderPieChart.setData(list);
		list = FXCollections.observableArrayList(
				new PieChart.Data("Living Mks (" + dc.format((double) living / (living + dead) * 100) + "%)", living),
				new PieChart.Data("Dead Mks (" + dc.format((double) dead / (dead + living) * 100) + "%)", dead));
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

		updateConstructs();
		constructs.setItems(new ObservableListWrapper<>(Kröw.CONSTRUCT_MINDSET.getConstructs()));

		nameTable.setCellValueFactory(new TableViewable.TableViewCellValueFactory<>("Name"));
		genderTable.setCellValueFactory(new TableViewable.TableViewCellValueFactory<>("Gender"));
		descriptionTable.setCellValueFactory(new TableViewable.TableViewCellValueFactory<>("Description"));
		lifeTable.setCellValueFactory(new TableViewable.TableViewCellValueFactory<>("Living"));

		markNameColumn.setCellValueFactory(new TableViewable.TableViewCellValueFactory<>("Name"));
		markDescriptionColumn.setCellValueFactory(new TableViewable.TableViewCellValueFactory<>("Description"));
		markGenderColumn.setCellValueFactory(new TableViewable.TableViewCellValueFactory<>("Gender"));
		markLifeColumn.setCellValueFactory(new TableViewable.TableViewCellValueFactory<>("Living"));
	}

}
