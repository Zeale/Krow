package zeale.windowbuilder.api;

import java.util.Stack;

import javafx.scene.Node;
import javafx.scene.effect.Effect;

public class NodeWrapper<N extends Node> {
	private final N node;
	private Window owner;
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) throws IDTakenException {
		for (NodeWrapper<?> nw : owner.getTrackedNodes())
			if (nw.getId().equals(id))
				throw new IDTakenException();
		this.id = id;
	}

	NodeWrapper(N node, Window owner, String id) throws IDTakenException {
		this.node = node;
		this.owner = owner;
		setId(id);
	}

	public Window getOwner() {
		return owner;
	}

	private Stack<Effect> editEffects = new Stack<>();

	void pushEditEffect(Effect effect) {
		editEffects.push(node.getEffect());
		node.setEffect(effect);
	}

	void removeEditEffect() {
		node.setEffect(editEffects.pop());
	}

	public N getNode() {
		return node;
	}

	public void setOwner(Window owner) {
		this.owner = owner;
	}

	
	public void dispose() {
		owner.removeNode(this);
		id=null;
		owner=null;
	}

}
