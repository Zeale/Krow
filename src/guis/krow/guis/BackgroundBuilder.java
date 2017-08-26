package krow.guis;

import java.util.ArrayList;
import java.util.Random;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.StrokeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import kröw.core.Kröw;

public final class BackgroundBuilder {

	private static final Random random = new Random();

	private static final int SHAPE_COUNT = 50;
	private static final double SHAPE_RADIUS = 50;
	private static final Color SHAPE_COLOR = Color.BLACK;
	private static final double SHAPE_MOVE_DURATION = 8;
	private static final Object TRANSLATOR_KEY = new Object(), ROTATOR_KEY = new Object(), COLORER_KEY = new Object();
	private static final Object IS_BEING_SHOVED_KEY = new Object();

	private BackgroundBuilder() {
	}

	public static ShapeBackgroundManager shapeBackground(final Pane pane, final Node... mouseDetectionNodes) {
		ShapeBackgroundManager manager = new ShapeBackgroundManager();
		final Random random = new Random();
		manager.addRandomShapes(SHAPE_COUNT);

		final EventHandler<MouseEvent> handler =

				event -> {
					final double mouseX = event.getSceneX(), mouseY = event.getSceneY();
					for (final Shape s : manager.getShapes())
						if (!s.getProperties().containsKey(IS_BEING_SHOVED_KEY)
								&& Kröw.getProgramSettings().isShapeBackgroundRespondToMouseMovement()) {
							final double shapeX = s.getLayoutX() + s.getTranslateX(),
									shapeY = s.getLayoutY() + s.getTranslateY();

							final double distX = mouseX - shapeX, distY = mouseY - shapeY;
							final double distance = Math.sqrt(distX * distX + distY * distY);
							if (distance <= SHAPE_RADIUS * 2) {

								final TranslateTransition translator = (TranslateTransition) s.getProperties()
										.get(TRANSLATOR_KEY);

								translator.stop();

								translator.setByX(-distX * 3);
								translator.setByY(-distY * 3);
								translator.setDuration(
										Duration.seconds((1 - random.nextDouble() / 8) * SHAPE_MOVE_DURATION / 3));

								final EventHandler<ActionEvent> onFinished = translator.getOnFinished();
								s.getProperties().put(IS_BEING_SHOVED_KEY, true);
								final Interpolator interpolator = translator.getInterpolator();
								translator.setOnFinished(event1 -> {
									s.getProperties().remove(IS_BEING_SHOVED_KEY);
									translator.setOnFinished(onFinished);
									translator.setInterpolator(interpolator);
									onFinished.handle(event1);
								});
								translator.setInterpolator(Interpolator.EASE_OUT);
								translator.play();
								Kröw.getSoundManager().playSound(Kröw.getSoundManager().POP, 0.7f);
							}
						}
				};

		pane.setOnMouseMoved(handler);
		for (final Node n : mouseDetectionNodes)
			n.setOnMouseMoved(handler);

		for (final Shape s : manager.getShapes())
			pane.getChildren().add(0, s);

		return manager;

	}

	public static class BackgroundManager {

		private double animationDuration;

		/**
		 * @return the animationDuration
		 */
		public double getAnimationDuration() {
			return animationDuration;
		}

		/**
		 * @param animationDuration
		 *            the animationDuration to set
		 */
		public void setAnimationDuration(double animationDuration) {
			this.animationDuration = animationDuration;
		}

		private EventHandler<MouseEvent> mouseMovementHandler;

		protected void setMouseMovementHandler(EventHandler<MouseEvent> mouseMovementHandler) {
			this.mouseMovementHandler = mouseMovementHandler;
		}

		protected EventHandler<MouseEvent> getMouseMovementHandler() {
			return mouseMovementHandler;
		}

		public void addMouseDetectionNodes(Node... nodes) {
			for (Node n : nodes)
				n.setOnMouseMoved(mouseMovementHandler);
		}

		public void removeMouseDetectionNodes(Node... nodes) {
			for (Node n : nodes)
				n.setOnMouseMoved(null);
		}

		private BackgroundManager() {

		}
	}

	public static class ShapeBackgroundManager extends BackgroundManager {

		private ArrayList<Shape> shapes = new ArrayList<>();

		public void setShapeColor(Paint color) {
			for (Shape s : shapes)
				s.setStroke(color);
		}

		public void removeAllShapes() {
			shapes.clear();
		}

		public void addRandomShapes(int count) {
			for (; count > 0; count--)
				addRandomShape();
		}

		public void animateShapes() {
			for (Shape s : shapes)
				((TranslateTransition) s.getProperties().get(TRANSLATOR_KEY)).play();
		}

