package zeale.guis;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import krow.guis.schedule_module.ScheduleEvent;
import kröw.core.managers.WindowManager.Page;

public class Schedule extends Page {

	public Schedule() {
	}

	@Override
	public String getWindowFile() {
		return "Schedule.fxml";
	}
	
	@FXML
	private ListView<ScheduleEvent> eventList;

	@Override
	public void initialize() {
		
	}

}
