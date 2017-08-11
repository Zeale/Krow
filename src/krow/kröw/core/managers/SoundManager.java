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

	public final Sound TICK = new Sound(Kröw.class.getResource("/krow/resources/sounds/Tick0.wav"));

	public class Sound {
		private URL location;

		public Sound(URL location) {
			this.location = location;
		}

		public void play() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
			playSound(location);
		}
	}

	public void playSound(Sound s) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		s.play();
	}

	public void playSound(URL location) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		AudioInputStream audioIn = AudioSystem.getAudioInputStream(location);
		Clip clip = AudioSystem.getClip();
		clip.open(audioIn);
		clip.start();
	}
}
