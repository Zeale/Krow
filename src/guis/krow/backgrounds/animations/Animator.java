package krow.backgrounds.animations;

import javafx.animation.Animation.Status;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import kröw.callables.Task;

public class Animator<NT extends Node> {

	private TranslateTransition translator;
	private RotateTransition rotator;
	private FadeTransition fader;
	private ScaleTransition scaler;

	public void stopAll() {
		if (translator != null)
			getTranslator().stop();
		if (rotator != null)
			getRotator().stop();
		if (fader != null)
			getFader().stop();
		if (scaler != null)
			getScaler().stop();
	}

	public final TranslateTransition getTranslator() {
		buildTranslator();
		return translator;
	}

	public final RotateTransition getRotator() {
		buildRotator();
		return rotator;
	}

	public final FadeTransition getFader() {
		buildFader();
		return fader;
	}

	public final ScaleTransition getScaler() {
		buildScaler();
		return scaler;
	}

	private final void buildTranslator() {
		if (translator == null) {
			translator = new TranslateTransition();
			translator.setNode(node);
		}
	}

	private final void buildRotator() {
		if (rotator == null) {
			rotator = new RotateTransition();
			rotator.setNode(node);
		}
	}

	private final void buildFader() {
		if (fader == null) {
			fader = new FadeTransition();
			fader.setNode(node);
		}
	}

	private final void buildScaler() {
		if (scaler == null) {
			scaler = new ScaleTransition();
			scaler.setNode(node);
		}
	}

	private final NT node;

	public Animator(NT node) {
		this.node = node;
	}

	public NT getNode() {
		return node;
	}

	/**
	 * <p>
	 * Runs the specified {@link Task} once all of the given {@link Transition}s
	 * have finished playing.
	 * <p>
	 * If the given {@link Transition}s are not already running, this method
	 * will attempt to play them. If a {@link Transition} has a parent, an
	 * exception will be caught. If the {@link Transition} with a parent is
	 * running, it will be accounted for by this method, otherwise, it will be
	 * ignored.
	 * 
	 * @param task
	 *            The {@link Task} to run when all the {@link Transition}s have
	 *            completed.
	 * @param transitions
	 *            The {@link Transition}s that will be run.
	 */
	public static void runWhenFinished(Task task, Transition... transitions) {

		new Object() {

			private int unfinishedAnimations;

			{

				for (Transition t : transitions) {
					t.setOnFinished(new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent event) {
							unfinishedAnimations--;
							if (unfinishedAnimations == 0)
								task.execute();
						}
					});
					unfinishedAnimations++;
				}

				for (Transition t : transitions)
					// Calling play when an animation is running and it has a
					// parent still throws an exception, even though the
					// documentation says a call under such circumstances will
					// "ha[ve] no effect". Because of this poor code (with
					// equivalently bad documentation), we'll need to find a way
					// to catch this exception.
					try {
						t.play();
					} catch (IllegalStateException e) {
						// This can cause extreme lag if exceptions are
						// repeatedly thrown. This may even need to be run in
						// its own thread...
						if (!(t.getStatus() == Status.RUNNING))
							unfinishedAnimations--;
					}
			}
		};
	}

	public void dispose() {
		stopAll();
		if (translator != null)
			translator.setNode(null);
		if (rotator != null)
			rotator.setNode(null);
		if (fader != null)
			fader.setNode(null);
		if (scaler != null)
			scaler.setNode(null);

		translator = null;
		rotator = null;
		fader = null;
		scaler = null;
	}

}
