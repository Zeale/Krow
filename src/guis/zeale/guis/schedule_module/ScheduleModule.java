package zeale.guis.schedule_module;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import krow.guis.schedule_module.ScheduleEvent;
import kröw.core.managers.WindowManager;
import kröw.core.managers.WindowManager.NotSwitchableException;
import kröw.core.managers.WindowManager.Page;

public class ScheduleModule extends Page {

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
		return eventList.getSelectionModel().getSelectedItem();
	}

	@FXML
	private ListView<ScheduleEvent> eventList;

	@Override
	public void initialize() {
		eventList.setCellFactory(new Callback<ListView<ScheduleEvent>, ListCell<ScheduleEvent>>() {

			@Override
			public ListCell<ScheduleEvent> call(ListView<ScheduleEvent> param) {
				ListCell<ScheduleEvent> cell = new ListCell<ScheduleEvent>() {

					@Override
					protected void updateItem(ScheduleEvent item, boolean empty) {

						if (getItem() != null && getItem() != item) {
						}

						super.updateItem(item, empty);

						String background;
						if ((getIndex() & 1) == 1)
							background = "#00000040";
						else
							background = "transparent";

						if (empty) {
							background = "transparent";
							setText("");
						} else {
							// TODO Set background based off of event date.

							// Add listeners
							item.name.setListener(this::setText);

							setText(item.name.get());
						}

						setStyle("-fx-background-color: " + background
								+ "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
					}
				};

				return cell;
			}
		});
	}

	private enum ListenerKeys {
		NAME, DESC;
	}

}
