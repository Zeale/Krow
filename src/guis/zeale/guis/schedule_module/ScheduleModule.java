package zeale.guis.schedule_module;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import krow.guis.GUIHelper;
import krow.guis.schedule_module.ScheduleEvent;
import kr�w.core.Kr�w;
import kr�w.core.managers.WindowManager;
import kr�w.core.managers.WindowManager.NotSwitchableException;
import kr�w.core.managers.WindowManager.Page;

public class ScheduleModule extends Page {

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

		dateColumn.setCellFactory(param -> {
			TableCell<ScheduleEvent, Date> cell = new TableCell<ScheduleEvent, Date>() {
				protected void updateItem(Date item, boolean empty) {
					super.updateItem(item, empty);

					if (empty) {
						setText(null);
					} else {
						setText(DateFormat.getDateInstance().format(item));
					}
				};
			};
			return cell;
		});

		nameColumn.setCellFactory(param -> {
			TableCell<ScheduleEvent, String> cell = new TableCell<ScheduleEvent, String>() {
				protected void updateItem(String item, boolean empty) {
					Kr�w.defout.println(item);
					super.updateItem(item, empty);

					if (empty) {
						setText(null);
					} else {
						setText(item);
					}
				};
			};
			return cell;
		});

		GUIHelper.addDefaultSettings(GUIHelper.buildMenu(root));
	}

}
