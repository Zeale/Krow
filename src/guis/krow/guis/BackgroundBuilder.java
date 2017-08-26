package krow.guis;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.StrokeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
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

	public static class BackgroundManager {

		private double animationDuration = SHAPE_MOVE_DURATION;
		protected Pane currentPane;
		private Color startColor = SHAPE_COLOR;

		private EventHandler<MouseEvent> mouseMovementHandler;

		protected BackgroundManager(Pane pane) {
			currentPane = pane;
			pane.setOnMouseMoved(getMouseMovementHandler());
		}

		protected BackgroundManager(Pane currentPane, Color startColor) {
			this(currentPane);
			this.startColor = startColor;
		}

		/**
		 * @return the currentPane
		 */
		public Pane getCurrentPane() {
			return currentPane;
		}

		/**
		 * @param currentPane
		 *            the currentPane to set
		 */
		public void setCurrentPane(Pane currentPane) {
			if (this.currentPane == currentPane)
				return;
			for (Node n : mouseDetectionNodes)
				removeMouseDetectionNodes(n);
			this.currentPane.setOnMouseMoved(null);
			this.currentPane = currentPane;
			currentPane.setOnMouseMoved(getMouseMovementHandler());
		}

		protected BackgroundManager(Pane currentPane, Color startColor, Node... detectionNodes) {
			this(currentPane);
			this.startColor = startColor;
			for (Node n : detectionNodes)
				n.setOnMouseMoved(getMouseMovementHandler());
		}

		protected BackgroundManager(Pane currentPane, Node... detectionNodes) {
			this(currentPane);
			for (Node n : detectionNodes)
				n.setOnMouseMoved(getMouseMovementHandler());
		}

		private ArrayList<Node> mouseDetectionNodes = new ArrayList<>();

		public void addMouseDetectionNodes(Node... nodes) {
			for (Node n : nodes) {
				n.setOnMouseMoved(mouseMovementHandler);
				mouseDetectionNodes.add(n);
			}
		}

		public void disableMouseInteraction() {
			currentPane.setOnMouseMoved(null);
		}

		public void enableMouseInteraction() {
			currentPane.setOnMouseMoved(getMouseMovementHandler());
		}

		/**
		 * @return the animationDuration
		 */
		public double getAnimationDuration() {
			return animationDuration;
		}

		protected EventHandler<MouseEvent> getMouseMovementHandler() {
			return mouseMovementHandler;
		}

		public Color getStartColor() {
			return startColor;
		}

		public void removeMouseDetectionNodes(Node... nodes) {
			for (Node n : nodes)
				n.setOnMouseMoved(null);
		}

		/**
		 * @param animationDuration
		 *            the animationDuration to set
		 */
		public void setAnimationDuration(double animationDuration) {
			this.animationDuration = animationDuration;
		}

		protected void setMouseMovementHandler(EventHandler<MouseEvent> mouseMovementHandler) {
			this.mouseMovementHandler = mouseMovementHandler;
			currentPane.setOnMouseMoved(mouseMovementHandler);
		}

	}

	public static class ShapeBackgroundManager extends BackgroundManager {

		private Stack<ColorAnimation> animation = new Stack<>();
		private boolean repeatColorAnimation;

		public static class ColorAnimation {

			private Color[] colors;
			private int runCount = 1;
			private boolean synchronous, even;

			/**
			 * @return the even
			 */
			public boolean isEven() {
				return even;
			}

			public ColorAnimation(boolean synchronous, boolean even, Color... colors) {
				this.colors = colors;
				this.synchronous = synchronous;
				this.even = even;
			}

			public ColorAnimation(int runCount, boolean synchronous, boolean even, Color... colors) {
				this.colors = colors;
				this.runCount = runCount;
				this.synchronous = synchronous;
				this.even = even;
			}

			public ColorAnimation(Color... colors) {
				this.colors = colors;
			}

			public ColorAnimation(int runCount, boolean synchronous, Color... colors) {
				this.colors = colors;
				this.runCount = runCount;
				this.synchronous = synchronous;
			}

			public ColorAnimation(boolean synchronous, Color... colors) {
				this.colors = colors;
				this.synchronous = synchronous;
			}

			public ColorAnimation(int runCount, Color... colors) {
				this.colors = colors;
				this.runCount = runCount;
			}

			/**
			 * @return the colors
			 */
			public Color[] getColors() {
				return colors;
			}

			/**
			 * @return the runCount
			 */
			public int getRunCount() {
				return runCount;
			}

			/**
			 * @return the synchronous
			 */
			public boolean isSynchronous() {
				return synchronous;
			}

		}

		public void setColorAnimations(boolean repeat, ColorAnimation... animations) {
			stopColorAnimations();
			repeatColorAnimation = repeat;

			animation.clear();
			for (ColorAnimation ca : animations)
				animation.add(0, ca);

		}

		public void addColorAnimations(ColorAnimation... animations) {
			for (ColorAnimation ca : animations)
				animation.add(0, ca);
		}

		public void playColorAnimations() {
			for (Shape s : getShapes()) {
				((StrokeTransition) s.getProperties().get(COLORER_KEY)).setOnFinished(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						setGlowColor(s, (Color) s.getStroke());
						shapesColored++;
						if (shapesColored >= getShapes().size() && !animation.isEmpty())
							animateColors();
					}

				});
			}
			animateColors();
		}

		private void setGlowColor(Shape s, Color c) {
			if (s.getEffect() != null && s.getEffect() instanceof DropShadow)
				((DropShadow) s.getEffect()).setColor(c);
		}

		private int shapesColored = 0, runCount = 0;

		private void animateColors() {
			shapesColored = 0;

			int i = 0;

			// Get first color animation.
			ColorAnimation currentAnimation = animation.peek();
			if (currentAnimation.getRunCount() <= runCount) {
				currentAnimation = animation.pop();
			}

			// Apply it to each shape.
			for (Shape s : getShapes()) {
				// Get transition
				StrokeTransition st = (StrokeTransition) s.getProperties().get(COLORER_KEY);

				// Handle time.
				st.setDuration(Duration.seconds(currentAnimation.isSynchronous() ? getAnimationDuration()
						: getAnimationDuration() * generateRandomMultiplier()));

				// Handle color distribution.
				if (currentAnimation.isEven()) {
					st.setToValue(currentAnimation.getColors()[i % currentAnimation.getColors().length]);
					i++;
				} else
					st.setToValue(
							currentAnimation.getColors()[(int) (Math.random() * currentAnimation.getColors().length)]);
			}

			// If the animation loops.
			if (currentAnimation.getRunCount() <= runCount) {
				if (repeatColorAnimation)
					animation.add(0, currentAnimation);// Add this to the front.
				runCount = 0;
			}
			for (Shape s : getShapes())
				((StrokeTransition) s.getProperties().get(COLORER_KEY)).play();

			runCount++;

		}

		public void setRepeatColorAnimations(boolean repeat) {
			repeatColorAnimation = repeat;
		}

		public void stopColorAnimations() {
			for (Shape s : getShapes())
				((StrokeTransition) s.getProperties().get(COLORER_KEY)).stop();
		}

		public void setColor(Color newColor) {
			for (Shape s : getShapes()) {
				fadeShapesToColor(s, newColor);
			}
		}

		private void fadeShapesToColor(Shape s, Color color) {
			fadeShapeToColor(s, color, Duration.seconds(getAnimationDuration() * generateRandomMultiplier()));
		}

		private void fadeShapeToColor(Shape s, Color color, EventHandler<ActionEvent> onFinished) {
			StrokeTransition st = (StrokeTransition) s.getProperties().get(COLORER_KEY);
			st.stop();
			st.setToValue(color);
			st.setDuration(Duration.seconds(getAnimationDuration() * generateRandomMultiplier()));
			st.setOnFinished(onFinished);
			st.play();
		}

		private void fadeShapeToColor(Shape s, Color color, Duration duration) {
			StrokeTransition st = (StrokeTransition) s.getProperties().get(COLORER_KEY);
			st.stop();
			st.setToValue(color);
			st.setDuration(duration);
			st.play();
		}

		public void setEvenShapeColorsSynchronously(Color... colors) {
			int i = 0;
			for (Shape s : getShapes())
				fadeShapeToColor(s, colors[i % colors.length], Duration.seconds(getAnimationDuration()));
		}

		public void setRandomShapeColorsSynchronously() {
			for (Shape s : getShapes())
				fadeShapeToColor(s, generateRandomColor(),
						Duration.seconds(generateRandomMultiplier() * getAnimationDuration()));
		}

		public void setRandomShapeColorsSynchronously(int count) {
			if (count > shapes.size())
				count = shapes.size();
			Color[] colors = new Color[count];
			for (int i = 0; i < count; i++)
				colors[i] = generateRandomColor();
			setEvenShapeColorsSynchronously(colors);
		}

		public void setColorsRandomly(Color... colors) {
			if (colors.length == 0)
				return;
			for (Shape s : getShapes())
				fadeShapesToColor(s, colors[(int) (Math.random() * colors.length)]);
		}

		public void setRandomColor() {
			setColor(generateRandomColor());
		}

		public void setRandomColors() {
			for (Shape s : getShapes())
				fadeShapesToColor(s, generateRandomColor());
		}

		public void disableGlow() {
			for (Shape s : getShapes())
				disableGlow(s);
		}

		public void setCustomGlowColor() {
			// TODO Store custom color in shape properties and check for it in
			// #fadeShapeColor(...)
		}

		public void enableGlow() {
			for (Shape s : getShapes())
				enableGlow(s);
		}

		public void enableGlow(Shape s) {
			if (s.getProperties().containsKey(EFFECT_KEY))
				s.setEffect((Effect) s.getProperties().get(EFFECT_KEY));
		}

		public void disableGlow(Shape s) {
			if (s.getProperties().containsKey(EFFECT_KEY))
				s.setEffect(null);
		}

		private Color generateRandomColor() {
			return new Color(random.nextDouble(), random.nextDouble(), random.nextDouble(), 1);
		}

		public void setColorsEvenly(Color... colors) {
			if (colors.length == 0)
				return;
			int i = 0;
			for (Shape s : getShapes())
				fadeShapesToColor(s, colors[i++ % colors.length]);

		}

		public void resetColor() {
			setColor(getStartColor());
		}

		@Override
		public void setCurrentPane(Pane currentPane) {
			if (this.currentPane == currentPane)
				return;
			for (Shape s : getShapes())
				getCurrentPane().getChildren().remove(s);
			super.setCurrentPane(currentPane);
			for (Shape s : getShapes())
				addShapeToPane(s);
		}

		private ArrayList<Shape> shapes = new ArrayList<>();

		private double customShapeSize = SHAPE_RADIUS;

		{
			setMouseMovementHandler(event -> {
				final double mouseX = event.getSceneX(), mouseY = event.getSceneY();
				for (final Shape s : getShapes())
					if (!s.getProperties().containsKey(IS_BEING_SHOVED_KEY)
							&& Kröw.getProgramSettings().isShapeBackgroundRespondToMouseMovement()) {
						final double shapeX = s.getLayoutX() + s.getTranslateX(),
								shapeY = s.getLayoutY() + s.getTranslateY();

						final double distX = mouseX - shapeX, distY = mouseY - shapeY;
						final double distance = Math.sqrt(distX * distX + distY * distY);
						if (distance <= customShapeSize * 2) {

							final TranslateTransition translator = (TranslateTransition) s.getProperties()
									.get(TRANSLATOR_KEY);

							translator.stop();

							translator.setByX(-distX * 3);
							translator.setByY(-distY * 3);
							translator.setDuration(
									Duration.seconds((1 - random.nextDouble() / 8) * getAnimationDuration() / 3));

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
			});
		}

		private boolean rotatable;

		protected ShapeBackgroundManager(Pane pane) {
			super(pane);
		}

		protected ShapeBackgroundManager(Pane currentPane, Color startColor) {
			super(currentPane, startColor);
		}

		public void addRandomShape() {
			buildShape(random.nextBoolean() ? new Circle(customShapeSize)
					: new Rectangle(customShapeSize * 1.8, customShapeSize * 1.8));
		}

		private void buildShape(Shape s) {
			if (s.getProperties().containsKey(BUILT_KEY) && s.getProperties().get(BUILT_KEY).equals(true))
				return;

			s.setStroke(SHAPE_COLOR);
			s.setFill(Color.TRANSPARENT);

			Effect effect = new DropShadow(BlurType.GAUSSIAN, SHAPE_COLOR, 20, 0.5, 0, 0);
			s.setEffect(effect);
			s.getProperties().put(EFFECT_KEY, effect);

			s.setLayoutX(0);
			s.setLayoutY(0);

			s.setTranslateX(random.nextInt((int) Kröw.getSystemProperties().getScreenWidth()) - customShapeSize);
			s.setTranslateY(random.nextInt((int) Kröw.getSystemProperties().getScreenHeight()) - customShapeSize);

			final TranslateTransition translator = new TranslateTransition();
			final RotateTransition rotator = new RotateTransition();
			final StrokeTransition colorer = new StrokeTransition();

			translator.setNode(s);
			rotator.setNode(s);
			colorer.setShape(s);

			s.getProperties().put(TRANSLATOR_KEY, translator);
			s.getProperties().put(ROTATOR_KEY, rotator);
			s.getProperties().put(COLORER_KEY, colorer);

			translator.setDuration(Duration.seconds(generateRandomMultiplier() * getAnimationDuration()));
			translator.setByX(calculateByX(s.getLayoutX(), s.getTranslateX()));
			translator.setByY(calculateByY(s.getLayoutY(), s.getTranslateY()));
			translator.setInterpolator(Interpolator.EASE_OUT);
			translator.setOnFinished(event -> animate(s));

			s.setCache(true);

			shapes.add(s);
			addShapeToPane(s);

			s.getProperties().put(BUILT_KEY, true);
		}

		public void addRandomShapes(int count) {
			for (; count > 0; count--)
				addRandomShape();
		}

		private void addShapeToPane(Shape s) {
			currentPane.getChildren().add(0, s);
		}

		private void animate(Shape c) {

			TranslateTransition translator = (TranslateTransition) c.getProperties().get(TRANSLATOR_KEY);

			final double multiplier = generateRandomMultiplier();
			translator.setByX(calculateByX(c.getLayoutX(), c.getTranslateX()) * multiplier);
			translator.setByY(calculateByY(c.getLayoutY(), c.getTranslateY()) * multiplier);
			translator.setDuration(Duration.seconds(getAnimationDuration() * multiplier));
			translator.play();
		}

		public void animateShapes() {
			for (Shape s : shapes)
				((TranslateTransition) s.getProperties().get(TRANSLATOR_KEY)).play();
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

		private double generateRandomMultiplier() {
			return 1 - random.nextDouble() / 8;
		}

		/**
		 * @return the customShapeSize
		 */
		public double getCustomShapeSize() {
			return customShapeSize;
		}

		private ArrayList<Shape> getShapes() {
			return shapes;
		}

		public boolean isRotatable() {
			return rotatable;
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

		public void removeAllShapes() {
			shapes.clear();
		}

		/**
		 * @param customShapeSize
		 *            the customShapeSize to set
		 */
		public void setCustomShapeSize(double customShapeSize) {
			this.customShapeSize = customShapeSize;
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

		public void setShapeColor(Paint color) {
			for (Shape s : shapes)
				s.setStroke(color);
		}
	}

	private static final Random random = new Random();
	private static final int SHAPE_COUNT = 50;
	private static final double SHAPE_RADIUS = 50;
	private static final Color SHAPE_COLOR = Color.BLACK;
	private static final double SHAPE_MOVE_DURATION = 8;

	private static final Object TRANSLATOR_KEY = new Object(), ROTATOR_KEY = new Object(), COLORER_KEY = new Object();

	private static final Object IS_BEING_SHOVED_KEY = new Object(), BUILT_KEY = new Object(), EFFECT_KEY = new Object();

	public static ShapeBackgroundManager shapeBackground(final Pane pane, final Node... mouseDetectionNodes) {
		ShapeBackgroundManager manager = new ShapeBackgroundManager(pane);
		manager.addRandomShapes(SHAPE_COUNT);

		for (final Node n : mouseDetectionNodes)
			n.setOnMouseMoved(manager.getMouseMovementHandler());

		return manager;

	}

	private BackgroundBuilder() {
	}

}
