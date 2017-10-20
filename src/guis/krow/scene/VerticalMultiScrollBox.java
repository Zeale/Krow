package krow.scene;

import java.util.List;

import javafx.scene.Node;

public class VerticalMultiScrollBox extends VerticalScrollBox {
	public VerticalMultiScrollBox() {
	}

	public class Menu {

		private final HorizontalScrollBox menuBox = new HorizontalScrollBox();

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
