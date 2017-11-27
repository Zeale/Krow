package krow.backgrounds;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javafx.animation.FadeTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import krow.backgrounds.animations.Animator;
import kröw.core.Kröw;

public class ImageBackground extends Background {

	private static final Random random = new Random();

	private final ImageViewFactory imageFactory;

	public ImageBackground(ImageViewFactory factory) {
		imageFactory = factory;
	}

	public void setImageCount(int count) {
		if (!hasUnderlyingPane())
			return;
		if (count < nodes.size())
			removeImages(nodes.size() - count);
		else if (count > nodes.size())
			addImages(count - nodes.size());
	}

	public void removeImages(int count) {
		if (!hasUnderlyingPane())
			return;
		if (count >= nodes.size())
			count = nodes.size() - 1;
		while (count > 0) {
			disposeImage(nodes.get(0));
			count--;
		}
	}

	private void disposeImage(Animator<ImageView> imgAnimator) {
		if (!hasUnderlyingPane())
			return;
		imgAnimator.stopAll();
		getCurrentPane().getChildren().remove(imgAnimator.getNode());
	}

	private void addImage(ImageView img) {
		if (!hasUnderlyingPane())
			return;
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
		return generateRand(random.nextBoolean());
	}

	private double generateRand(final boolean positive) {
		return (random.nextInt(50) + 50) * (positive ? 1 : -1);
	}

	private double generateRandomMultiplier() {
		return 1 - random.nextDouble() / 8;
	}

	public void addImages(int count) {
		if (!hasUnderlyingPane())
			return;
		for (; count > 0; count--)
			addImage(imageFactory.make());
	}

	public ImageBackground(final Image image) {
		imageFactory = () -> new ImageView(image);
	}

	private List<Animator<ImageView>> nodes = new LinkedList<>();

	@Override
	public void show(Pane pane) {
		setCurrentPane(pane);
		for (Animator<ImageView> iv : nodes) {
			if (!pane.getChildren().contains(iv.getNode()))
				pane.getChildren().add(iv.getNode());
			iv.getNode().setVisible(true);
		}
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
			for (Animator<ImageView> a : nodes)
				currentPane.getChildren().remove(a.getNode());
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
