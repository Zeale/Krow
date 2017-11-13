package krow.backgrounds;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

public final class BackgroundBuilder {

	private static final int SHAPE_COUNT = 50;

	public static ShapeBackground shapeBackground(Pane pane, Node... mouseDetectionNodes) {
		final ShapeBackground manager = new ShapeBackground();
		manager.setCurrentPane(pane);
		manager.addMouseDetectionNodes(mouseDetectionNodes);
		manager.addRandomShapes(SHAPE_COUNT);

		return manager;

	}

	private BackgroundBuilder() {
	}

}
