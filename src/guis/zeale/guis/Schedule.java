package zeale.guis;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import kr�w.core.managers.WindowManager.Page;

public class Schedule extends Page {

	public Schedule() {
	}

	@Override
	public String getWindowFile() {
		return "Schedule.fxml";
	}
	
	@FXML
	private ListView<?> eventList;

	@Override
	public void initialize() {
		
	}

}
