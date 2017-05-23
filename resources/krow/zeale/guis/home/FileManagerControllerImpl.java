package krow.zeale.guis.home;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import kröw.zeale.v1.program.core.Backup;
import kröw.zeale.v1.program.core.Kröw;
import wolf.mindset.MindsetObject;
import wolf.mindset.ObjectAlreadyExistsException;
import wolf.zeale.Wolf;
import wolf.zeale.collections.ObservableListWrapper;
import wolf.zeale.guis.Window;

/**
 * @author Zeale
 *
 */
public class FileManagerControllerImpl {

	/**
	 * A callback that will be executed when the user requests to close this
	 * window.
	 */
	private Runnable closeListener;

	/**
	 * A {@link TreeView} that stores all the files that the user attempts to
	 * input.
	 */
	@FXML
	TreeView<File> importedFileTreeView;

	/**
	 * The {@link TableView} of all exportable objects.
	 */
	@FXML
	private TableView<MindsetObjectCheckBoxWrapper> exportTable;
	/**
	 * <p>
	 * {@link TableColumn}s that are put in {@link #exportTable}.
	 * <p>
	 * {@link #exportTableNameColumn} - The {@link TableColumn} that shows names
	 * of {@link MindsetObject}s in the {@link #exportTable}.
	 * <p>
	 * {@link #exportTableTypeColumn} - The {@link TableColumn} that shows types
	 * of {@link MindsetObject}s in the {@link #exportTable}.
	 */
	@FXML
	private TableColumn<MindsetObjectCheckBoxWrapper, String> exportTableTypeColumn, exportTableNameColumn;
	/**
	 * <p>
	 * A {@link TableColumn} that allows the user to select whether or not they
	 * want to export a {@link MindsetObject}.
	 */
	@FXML
	private TableColumn<MindsetObjectCheckBoxWrapper, Boolean> exportTableSelectColumn;

	/**
	 * The Tabs of this window.
	 */
	@FXML
	Tab tabImport, tabExport, tabBackup, tabRestore;

	/**
	 * <p>
	 * {@link #backupDateColumn} - The {@link TableColumn} that shows the date
	 * of a backup's creation in the {@link #tabBackup backup tab}.
	 * <p>
	 * {@link #backupSizeColumn} - The {@link TableColumn} that shows the size
	 * of each backup in the {@link #tabBackup backup tab}.
	 * <p>
	 * {@link #backupObjectCountColumn} - The {@link TableColumn} that shows the
	 * amount of objects in each backup in the {@link #tabBackup backup tab}.
	 * <p>
	 * {@link #restoreDateColumn} - The {@link TableColumn} that shows the date
	 * a backup was made in the {@link #tabRestore restore tab}.
	 * <p>
	 * {@link #restoreSizeColumn} - The {@link TableColumn} that shows the size
	 * of a backup in the {@link #tabRestore restore tab}.
	 * <p>
	 * {@link #restoreObjectCountColumn} - The {@link TableColumn} that shows
	 * the amount of objects in each backup in the {@link #tabRestore restore
	 * tab}.
	 *
	 */
	@FXML
	private TableColumn<Backup, String> backupDateColumn, backupSizeColumn, backupObjectCountColumn, restoreDateColumn,
			restoreSizeColumn, restoreObjectCountColumn;

	/**
	 * <p>
	 * {@link #backupTable} - A {@link TableView} that shows all the backups
	 * that the user has made. This is located in the {@link #tabBackup backup
	 * tab}.
	 * <p>
	 * {@link #restoreTable} - A {@link TableView} that shows the loaded
	 * backups, (much like the {@link #backupTable}), but can be clicked to
	 * perform a restore. When a user clicks on this table, a backup is
	 * retrieved from the focused cell and its
	 * {@link Backup#restore(boolean, boolean) restore(boolean, boolean)} method
	 * is called. (Its {@link Backup#freshRestore() freshRestore()} method may
	 * be called under certain circumstances. See {@link #restoreTableClicked()
	 * its onClick event handler} for more details.)
	 */
	@FXML
	private TableView<Backup> backupTable, restoreTable;

	/**
	 * This {@link CheckBox} governs whether imported objects will replace
	 * existing objects. It is found in the {@link #tabImport import tab}.
	 */
	@FXML
	private CheckBox replace;

	/**
	 * An area in the {@link #tabImport import tab} where the user can drag and
	 * drop files. These files can then be imported.
	 */
	@FXML
	Region fileDropRegion;

	/**
	 * The root of a {@link FileManager}'s scene. All of a {@link FileManager}'s
	 * other nodes are wrapped by this or a node inside this.
	 */
	@FXML
	TabPane layout;

	/**
	 * A list of {@link MindsetObject} wrappers. This is what is stored in the
	 * export table. This was made a list of wrappers so that
	 */
	private final ObservableListWrapper<MindsetObjectCheckBoxWrapper> list = new ObservableListWrapper<>(
			new ArrayList<>());

	private File exportDirectory;