		private boolean rotatable;

		public boolean isRotatable() {
			return rotatable;
		}

		public void setRotatable(boolean rotatable) {
			boolean wasRot = isRotatable();
			this.rotatable = rotatable;

			if (wasRot && !rotatable) {
				realignRotations();
			} else if (rotatable && !wasRot) {

				for (Shape s : shapes) {
					RotateTransition rt = (RotateTransition) s.getProperties().get(ROTATOR_KEY);
					rt.stop();
					buildRotator(rt);
					rt.play();
				}
			}
		}

		public void buildRotator(RotateTransition rt) {
			rt.stop();
			rt.setToAngle(Double.NaN);
			rt.setDuration(Duration.seconds(getAnimationDuration() * generateRandomMultiplier()));
			rt.setByAngle(random.nextDouble() * 720);
			rt.setOnFinished(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					rt.setDuration(Duration.seconds(getAnimationDuration() * generateRandomMultiplier()));
					rt.setByAngle((random.nextDouble() - 0.5) * 720);
					rt.play();
				}
			});
		}

		public void realignRotations() {
			for (Shape s : shapes) {
				RotateTransition rt = (RotateTransition) s.getProperties().get(ROTATOR_KEY);
				rt.stop();
				rt.setToAngle(0);
				rt.setDuration(Duration.seconds(generateRandomMultiplier() * getAnimationDuration() / 2));
				rt.setOnFinished(null);
				rt.play();
			}
		}

		private double generateRandomMultiplier() {
			return 1 - random.nextDouble() / 8;
		}

		private void animate(Shape c) {

			TranslateTransition translator = (TranslateTransition) c.getProperties().get(TRANSLATOR_KEY);

			final double multiplier = generateRandomMultiplier();
			translator.setByX(calculateByX(c.getLayoutX(), c.getTranslateX()) * multiplier);
			translator.setByY(calculateByY(c.getLayoutY(), c.getTranslateY()) * multiplier);
			translator.setDuration(Duration.seconds(getAnimationDuration() * multiplier));
			translator.play();
		}

		private double calculateByX(double layoutX, double translateX) {
			double numb = generateRand();
			if (numb + layoutX + translateX > Kröw.getSystemProperties().getScreenWidth())
				numb -= 100;

			if (numb + layoutX + translateX < 0)
				numb += 100;

			return numb;
		}

		private double calculateByY(double layoutY, double translateY) {
			double numb = generateRand();
			if (numb + layoutY + translateY > Kröw.getSystemProperties().getScreenHeight())
				numb -= 100;

			if (numb + layoutY + translateY < 0)
				numb += 100;

			return numb;
		}

		private double generateRand() {
			return generateRand(random.nextBoolean());
		}

		private double generateRand(final boolean positive) {
			return (random.nextInt(50) + 50) * (positive ? 1 : -1);
		}

		public void addRandomShape() {
			final Shape c = random.nextBoolean() ? new Circle(SHAPE_RADIUS)
					: new Rectangle(SHAPE_RADIUS * 1.8, SHAPE_RADIUS * 1.8);
			c.setStroke(SHAPE_COLOR);
			c.setFill(Color.TRANSPARENT);
			c.setEffect(new DropShadow(BlurType.GAUSSIAN, SHAPE_COLOR, 20, 0.5, 0, 0));
			c.setLayoutX(0);
			c.setLayoutY(0);
			c.setTranslateX(random.nextInt((int) Kröw.getSystemProperties().getScreenWidth()) - SHAPE_RADIUS);
			c.setTranslateY(random.nextInt((int) Kröw.getSystemProperties().getScreenHeight()) - SHAPE_RADIUS);
			final TranslateTransition translator = new TranslateTransition();
			final RotateTransition rotator = new RotateTransition();
			final StrokeTransition colorer = new StrokeTransition();
			translator.setNode(c);
			rotator.setNode(c);
			colorer.setShape(c);
			c.getProperties().put(TRANSLATOR_KEY, translator);
			c.getProperties().put(ROTATOR_KEY, rotator);
			c.getProperties().put(COLORER_KEY, colorer);

			translator.setDuration(Duration.seconds(generateRandomMultiplier() * getAnimationDuration()));
			translator.setByX(calculateByX(c.getLayoutX(), c.getTranslateX()));
			translator.setByY(calculateByY(c.getLayoutY(), c.getTranslateY()));
			translator.setInterpolator(Interpolator.EASE_OUT);
			translator.setOnFinished(event -> animate(c));

			c.setCache(true);

			shapes.add(c);
		}

		private ArrayList<Shape> getShapes() {
			return shapes;
		}

		private ShapeBackgroundManager() {

		}
	}

}
