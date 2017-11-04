package zeale.guis.schedule_module;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import krow.guis.schedule_module.ScheduleEvent;
import kr�w.core.managers.WindowManager;
import kr�w.core.managers.WindowManager.NotSwitchableException;
import kr�w.core.managers.WindowManager.Page;

public class NewEvent extends Page {

	private final ScheduleModule module;

	@FXML
	private TextField nameInput;
	@FXML
	private TextArea descInput;
	@FXML
	private Pane root;
	@FXML
	private DatePicker dateInput;

	private ScheduleEvent event;

	// Called when making an event.
	NewEvent(ScheduleModule module) {
		event = new ScheduleEvent();
		this.module = module;
	}

	// Called when editing an event.
	NewEvent(ScheduleModule module, ScheduleEvent event) {
		this.event = event;
		this.module = module;
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

		for (Node n : root.getChildren())
			n.setEffect(effect);
		nameInput.setText(event.name.get());
		descInput.setText(event.description.get());
		dateInput.setValue(Instant.ofEpochMilli(event.dueDate.get()).atZone(ZoneId.systemDefault()).toLocalDate(););
	}

	@FXML
	private void save() {

		try {
			WindowManager.setScene(new ScheduleModule());
		} catch (IOException | NotSwitchableException e) {
			e.printStackTrace();
		}

		event.name.set(nameInput.getText());
		event.description.set(descInput.getText());
		event.dueDate.set(dateInput.getValue().);

		if (!module.containsEvent(event))
			module.addEvent(event);
	}

}
