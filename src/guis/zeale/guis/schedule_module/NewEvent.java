package zeale.guis.schedule_module;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import krow.guis.schedule_module.ScheduleEvent;
import kröw.core.managers.WindowManager;
import kröw.core.managers.WindowManager.NotSwitchableException;
import kröw.core.managers.WindowManager.Page;

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

	private final ScheduleEvent event;

	// Called when making an event.
	NewEvent(final ScheduleModule module) {
		event = new ScheduleEvent();
		this.module = module;
	}

	// Called when editing an event.
	public NewEvent(final ScheduleModule module, final ScheduleEvent event) {
		this.event = event;
		this.module = module;
	}

	@Override
	public String getWindowFile() {
		return "NewEvent.fxml";
	}

	@Override
	public void initialize() {
		final DropShadow effect = new DropShadow();
		effect.setOffsetX(40);
		effect.setOffsetY(30);
		effect.setSpread(0.1);
		effect.setRadius(0.2);
		effect.setWidth(11.5);
		effect.setHeight(11.5);
		effect.setColor(new Color(0, 0, 0, 0.65));

		for (final Node n : root.getChildren())
			n.setEffect(effect);
		nameInput.setText(event.name.get());
		descInput.setText(event.description.get());
		dateInput.setValue(Instant.ofEpochMilli(event.dueDate.get()).atZone(ZoneId.systemDefault()).toLocalDate());
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
		event.dueDate.set(dateInput.getValue() == null ? System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)
				: dateInput.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());

		if (!module.containsEvent(event))
			module.addEvent(event);
	}

}
