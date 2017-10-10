package krow.scene;

import java.util.List;

import javafx.scene.Node;

public class MultiScrollBox extends HorizontalScrollBox {

	public MultiScrollBox() {
	}

	public class Menu {

		private final VerticalScrollBox menuBox = new VerticalScrollBox();

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
