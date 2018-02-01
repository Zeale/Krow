package zeale.windowbuilder.api;

import java.util.LinkedList;
import java.util.List;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.scene.Node;

public class Window extends AbstractedWindow {

	private List<Node> trackedNodes = new LinkedList<>();

	public List<Node> getTrackedNodes() {
		return trackedNodes;
	}

	public ReadOnlyBooleanProperty focusedProperty() {
		return stage.focusedProperty();
	}

}
