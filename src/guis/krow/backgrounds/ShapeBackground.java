package krow.backgrounds;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;
import java.util.UUID;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.StrokeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import krow.guis.ShapeFactory;
import kröw.core.Kröw;

// TODO Change addition of mouse detection event handlers to use the addEventHandler method of Nodes/Panes, rather than the setOnMouseMoved method.
public class ShapeBackground extends Background {

	private static final Object TRANSLATOR_KEY = new Object();
	private static final Object ROTATOR_KEY = new Object();
	private static final Object COLORER_KEY = new Object();
	private static final Object FADER_KEY = new Object();
	private static final Object SCALER_KEY = new Object();

	private static final Object IS_BEING_SHOVED_KEY = new Object();
	private static final Object BUILT_KEY = new Object();
	private static final Object EFFECT_KEY = new Object();
	private static final Object BACKGROUND_ID_KEY = new Object();

	private static final Random random = new Random();
	private static final double SHAPE_RADIUS = 100;
	private static final Color SHAPE_COLOR = Color.BLACK;

	private UUID backgroundID = UUID.randomUUID();

	public static class ColorAnimation {

		public static ShapeBackground.ColorAnimation generateRandomColorAnimation() {
			return generateRandomColorAnimation(random.nextInt(19) + 1);
		}

		public static ShapeBackground.ColorAnimation generateRandomColorAnimation(final int colorCount) {
			final Color[] colors = new Color[colorCount];
			for (int i = 0; i < colorCount; i++)
				colors[i] = generateRandomColor();
			return new ColorAnimation(random.nextInt(random.nextInt(5) + 2) + 1, random.nextBoolean(),
					random.nextBoolean(), colors);
		}

		public static ShapeBackground.ColorAnimation[] generateRandomColorAnimations() {
			return generateRandomColorAnimations(random.nextInt(10) + 1);
		}

		public static ShapeBackground.ColorAnimation[] generateRandomColorAnimations(final int count) {
			final ShapeBackground.ColorAnimation[] animations = new ShapeBackground.ColorAnimation[count];
			for (int i = 0; i < count; i++)
				animations[i] = generateRandomColorAnimation();
			return animations;
		}

		public static ShapeBackground.ColorAnimation rainbow(final int runCount, final boolean synchronous,
				final boolean even) {
			return new ColorAnimation(runCount, synchronous, even, Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN,
					Color.BLUE, Color.INDIGO, Color.VIOLET);
		}

		private final Color[] colors;
		private int runCount = 1;
		private boolean synchronous, even;

		public ColorAnimation(final boolean synchronous, final boolean even, final Color... colors) {
			this.colors = colors;
			this.synchronous = synchronous;
			this.even = even;
		}

		public ColorAnimation(final boolean synchronous, final Color... colors) {
			this.colors = colors;
			this.synchronous = synchronous;
		}

		public ColorAnimation(final Color... colors) {
			this.colors = colors;
		}

		public ColorAnimation(final int runCount, final boolean synchronous, final boolean even,
				final Color... colors) {
			this.colors = colors;
			this.runCount = runCount;
			this.synchronous = synchronous;
			this.even = even;
		}

		public ColorAnimation(final int runCount, final boolean synchronous, final Color... colors) {
			this.colors = colors;
			this.runCount = runCount;
			this.synchronous = synchronous;
		}

		public ColorAnimation(final int runCount, final Color... colors) {
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
		 * @return the even
		 */
		public boolean isEven() {
			return even;
		}

		/**
		 * @return the synchronous
		 */
		public boolean isSynchronous() {
			return synchronous;
		}

	}

	private static Color generateRandomColor() {
		return new Color(random.nextDouble(), random.nextDouble(), random.nextDouble(), 1);
	}

	private final Stack<ShapeBackground.ColorAnimation> animation = new Stack<>();

	private boolean repeatColorAnimation;

