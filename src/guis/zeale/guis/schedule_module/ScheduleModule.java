package zeale.guis.schedule_module;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import krow.guis.schedule_module.ScheduleEvent;
import kröw.core.managers.WindowManager.Page;

public class ScheduleModule extends Page {

	public ScheduleModule() {
	}

	@Override
	public String getWindowFile() {
		return "ScheduleModule.fxml";
	}

	@FXML
	private Group selected, unselected;

	@FXML
	private void addEvent() {

	}

	@FXML
	private void renameEvent() {

	}

	@FXML
	private void deleteEvent() {

	}

	@FXML
	private void editEvent() {

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

					private void setDescription(String description) {

					}

					@SuppressWarnings("unchecked")
					@Override
					protected void updateItem(ScheduleEvent item, boolean empty) {

						if (getItem() != null && getItem() != item) {
							item.name.removeListener(
									(ChangeListener<? super String>) getProperties().remove(ListenerKeys.NAME));
							item.description.removeListener(
									(ChangeListener<? super String>) getProperties().remove(ListenerKeys.DESC));
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
							getProperties().put(ListenerKeys.NAME,
									(ChangeListener<String>) (observable, oldValue, newValue) -> setText(newValue));
							getProperties().put(ListenerKeys.DESC, (ChangeListener<String>) (observable, oldValue,
									newValue) -> setDescription(newValue));
							item.name.addListener(
									(ChangeListener<? super String>) getProperties().get(ListenerKeys.NAME));
							item.description.addListener(
									(ChangeListener<? super String>) getProperties().get(ListenerKeys.DESC));

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
