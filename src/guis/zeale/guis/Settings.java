package zeale.guis;

import static kröw.core.Kröw.getProgramSettings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import krow.guis.GUIHelper;
import kröw.core.Kröw;
import kröw.gui.Application;
import kröw.gui.ApplicationManager;

public class Settings extends Application {

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
		 * // Setting tabs are nested with the TreeItem's nesting behavior:
		 *
		 * TreeItem<SettingTab> tab = new TreeItem<>(new SettingTab("Tab Name",
		 * settings...));
		 *
		 *
		 * TreeItem<SettingTab> subTab = new TreeItem<>(new SettingTab("Sub Tab Name",
		 * settings...));
		 *
		 * tab.getChildren().add(subTab);
		 *
		 * // Make sure to add the items at the end:
		 *
		 * addItem(tab);
		 *
		 */

		/*
		 * // Settings are nested with their own nesting behavior:
		 *
		 * Setting parent = new Setting("Setting Name"); Setting child = new
		 *
		 * Setting("Sub Setting Name"); parent.getChildren().add(new
		 *
		 * TreeItem<Setting>(child));
		 *
		 * tab.getValue().getChildren().add(parent);
		 *
		 */

		// This needs to be remodeled or something wtf.

		addItem(new TreeItem<>(
				new SettingTab("Visual",
						new Setting("Background mouse response: "
								+ (getProgramSettings().isShapeBackgroundRespondToMouseMovement() ? "on" : "off"),
								cell -> {
									Kröw.getProgramSettings().setShapeBackgroundRespondToMouseMovement(
											!Kröw.getProgramSettings().isShapeBackgroundRespondToMouseMovement());
									cell.getTreeItem().getValue().setText("Background mouse response: "
											+ (Kröw.getProgramSettings().isShapeBackgroundRespondToMouseMovement()
													? "on"
													: "off"));

								}),
						new Setting(
								"Current animation mode: "
										+ (getProgramSettings().getCurrentAnimationMode() == 0 ? "Normal" : "Lengthy"),
								cell -> {
									Kröw.getProgramSettings().setCurrentAnimationMode(
											Kröw.getProgramSettings().getCurrentAnimationMode() == 0 ? 1 : 0);
									cell.getItem().setText("Current animation mode: "
											+ (Kröw.getProgramSettings().getCurrentAnimationMode() == 0 ? "Normal"
													: "Lengthy"));
								}),
						new Setting("Program background: "
								+ (getProgramSettings().getGlobalProgramBackground() == 0 ? "Solid gray (Default)"
										: getProgramSettings().getGlobalProgramBackground() == 1
												? "Moderately transparent"
												: "Completely transparent"),
								new Togglable() {

									@Override
									public void onToggled(TreeCell<Setting> cell) {
										getProgramSettings().setGlobalProgramBackground(
												getProgramSettings().getGlobalProgramBackground() == 2 ? 0
														: getProgramSettings().getGlobalProgramBackground() + 1);
										cell.getItem()
												.setText("Program background: "
														+ (getProgramSettings().getGlobalProgramBackground() == 0
																? "Solid gray (Default)"
																: getProgramSettings().getGlobalProgramBackground() == 1
																		? "Moderately transparent"
																		: "Completely transparent"));
									}
								}))));// This is all in one method call. Cancer.

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

		final SettingTab appTab = new SettingTab("Apps");
		final SettingTab chatRoomTab = new SettingTab("Chat Room"), statisticsTab = new SettingTab("Statistics"),
				toolsTab = new SettingTab("Tools"), mathTab = new SettingTab("Math App"),
				backgroundTab = new SettingTab("Background App");
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

