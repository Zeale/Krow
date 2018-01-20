package kröw.gui;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import krow.backgrounds.Background;
import krow.backgrounds.ShapeBackground;
import kröw.data.common.kvp.KVPDataObject;
import kröw.data.common.kvp.KeyValuePairData;
import kröw.data.in.KVPDataReader;
import kröw.data.out.KVPDataWriter;

public abstract class Application {

	private KeyValuePairData data = new KeyValuePairData();
	private KVPDataReader reader;// = get reader for this page somehow.
	private KVPDataWriter writer;// This stuff will be replaced with the Protection API stuff. :)
	private static Background defaultBackground = null;

	protected final KVPDataObject storeData(String key, KVPDataObject data) {
		return this.data.put(key, data);
	}

	protected final KVPDataObject getData(String key) {
		return data.get(key);
	}

	protected final void saveData() {
		// TODO Write #data to this Page's file so it can be loaded back up later.
	}

	protected final void loadData() {
		// TODO Populate #data from a file.
	}

	/**
	 * Constructs a {@link Application} object.
	 */
	protected Application() {

	}

	/**
	 * <p>
	 * Sets the default {@link Background}.
	 * <p>
	 * The default {@link Background} is used at the expense of other apps. This
	 * means that it is completely up to another app of whether or not to render a
	 * background from {@link #getDefaultBackground()}.
	 * <p>
	 * For the purpose of allowing the user to keep whatever background they choose
	 * in apps that support it, <b>this method should not be called unless the user
	 * wishes to modify the default background.</b> If a app or page wants to render
	 * a custom background, it should not change any part of the program's default
	 * {@link Background}. The said app should make use of a <i>custom
	 * {@link Background}</i>, so that whatever settings the user has applied can be
	 * kept in this default one.
	 * <p>
	 * One last thing: If the given manager is null, this method will throw an
	 * {@link IllegalArgumentException}. The default {@link Background} is expected
	 * to be used by other apps for rendering the user's chosen background, so if
	 * it's null and other apps try to call methods off of it, there'd be
	 * {@link NullPointerException}s all over the place.
	 * 
	 * @param manager
	 *            The new default {@link Background}. Apps that reference
	 *            {@link #getDefaultBackground()} will be given this new manager
	 *            when they are called.
	 *            <p>
	 *            This cannot be null; such will result in an
	 *            {@link IllegalArgumentException}.
	 */
	protected final void setDefaultBackground(Background manager) {
		if (manager == null)
			throw new IllegalArgumentException("The BackgroundManager mustn't be null.");
		Application.defaultBackground = manager;
	}

	/**
	 * <p>
	 * Returns {@code Kröw}'s default {@link Background}. This is used to store the
	 * user's chosen background settings.
	 * <p>
	 * The default {@link Background} should only be retrieved for
	 * <b><i>showing</i></b> the user's chosen background settings, unless the
	 * {@link Application} that makes use of this method allows the user to modify the
	 * default background.
	 * <p>
	 * If a page edited the default {@link Background}, (rather than using its own
	 * to show a background), every time it was initialized, then any settings that
	 * the user saved to the default background manager, would be changed by the
	 * page every time the page was initialized. This is not good. :(
	 * 
	 * @return {@code Kröw}'s default {@link Background}.
	 */
	protected final Background getDefaultBackground() {
		if (Application.defaultBackground == null)
			Application.defaultBackground = Application.loadDefaultBackground();
		return Application.defaultBackground;
	}

	protected final void applyDefaultBackground(Pane pane) {
		getDefaultBackground().show(pane);
	}

	/**
	 * <p>
	 * Checked when switching {@link Application}s to verify that the current page permits
	 * the user to go to a different page. This should be overridden to return false
	 * when a window reaches a scenario in which it does not want its user to leave.
	 * <p>
	 * Basically, return false when {@link Application}s shouldn't be switched.
	 *
	 * @return Whether or not the scene can currently be switched.
	 */
	public boolean canSwitchPage(final Class<? extends Application> newSceneClass) {
		return true;
	}

	/**
	 * <p>
	 * Returns the relative path to the FXML file that this {@link Application} represents.
	 *
	 * @return A {@link String} object which represents the relative path of the
	 *         FXML file that this {@link Application} represents.
	 */
	public abstract String getWindowFile();

	/**
	 * <p>
	 * This method is called when a {@link Application} is initialized.
	 * <p>
	 * Specifically, this method is called after a {@link Application} has had its
	 * <code>@FXML</code> fields set. This method is the optimal place for
	 * subclasses to use the {@link WindowManager#setPaneDraggableByNode(Node)}
	 * method.
	 * <p>
	 * This would have been reduced to <code>protected</code> visibility, however
	 * JavaFX needs it public to be able to call it. It should be treated as if it
	 * were <code>protected</code>.
	 */
	public void initialize() {

	}

	/**
	 * <p>
	 * This method is called when {@link WindowManager#goBack()} is called and this
	 * {@link Application} is shown. It's is like an extra initialize method which is called
	 * only when the {@link WindowManager#goBack()} method shows this {@link Application}.
	 */
	public void onBack() {

	}

	protected void onPageSwitched() {

	}

	private static final Background loadDefaultBackground() {
		ShapeBackground sb = new ShapeBackground();
		sb.addRandomShapes(50);
		return sb;
	}

}