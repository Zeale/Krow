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

	/**
	 * The {@link Stage} that this {@link FileManager} will use to render it's
	 * GUI.
	 */
	private final Stage stage = new Stage();
	/**
	 * The controller object of the FXML-made {@link Scene} of this
	 * {@link FileManager}.
	 */
	private final FileManagerControllerImpl controller;
	/**
	 * The tabs of the {@link FileManager}. These can be passed in to
	 * {@link #show(Tab)} and {@link #setTab(Tab)}.
	 */
	public final Tab IMPORT, EXPORT, BACKUP, RESTORE;

	/**
	 * <p>
	 * Constructs a {@link FileManager} object.
	 * <p>
	 * This constructor needs to be called from the JavaFX Application thread.
	 * <p>
	 * This method loads the <code>FileManager.fxml</code> and passes it in to
	 * the {@link Scene#Scene(javafx.scene.Parent)} constructor. The
	 * {@link Scene} object returned is then passed into the {@link #stage}'s
	 * {@link Stage#setScene(Scene) setScene(Scene)} method.
	 */
	public FileManager() {
		// Create a loader object so we can get the controller later on.
		final FXMLLoader loader = new FXMLLoader(FileManager.class.getResource("FileManager.fxml"));

		try {
			// Attempt to load the FileManager.fxml file and make a Scene object
			// with it. Assign the Scene object to our stage.
			stage.setScene(new Scene(loader.load()));
			// Remove the border from our new Stage. This way, the Window won't
			// have a minimize, resize, and close button at the top right. The
			// window will also loose its border.
			stage.initStyle(StageStyle.UNDECORATED);
		} catch (final IOException e) {
			// This block should never run.
			e.printStackTrace();
		}
		// Assign the window's controller to our 'controller' variable
		controller = loader.<FileManagerControllerImpl>getController();

		// Allow the user to drag the window around using the TabPane.
		Window.setPaneDraggableByNode(stage, controller.layout);

		// Assign these variables so they can be passed in to setTAb(Tab) and
		// show(Tab) by the HomeWindow class.
		IMPORT = controller.tabImport;
		EXPORT = controller.tabExport;
		BACKUP = controller.tabBackup;
		RESTORE = controller.tabRestore;

		// The controller doesn't have access to the stage object, so we have to
		// queue the stage.hide(); method to run when the user presses "close".

		// The Controller knows when the user clicks close, so we tell it to run
		// what we want when they click close.
		controller.setCloseListener(stage::hide);// Pass a 'Method Reference".

		// Center the stage on the screen. Obviously...
		stage.centerOnScreen();
	}

	/**
	 * Centers this Window to the middle of the screen. The middle of this
	 * Window will be at the middle of the screen once this method has returned.
	 * See {@link Stage#centerOnScreen()} for more information.
	 */
	public void centerOnScreen() {
		stage.centerOnScreen();
	}

	/**
	 * Gets an {@link ObservableList} of {@link TreeItem}s each of which are of
	 * {@link File}s, and returns it. The given list contains all the Files
	 * given by the user to be imported.
	 *
	 * @return An {@link ObservableList} of {@link TreeItem}s of {@link File}s
	 *         containing all the given files by the user.
	 */
	public ObservableList<TreeItem<File>> getFiles() {
		return controller.importedFileTreeView.getTreeItem(0).getChildren();
	}

	/**
	 * Attempts to hide the window.
	 */
	public void hide() {
		stage.hide();
	}

	/**
	 * Sets the currently showing {@link Tab} to the {@link Tab} specified.
	 *
	 * @param tab
	 *            The {@link Tab} to show.
	 */
	public void setTab(final Tab tab) {
		controller.layout.getSelectionModel().select(tab);
	}

	/**
	 * Shows the FileManager's stage.
	 *
	 * @return If this {@link FileManager}'s visibility was changed to
	 *         "showing", <code>false</code> otherwise.
	 */
	public boolean show() {
		if (stage.isShowing())
			return false;
		stage.show();
		return true;
	}

	/**
	 * <p>
	 * Shows this window and sets the current {@link Tab}.
	 * <p>
	 * If this window is already showing, the tab is not changed.
	 *
	 * @param tab
	 *            The tab to show.
	 * @return If this {@link FileManager}'s visibility was changed to
	 *         "showing", <code>false</code> otherwise.
	 */
	public boolean show(final Tab tab) {
		// Assert that the window is not showing.
		if (stage.isShowing())
			return false;
		setTab(tab);
		return show();
	}

	/**
	 * Sends this window behind any other windows that it may be overlapping.
	 */
	public void toBack() {
		stage.toBack();
	}

	/**
	 * Sends this window infront of any windows that it may be overlapping.
	 */
	public void toFront() {
		stage.toFront();
	}

}
