package zeale.guis;

import java.util.ArrayList;
import java.util.Random;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import krow.guis.GUIHelper;
import kröw.core.Kröw;
import kröw.core.managers.WindowManager;
import kröw.core.managers.WindowManager.Page;

public class Settings extends Page {

	public final class Setting {
		private String text;
		private Togglable togglable;

		public Setting(final String text) {
			togglable = cell -> {
			};
			this.text = text;
		}

		public Setting(final String text, final Togglable togglable) {
			this.text = text;
			this.togglable = togglable;
		}

		/**
		 * @return the text
		 */
		public final String getText() {
			return text;
		}

		/**
		 * @param text
		 *            the text to set
		 */
		public final void setText(String text) {
			this.text = text;
		}

		/**
		 * @return the togglable
		 */
		public final Togglable getTogglable() {
			return togglable;
		}

		/**
		 * @param togglable
		 *            the togglable to set
		 */
		public final void setTogglable(Togglable togglable) {
			this.togglable = togglable;
		}

	}

	public final class SettingTab {
		private final String text;
		private final ArrayList<Setting> children = new ArrayList<>();

		public SettingTab(final String text, final Setting... children) {
			this.text = text;
			for (final Setting c : children)
				this.children.add(c);
		}

		@Override
		public String toString() {
			return super.toString() + "text=[" + text + "],child-count=[" + children.size() + "]";
		}

	}

	private static interface Togglable {
		void onToggled(TreeCell<Setting> cell);
	}

	@FXML
	private AnchorPane pane;

	@FXML
	private Label settingsLabel;

	@FXML
	private TreeView<SettingTab> tabList;

	@FXML
	private TreeView<Setting> optionBox;

	private final void addDefaultItems() {
		/*
		 * addItem(new TreeItem<>(new SettingTab("General", new
		 * Setting("Open on startup"))));
		 * 
		 * addItem(new TreeItem<>(new SettingTab("Keys", new Setting("Test1"),
		 * new Setting(""), new Setting("Test3")))); addItem(new TreeItem<>(new
		 * SettingTab("Video"))); addItem(new TreeItem<>(new
		 * SettingTab("Sound")));
		 */

		addItem(new TreeItem<Settings.SettingTab>(new SettingTab("Visual",
				new Setting(
						"Background mouse response: "
								+ (GUIHelper.isShapeBackgroundRespondToMouseMovement() ? "on" : "off"),
						new Togglable() {

							@Override
							public void onToggled(TreeCell<Setting> cell) {
								GUIHelper.setShapeBackgroundRespondToMouseMovement(
										!GUIHelper.isShapeBackgroundRespondToMouseMovement());
								cell.getTreeItem().getValue().setText("Background mouse response: "
										+ (GUIHelper.isShapeBackgroundRespondToMouseMovement() ? "on" : "off"));

							}
						}))));

		/*
		 * TreeItem<SettingTab> settingTab = new TreeItem<>(new
		 * SettingTab("Menu1", new Setting("Okay")));
		 * settingTab.getChildren().add(new TreeItem<>(new
		 * SettingTab("SubMenu1", new Setting("Potato"))));
		 * settingTab.getChildren().add(new TreeItem<>(new
		 * SettingTab("SubMenu2"))); settingTab.getChildren().add(new
		 * TreeItem<>(new SettingTab("SubMenu3", new Setting("Potato"))));
		 * settingTab.getChildren().add(new TreeItem<>(new
		 * SettingTab("SubMenu4"))); settingTab.getChildren().add(new
		 * TreeItem<>(new SettingTab("SubMenu5", new Setting("Potato"))));
		 * settingTab.getChildren().add(new TreeItem<>(new
		 * SettingTab("SubMenu6"))); addItem(settingTab);
		 * 
		 * settingTab = new TreeItem<>(new SettingTab("Menu2", new
		 * Setting("AlsoTest"))); settingTab.getChildren().add(new
		 * TreeItem<>(new SettingTab("SubMenu1", new Setting("Potato"))));
		 * settingTab.getChildren().add(new TreeItem<>(new
		 * SettingTab("SubMenu2"))); settingTab.getChildren().add(new
		 * TreeItem<>(new SettingTab("SubMenu3", new Setting("Potato"))));
		 * settingTab.getChildren().add(new TreeItem<>(new
		 * SettingTab("SubMenu4"))); addItem(settingTab);
		 */
	}

