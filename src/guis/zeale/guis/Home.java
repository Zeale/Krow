
package zeale.guis;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import krow.guis.GUIHelper;
import krow.scene.ScrollMenu;
import kröw.core.Kröw;
import kröw.core.managers.WindowManager;
import kröw.core.managers.WindowManager.NotSwitchableException;
import kröw.core.managers.WindowManager.Page;

public class Home extends ScrollMenu {

	private static final Image CHAT_ROOM_MENU_ICON = new Image("krow/resources/ChatRoomIcon_hd.png");

	@Override
	public boolean canSwitchPage(final Class<? extends Page> newSceneClass) {
		if (newSceneClass.equals(Home.class))
			return false;
		return true;
	}

	@Override
	public String getWindowFile() {
		return "Home.fxml";
	}

	@FXML
	@Override
	public void initialize() {

		super.initialize();

		// Styles. Yaaa...
		horizontalScroll.setStyle(
				"-fx-background-color:  linear-gradient(to right, #00000020 0%, #000000A8 45%, #000000A8 55%, #00000020 100%);");

		loadDefaultImages();

		GUIHelper.addDefaultSettings(GUIHelper.buildMenu(pane));
		GUIHelper.applyShapeBackground(pane);
	}

	protected final void loadDefaultImages() {
		// Now to add the default images to our horizontal scroll container.
		final ImageView chatRoom = new ImageView(CHAT_ROOM_MENU_ICON);
		chatRoom.setOnMouseClicked(event -> {
			try {
				WindowManager.setScene(ChatRoom.class);
			} catch (InstantiationException | IllegalAccessException | IOException | NotSwitchableException e1) {
				e1.printStackTrace();
			}
		});
		chatRoom.setPickOnBounds(true);

		final ImageView krow = new ImageView(Kröw.IMAGE_KRÖW);
		// This code assures that clicking in a transparent portion of the image
		// will still cause a click to be registered by the event handler.
		// Event handler
		krow.setOnMouseClicked(event -> {
			try {
				WindowManager.setScene(Tools.class);
			} catch (InstantiationException | IllegalAccessException | IOException
					| WindowManager.NotSwitchableException e) {
				e.printStackTrace();
			}
		});

		final ImageView settings = new ImageView("krow/resources/Settings.png");
		settings.setOnMouseClicked(event -> {
			try {
				WindowManager.setScene(Settings.class);
			} catch (InstantiationException | IllegalAccessException | IOException e) {
				e.printStackTrace();
			} catch (final WindowManager.NotSwitchableException e) {
				e.printStackTrace();
			}
		});

		final ImageView statistics = new ImageView("krow/resources/Statistics.png");
		statistics.setOnMouseClicked(event -> {
			try {
				WindowManager.setScene(Statistics.class);
			} catch (InstantiationException | IllegalAccessException | IOException | NotSwitchableException e) {
				e.printStackTrace();
			}
		});
		statistics.setPickOnBounds(true);

		addImage(settings);
		addImage(krow);
		addImage(chatRoom);
		addImage(statistics);
	}

}