package zeale.guis.schedule_module;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
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
import kröw.core.Kröw;
import kröw.core.managers.WindowManager;
import kröw.core.managers.WindowManager.NotSwitchableException;
import kröw.core.managers.WindowManager.Page;

public class ScheduleModule extends Page {

	static {
		File file = new File(Kröw.DATA_DIRECTORY, "ScheduleModule");
	}
	private static final double MILLIS_UNTIL_IMPORTANT = 1728e6;

	public final static Background EMPTY_CELL_BACKGROUND = new Background(
			new BackgroundFill(Color.TRANSPARENT, null, null));

	private static final Background buildBackground(Color color) {
		return new Background(new BackgroundFill(color, null, null));
	}

	private static final Calendar calendar = Calendar.getInstance();

	private static final Color getColorFromDueDate(double time) {
		// TODO Implement
		return null;
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
	private TableColumn<ScheduleEvent, Date> dateColumn;
	@FXML
	private TableColumn<ScheduleEvent, String> nameColumn;

	@Override
	public void initialize() {

		dateColumn.setCellValueFactory(param -> param.getValue().dueDate);
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
							setBackground(buildBackground(new Color(0, 0, 0, 0.3)));
					}
				};
				return row;
			}
		});

		dateColumn.setCellFactory(param -> {
			TableCell<ScheduleEvent, Date> cell = new TableCell<ScheduleEvent, Date>() {
				protected void updateItem(Date item, boolean empty) {
					if (getItem() == item)
						return;
					super.updateItem(item, empty);

					if (item == null || empty) {
						setText(null);
						setGraphic(null);
					} else {
						setText(DateFormat.getDateInstance().format(item));
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

		GUIHelper.addDefaultSettings(GUIHelper.buildMenu(root));
	}

}
