package krow.guis.create;

import javafx.application.Platform;
import javafx.fxml.FXML;
import wolf.zeale.guis.Window;

public abstract class CreateWindow extends Window {

	@FXML
	protected abstract void _event_back();

	@FXML
	protected void _event_close() {
		Platform.exit();
	}

	@FXML
	protected abstract void _event_create();

	@FXML
	protected abstract void _event_home();
}
