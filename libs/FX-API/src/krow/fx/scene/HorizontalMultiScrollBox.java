package krow.fx.scene;

import java.util.List;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;

public class HorizontalMultiScrollBox extends HorizontalScrollBox {

	public class Menu {

		private final VerticalScrollBox menuBox = new VerticalScrollBox();
		{
			menuBox.selectCenter();
			menuBox.getChildren().addListener((ListChangeListener<Object>) c -> menuBox.centerNodes());
		}

		public Menu() {
			add();
		}

		public void add() {
			getChildren().add(menuBox);
		}

		public void add(final int position) {
			getChildren().add(position, menuBox);
		}

		public VerticalScrollBox getMenuBox() {
			return menuBox;
		}

		public List<Node> getMenuItemList() {
			return menuBox.getChildren();
		}

		public void remove() {
			menuBox.getChildren().remove(menuBox);
		}

	}

	{
		setFillHeight(false);
	}

	public HorizontalMultiScrollBox() {
	}

}
