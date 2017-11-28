package krow.backgrounds;

import javafx.animation.FillTransition;
import javafx.animation.StrokeTransition;
import javafx.scene.shape.Shape;
import krow.backgrounds.animations.Animator;

public class ShapeAnimator extends Animator<Shape> {

	protected FillTransition filler;
	protected StrokeTransition stroker;

	public ShapeAnimator(Shape node) {
		super(node);
	}

}