	public final void addItem(final TreeItem<SettingTab> child) {
		tabList.getRoot().getChildren().add(child);
	}

	@SafeVarargs
	public final void addItem(final TreeItem<SettingTab> child, final TreeItem<SettingTab>... children) {
		for (final TreeItem<SettingTab> ti : children)
			child.getChildren().add(ti);
		addItem(child);
	}

	@Override
	public String getWindowFile() {
		return "Settings.fxml";
	}

	@Override
	public void initialize() {
		settingsLabel.setLayoutX(WindowManager.getStage().getScene().getWidth() / 2 - settingsLabel.getPrefWidth() / 2);
		optionBox.setLayoutX(WindowManager.getStage().getScene().getWidth() / 2 - optionBox.getPrefWidth() / 2);
		tabList.setRoot(new TreeItem<>());
		optionBox.setRoot(new TreeItem<>());
		tabList.setCellFactory(param -> {
			final TreeCell<SettingTab> cell = new TreeCell<SettingTab>() {
				@Override
				protected void updateItem(final SettingTab settingTab, final boolean empty) {
					super.updateItem(settingTab, empty);

					String background;

					if ((getIndex() & 1) == 1)
						background = "#00000040";
					else
						background = "transparent";

					if (empty) {
						background = "transparent";
						setText("");
					} else
						setText(settingTab.text);
					if (getTreeItem() != null)
						if (getTreeItem().getParent() != getTreeView().getRoot()) {
							final Random rand = new Random();
							final int r = rand.nextInt(256), g = rand.nextInt(256), b = rand.nextInt(256);
							background = "#";
							background += (Integer.toHexString(r).length() == 1 ? "0" : "") + Integer.toHexString(r);
							background += (Integer.toHexString(g).length() == 1 ? "0" : "") + Integer.toHexString(g);
							background += (Integer.toHexString(b).length() == 1 ? "0" : "") + Integer.toHexString(b);
							background += "40";
						}

					setStyle("-fx-background-color: " + background
							+ "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
				}

				@Override
				public void updateSelected(final boolean selected) {
					super.updateSelected(selected);
					if (selected) {
						setStyle(
								"-fx-background-color: #FFFFFFBA; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
						optionBox.getRoot().getChildren().clear();
						settingsLabel.setText(getItem().text);
						for (final Setting t : getItem().children)
							optionBox.getRoot().getChildren().add(new TreeItem<>(t));
						Kröw.getSoundManager().playSound(Kröw.getSoundManager().TICK);
					} else
						updateItem(getItem(), isEmpty());

				}
			};

			return cell;
		});
		optionBox.setCellFactory(param -> {
			final TreeCell<Setting> cell = new TreeCell<Setting>() {
				{
					setOnMouseClicked(new EventHandler<Event>() {

						@Override
						public void handle(Event event) {
							getItem().getTogglable().onToggled(getThis());
							updateItem(getItem(), isEmpty());
						}
					});

				}

				@Override
				protected void updateItem(final Setting item, final boolean empty) {
					super.updateItem(item, empty);

					if (empty)
						setText("");
					else
						setText(item.getText());
				}

				@Override
				public void updateSelected(final boolean selected) {
					super.updateSelected(selected);
					updateItem(getItem(), isEmpty());
				}

				private TreeCell<Setting> getThis() {
					return this;
				}
			};
			return cell;
		});
		addDefaultItems();

		GUIHelper.addDefaultSettings(GUIHelper.buildMenu(pane));
	}

}