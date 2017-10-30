package zeale.guis.schedule_module;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import krow.guis.GUIHelper;
import krow.guis.schedule_module.ScheduleEvent;
import kr�w.core.Kr�w;
import kr�w.core.managers.WindowManager;
import kr�w.core.managers.WindowManager.NotSwitchableException;
import kr�w.core.managers.WindowManager.Page;

public class ScheduleModule extends Page {

	static {
		// File file = new File(Kr�w.DATA_DIRECTORY, "ScheduleModule");
	}

	// 20 days in milliseconds.
	private static final double MILLIS_UNTIL_IMPORTANT = 1728e6;

	public final static Background EMPTY_CELL_BACKGROUND = new Background(
			new BackgroundFill(Color.TRANSPARENT, null, null));

	private static final Background buildBackground(Color color) {
		return new Background(new BackgroundFill(color, null, null));
	}

	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yy ~ hh:mm");

	private static final Color getColorFromDueDate(long time) {

		// Get the time until the date is due.
		long timeUntilDue = time - System.currentTimeMillis();

		// If the due date has passed, make the event red.
		if (timeUntilDue < 0)
			return Color.RED;

		// The color that an event will become as its due date approaches.
		Color dueColor = Color.GOLD.interpolate(Color.ORANGE, 0.6);

		// Interpolate the color between dueColor and our background color; the
		// color will approach dueColor as the timeUntilDue approaches 0.
		return dueColor.interpolate(new Color(0, 0, 0, 0.3), timeUntilDue / MILLIS_UNTIL_IMPORTANT);
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
			WindowManager.setScene(new NewEvent());
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

	@Override
	public void initialize() {

		System.setErr(Kr�w.deferr);

		dateColumn.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<ScheduleEvent, Number>, ObservableValue<Number>>() {

					@Override
					public ObservableValue<Number> call(CellDataFeatures<ScheduleEvent, Number> param) {
						return param.getValue().dueDate;
					}
				});
		nameColumn.setCellValueFactory(param -> param.getValue().name);

		eventTable.setRowFactory(new Callback<TableView<ScheduleEvent>, TableRow<ScheduleEvent>>() {

			@Override
			public TableRow<ScheduleEvent> call(TableView<ScheduleEvent> param) {
				TableRow<ScheduleEvent> row = new TableRow<ScheduleEvent>() {

					{

						setBackground(buildBackground(new Color(0, 0, 0, 0.3)));

						setOnMouseClicked(new EventHandler<MouseEvent>() {
							@Override
							public void handle(MouseEvent event) {
								if (event.getButton() == (MouseButton.PRIMARY) && !isEmpty()) {
									try {
										WindowManager.setScene(new NewEvent(getItem()));
									} catch (IOException | NotSwitchableException e) {
										e.printStackTrace();
									}
								}
							}
						});
					}

					@Override
					protected void updateItem(ScheduleEvent item, boolean empty) {
						if (item == getItem())
							return;
						super.updateItem(item, empty);

						if (item == null || empty) {
							setBackground(buildBackground(new Color(0, 0, 0, 0.3)));
						} else
							setBackground(buildBackground(getColorFromDueDate(getItem().dueDate.get())));
					}
				};
				return row;
			}
		});

		dateColumn.setCellFactory(param -> {
			TableCell<ScheduleEvent, Number> cell = new TableCell<ScheduleEvent, Number>() {

				protected void updateItem(Number item, boolean empty) {
					if (getItem() == item)
						return;
					super.updateItem(item, empty);

					if (item == null || empty) {
						setText(null);
						setGraphic(null);
					} else {
						setText(dateFormatter.format(new Date((long) item)));
						setGraphic(null);
					}
				};
			};
			return cell;
		});

		nameColumn.setCellFactory(param -> {
			TableCell<ScheduleEvent, String> cell = new TableCell<ScheduleEvent, String>() {

				protected void updateItem(String item, boolean empty) {
					if (getItem() == item)
						return;
					super.updateItem(item, empty);

					if (item == null || empty) {
						setText(null);
						setGraphic(null);
					} else {
						setText(item);
						setGraphic(null);
					}
				};
			};
			return cell;
		});

		// Testing dates
		eventTable.getItems().addAll(
				new ScheduleEvent("Test", "test", System.currentTimeMillis() + TimeUnit.HOURS.toMillis(8)),
				new ScheduleEvent("Test", "test", System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)),
				new ScheduleEvent("Test", "test", System.currentTimeMillis() + TimeUnit.DAYS.toMillis(6)),
				new ScheduleEvent("Test", "test", System.currentTimeMillis() + TimeUnit.DAYS.toMillis(13)),
				new ScheduleEvent("Test", "test", System.currentTimeMillis() + TimeUnit.DAYS.toMillis(18)),
				new ScheduleEvent("Test", "test", System.currentTimeMillis() + TimeUnit.DAYS.toMillis(50)),
				new ScheduleEvent("Test", "test", System.currentTimeMillis() + TimeUnit.HOURS.toMillis(-8)));

		GUIHelper.addDefaultSettings(GUIHelper.buildMenu(root));
	}

}
