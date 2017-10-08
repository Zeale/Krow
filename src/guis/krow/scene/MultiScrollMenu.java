package krow.scene;

import java.util.List;

import javafx.scene.Node;

public class MultiScrollMenu extends HorizontalScrollBox {

	public MultiScrollMenu() {
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