		// I'm wrapping this part in an object so that I can define and use methods, and
		// so that said methods won't clog up the Settings class. Just this method in
		// the Settings class. :)
		//
		// This Settings API seriously needs to be remodeled. Even though it allows for
		// so much.
		new Object() {

			private String parseSpeedInt() {
				int speedInt = Kröw.getProgramSettings().getStatsAppUpdateSpeed();
				switch (speedInt) {

				case 0:
				default:
					return "Extremely Slow (Every 5 Minutes)";
				case 1:
					return "Very Slow (Every Minute)";
				case 2:
					return "Slow (Every 30 Seconds)";
				case 3:
					return "Normal (Every 10 Seconds)";
				case 4:
					return "Abnormal (I'm not really sure)";
				case 5:
					return "Fast (Every second)";
				case 6:
					return "Also Abnromal (Somewhere between every year and every millisecond)";
				case 7:
					return "Autistically Fast (Every 1/25th of a second)";
				case 8:
					return "Very Fast (Every 1/100th of a second; May be laggy/cause issues)";
				case 9:
					return "Extremely Fast (Every millisecond; May be laggy/cause issues)";

				}
			}

			{

				Setting updateSpeedSetting = new Setting("Update Speed: " + parseSpeedInt());
				Togglable updateSpeedTogglable = new Togglable() {

					@Override
					public void onToggled(TreeCell<Setting> cell) {
						if (Kröw.getProgramSettings().getStatsAppUpdateSpeed() == 9)
							Kröw.getProgramSettings().setStatsAppUpdateSpeed(0);
						else
							Kröw.getProgramSettings()
									.setStatsAppUpdateSpeed(Kröw.getProgramSettings().getStatsAppUpdateSpeed() + 1);
						updateSpeedSetting.setText("Update Speed: " + parseSpeedInt());
					}
				};
				updateSpeedSetting.setTogglable(updateSpeedTogglable);
				statisticsTab.getChildren().add(updateSpeedSetting);
			}
		};

		final TreeItem<SettingTab> appsItem = new TreeItem<>(appTab);
		appsItem.getChildren().add(new TreeItem<>(chatRoomTab));
		appsItem.getChildren().add(new TreeItem<>(statisticsTab));
		appsItem.getChildren().add(new TreeItem<>(toolsTab));
		appsItem.getChildren().add(new TreeItem<>(mathTab));
		appsItem.getChildren().add(new TreeItem<>(backgroundTab));

		addItem(appsItem);

		/*
		 * TreeItem<SettingTab> settingTab = new TreeItem<>(new SettingTab("Menu1", new
		 * Setting("Okay"))); settingTab.getChildren().add(new TreeItem<>(new
		 * SettingTab("SubMenu1", new Setting("Potato"))));
		 * settingTab.getChildren().add(new TreeItem<>(new SettingTab("SubMenu2")));
		 * settingTab.getChildren().add(new TreeItem<>(new SettingTab("SubMenu3", new
		 * Setting("Potato")))); settingTab.getChildren().add(new TreeItem<>(new
		 * SettingTab("SubMenu4"))); settingTab.getChildren().add(new TreeItem<>(new
		 * SettingTab("SubMenu5", new Setting("Potato"))));
		 * settingTab.getChildren().add(new TreeItem<>(new SettingTab("SubMenu6")));
		 * addItem(settingTab);
		 *
		 * settingTab = new TreeItem<>(new SettingTab("Menu2", new
		 * Setting("AlsoTest"))); settingTab.getChildren().add(new TreeItem<>(new
		 * SettingTab("SubMenu1", new Setting("Potato"))));
		 * settingTab.getChildren().add(new TreeItem<>(new SettingTab("SubMenu2")));
		 * settingTab.getChildren().add(new TreeItem<>(new SettingTab("SubMenu3", new
		 * Setting("Potato")))); settingTab.getChildren().add(new TreeItem<>(new
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
	public void initialize() {
		settingsLabel
				.setLayoutX(ApplicationManager.getStage().getScene().getWidth() / 2 - settingsLabel.getPrefWidth() / 2);
		optionBox.setLayoutX(ApplicationManager.getStage().getScene().getWidth() / 2 - optionBox.getPrefWidth() / 2);

		tabList.setPrefSize(Kröw.scaleWidth(313), Kröw.scaleHeight(1040));
		optionBox.setPrefSize(Kröw.scaleHeight(810), Kröw.scaleWidth(850));
		optionBox.setLayoutX(ApplicationManager.getStage().getScene().getWidth() / 2 - optionBox.getPrefWidth() / 2);

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
						try {
							Kröw.getSoundManager().playSound(Kröw.getSoundManager().TICK);
						} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
						}
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

						if (!isEmpty())
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