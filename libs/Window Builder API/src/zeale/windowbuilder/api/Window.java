package zeale.windowbuilder.api;

import java.util.LinkedList;
import java.util.List;

import javafx.scene.Node;

public class Window extends AbstractedWindow {

	private List<Node> trackedNodes = new LinkedList<>();

	public List<Node> getTrackedNodes() {
		return trackedNodes;
	}

	// TODO Bind to focused property to see if the window is "Selected." This might
	// need to be done in the WindowBuilder class.

}
