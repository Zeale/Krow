package kröw.core.managers;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
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

	public void playSound(final Sound s) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		s.play();
	}

	public void playSound(final URL location)
			throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		final AudioInputStream audioIn = AudioSystem.getAudioInputStream(location);
		final Clip clip = AudioSystem.getClip();
		clip.open(audioIn);
		clip.start();
	}
}
