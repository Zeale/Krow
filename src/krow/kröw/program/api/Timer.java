package kröw.program.api;

public class Timer {
	private long time = System.nanoTime();

	public Timer() {
	}

	public long endTimer() {
		final long l = System.nanoTime();
		return l - time;
	}

	public void restartTimer() {
		startTimer();
	}

	public void startTimer() {
		time = System.nanoTime();
	}

}