	private int shapesColored = 0, runCount = 0;

	private final ArrayList<Shape> shapes = new ArrayList<>();

	private double customShapeSize = SHAPE_RADIUS;

	private boolean rotatable;

	private boolean disabled;

	public ShapeBackground() {
		super();
	}

	public ShapeBackground(final Color startColor) {
		super(startColor);
	}

	public void addColorAnimations(final ShapeBackground.ColorAnimation... animations) {
		for (final ShapeBackground.ColorAnimation ca : animations)
			animation.add(0, ca);
	}

	public void addRandomShape() {
		addRandomShape(true);
	}

	private void addRandomShape(boolean updatePane) {

		prepareShape(ShapeFactory.buildRegularShape(100, random.nextInt(14) + 3));

		/*
		 * switch (random.nextInt(3)) {
		 *
		 * case 1: buildShape(new Circle(customShapeSize / 2)); break; case 2:
		 * buildShape(new Rectangle(customShapeSize, customShapeSize)); break;
		 *
		 * default: case 0: buildShape(ShapeFactory.buildTriangle()); break; }
		 */

		if (updatePane)
			updatePane();

	}

	public void applyDefaultMouseMovementHandler() {
		setMouseMovementHandler(event -> {
			final double mouseX = event.getX(), mouseY = event.getY();
			for (final Shape s : getShapes())
				if (!s.getProperties().containsKey(IS_BEING_SHOVED_KEY)
						&& Kröw.getProgramSettings().isShapeBackgroundRespondToMouseMovement() && !isDisabled()) {

					final double shapeX = s.getLayoutX() + s.getTranslateX(),
							shapeY = s.getLayoutY() + s.getTranslateY();

					final double distX = mouseX - shapeX, distY = mouseY - shapeY;
					final double distance = Math.sqrt(distX * distX + distY * distY);

					if (distance <= customShapeSize) {

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
						Kröw.getSoundManager().playSound(Kröw.getSoundManager().POP, 1f);
					}
				}
		});
	}

	public void addRandomShapes(int count) {
		for (; count > 0; count--)
			addRandomShape(false);
		updatePane();
	}

	public void addShape(final Shape shape) {
		prepareShape(shape);
	}

	public void addShapes(final Shape... shapes) {
		for (final Shape s : shapes)
			prepareShape(s);
	}

	private void animate(final Shape c) {

		final TranslateTransition translator = (TranslateTransition) c.getProperties().get(TRANSLATOR_KEY);

		final double multiplier = generateRandomMultiplier();
		translator.setByX(calculateByX(c.getLayoutX(), c.getTranslateX()) * multiplier);
		translator.setByY(calculateByY(c.getLayoutY(), c.getTranslateY()) * multiplier);
		translator.setDuration(Duration.seconds(getAnimationDuration() * multiplier));
		translator.play();
	}

