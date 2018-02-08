package zeale.windowbuilder.api;

import java.util.LinkedList;
import java.util.List;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.WindowEvent;

//TODO This will soon need to be savable.....
public class Window extends AbstractedWindow {

	private AnchorPane pane = new AnchorPane(), root = pane;
	private WindowBuilder owner;

	public WindowBuilder getOwner() {
		return owner;
	}

	{
		stage.setScene(new Scene(root));
		stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, event -> event.consume());
	}

	public Window(WindowBuilder owner) {
		this.owner = owner;
	}

	private List<NodeWrapper<?>> trackedNodes = new LinkedList<>();

	public List<NodeWrapper<?>> getTrackedNodes() {
		return trackedNodes;
	}

	public ReadOnlyBooleanProperty focusedProperty() {
		return stage.focusedProperty();
	}

	public void addNode(NodeWrapper<?> nodeWrapper) {
		trackedNodes.add(nodeWrapper);
	}

	public void addToRoot(NodeWrapper<?> node) {
		root.getChildren().add(node.getNode());
	}

	public void removeNode(NodeWrapper<?> nodeWrapper) {
		trackedNodes.remove(nodeWrapper);
	}

}
