/**
 * 
 */
package kröw.api.interfacing;

import java.io.IOException;

import javafx.scene.image.Image;
import kröw.gui.Application;
import kröw.gui.ApplicationManager;
import kröw.gui.exceptions.NotSwitchableException;

/**
 * @author Zeale
 *
 */
public abstract class Module {
	private Class<? extends Application> home;
	private Class<? extends Application>[] otherApps;
	private final String shortName;
	private Image icon;

	@SafeVarargs
	public Module(String name, Class<? extends Application> home, Class<? extends Application>... otherApps) {
		shortName = name;
		this.home = home;
		this.otherApps = otherApps;
	}

	protected Image getIcon() {
		return icon;
	}

	public void setIcon(Image icon) {
		this.icon = icon;
	}

	public String getShortName() {
		return shortName;
	}

	public final void open()
			throws InstantiationException, IllegalAccessException, IOException, NotSwitchableException {
		ApplicationManager.setScene(home);
	}

}
