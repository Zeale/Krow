package krow.backgrounds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javafx.animation.Animation.Status;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import krow.backgrounds.animations.Animator;
import kröw.core.Kröw;

public class ImageBackground extends Background {

	private static final Random RANDOM = new Random();

	private final ImageViewFactory imageFactory;
	private final UUID uniqueIdentifier = UUID.randomUUID();

	public void applyDefaultMouseEventHandler() {
		EventHandler<MouseEvent> handler = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				final double mouseX = event.getX(), mouseY = event.getY();
				for (final Animator<ImageView> a : nodes)
					if (a.getTranslator().getStatus() == Status.RUNNING) {

						final double shapeX = a.getNode().getLayoutX() + a.getNode().getTranslateX(),
								shapeY = a.getNode().getLayoutY() + a.getNode().getTranslateY();

						final double distX = mouseX - shapeX, distY = mouseY - shapeY;
						final double distance = Math.sqrt(distX * distX + distY * distY);

						if (distance <= 100) {

							final TranslateTransition translator = a.getTranslator();

							translator.stop();

							translator.setByX(-distX * 3);
							translator.setByY(-distY * 3);
							translator.setDuration(
									Duration.seconds((1 - RANDOM.nextDouble() / 8) * getAnimationDuration() / 3));

							final EventHandler<ActionEvent> onFinished = translator.getOnFinished();
							final Interpolator interpolator = translator.getInterpolator();
							translator.setOnFinished(event1 -> {
								translator.setOnFinished(onFinished);
								translator.setInterpolator(interpolator);
								onFinished.handle(event1);
							});
							translator.setInterpolator(Interpolator.EASE_OUT);
							translator.play();
							Kröw.getSoundManager().playSound(Kröw.getSoundManager().POP, 1f);
						}
					}

			}
		};
		setMouseMovementHandler(handler);
	}

	public void setImageCount(int amount) {
		if (amount < 0)
			throw new IllegalArgumentException(
					"An image count parameter with a negative value was given. You can't have a negative number of images.");
		if (amount > nodes.size())
			for (int i = 0, nodesToAdd = amount - nodes.size(); i < nodesToAdd; i++)
				addImage(imageFactory.make());
		else if (amount < nodes.size())
			for (int i = 0; i < nodes.size() - amount; i++)
				removeImage();
		else if (amount == nodes.size())
			return;
		updatePane();
	}

	public final void removeImages(int count) {
		if (count > getImageCount())
			throw new IllegalArgumentException("Can't remove " + count + " images");
		if (count == 0)
			return;
		if (count < 0)
			addImages(count);
		for (int i = 0; i < count; i++)
			nodes.remove(0);
		updatePane();

	}

	public final int getImageCount() {
		return nodes.size();
	}

	public final void addImages(int count) {
		for (int i = 0; i < count; i++)
			addImage(imageFactory.make());
		updatePane();
	}

	public ImageBackground(ImageViewFactory factory) {
		imageFactory = factory;
	}

	protected final void removeImage() {
		if (!nodes.isEmpty()) {
			Animator<ImageView> img = nodes.get(0);
			if (hasUnderlyingPane())
				getCurrentPane().getChildren().remove(img);
			img.dispose();
			nodes.remove(img);
		}
	}

	protected final void addImage(ImageView img) {
		Animator<ImageView> animator = new Animator<ImageView>(img);
		img.setTranslateX(Math.random() * (getCurrentPane().getWidth() == 0
				? Kröw.getSystemProperties().getScreenWidth() : getCurrentPane().getWidth()));
		img.setTranslateY(Math.random() * (getCurrentPane().getHeight() == 0
				? Kröw.getSystemProperties().getScreenHeight() : getCurrentPane().getHeight()));

		animator.getTranslator().setByX(calculateByX(img.getTranslateX()));
		animator.getTranslator().setByY(calculateByY(img.getTranslateY()));
		animator.getTranslator().setFromX(img.getTranslateX());
		animator.getTranslator().setFromY(img.getTranslateY());
		animator.getTranslator().setDuration(Duration.seconds(getAnimationDuration() * generateRandomMultiplier()));

		animator.getTranslator().setOnFinished(event -> {
			animator.getTranslator().setByX(calculateByX(img.getTranslateX()));
			animator.getTranslator().setByY(calculateByY(img.getTranslateY()));
			animator.getTranslator().setFromX(img.getTranslateX());
			animator.getTranslator().setFromY(img.getTranslateY());
			animator.getTranslator().setDuration(Duration.seconds(getAnimationDuration() * generateRandomMultiplier()));

			animator.getTranslator().play();
		});

		animator.getTranslator().play();

		nodes.add(animator);
	}

	private void updatePane() {
		if (!hasUnderlyingPane())
			return;
		Collection<Node> nodesToRemove = new ArrayList<>();
		for (Node n : getCurrentPane().getChildren())
			if (n.getProperties().get(this) == uniqueIdentifier && !nodes.contains(n))
				nodesToRemove.add(n);
		getCurrentPane().getChildren().removeAll(nodesToRemove);

		for (Animator<ImageView> a : nodes)
			if (!getCurrentPane().getChildren().contains(a.getNode()))
				// #86 - Nodes must be added to front of list so they are
				// rendered first, and, thus, appear behind everything else.
				getCurrentPane().getChildren().add(0, a.getNode());
	}

	@Override
	public void dispose() {
		super.dispose();
		nodes.clear();
	}

	@Override
	public void setCurrentPane(Pane newPane) {
		if (newPane == null) {
			disable();
			return;
		}
		if (newPane == getCurrentPane())
			return;
		super.setCurrentPane(newPane);
		updatePane();
	}

	private double calculateByX(double pos) {
		double numb = generateRand();
		if (numb + pos > Kröw.getSystemProperties().getScreenWidth())
			numb -= 100;

		if (numb + pos < 0)
			numb += 100;

		return numb;
	}

	private double calculateByY(double pos) {
		double numb = generateRand();
		if (numb + pos > Kröw.getSystemProperties().getScreenHeight())
			numb -= 100;

		if (numb + pos < 0)
			numb += 100;

		return numb;
	}

	private double generateRand() {
		return generateRand(RANDOM.nextBoolean());
	}

	private double generateRand(final boolean positive) {
		return (RANDOM.nextInt(50) + 50) * (positive ? 1 : -1);
	}

	private double generateRandomMultiplier() {
		return 1 - RANDOM.nextDouble() / 8;
	}

	public ImageBackground(final Image image) {
		imageFactory = () -> {
			ImageView imageView = new ImageView(image);
			imageView.setFitHeight(100);
			imageView.setFitWidth(100);
			return imageView;
		};
	}

	private List<Animator<ImageView>> nodes = new LinkedList<>();

	@Override
	public void show(Pane pane) {
		setCurrentPane(pane);
		enable();
	}

	private boolean disabled;

	@Override
	public void disable() {
		if (disabled)
			return;
		FadeTransition[] fts = new FadeTransition[nodes.size()];
		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).getTranslator().stop();
			fts[i] = nodes.get(i).getFader();

			fts[i].stop();
			fts[i].setDuration(Duration.seconds(getAnimationDuration() / 3));
			fts[i].setFromValue(nodes.get(i).getNode().getOpacity());
			fts[i].setToValue(0);
		}

		Animator.runWhenFinished(() -> {
			nodes.clear();
			updatePane();
		}, fts);
		disabled = true;
	}

	@Override
	public void enable() {
		if (!disabled)
			return;
		FadeTransition[] fts = new FadeTransition[nodes.size()];
		for (int i = 0; i < nodes.size(); i++) {
			currentPane.getChildren().add(nodes.get(i).getNode());
			fts[i] = nodes.get(i).getFader();

			fts[i].stop();
			fts[i].setDuration(Duration.seconds(getAnimationDuration() / 3));
			fts[i].setFromValue(nodes.get(i).getNode().getOpacity());
			fts[i].setToValue(1);
		}

		Animator.runWhenFinished(() -> {
			for (Animator<ImageView> a : nodes)
				a.getTranslator().play();
		}, fts);

		disabled = false;
	}

	public void animate() {
		for (Animator<ImageView> a : nodes)
			a.getTranslator().play();
	}

	public void disableAnimation() {
		for (Animator<ImageView> a : nodes)
			a.getTranslator().pause();
	}

	@Override
	public void fadeIn() {
		for (Animator<ImageView> a : nodes) {
			a.getFader().stop();
			a.getFader().setFromValue(a.getNode().getOpacity());
			a.getFader().setToValue(1);
		}

		for (Animator<ImageView> a : nodes)
			a.getFader().play();

	}

	@Override
	public void fadeOut() {
		for (Animator<ImageView> a : nodes) {
			a.getFader().stop();
			a.getFader().setFromValue(a.getNode().getOpacity());
			a.getFader().setToValue(0);
		}

		for (Animator<ImageView> a : nodes)
			a.getFader().play();
	}

	@FunctionalInterface
	public static interface ImageViewFactory {
		ImageView make();
	}

}