	/**
	 * <p>
	 * The directory that the {@link DirectoryChooser} will start in when the
	 * user attempts to choose a directory to export objects to.
	 * <p>
	 * This variable is modified when the user {@link #exportFolderSelected()
	 * chooses a directory}.
	 */
	private static File initialDirectory = new File("C:/");

	/**
	 * Clears all the items in the {@link #tabImport import tab}'s
	 * {@link #importedFileTreeView file viewer}.
	 */
	@FXML
	private void clearTree() {
		importedFileTreeView.getRoot().getChildren().clear();
	}

	/**
	 * Called when the user attempts to close the program.
	 */
	@FXML
	private void close() {
		closeListener.run();
		exportDirectory = null;
	}

	/**
	 * This method is called when the user attemtps to create a {@link Backup}
	 * of their data.
	 */
	@FXML
	private void createBackup() {
		final Backup backup = new Backup();
		try {
			backup.make();
		} catch (final IOException e) {
			Window.spawnLabelAtMousePos("Backup Failure", Color.RED);
			if (Wolf.DEBUG_MODE) {
				System.out.println("\n\n");
				e.printStackTrace();
			}
		}
	}

	@FXML
	private void deselectAllExportableItems() {
		for (final MindsetObjectCheckBoxWrapper m : list)
			m.checked.set(false);
	}

	@FXML
	private void exportFolderSelected() {
		final DirectoryChooser chooser = new DirectoryChooser();
		chooser.setInitialDirectory(FileManagerControllerImpl.initialDirectory);

		final File dir = chooser.showDialog(Window.getStage());

		if (dir == null)
			return;
		if (!dir.isDirectory()) {
			Window.spawnLabelAtMousePos("You must select a directory, not a file.", Color.RED);
			return;
		}
		exportDirectory = FileManagerControllerImpl.initialDirectory = dir;
	}

