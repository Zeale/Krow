package zeale.windowbuilder.api;

import java.util.Stack;

import javafx.scene.Node;
import javafx.scene.effect.Effect;

public class NodeWrapper<N extends Node> {
	private final N node;
	private Window owner;

	NodeWrapper(N node, Window owner) {
		this.node = node;
		this.owner = owner;
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

}
