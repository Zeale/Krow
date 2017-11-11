package zeale.guis.schedule_module;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import krow.guis.GUIHelper;
import krow.guis.PopupHelper;
import krow.guis.PopupHelper.PopupWrapper;
import krow.guis.schedule_module.ScheduleEvent;
import kröw.core.Kröw;
import kröw.core.managers.WindowManager;
import kröw.core.managers.WindowManager.NotSwitchableException;
import kröw.core.managers.WindowManager.Page;

public class ScheduleModule extends Page {
	public static final File DATA_DIR = new File(Kröw.DATA_DIRECTORY, "Schedule");

	private static final ObservableList<ScheduleEvent> events = FXCollections.observableArrayList();

	static {
		// TODO Delete this.
		System.setErr(Kröw.deferr);
		System.setOut(Kröw.defout);
		importData();
	}

	private static final Color EMPTY_CELL_COLOR = Color.TRANSPARENT, DEFAULT_START_COLOR = new Color(0, 0, 0, 0.3),
			DEFAULT_END_COLOR = Color.GOLD, COMPLETE_COLOR = Color.GREEN, URGENT_END_COLOR = Color.HOTPINK;

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

	private static final void overwriteImport() {
		events.clear();
		importData();
	}

	/**
	 * 20 days in milliseconds.
	 */
	private static final double MILLIS_UNTIL_IMPORTANT = 1728e6;

	private static final Background buildBackground(Color color) {
		return new Background(new BackgroundFill(color, null, null));
	}

	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yy"/* + " ~ hh:mm:ss" */);

	private static final Color getColorFromDueDate(long time) {
		return getColorFromDueDate(time, DEFAULT_START_COLOR, DEFAULT_END_COLOR.interpolate(Color.ORANGE, 0.6));
	}

	private static final Color getColorFromDueDate(long time, Color upcomingColor, Color dueColor) {

		// Get the time until the date is due.
		long timeUntilDue = time - System.currentTimeMillis();

		// If the due date has passed, make the event red.
		if (timeUntilDue < 0)
			return Color.RED;

		// Interpolate the color between dueColor and our background color; the
		// color will approach dueColor as the timeUntilDue approaches 0.
		return dueColor.interpolate(upcomingColor, timeUntilDue / MILLIS_UNTIL_IMPORTANT);
	}

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

		eventTable.setRowFactory(new Callback<TableView<ScheduleEvent>, TableRow<ScheduleEvent>>() {
			@Override
			public TableRow<ScheduleEvent> call(TableView<ScheduleEvent> param) {
				return new TableRow<ScheduleEvent>() {

					private void resetBackground() {
						if (isEmpty() || getItem() == null) {
							setBackground(buildBackground(Color.TRANSPARENT));
							return;
						}

						setBackground(buildBackground(getItem().complete.get() ? COMPLETE_COLOR
								: (getItem().urgent.get() ? getColorFromDueDate(getItem().dueDate.get(),
										DEFAULT_START_COLOR, URGENT_END_COLOR)
										: getColorFromDueDate(getItem().dueDate.get()))));
						setTextFill(Color.WHITE);
					}

					private TableRow<ScheduleEvent> getThis() {
						return this;
					}

					{

						// Set the default background (so it isn't white)
						setBackground(buildBackground(EMPTY_CELL_COLOR));

						Label delete = new Label("Delete");
						PopupWrapper<VBox> wrapper = PopupHelper.buildPopup(delete);
						PopupHelper.applyRightClickPopup(getThis(), wrapper.popup);
						delete.setOnMouseClicked(event -> {

							if (!isEmpty() && getItem() != null && event.getButton() == MouseButton.PRIMARY) {
								getItem().delete();
								events.remove(getItem());
								wrapper.popup.hide();
							}
						});

						setTextFill(Color.WHITE);

						setOnMouseClicked(event -> {
							if (event.getButton() == (MouseButton.PRIMARY) && !isEmpty()
									&& !(event.getPickResult().getIntersectedNode() instanceof SelectableCell)) {
								try {
									WindowManager.setScene(new NewEvent(ScheduleModule.this, getItem()));
									event.consume();
								} catch (IOException | NotSwitchableException e) {
									e.printStackTrace();
								}
							}
						});

						setOnMouseEntered(event -> {
							if (!isEmpty()) {
								setBackground(buildBackground(Color.WHITE));
								setTextFill(Color.BLACK);
							}
						});

						setOnMouseExited(event -> {
							resetBackground();
						});
					}

					@Override
					protected void updateItem(ScheduleEvent item, boolean empty) {
						if (item == getItem())
							return;
						super.updateItem(item, empty);

						if (item == null || empty) {
							setBackground(buildBackground(EMPTY_CELL_COLOR));
						} else {
							resetBackground();
						}
					}
				};
			}
		});

		dateColumn.setCellFactory(param -> {
			return new TableCell<ScheduleEvent, Number>() {

				{
					// Default cell text fill
					setTextFill(Color.WHITE);
				}

				public void bindTextFill() {
					getTableRow().textFillProperty().bindBidirectional(textFillProperty());
				}

				public void unbindTextFill() {
					getTableRow().textFillProperty().unbindBidirectional(textFillProperty());
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

				public void unbindTextFill() {
					getTableRow().textFillProperty().unbindBidirectional(textFillProperty());
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
