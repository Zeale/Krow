package kröw.gui;

public interface WindowControllerFactory<T extends Application> {
	T createWindow();
}