	@FXML
	private void exportSelectedObjects() {
		if (exportDirectory == null) {
			Window.spawnLabelAtMousePos("You haven't selected an export directory.", Color.RED);
			return;
		}
		boolean exported = false;
		for (final MindsetObjectCheckBoxWrapper m : list)
			if (m.checked.get())
				try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(
						new File(exportDirectory, "[" + m.object.getType() + "]" + m.object.getName())))) {
					if (!exported)
						exported = true;
					os.writeObject(m.object);
				} catch (final IOException e) {
					System.out.println("The Object, " + m.object.getName() + ", could not be saved.");
					if (Wolf.DEBUG_MODE) {
						System.out.println("\n\n");
						e.printStackTrace();
					}
				}
		if (!exported) {
			Window.spawnLabelAtMousePos("Nothing is selected. What do you want me to export?", Color.WHITE);
			return;
		}
		exportDirectory = null;

		Window.spawnLabelAtMousePos("Successfully exported the selected objects.", Color.GREEN);
	}

	@FXML
	private void importSelectedFiles() {
		final List<File> files = new ArrayList<>();
		for (final TreeItem<File> ti : importedFileTreeView.getRoot().getChildren())
			files.add(ti.getValue());
		if (files.isEmpty()) {
			Window.spawnLabelAtMousePos("There are no selected files...", Color.RED);
			return;
		}
		for (final File f : files)
			try {
				final MindsetObject object = Kröw.loadMindsetObject(f);
				object.getMindsetModel().attatch(Kröw.CONSTRUCT_MINDSET);
			} catch (final IOException e) {
				System.err.println(
						"An unknown exception occurred when loading an object. It is likely that the given file, "
								+ f.toString()
								+ ", is not a valid Java, Serializable object, (which is what this program uses to save/load things).");
			} catch (final ClassNotFoundException | ClassCastException e) {
				System.err.println("The object was loaded, but it doesn't belong to this program.");
			} catch (final ObjectAlreadyExistsException e) {
				if (replace.isSelected()) {
					e.getVictim().getMindsetModel().detatch();
					try {
						e.getThrower().getMindsetModel().attatch(Kröw.CONSTRUCT_MINDSET);
					} catch (final ObjectAlreadyExistsException e1) {
					}
				} else {
					System.err.println("The object, " + e.getThrower().getName()
							+ ", was loaded successfully but already exists in the program. Replacing was unselected, so the object will be ignored.");
					if (files.size() == 1) {
						Window.spawnLabelAtMousePos("That object already exists!", Color.RED);
						return;
					}
				}
			}
		Window.spawnLabelAtMousePos("Import complete!", Color.GREEN);
		try {
			Window.setScene(HomeWindow.class);
		} catch (InstantiationException | IllegalAccessException | IOException e) {
		}
	}

	@FXML
	private void restoreTableClicked() {
		// Allocate boolean variables
		boolean clear = false, overwrite = false;
		boolean backup = false;

		// Build Alert dialog
		final Alert dialog = new Alert(AlertType.CONFIRMATION);

		final ButtonType buttonTypeYes = new ButtonType("Yes");
		final ButtonType buttonTypeNo = new ButtonType("No");
		dialog.getButtonTypes().setAll(buttonTypeNo, buttonTypeYes);

		// Build & show "Are you sure?" question
		dialog.setTitle("?");
		dialog.setContentText("Are you sure you want to continue?");
		Optional<ButtonType> result = dialog.showAndWait();

		if (!result.isPresent() || result.get() == buttonTypeNo)
			return;

		// Build & show Backup question
		dialog.setContentText("Do you want to backup your current data?");
		result = dialog.showAndWait();

		if (result.isPresent())
			backup = result.get() == buttonTypeYes;
		else
			return;

		// Build & show Clear question
		dialog.setContentText("Do you want to clear your current data" + (backup ? " afterward the backup?" : "?"));
		result = dialog.showAndWait();

		if (result.isPresent())
			clear = result.get() == buttonTypeYes;
		else
			return;

		// If they aren't clearing their data, ask for overwriting
		if (!clear) {

			dialog.setContentText("Do you want to overwrite your current data?");
			result = dialog.showAndWait();

			if (result.isPresent())
				overwrite = result.get() == buttonTypeYes;
			else
				return;

		}

		// Select backup
		final Backup b = restoreTable.getFocusModel().getFocusedItem();

		// If no backups exist, b will be null.
		if (b == null) {
			Window.spawnLabelAtMousePos("There are no backups...", Color.PURPLE);
			return; // Don't attempt restore if no backups exist
		}

		// Actually do the restore stuff
		if (clear) {
			final Backup newBackup = b.freshRestore();
			if (backup)
				try {
					newBackup.make();
				} catch (final IOException e1) {
				}
		} else
			b.restore(overwrite, backup);

		// Attempt to set the current stage to the HomeWindow to refresh its
		// data. (This may be bad if the user changes windows with the
		// FileManager
		// open.)
		try {
			Window.setScene(HomeWindow.class);
		} catch (InstantiationException | IllegalAccessException | IOException e) {
		}
	}

	@FXML
	private void selectAllExportableItems() {
		for (final MindsetObjectCheckBoxWrapper m : list)
			m.checked.set(true);
	}

	public void initialize() {
		importedFileTreeView.setRoot(new TreeItem<>());
		fileDropRegion.setOnDragOver(event -> {
			if (event.getGestureSource() != fileDropRegion && event.getGestureSource() != fileDropRegion
					&& event.getDragboard().hasFiles())
				event.acceptTransferModes(TransferMode.COPY);
			event.consume();
		});

		fileDropRegion.setOnDragDropped(event -> {
			if (event.getDragboard().hasFiles())
				for (final File f0 : event.getDragboard().getFiles())
					FileLoop: for (final File f1 : Wolf.getAllFilesFromDirectory(f0)) {
						for (final TreeItem<File> ti : importedFileTreeView.getRoot().getChildren())
							if (ti.getValue().equals(f1))
								continue FileLoop;
						importedFileTreeView.getRoot().getChildren().add(new TreeItem<>(f1));
					}
		});

		exportTable.setOnMouseClicked(event -> {

			final int index = exportTable.getFocusModel().getFocusedIndex();
			if (index == -1)
				Window.spawnLabelAtMousePos("There is nothing to select...", Color.PURPLE);
			final ObservableValue<Boolean> value = exportTableSelectColumn.getCellObservableValue(index);
			((BooleanProperty) value).set(!value.getValue());
		});

		for (final MindsetObject o : Kröw.CONSTRUCT_MINDSET.getAllObjects())
			list.add(new MindsetObjectCheckBoxWrapper(o, new SimpleBooleanProperty(false)));

		exportTable.setItems(list);
		exportTableNameColumn
				.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().object.getName()));
		exportTableSelectColumn.setCellFactory(param -> new CheckBoxTableCell<>(param1 -> {
			return list.get(param1).checked;
		}));

		exportTableSelectColumn.setCellValueFactory(param -> {
			return param.getValue().checked;
		});
		exportTableSelectColumn.setEditable(true);
		exportTable.setEditable(true);
		exportTableTypeColumn
				.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().object.getType()));

		backupTable.setItems(Backup.getObservableBackupList());
		backupDateColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(
				new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(param.getValue().getDateCreated())));
		backupSizeColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getSize() + "B"));
		backupObjectCountColumn.setCellValueFactory(
				param -> new ReadOnlyObjectWrapper<>(Integer.toString(param.getValue().getObjectCount())));

		restoreTable.setItems(Backup.getObservableBackupList());
		restoreDateColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(
				new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(param.getValue().getDateCreated())));
		restoreSizeColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getSize() + "B"));
		restoreObjectCountColumn.setCellValueFactory(
				param -> new ReadOnlyObjectWrapper<>(Integer.toString(param.getValue().getObjectCount())));
	}

	public void setCloseListener(final Runnable closeListener) {
		this.closeListener = closeListener;
	}

	public class MindsetObjectCheckBoxWrapper {

		private final MindsetObject object;
		private final BooleanProperty checked;

		private MindsetObjectCheckBoxWrapper(final MindsetObject object, final BooleanProperty checked) {
			this.object = object;
			this.checked = checked;
		}

		public BooleanProperty checkedProperty() {
			return checked;
		}

		public boolean getChecked() {
			return checked.get();
		}

		public void setChecked(final boolean checked) {
			this.checked.set(checked);
		}

	}

}