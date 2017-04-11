package krow.zeale.guis.home;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import kröw.libs.Construct;
import kröw.libs.Law;
import kröw.zeale.v1.program.core.Kröw;
import kröw.zeale.v1.program.guis.Window;

public class HomeWindow extends Window {

	@FXML
	private ScrollPane bottomPane;

	@FXML
	private TableView<Construct> constructs;
	@FXML
	private TableView<Law> laws;

	@FXML
	private TableColumn<Construct, String> constName, constDesc;
	@FXML
	private TableColumn<Law, String> lawName, lawDesc;

	@FXML
	private void initialize() {
		bottomPane.setHbarPolicy(ScrollBarPolicy.NEVER);

		constructs.setEditable(false);
		laws.setEditable(false);
		constDesc.setEditable(false);
		constName.setEditable(false);
		lawDesc.setEditable(false);
		lawName.setEditable(false);
		constDesc.setResizable(false);
		constName.setResizable(false);
		lawName.setResizable(false);
		lawDesc.setResizable(false);

		constructs.setItems(Kröw.INSTANCE.getConstructs());
		laws.setItems(Kröw.INSTANCE.getLaws());
		constDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
		constName.setCellValueFactory(new PropertyValueFactory<>("name"));
		lawName.setCellValueFactory(new PropertyValueFactory<>("name"));
		lawDesc.setCellValueFactory(new PropertyValueFactory<>("description"));

	}

	@FXML
	private void onCloseRequested() {
		Platform.exit();
	}

}
