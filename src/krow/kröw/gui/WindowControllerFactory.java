package kr�w.gui;

public interface WindowControllerFactory<T extends Application> {
	T createWindow();
}
