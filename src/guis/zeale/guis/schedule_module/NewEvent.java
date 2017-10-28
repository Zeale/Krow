package zeale.guis.schedule_module;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import krow.guis.schedule_module.ScheduleEvent;
import kröw.core.Kröw;
import kröw.core.managers.WindowManager;
import kröw.core.managers.WindowManager.NotSwitchableException;
import kröw.core.managers.WindowManager.Page;

public class NewEvent extends Page {

	@FXML
	private TextField nameInput;
	@FXML
	private TextArea descInput;
	@FXML
	private Pane root;

	private ScheduleEvent event;

	// Called when making an event.
	NewEvent() {
		event = new ScheduleEvent();
	}

	// Called when editing an event.
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

		for (Node n : root.getChildren())
			n.setEffect(effect);
		nameInput.setText(event.name.get());
		descInput.setText(event.description.get());
	}

	@FXML
	private void save() {
		event.name.set(nameInput.getText());
		event.description.set(descInput.getText());
		if (!ScheduleModule.getInstance().getItems().contains(event)) {
			ScheduleModule.getInstance().getItems().add(event);
			ScheduleModule.getInstance().refresh();
		}
		try {
			WindowManager.setScene(ScheduleModule.class);
		} catch (InstantiationException | IllegalAccessException | IOException | NotSwitchableException e) {
			e.printStackTrace();
		}

	}

}
