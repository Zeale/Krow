package krow.zeale.guis.home;

import java.io.File;
import java.io.IOException;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import wolf.zeale.guis.Window;

public class FileManager {

	private final Stage stage = new Stage();
	private final FileManagerControllerImpl controller;
	public final Tab IMPORT, EXPORT, BACKUP, RESTORE;

	public FileManager() {
		final FXMLLoader loader = new FXMLLoader(FileManager.class.getResource("FileManager.fxml"));
		try {
			stage.setScene(new Scene(loader.load()));
			stage.initStyle(StageStyle.UNDECORATED);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		controller = loader.<FileManagerControllerImpl>getController();
		Window.setPaneDraggableByNode(stage, controller.layout);

		IMPORT = controller.tabImport;
		EXPORT = controller.tabExport;
		BACKUP = controller.tabBackup;
		RESTORE = controller.tabRestore;
		controller.setCloseListener(stage::hide);
		stage.centerOnScreen();
	}

	public void centerOnScreen() {
		stage.centerOnScreen();
	}

	public ObservableList<TreeItem<File>> getFiles() {
		return controller.selectedFileViewer.getTreeItem(0).getChildren();
	}

	public void hide() {
		stage.hide();
	}

	public void setTab(final Tab tab) {
		controller.layout.getSelectionModel().select(tab);
	}

	public boolean show() {
		if (stage.isShowing())
			return false;
		stage.show();
		return true;
	}

	public boolean show(final Tab tab) {
		if (stage.isShowing())
			return false;
		setTab(tab);
		return show();
	}

	public void toBack() {
		stage.toBack();
	}

	public void toFront() {
		stage.toFront();
	}

}
