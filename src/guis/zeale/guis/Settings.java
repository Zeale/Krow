package zeale.guis;

import java.util.ArrayList;
import java.util.Random;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import krow.guis.GUIHelper;
import kröw.core.Kröw;
import kröw.core.managers.WindowManager;
import kröw.core.managers.WindowManager.Page;

public class Settings extends Page {

	public class Setting {
		private String text;
		private Togglable togglable;

		private final ArrayList<TreeItem<Setting>> children = new ArrayList<>();

		public Setting(final String text) {
			this.text = text;
		}

		public Setting(final String text, final Togglable togglable) {
			this.text = text;
			this.togglable = togglable;
		}

		@SafeVarargs
		public Setting(final String text, final Togglable togglable, final TreeItem<Setting>... children) {
			this.text = text;
			this.togglable = togglable;
			for (final TreeItem<Setting> s : children)
				this.children.add(s);
		}

		public ArrayList<TreeItem<Setting>> getChildren() {
			return children;
		}

		/**
		 * @return the text
		 */
		public final String getText() {
			return text;
		}

		/**
		 * @return the togglable
		 */
		public final Togglable getTogglable() {
			return togglable;
		}

		/**
		 * @param text
		 *            the text to set
		 */
		public final void setText(final String text) {
			this.text = text;
		}

		/**
		 * @param togglable
		 *            the togglable to set
		 */
		public final void setTogglable(final Togglable togglable) {
			this.togglable = togglable;
		}

	}

	public final class SettingTab {
		private String text;
		private final ArrayList<Setting> children = new ArrayList<>();

		public SettingTab(final String text, final Setting... children) {
			this.text = text;
			for (final Setting c : children)
				this.children.add(c);
		}

		/**
		 * @return the children
		 */
		public final ArrayList<Setting> getChildren() {
			return children;
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
		public final void setText(final String text) {
			this.text = text;
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

		addItem(new TreeItem<>(
				new SettingTab("Visual",
						new Setting("Background mouse response: "
								+ (Kröw.getProgramSettings().isShapeBackgroundRespondToMouseMovement() ? "on" : "off"),
								cell -> {
									Kröw.getProgramSettings().setShapeBackgroundRespondToMouseMovement(
											!Kröw.getProgramSettings().isShapeBackgroundRespondToMouseMovement());
									cell.getTreeItem().getValue().setText("Background mouse response: "
											+ (Kröw.getProgramSettings().isShapeBackgroundRespondToMouseMovement()
													? "on" : "off"));

								}),
						new Setting("Current animation mode: "
								+ (Kröw.getProgramSettings().getCurrentAnimationMode() == 0 ? "Normal" : "Lengthy"),
								cell -> {
									Kröw.getProgramSettings().setCurrentAnimationMode(
											Kröw.getProgramSettings().getCurrentAnimationMode() == 0 ? 1 : 0);
									cell.getItem()
											.setText("Current animation mode: "
													+ (Kröw.getProgramSettings().getCurrentAnimationMode() == 0
															? "Normal" : "Lengthy"));
								}))));

		final TreeItem<SettingTab> program = new TreeItem<>(new SettingTab("Program"));

		final TreeItem<Setting> openOnDoubleClick = new TreeItem<>(new Setting(
				"Open with tray icon: "
						+ (Kröw.getProgramSettings().isOpenProgramOnDoubleClickTrayIcon() ? "Yes" : "No"),
				(Togglable) cell -> {
					Kröw.getProgramSettings().setOpenProgramOnDoubleClickTrayIcon(
							!Kröw.getProgramSettings().isOpenProgramOnDoubleClickTrayIcon());
					cell.getTreeItem().getValue().setText("Open with tray icon: "
							+ (Kröw.getProgramSettings().isOpenProgramOnDoubleClickTrayIcon() ? "Yes" : "No"));
				}));

		final Setting useTrayIcon = new Setting(
				"Use tray icon: " + (Kröw.getProgramSettings().isUseTrayIcon() ? "Yes" : "No"));

		useTrayIcon.setTogglable(cell -> {
			Kröw.getProgramSettings().setUseTrayIcon(!Kröw.getProgramSettings().isUseTrayIcon());
			useTrayIcon.setText("Use tray icon: " + (Kröw.getProgramSettings().isUseTrayIcon() ? "Yes" : "No"));

			if (Kröw.getProgramSettings().isUseTrayIcon()) {
				useTrayIcon.getChildren().add(openOnDoubleClick);
				openOnDoubleClick.getValue().setText("Open with tray icon: "
						+ (Kröw.getProgramSettings().isOpenProgramOnDoubleClickTrayIcon() ? "Yes" : "No"));
			} else
				useTrayIcon.getChildren().remove(openOnDoubleClick);

		});

		if (Kröw.getProgramSettings().isUseTrayIcon())
			useTrayIcon.getChildren().add(openOnDoubleClick);

		program.getValue().getChildren().add(useTrayIcon);
		addItem(program);

		final SettingTab appsTab = new SettingTab("Apps");
		final SettingTab chatRoomTab = new SettingTab("Chat Room");
		{
			final Setting hostServerSetting = new Setting("Start server when the Chat Room app opens: "
					+ (Kröw.getProgramSettings().isChatRoomHostServer() ? "Yes" : "No"));
			final Togglable hostServerTogglable = cell -> {
				Kröw.getProgramSettings().setChatRoomHostServer(!Kröw.getProgramSettings().isChatRoomHostServer());
				hostServerSetting.setText("Start server when the Chat Room app opens: "
						+ (Kröw.getProgramSettings().isChatRoomHostServer() ? "Yes" : "No"));
			};

			hostServerSetting.setTogglable(hostServerTogglable);
			chatRoomTab.getChildren().add(hostServerSetting);
		}

		final TreeItem<SettingTab> appsItem = new TreeItem<>(appsTab);
		appsItem.getChildren().add(new TreeItem<>(chatRoomTab));

		addItem(appsItem);

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
						setText(settingTab.getText());
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
					setOnMouseClicked(event -> {
						if (event.getPickResult().getIntersectedNode() instanceof StackPane) {
							event.consume();
							return;
						}

						getItem().getTogglable().onToggled(getThis());
						updateItem(getItem(), isEmpty());
					});

				}

				private TreeCell<Setting> getThis() {
					return this;
				}

				@Override
				protected void updateItem(final Setting item, final boolean empty) {
					super.updateItem(item, empty);

					if (empty)
						setText("");
					else
						setText(item.getText());
					if (!(getTreeItem() == null)) {
						getTreeItem().getChildren().clear();
						for (final TreeItem<Setting> s : getTreeItem().getValue().getChildren())
							getTreeItem().getChildren().add(s);

					}

				}

				@Override
				public void updateSelected(final boolean selected) {
					super.updateSelected(selected);
					updateItem(getItem(), isEmpty());
				}
			};
			return cell;
		});
		addDefaultItems();

		GUIHelper.addDefaultSettings(GUIHelper.buildMenu(pane));
	}

}