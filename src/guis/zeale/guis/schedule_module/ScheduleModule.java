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
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;
import krow.guis.GUIHelper;
import krow.guis.schedule_module.ScheduleEvent;
import kr�w.core.managers.WindowManager;
import kr�w.core.managers.WindowManager.NotSwitchableException;
import kr�w.core.managers.WindowManager.Page;

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
	private TableColumn<ScheduleEvent, Date> dateColumn;
	@FXML
	private TableColumn<ScheduleEvent, String> nameColumn;

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
			TableRow<ScheduleEvent> cell = new TableRow<ScheduleEvent>() {

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

						setAlignment(Pos.CENTER);

					}

					setStyle("-fx-background-color: " + background
							+ "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
				}
			};

			return cell;
		});

		GUIHelper.addDefaultSettings(GUIHelper.buildMenu(root));
	}

}
