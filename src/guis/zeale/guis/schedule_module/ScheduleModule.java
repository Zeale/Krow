package zeale.guis.schedule_module;

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
import kröw.core.managers.WindowManager;
import kröw.core.managers.WindowManager.NotSwitchableException;
import kröw.core.managers.WindowManager.Page;

public class ScheduleModule extends Page {

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
						setOnMouseClicked(new EventHandler<MouseEvent>() {

							@Override
							public void handle(MouseEvent event) {
								if (event.getButton() == (MouseButton.PRIMARY)) {
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
							setBackground(EMPTY_CELL_BACKGROUND);
							setBackground(buildBackground(new Color(0, 0, 0, 0.3)));
						}
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
						setBackground(EMPTY_CELL_BACKGROUND);
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
						setBackground(EMPTY_CELL_BACKGROUND);
					} else {
						setText(item);
						setGraphic(null);
					}
				};
			};
			return cell;
		});
		eventTable.setItems(FXCollections.observableArrayList(new ScheduleEvent("Desc", "Name"),
				new ScheduleEvent("Desc", "Name"), new ScheduleEvent("Desc", "Name"), new ScheduleEvent("Desc", "Name"),
				new ScheduleEvent("Desc", "Name"), new ScheduleEvent("Desc", "Name")));

		GUIHelper.addDefaultSettings(GUIHelper.buildMenu(root));
	}

}
