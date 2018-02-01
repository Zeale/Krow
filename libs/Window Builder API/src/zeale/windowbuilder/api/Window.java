package zeale.windowbuilder.api;

import java.util.LinkedList;
import java.util.List;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

public class Window extends AbstractedWindow {

	private AnchorPane pane = new AnchorPane(), root = pane;
	{
		stage.setScene(new Scene(root));
	}

	private List<Node> trackedNodes = new LinkedList<>();

	public List<Node> getTrackedNodes() {
		return trackedNodes;
	}

	public ReadOnlyBooleanProperty focusedProperty() {
		return stage.focusedProperty();
	}

	public void addNode(Node node) {
		trackedNodes.add(node);
		pane.getChildren().add(node);
	}

}
