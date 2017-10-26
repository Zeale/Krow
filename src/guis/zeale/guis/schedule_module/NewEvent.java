package zeale.guis.schedule_module;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import krow.guis.schedule_module.ScheduleEvent;
import kröw.core.managers.WindowManager.Page;

public class NewEvent extends Page {

	@FXML
	private TextField nameInput;

	private ScheduleEvent event;

	NewEvent() {
		event = new ScheduleEvent();
	}

	NewEvent(ScheduleEvent event) {
		this.event = event;
	}

	@Override
	public String getWindowFile() {
		return "NewEvent.fxml";
	}

	@Override
	public void initialize() {
		DropShadow effect = new DropShadow();
		effect.setOffsetX(40);
		effect.setOffsetY(30);
		effect.setSpread(0.1);
		effect.setRadius(0.2);
		effect.setWidth(11.5);
		effect.setHeight(11.5);
		effect.setColor(new Color(0, 0, 0, 0.65));
		nameInput.setEffect(effect);
	}

}
