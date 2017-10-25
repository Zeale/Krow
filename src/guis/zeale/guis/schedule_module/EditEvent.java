package zeale.guis.schedule_module;

import krow.guis.schedule_module.ScheduleEvent;
import kröw.core.managers.WindowManager.Page;

public class EditEvent extends Page {

	private ScheduleEvent event;

	public EditEvent(ScheduleEvent event) {

	}

	@Override
	public void initialize() {
		// TODO Build editable Nodes based off of event's fields.
	}

	@Override
	public String getWindowFile() {
		return "EditEvent.fxml";
	}

}
