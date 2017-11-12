package zeale.guis.schedule_module;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import krow.guis.GUIHelper;
import krow.guis.schedule_module.ScheduleEvent;
import krow.guis.schedule_module.ScheduleRow;
import krow.guis.schedule_module.SelectableCell;
import kröw.core.Kröw;
import kröw.core.managers.WindowManager;
import kröw.core.managers.WindowManager.NotSwitchableException;
import kröw.core.managers.WindowManager.Page;

public class ScheduleModule extends Page {
	public static final File DATA_DIR = new File(Kröw.DATA_DIRECTORY, "Schedule");

	private static final ObservableList<ScheduleEvent> events = FXCollections.observableArrayList();

	static {
		importData();
	}

	public ObservableList<ScheduleEvent> getEventList() {
		return events;
	}

	private static final void importData() {
		try {

			if (DATA_DIR.exists()) {
				if (!DATA_DIR.isDirectory())
					DATA_DIR.delete();
			}

			DATA_DIR.mkdirs();

			if (DATA_DIR.listFiles() != null)
				for (File f : DATA_DIR.listFiles())
					try {
						ScheduleEvent sc = ScheduleEvent.load(f);
						sc.autoSave = true;
						events.add(sc);
					} catch (Exception e) {
						System.err
								.println("Failed to load a single ScheduleEvent from the directory. The file path is: "
										+ f.getAbsolutePath());
						e.printStackTrace();
					}
		} catch (Exception e) {
			System.err.println(
					"An exception occurred while loading saved Schedule Events. Due to the likely size of the following text, the error will be printed to the System error output.");
			e.printStackTrace(Kröw.deferr);
		}
		events.sort(null);
	}

	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yy"/* + " ~ hh:mm:ss" */);

	@FXML
	private Pane root;

	public ScheduleModule() {
	}

	@Override
	public String getWindowFile() {
		return "ScheduleModule.fxml";
	}

	@FXML
	private void addEvent() {
		try {
			WindowManager.setScene(new NewEvent(this));
		} catch (IOException | NotSwitchableException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private TableView<ScheduleEvent> eventTable;
	@FXML
	private TableColumn<ScheduleEvent, Number> dateColumn;
	@FXML
	private TableColumn<ScheduleEvent, String> nameColumn;
	@FXML
	private TableColumn<ScheduleEvent, Boolean> urgencyColumn, completeColumn;

	@Override
	public void initialize() {

		dateColumn.setCellValueFactory(param -> param.getValue().dueDate);
		nameColumn.setCellValueFactory(param -> param.getValue().name);
		urgencyColumn.setCellValueFactory(param -> param.getValue().urgent);
		completeColumn.setCellValueFactory(param -> param.getValue().complete);

		eventTable.setRowFactory(param -> new ScheduleRow(ScheduleModule.this));

		dateColumn.setCellFactory(param -> {
			return new TableCell<ScheduleEvent, Number>() {

				{
					// Default cell text fill
					setTextFill(Color.WHITE);
				}

				public void bindTextFill() {
					getTableRow().textFillProperty().bindBidirectional(textFillProperty());
				}

				protected void updateItem(Number item, boolean empty) {
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

				protected void updateItem(String item, boolean empty) {
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

		urgencyColumn
				.setCellFactory(new Callback<TableColumn<ScheduleEvent, Boolean>, TableCell<ScheduleEvent, Boolean>>() {

					@Override
					public TableCell<ScheduleEvent, Boolean> call(TableColumn<ScheduleEvent, Boolean> param) {
						return new SelectableCell<ScheduleEvent>(new Callback<Integer, BooleanProperty>() {

							@Override
							public BooleanProperty call(Integer param) {
								return events.get(param).urgent;
							}
						});
					}
				});
		completeColumn
				.setCellFactory(new Callback<TableColumn<ScheduleEvent, Boolean>, TableCell<ScheduleEvent, Boolean>>() {

					@Override
					public TableCell<ScheduleEvent, Boolean> call(TableColumn<ScheduleEvent, Boolean> param) {
						return new SelectableCell<ScheduleEvent>(new Callback<Integer, BooleanProperty>() {

							@Override
							public BooleanProperty call(Integer param) {
								return events.get(param).complete;
							}
						});
					}
				});

		// Testing dates
		eventTable.setItems(events);

		GUIHelper.addDefaultSettings(GUIHelper.buildMenu(root));
	}

	public void addEvent(ScheduleEvent event) {
		events.add(event);
	}

	public void removeEvent(ScheduleEvent event) {
		events.remove(event);
	}

	public void removeEvent(int index) {
		events.remove(index);
	}

	public boolean containsEvent(ScheduleEvent event) {
		return events.contains(event);
	}

}
