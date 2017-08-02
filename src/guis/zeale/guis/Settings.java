package zeale.guis;

import java.util.ArrayList;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;
import kröw.libs.guis.Window;

public class Settings extends Window {

	@FXML
	private Label settingsLabel;
	@FXML
	private TreeView<Item> tabList;

	@Override
	public String getWindowFile() {
		return "Settings.fxml";
	}

	private final void addDefaultItems() {
		addItem(new TreeItem<Settings.Item>(new Item("General", new Label("Open on startup") {
			{
				setOnMouseClicked(new EventHandler<Event>() {

					@Override
					public void handle(Event event) {
						setStyle("-fx-background-color: lightblue;");
					}
				});
			}
		})));

		addItem(new TreeItem<Settings.Item>(new Item("Keys", new Label("Test1"), new Label(), new Label("Test3"))));
		addItem(new TreeItem<Settings.Item>(new Item("Video")));
		addItem(new TreeItem<Settings.Item>(new Item("Sound")));

		TreeItem<Item> item = new TreeItem<>(new Item("Test", new Label("AlsoTest")));
		item.getChildren().add(new TreeItem<Settings.Item>(new Item("Hi", new Label("Potato"))));
		addItem(item);

	}

	@Override
	public void initialize() {
		settingsLabel.setLayoutX(Window.getStage().getScene().getWidth() / 2 - settingsLabel.getWidth() / 2);
		tabList.setRoot(new TreeItem<>());
		tabList.setShowRoot(false);
		tabList.setCellFactory(new Callback<TreeView<Item>, TreeCell<Item>>() {

			@Override
			public TreeCell<Item> call(TreeView<Item> param) {
				TreeCell<Item> cell = new TreeCell<Item>() {
					@Override
					protected void updateItem(Item item, boolean empty) {
						super.updateItem(item, empty);

						String background;

						if ((getIndex() & 1) == 1)
							background = "#00000040";
						else
							background = "transparent";

						if (empty) {
							background = "transparent";
							setText("");
						} else
							setText(item.text);

						this.setStyle("-fx-background-color: " + background
								+ "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
					}

					@Override
					public void updateSelected(boolean selected) {
						if (selected)
							this.setStyle(
									"-fx-background-color: #FFFFFFBA; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
						else
							updateItem(getItem(), isEmpty());
						super.updateSelected(selected);
					}
				};

				return cell;
			}
		});
		addDefaultItems();
	}

	public final void addItem(TreeItem<Item> child) {
		tabList.getRoot().getChildren().add(child);
	}

	@SafeVarargs
	public final void addItem(TreeItem<Item> child, TreeItem<Item>... children) {
		for (TreeItem<Item> ti : children)
			child.getChildren().add(ti);
		addItem(child);
	}

	public final class Item {
		private final String text;
		private final ArrayList<Node> children = new ArrayList<>();

		public Item(String text, Node... children) {
			this.text = text;
			for (Node c : children)
				this.children.add(c);
		}

		@Override
		public String toString() {
			return super.toString() + "text=[" + text + "],child-count=[" + children.size() + "]";
		}

	}

}