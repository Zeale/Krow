package zeale.guis;

import java.util.ArrayList;
import java.util.Random;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;
import kröw.libs.guis.Window;
import zeale.guis.Settings.Setting;

public class Settings extends Window {

	@FXML
	private Label settingsLabel;
	@FXML
	private TreeView<SettingTab> tabList;
	@FXML
	private TreeView<Setting> optionBox;

	@Override
	public String getWindowFile() {
		return "Settings.fxml";
	}

	private final void addDefaultItems() {
		addItem(new TreeItem<Settings.SettingTab>(new SettingTab("General", new Setting("Open on startup"))));

		addItem(new TreeItem<Settings.SettingTab>(
				new SettingTab("Keys", new Setting("Test1"), new Setting(""), new Setting("Test3"))));
		addItem(new TreeItem<Settings.SettingTab>(new SettingTab("Video")));
		addItem(new TreeItem<Settings.SettingTab>(new SettingTab("Sound")));

		TreeItem<SettingTab> settingTab = new TreeItem<>(new SettingTab("Menu1", new Setting("Okay")));
		settingTab.getChildren()
				.add(new TreeItem<Settings.SettingTab>(new SettingTab("SubMenu1", new Setting("Potato"))));
		settingTab.getChildren().add(new TreeItem<Settings.SettingTab>(new SettingTab("SubMenu2")));
		settingTab.getChildren()
				.add(new TreeItem<Settings.SettingTab>(new SettingTab("SubMenu3", new Setting("Potato"))));
		settingTab.getChildren().add(new TreeItem<Settings.SettingTab>(new SettingTab("SubMenu4")));
		settingTab.getChildren()
				.add(new TreeItem<Settings.SettingTab>(new SettingTab("SubMenu5", new Setting("Potato"))));
		settingTab.getChildren().add(new TreeItem<Settings.SettingTab>(new SettingTab("SubMenu6")));
		addItem(settingTab);

		settingTab = new TreeItem<>(new SettingTab("Menu2", new Setting("AlsoTest")));
		settingTab.getChildren()
				.add(new TreeItem<Settings.SettingTab>(new SettingTab("SubMenu1", new Setting("Potato"))));
		settingTab.getChildren().add(new TreeItem<Settings.SettingTab>(new SettingTab("SubMenu2")));
		settingTab.getChildren()
				.add(new TreeItem<Settings.SettingTab>(new SettingTab("SubMenu3", new Setting("Potato"))));
		settingTab.getChildren().add(new TreeItem<Settings.SettingTab>(new SettingTab("SubMenu4")));
		addItem(settingTab);
	}

	@Override
	public void initialize() {
		settingsLabel.setLayoutX(Window.getStage().getScene().getWidth() / 2 - settingsLabel.getPrefWidth() / 2);
		optionBox.setLayoutX(Window.getStage().getScene().getWidth() / 2 - optionBox.getPrefWidth() / 2);
		tabList.setRoot(new TreeItem<>());
		optionBox.setRoot(new TreeItem<>());
		tabList.setCellFactory(new Callback<TreeView<SettingTab>, TreeCell<SettingTab>>() {

			@Override
			public TreeCell<SettingTab> call(TreeView<SettingTab> param) {
				TreeCell<SettingTab> cell = new TreeCell<SettingTab>() {
					@Override
					protected void updateItem(SettingTab settingTab, boolean empty) {
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
								Random rand = new Random();
								int r = rand.nextInt(256), g = rand.nextInt(256), b = rand.nextInt(256);
								background = "#";
								background += (Integer.toHexString(r).length() == 1 ? "0" : "")
										+ Integer.toHexString(r);
								background += (Integer.toHexString(g).length() == 1 ? "0" : "")
										+ Integer.toHexString(g);
								background += (Integer.toHexString(b).length() == 1 ? "0" : "")
										+ Integer.toHexString(b);
								background += "40";
							}

						this.setStyle("-fx-background-color: " + background
								+ "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
					}

					@Override
					public void updateSelected(boolean selected) {
						super.updateSelected(selected);
						if (selected) {
							this.setStyle(
									"-fx-background-color: #FFFFFFBA; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
							optionBox.getRoot().getChildren().clear();
							settingsLabel.setText(getItem().text);
							for (Setting t : getItem().children) {
								optionBox.getRoot().getChildren().add(new TreeItem<Settings.Setting>(t));
							}
						} else
							updateItem(getItem(), isEmpty());

					}
				};

				return cell;
			}
		});
		optionBox.setCellFactory(new Callback<TreeView<Setting>, TreeCell<Setting>>() {

			@Override
			public TreeCell<Setting> call(TreeView<Setting> param) {
				TreeCell<Setting> cell = new TreeCell<Setting>() {

					@Override
					protected void updateItem(Setting item, boolean empty) {
						super.updateItem(item, empty);

						if (empty)
							setText("");
						else
							setText(item.text);
					}

					@Override
					public void updateSelected(boolean selected) {
						super.updateSelected(selected);
						getItem().togglable.onToggled(this);
					}
				};
				return cell;
			}
		});
		addDefaultItems();

	}

	public final void addItem(TreeItem<SettingTab> child) {
		tabList.getRoot().getChildren().add(child);
	}

	@SafeVarargs
	public final void addItem(TreeItem<SettingTab> child, TreeItem<SettingTab>... children) {
		for (TreeItem<SettingTab> ti : children)
			child.getChildren().add(ti);
		addItem(child);
	}

	public final class SettingTab {
		private final String text;
		private final ArrayList<Setting> children = new ArrayList<>();

		public SettingTab(String text, Setting... children) {
			this.text = text;
			for (Setting c : children)
				this.children.add(c);
		}

		@Override
		public String toString() {
			return super.toString() + "text=[" + text + "],child-count=[" + children.size() + "]";
		}

	}

	public final class Setting {
		private String text;
		private Togglable togglable;

		public Setting(String text) {
			togglable = cell -> {
			};
			this.text = text;
		}

		public Setting(String text, Togglable togglable) {
			this.text = text;
			this.togglable = togglable;
		}

	}

	private static interface Togglable {
		void onToggled(TreeCell<Setting> cell);
	}

}