package krow.zeale.guis.home;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
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
	private MenuBar menuBar;

	@FXML
	private TableView<Construct> constructs;
	@FXML
	private TableView<Law> laws;

	@FXML
	private TableColumn<Construct, String> constName, constDesc;
	@FXML
	private TableColumn<Law, String> lawName, lawDesc;

	@SuppressWarnings("deprecation")
	@FXML
	private void initialize() {

		// The window can now be dragged around the screen by the Menu Bar.
		Window.setPaneDraggableByNode(menuBar);

		/*
		 * "If it's not intended for use then add a workaround method..."
		 *
		 * ~ Zeale
		 *
		 * Here I'm calling a deprecated method because the JavaFX APIs didn't
		 * have a simple way to make table columns non-reorderable.
		 * <strike>These may or may not work, but it's worth a shot.</strike>
		 * These do work... :)
		 */
		constDesc.impl_setReorderable(false);
		constName.impl_setReorderable(false);
		lawDesc.impl_setReorderable(false);
		lawName.impl_setReorderable(false);

		constructs.setItems(Kröw.INSTANCE.getConstructs());
		laws.setItems(Kröw.INSTANCE.getLaws());
		constDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
		constName.setCellValueFactory(new PropertyValueFactory<>("name"));
		lawDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
		lawName.setCellValueFactory(new PropertyValueFactory<>("name"));

		// A Test line of code to display a construct...
		// Kröw.INSTANCE.getConstructs().add(new Construct("Kröw", "Programs"));

	}

	@FXML
	private void onCloseRequested() {
		Platform.exit();
	}

}
