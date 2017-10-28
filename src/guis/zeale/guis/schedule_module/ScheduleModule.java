package zeale.guis.schedule_module;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;
import krow.guis.GUIHelper;
import krow.guis.schedule_module.ScheduleEvent;
import kröw.callables.ParameterizedTask;
import kröw.core.Kröw;
import kröw.core.managers.WindowManager;
import kröw.core.managers.WindowManager.NotSwitchableException;
import kröw.core.managers.WindowManager.Page;

public class ScheduleModule extends Page {

	private static ScheduleModule instance;
	@FXML
	private Pane root;

	/**
	 * @return the instance
	 */
	static ScheduleModule getInstance() {
		return instance;
	}

	void refresh() {
		eventTable.refresh();
	}

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

	private ScheduleEvent getSelectedEvent() {
		return eventTable.getSelectionModel().getSelectedItem();
	}

	@FXML
	private TableView<ScheduleEvent> eventTable;
	@FXML
	private TableColumn<ScheduleEvent, ScheduleEvent> dateColumn;
	@FXML
	private TableColumn<ScheduleEvent, ScheduleEvent> nameColumn;

	ObservableList<ScheduleEvent> getItems() {
		return eventTable.getItems();
	}

	@Override
	public void initialize() {
		if (instance != null)
			for (ScheduleEvent se : instance.getItems())
				getItems().add(se);
		instance = this;
		eventTable.setRowFactory(param -> {
			TableRow<ScheduleEvent> t = new TableRow<ScheduleEvent>() {
				{
					setOnMouseClicked(event -> {
						if (!isEmpty() && event.getButton().equals(MouseButton.PRIMARY)) {
							try {
								WindowManager.setScene(new NewEvent(getItem()));
							} catch (IOException | NotSwitchableException e) {
								e.printStackTrace();
							}
						}
					});
				}

				@Override
				protected void updateItem(ScheduleEvent item, boolean empty) {
					super.updateItem(item, empty);

					String background;
					if ((getIndex() & 1) == 1)
						background = "#00000040";
					else
						background = "#00000020";

					if (empty) {
						background = "transparent";
						setText("");
					} else {

					}

					setStyle("-fx-background-color: " + background
							+ "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
				}
			};

			return t;

		});

		dateColumn.setCellFactory(param -> {
			TableCell<ScheduleEvent, ScheduleEvent> cell = new TableCell<ScheduleEvent, ScheduleEvent>() {

				private void setDate(Date date) {
					setText(DateFormat.getInstance().format(date));
				}

				@Override
				protected void updateItem(ScheduleEvent item, boolean empty) {
					super.updateItem(item, empty);

					if (empty) {
						setText(null);
						setGraphic(null);
					} else {
						setDate(item.dueDate.getDate());
						item.dueDate.setListener(this::setDate);
					}

				}
			};
			return cell;
		});

		nameColumn.setCellFactory(param -> {
			TableCell<ScheduleEvent, ScheduleEvent> cell = new TableCell<ScheduleEvent, ScheduleEvent>() {
				@Override
				protected void updateItem(ScheduleEvent item, boolean empty) {
					if (empty) {
						setText(null);
						setGraphic(null);
					} else {
						setText(item.name.get());
						item.name.setListener(this::setText);
					}
					super.updateItem(item, empty);
				}
			};
			return cell;
		});

		GUIHelper.addDefaultSettings(GUIHelper.buildMenu(root));
	}

}