	private void animateColors() {
		shapesColored = 0;

		int i = 0;

		// Get first color animation.
		ShapeBackground.ColorAnimation currentAnimation = animation.peek();
		if (currentAnimation.getRunCount() <= runCount)
			currentAnimation = animation.pop();

		// Apply it to each shape.
		for (final Shape s : getShapes()) {
			// Get transition
			final StrokeTransition st = (StrokeTransition) s.getProperties().get(COLORER_KEY);

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
		for (final Shape s : getShapes())
			((StrokeTransition) s.getProperties().get(COLORER_KEY)).play();

		runCount++;

	}

	public void animateShapes() {
		for (final Shape s : shapes)
			((TranslateTransition) s.getProperties().get(TRANSLATOR_KEY)).play();
	}

	public void buildRotator(final RotateTransition rt) {
		rt.stop();
		rt.setToAngle(Double.NaN);
		rt.setDuration(Duration.seconds(getAnimationDuration() * generateRandomMultiplier()));
		rt.setByAngle(random.nextDouble() * 720);
		rt.setOnFinished(event -> {
			rt.setDuration(Duration.seconds(getAnimationDuration() * generateRandomMultiplier()));
			rt.setByAngle((random.nextDouble() - 0.5) * 720);
			rt.play();
		});
	}

	private void prepareShape(final Shape s) {
		if (s.getProperties().containsKey(BUILT_KEY) && s.getProperties().get(BUILT_KEY).equals(true))
			return;

		s.setStroke(SHAPE_COLOR);
		s.setFill(Color.TRANSPARENT);

		final Effect effect = new DropShadow(BlurType.GAUSSIAN, SHAPE_COLOR, 20, 0.5, 0, 0);
		s.setEffect(effect);
		s.getProperties().put(EFFECT_KEY, effect);

		s.setLayoutX(0);
		s.setLayoutY(0);

		s.setTranslateX(random.nextInt((int) Kröw.getSystemProperties().getScreenWidth()) - customShapeSize);
		s.setTranslateY(random.nextInt((int) Kröw.getSystemProperties().getScreenHeight()) - customShapeSize);

		final TranslateTransition translator = new TranslateTransition();
		final RotateTransition rotator = new RotateTransition();
		final StrokeTransition colorer = new StrokeTransition();
		final FadeTransition fader = new FadeTransition();
		final ScaleTransition scaler = new ScaleTransition();

		translator.setNode(s);
		rotator.setNode(s);
		colorer.setShape(s);
		fader.setNode(s);
		scaler.setNode(s);

		s.getProperties().put(TRANSLATOR_KEY, translator);
		s.getProperties().put(ROTATOR_KEY, rotator);
		s.getProperties().put(COLORER_KEY, colorer);
		s.getProperties().put(FADER_KEY, fader);
		s.getProperties().put(SCALER_KEY, scaler);

		translator.setDuration(Duration.seconds(generateRandomMultiplier() * getAnimationDuration()));
		translator.setByX(calculateByX(s.getLayoutX(), s.getTranslateX()));
		translator.setByY(calculateByY(s.getLayoutY(), s.getTranslateY()));
		translator.setInterpolator(Interpolator.EASE_OUT);
		translator.setOnFinished(event -> animate(s));

		s.setCache(true);
		s.setCacheHint(CacheHint.SPEED);

		s.getProperties().put(BACKGROUND_ID_KEY, backgroundID);
		shapes.add(s);

		s.getProperties().put(BUILT_KEY, true);
	}

	private void updatePane() {
		if (currentPane != null) {
			for (Node n : currentPane.getChildren())
				// An == comparison is made here because of that astronomically
				// slim chance that 2 UUIDs can actually be equal. We, honestly,
				// could've used a regular object instead, but UUIDs can be
				// easily converted to and from Strings. This may come into play
				// later...
				//
				// Also, the chance of conflicting UUIDs is obscenely small.
				// Check out Wikipedia.
				if (n.getProperties().get(BACKGROUND_ID_KEY) == backgroundID && !shapes.contains(n)) {
					currentPane.getChildren().remove(n);
				}
			for (Node n : shapes)
				if (!currentPane.getChildren().contains(n))
					currentPane.getChildren().add(n);
		}

	}

	private double calculateByX(final double layoutX, final double translateX) {
		double numb = generateRand();
		if (numb + layoutX + translateX > Kröw.getSystemProperties().getScreenWidth())
			numb -= 100;

		if (numb + layoutX + translateX < 0)
			numb += 100;

		return numb;
	}

	private double calculateByY(final double layoutY, final double translateY) {
		double numb = generateRand();
		if (numb + layoutY + translateY > Kröw.getSystemProperties().getScreenHeight())
			numb -= 100;

		if (numb + layoutY + translateY < 0)
			numb += 100;

		return numb;
	}

	@Override
	public void disable() {
		disabled = true;
		stopAnimations();
	}

	public void disableGlow() {
		for (final Shape s : getShapes())
			disableGlow(s);
	}

	public void disableGlow(final Shape s) {
		if (s.getProperties().containsKey(EFFECT_KEY))
			s.setEffect(null);
	}

	@Override
	public void enable() {
		disabled = false;
		for (final Shape s : getShapes()) {
			final TranslateTransition translator = (TranslateTransition) s.getProperties().get(TRANSLATOR_KEY);
			final RotateTransition rotator = (RotateTransition) s.getProperties().get(ROTATOR_KEY);
			final StrokeTransition colorer = (StrokeTransition) s.getProperties().get(COLORER_KEY);
			translator.play();
			rotator.play();
			colorer.play();
		}
	}

	@Override
	public void dispose() {
		if (hasUnderlyingPane())
			getCurrentPane().getChildren().removeAll(getShapes());
		getShapes().clear();
		super.dispose();
	}

	public void enableGlow() {
		for (final Shape s : getShapes())
			enableGlow(s);
	}

	public void enableGlow(final Shape s) {
		if (s.getProperties().containsKey(EFFECT_KEY))
			s.setEffect((Effect) s.getProperties().get(EFFECT_KEY));
	}

	@Override
	public void fadeIn() {
		fadeIn(Duration.seconds(generateRandomMultiplier() * getAnimationDuration() / 6));
	}

	public void fadeIn(final Duration time) {

		new Object() {
			private volatile int amountFaded = 0;
			{

				for (final Shape s : getShapes()) {
					final FadeTransition fader = (FadeTransition) s.getProperties().get(FADER_KEY);
					fader.stop();
					fader.setOnFinished(event -> {
						amountFaded++;
						fader.setOnFinished(null);
						if (amountFaded == getShapes().size())
							enable();
					});
					fader.setDuration(time);
					fader.setToValue(1);
					fader.play();
				}
			}
		};
	}

	@Override
	public void fadeOut() {
		fadeOut(Duration.seconds(generateRandomMultiplier() * getAnimationDuration() / 6));
	}

	public void fadeOut(final Duration time) {
		disable();
		for (final Shape s : getShapes()) {
			final FadeTransition fader = (FadeTransition) s.getProperties().get(FADER_KEY);
			fader.stop();
			fader.setOnFinished(null);
			fader.setDuration(time);
			fader.setToValue(0);
			fader.play();
		}
	}

	private void fadeShapesToColor(final Shape s, final Color color) {
		fadeShapeToColor(s, color, Duration.seconds(getAnimationDuration() * generateRandomMultiplier()));
	}

	private void fadeShapeToColor(final Shape s, final Color color, final Duration duration) {
		final StrokeTransition st = (StrokeTransition) s.getProperties().get(COLORER_KEY);
		st.stop();
		st.setToValue(color);
		st.setDuration(duration);
		st.play();
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

	/**
	 * @return the disabled
	 */
	public final boolean isDisabled() {
		return disabled;
	}

	public boolean isRotatable() {
		return rotatable;
	}

	public void playColorAnimations() {
		for (final Shape s : getShapes())
			((StrokeTransition) s.getProperties().get(COLORER_KEY)).setOnFinished(event -> {
				setGlowColor(s, (Color) s.getStroke());
				shapesColored++;
				if (shapesColored >= getShapes().size() && !animation.isEmpty())
					animateColors();
			});
		animateColors();
	}

	public void realignRotations() {
		for (final Shape s : shapes) {
			final RotateTransition rt = (RotateTransition) s.getProperties().get(ROTATOR_KEY);
			rt.stop();
			rt.setToAngle(0);
			rt.setDuration(Duration.seconds(generateRandomMultiplier() * getAnimationDuration() / 2));
			rt.setOnFinished(null);
			rt.play();
		}
	}

	public void removeAllShapes() {
		shapes.clear();
		updatePane();
	}

	public void resetColor() {
		setColor(getStartColor());
	}

	public void setColor(final Color newColor) {
		for (final Shape s : getShapes())
			fadeShapesToColor(s, newColor);
	}

	public void setColorAnimations(final boolean repeat, final ShapeBackground.ColorAnimation... animations) {
		stopColorAnimations();
		repeatColorAnimation = repeat;

		animation.clear();
		for (final ShapeBackground.ColorAnimation ca : animations)
			animation.add(0, ca);

	}

	public void setColorsEvenly(final Color... colors) {
		if (colors.length == 0)
			return;
		int i = 0;
		for (final Shape s : getShapes())
			fadeShapesToColor(s, colors[i++ % colors.length]);

	}

	public void setColorsRandomly(final Color... colors) {
		if (colors.length == 0)
			return;
		for (final Shape s : getShapes())
			fadeShapesToColor(s, colors[(int) (Math.random() * colors.length)]);
	}

	@Override
	public void setCurrentPane(final Pane currentPane) {
		if (this.currentPane == currentPane || currentPane == null)
			return;
		if (hasUnderlyingPane())
			for (final Shape s : getShapes())
				getCurrentPane().getChildren().remove(s);
		super.setCurrentPane(currentPane);
		updatePane();

		applyDefaultMouseMovementHandler();
	}

	/**
	 * @param customShapeSize
	 *            the customShapeSize to set
	 */
	public void setCustomShapeSize(final double customShapeSize) {
		this.customShapeSize = customShapeSize;
	}

	/**
	 * @param disabled
	 *            the disabled to set
	 */
	public final void setDisabled(final boolean disabled) {
		this.disabled = disabled;
	}

	public void setEvenShapeColorsSynchronously(final Color... colors) {
		final int i = 0;
		for (final Shape s : getShapes())
			fadeShapeToColor(s, colors[i % colors.length], Duration.seconds(getAnimationDuration()));
	}

	private void setGlowColor(final Shape s, final Color c) {
		if (s.getEffect() != null && s.getEffect() instanceof DropShadow)
			((DropShadow) s.getEffect()).setColor(c);
	}

	public void setRandomColor() {
		setColor(generateRandomColor());
	}

	public void setRandomColors() {
		for (final Shape s : getShapes())
			fadeShapesToColor(s, generateRandomColor());
	}

	public void setRandomShapeColorsSynchronously() {
		for (final Shape s : getShapes())
			fadeShapeToColor(s, generateRandomColor(),
					Duration.seconds(generateRandomMultiplier() * getAnimationDuration()));
	}

	public void setRandomShapeColorsSynchronously(int count) {
		if (count > shapes.size())
			count = shapes.size();
		final Color[] colors = new Color[count];
		for (int i = 0; i < count; i++)
			colors[i] = generateRandomColor();
		setEvenShapeColorsSynchronously(colors);
	}

	public void setRepeatColorAnimations(final boolean repeat) {
		repeatColorAnimation = repeat;
	}

	public void setRotatable(final boolean rotatable) {
		final boolean wasRot = isRotatable();
		this.rotatable = rotatable;

		if (wasRot && !rotatable)
			realignRotations();
		else if (rotatable && !wasRot)
			for (final Shape s : shapes) {
				final RotateTransition rt = (RotateTransition) s.getProperties().get(ROTATOR_KEY);
				rt.stop();
				buildRotator(rt);
				rt.play();
			}
	}

	public void setShapeColor(final Paint color) {
		for (final Shape s : shapes)
			s.setStroke(color);
	}

	public void stopAnimations() {
		for (final Shape s : getShapes()) {
			final TranslateTransition translator = (TranslateTransition) s.getProperties().get(TRANSLATOR_KEY);
			final RotateTransition rotator = (RotateTransition) s.getProperties().get(ROTATOR_KEY);
			final StrokeTransition colorer = (StrokeTransition) s.getProperties().get(COLORER_KEY);
			translator.stop();
			rotator.stop();
			colorer.stop();
		}
	}

	public void stopColorAnimations() {
		for (final Shape s : getShapes())
			((StrokeTransition) s.getProperties().get(COLORER_KEY)).stop();
	}

	public void zoomIn() {
		stopAnimations();
		for (final Shape s : getShapes()) {
			final ScaleTransition scaler = (ScaleTransition) s.getProperties().get(SCALER_KEY);
			final TranslateTransition translator = (TranslateTransition) s.getProperties().get(TRANSLATOR_KEY);
			final FadeTransition fader = (FadeTransition) s.getProperties().get(FADER_KEY);
			scaler.stop();
			translator.stop();
			fader.stop();

			final Duration duration = Duration.seconds(1.5);
			scaler.setDuration(duration);
			translator.setDuration(duration);
			fader.setDuration(Duration.seconds(0.95));

			final Interpolator interpolator = Interpolator.SPLINE(0.9, 0.9, 0.3, 0.3);

			scaler.setInterpolator(interpolator);
			translator.setInterpolator(interpolator);
			fader.setInterpolator(interpolator);

			scaler.setToX(s.getScaleX() * 5);
			scaler.setToY(s.getScaleY() * 5);

			final double distX = s.getLayoutX() + s.getTranslateX() - Kröw.getSystemProperties().getScreenWidth() / 2,
					distY = s.getLayoutY() + s.getTranslateY() - Kröw.getSystemProperties().getScreenHeight() / 2;
			// Subtracting half the screen size sets the coord (0,0) in the
			// center of the screen, rather than the top left,
			// (conceptually, of course...).

			translator.setByX(distX * 5);
			translator.setByY(distY * 5);

			fader.setToValue(0);

			scaler.setOnFinished(null);
			translator.setOnFinished(null);
			fader.setOnFinished(null);

			scaler.play();
			translator.play();
			fader.play();

		}
	}

	public void zoomOut() {
		for (final Shape s : getShapes()) {
			final ScaleTransition scaler = (ScaleTransition) s.getProperties().get(SCALER_KEY);
			final TranslateTransition translator = (TranslateTransition) s.getProperties().get(TRANSLATOR_KEY);
			final FadeTransition fader = (FadeTransition) s.getProperties().get(FADER_KEY);
			scaler.stop();
			translator.stop();
			fader.stop();

			final Duration duration = Duration.seconds(1.5);
			scaler.setDuration(duration);
			translator.setDuration(duration);
			fader.setDuration(Duration.seconds(1.3));

			scaler.setInterpolator(Interpolator.TANGENT(Duration.seconds(4), 0.9, Duration.seconds(9), 0.3));
			translator.setInterpolator(Interpolator.TANGENT(Duration.seconds(4), 0.9, Duration.seconds(9), 0.3));
			fader.setInterpolator(Interpolator.TANGENT(Duration.seconds(4), 0.9, Duration.seconds(9), 0.3));

			scaler.setToX(s.getScaleX() / 5);
			scaler.setToY(s.getScaleY() / 5);

			final double distX = s.getLayoutX() + s.getTranslateX() - Kröw.getSystemProperties().getScreenWidth() / 2,
					distY = s.getLayoutY() + s.getTranslateY() - Kröw.getSystemProperties().getScreenHeight() / 2;

			translator.setByX(distX / 5);
			translator.setByY(distY / 5);

			fader.setToValue(1);

			scaler.setOnFinished(null);
			translator.setOnFinished(event -> animate(s));
			fader.setOnFinished(null);

			scaler.play();
			translator.play();
			fader.play();
		}

	}

	@Override
	public void show(Pane pane) {
		if (pane == null) {
			disable();
			fadeOut(Duration.millis(10));
			return;
		}
		setCurrentPane(pane);
		animateShapes();
	}

	@Override
	public String toString() {
		return super.toString();
	}

}