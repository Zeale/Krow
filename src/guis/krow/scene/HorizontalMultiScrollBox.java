package krow.scene;

import java.util.List;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;

public class HorizontalMultiScrollBox extends HorizontalScrollBox {

	{
		setFillHeight(false);
	}

	public HorizontalMultiScrollBox() {
	}

	public class Menu {

		private final VerticalScrollBox menuBox = new VerticalScrollBox();
		{
			menuBox.selectCenter();
			menuBox.getChildren().addListener((ListChangeListener<Object>) c -> menuBox.centerNodes());
		}

		public VerticalScrollBox getMenuBox() {
			return menuBox;
		}

		public Menu() {
			add();
		}

		public List<Node> getMenuItemList() {
			return menuBox.getChildren();
		}

		public void remove() {
			menuBox.getChildren().remove(menuBox);
		}

		public void add() {
			getChildren().add(menuBox);
		}

		public void add(int position) {
			getChildren().add(position, menuBox);
		}

	}

}
