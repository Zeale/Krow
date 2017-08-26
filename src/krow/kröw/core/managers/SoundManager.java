package kröw.core.managers;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import kröw.core.Kröw;

public class SoundManager {

	public class Sound {
		private final URL location;

		public Sound(final URL location) {
			this.location = location;
		}

		public void play() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
			playSound(location);
		}
	}

	public final Sound TICK = new Sound(Kröw.class.getResource("/krow/resources/sounds/Tick0.wav"));
	public final Sound POP = new Sound(Kröw.class.getResource("/krow/resources/sounds/Pop0.wav"));

	public void playSound(final Sound s) {
		try {
			s.play();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public void playSound(final Sound s, final float volume) {
		try {
			playSound(s.location, volume);
		} catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		}
	}

	public void playSound(final URL location)
			throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		final AudioInputStream audioIn = AudioSystem.getAudioInputStream(location);
		final Clip clip = AudioSystem.getClip();
		clip.open(audioIn);
		clip.start();
	}

	public void playSound(final URL location, final float volume)
			throws LineUnavailableException, UnsupportedAudioFileException, IOException {
		final AudioInputStream audioIn = AudioSystem.getAudioInputStream(location);
		final Clip clip = AudioSystem.getClip();
		clip.open(audioIn);
		final FloatControl vol = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		vol.setValue((vol.getMaximum() - vol.getMinimum()) * volume + vol.getMinimum());
		clip.start();

	}
}
