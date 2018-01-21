package zeale.guis.schedule_app;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import krow.guis.GUIHelper;
import krow.guis.schedule_app.ScheduleEvent;
import krow.guis.schedule_app.ScheduleRow;
import krow.guis.schedule_app.SelectableCell;
import kröw.core.Kröw;
import kröw.gui.Application;
import kröw.gui.ApplicationManager;
import kröw.gui.exceptions.NotSwitchableException;

public class ScheduleApp extends Application {
	public static final File DATA_DIR = new File(Kröw.DATA_DIRECTORY, "Schedule");

	private static final ObservableList<ScheduleEvent> events = FXCollections.observableArrayList();

	static {
		importData();
	}

	{
		events.sort(null);
	}

	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yy"/* + " ~ hh:mm:ss" */);

	private static final void importData() {
		try {

			if (DATA_DIR.exists())
				if (!DATA_DIR.isDirectory())
					DATA_DIR.delete();

			DATA_DIR.mkdirs();

			if (DATA_DIR.listFiles() != null)
				for (final File f : DATA_DIR.listFiles())
					try {
						final ScheduleEvent sc = ScheduleEvent.load(f);
						sc.autoSave = true;
						events.add(sc);
					} catch (final Exception e) {
						System.err
								.println("Failed to load a single ScheduleEvent from the directory. The file path is: "
										+ f.getAbsolutePath());
						e.printStackTrace();
					}
		} catch (final Exception e) {
			System.err.println(
					"An exception occurred while loading saved Schedule Events. Due to the likely size of the following text, the error will be printed to the System error output.");
			e.printStackTrace(Kröw.deferr);
		}
		events.sort(null);
	}

	@FXML
	private Pane root;

	@FXML
	private TableView<ScheduleEvent> eventTable;

	@FXML
	private TableColumn<ScheduleEvent, Number> dateColumn;

	@FXML
	private TableColumn<ScheduleEvent, String> nameColumn;

	@FXML
	private TableColumn<ScheduleEvent, ScheduleEvent> urgencyColumn, completeColumn;

	public ScheduleApp() {
	}

	@FXML
	private void addEvent() {
		ApplicationManager.setScene(NewEvent.class.getResource("NewEvent.fxml"), new NewEvent(this));
	}

	public void addEvent(final ScheduleEvent event) {
		events.add(event);
		events.sort(null);
	}

	public boolean containsEvent(final ScheduleEvent event) {
		return events.contains(event);
	}

	public ObservableList<ScheduleEvent> getEventList() {
		return events;
	}

	@Override
	public void initialize() {

		dateColumn.setCellValueFactory(param -> param.getValue().dueDate);
		nameColumn.setCellValueFactory(param -> param.getValue().name);
		urgencyColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));
		completeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));

		eventTable.setRowFactory(param -> new ScheduleRow(ScheduleApp.this));

		dateColumn.setCellFactory(param -> {
			return new TableCell<ScheduleEvent, Number>() {

				{
					// Default cell text fill
					setTextFill(Color.WHITE);
				}

				public void bindTextFill() {
					getTableRow().textFillProperty().bindBidirectional(textFillProperty());
				}

				@Override
				protected void updateItem(final Number item, final boolean empty) {
					if (getItem() == item)
						return;

					super.updateItem(item, empty);

					if (item == null || empty) {
						setText(null);
						setGraphic(null);
					} else {
						bindTextFill();
						setText(dateFormatter.format(new Date((long) item)));
						setGraphic(null);
					}
				};
			};
		});

		nameColumn.setCellFactory(param -> {
			return new TableCell<ScheduleEvent, String>() {

				{
					setTextFill(Color.WHITE);
				}

				public void bindTextFill() {
					getTableRow().textFillProperty().bindBidirectional(textFillProperty());
				}

				@Override
				protected void updateItem(final String item, final boolean empty) {
					if (getItem() == item)
						return;

					super.updateItem(item, empty);

					if (item == null || empty) {
						setText(null);
						setGraphic(null);
					} else {
						bindTextFill();
						setText(item);
						setGraphic(null);
					}
				};
			};
		});

		urgencyColumn.setCellFactory(param -> new SelectableCell<>(param1 -> param1.urgent));
		completeColumn.setCellFactory(param -> new SelectableCell<>(param1 -> param1.complete));

		// Testing dates
		eventTable.setItems(events);

		GUIHelper.addDefaultSettings(GUIHelper.buildMenu(root));
	}

	public void removeEvent(final int index) {
		events.remove(index);
		events.sort(null);
	}

	public void removeEvent(final ScheduleEvent event) {
		events.remove(event);
		events.sort(null);
	}

	@FXML
	private void refreshTable() {
		events.sort(null);
	}

}
