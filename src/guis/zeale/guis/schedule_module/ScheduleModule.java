package zeale.guis.schedule_module;

import java.io.IOException;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import krow.guis.GUIHelper;
import krow.guis.PopupHelper;
import krow.guis.schedule_module.ScheduleEvent;
import kröw.core.Kröw;
import kröw.core.managers.WindowManager;
import kröw.core.managers.WindowManager.NotSwitchableException;
import kröw.core.managers.WindowManager.Page;
import zeale.guis.developer_module.ConsoleModule;

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
		return eventList.getSelectionModel().getSelectedItem();
	}

	@FXML
	private ListView<ScheduleEvent> eventList;

	ObservableList<ScheduleEvent> getItems() {
		return eventList.getItems();
	}

	@Override
	public void initialize() {
		if (instance != null)
			for (ScheduleEvent se : instance.getItems())
				getItems().add(se);
		instance = this;
		eventList.setCellFactory(new Callback<ListView<ScheduleEvent>, ListCell<ScheduleEvent>>() {

			@Override
			public ListCell<ScheduleEvent> call(ListView<ScheduleEvent> param) {
				ListCell<ScheduleEvent> cell = new ListCell<ScheduleEvent>() {

					@Override
					protected void updateItem(ScheduleEvent item, boolean empty) {

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

							setAlignment(Pos.CENTER);

							Label edit = new Label("edit"), delete = new Label("delete");
							edit.setOnMouseClicked(new EventHandler<Event>() {

								@Override
								public void handle(Event event) {
									try {
										WindowManager.setScene(new NewEvent(getItem()));
									} catch (IOException | NotSwitchableException e) {
										e.printStackTrace(Kröw.deferr);
									}
								}
							});
							PopupHelper.buildRightClickPopup(this, edit, delete);
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

		GUIHelper.addDefaultSettings(GUIHelper.buildMenu(root));
	}

}
