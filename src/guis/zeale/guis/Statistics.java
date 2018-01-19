package zeale.guis;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;
import krow.guis.GUIHelper;
import kröw.annotations.AutoLoad;
import kröw.annotations.LoadTime;
import kröw.callables.ParameterizedTask;
import kröw.core.Kröw;
import kröw.core.managers.WindowManager;
import kröw.core.managers.WindowManager.App;

public class Statistics extends WindowManager.App {

	public static class AutoUpdatingStatistic extends Statistic {
		private static Thread globalUpdater = new Thread(new Runnable() {

			@Override
			public void run() {
				closing = false;
				while (statistics.size() > 0 && !closing) {
					for (final AutoUpdatingStatistic s : statistics)
						try {
							if (closing)
								return;
							s.updateTask.execute(s);
						} catch (final Exception e) {
							e.printStackTrace();
						}
					try {
						Thread.sleep(globalTimeout);
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}
				}
				globalUpdater = new Thread(this);
				globalUpdater.setDaemon(true);
			}
		});

		static {
			globalUpdater.setDaemon(true);
		}

		private static Collection<AutoUpdatingStatistic> statistics = new ConcurrentLinkedQueue<>();

		private static long globalTimeout = Kröw.getProgramSettings().getStatsModuleUpdateSpeed();

		/**
		 * @return the timeout
		 */
		public static final long getTimeout() {
			return globalTimeout;
		}

		/**
		 * @param timeout
		 *            the timeout to set
		 */
		public static final void setTimeout(final long timeout) {
			AutoUpdatingStatistic.globalTimeout = timeout;
		}

		public static void startChecker() {
			if (!AutoUpdatingStatistic.globalUpdater.isInterrupted() && !AutoUpdatingStatistic.globalUpdater.isAlive()
					&& statistics.size() > 0)
				AutoUpdatingStatistic.globalUpdater.start();
		}

		private ParameterizedTask<AutoUpdatingStatistic> updateTask;

		private long privateTimeout;

		private boolean update;

		private Thread privateUpdater = new Thread(new Runnable() {

			@Override
			public void run() {
				while (update) {
					if (getUpdateTask() != null)
						getUpdateTask().execute(AutoUpdatingStatistic.this);
					try {
						Thread.sleep(privateTimeout);
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}
					// TODO Add a stop event
					privateUpdater = new Thread(this);
				}
			}
		});

		{
			privateUpdater.setDaemon(true);
		}

		public AutoUpdatingStatistic(final ParameterizedTask<AutoUpdatingStatistic> updater) {
			updateTask = updater;
			statistics.add(this);
			startChecker();
		}

		public AutoUpdatingStatistic(final ParameterizedTask<AutoUpdatingStatistic> updateTask,
				final long privateTimeout) throws IllegalArgumentException {
			this.updateTask = updateTask;
			if (!setPrivateTimeout(privateTimeout))
				throw new IllegalArgumentException("Private timeout cannot be below 1.");
		}

		public AutoUpdatingStatistic(final String stat, final Number val,
				final ParameterizedTask<AutoUpdatingStatistic> updateTask) {
			this(stat, val.toString(), null, updateTask);
		}

		public AutoUpdatingStatistic(final String stat, final Number val,
				final ParameterizedTask<AutoUpdatingStatistic> updateTask, final long privateTimeout)
				throws IllegalArgumentException {
			super(stat, val);
			this.updateTask = updateTask;
			if (!setPrivateTimeout(privateTimeout))
				throw new IllegalArgumentException("Private timeout cannot be below 1.");
		}

		public AutoUpdatingStatistic(final String stat, final String val, final Paint statColor,
				final ParameterizedTask<AutoUpdatingStatistic> updater) {
			super(stat, val, statColor);
			updateTask = updater;
			startChecker();
			statistics.add(this);
		}

		public AutoUpdatingStatistic(final String stat, final String val, final Paint statColor,
				final ParameterizedTask<AutoUpdatingStatistic> updateTask, final long privateTimeout,
				final boolean update, final Thread privateUpdater) throws IllegalArgumentException {
			super(stat, val, statColor);
			this.updateTask = updateTask;
			if (!setPrivateTimeout(privateTimeout))
				throw new IllegalArgumentException("Private timeout cannot be below 1.");
		}

		public AutoUpdatingStatistic(final String stat, final String val,
				final ParameterizedTask<AutoUpdatingStatistic> updater) {
			this(stat, val, null, updater);
		}

		public AutoUpdatingStatistic(final String stat, final String val,
				final ParameterizedTask<AutoUpdatingStatistic> updateTask, final long privateTimeout)
				throws IllegalArgumentException {
			super(stat, val);
			this.updateTask = updateTask;
			if (!setPrivateTimeout(privateTimeout))

				throw new IllegalArgumentException("Private timeout cannot be below 1.");
		}

		public void dispose() {
			statistics.remove(this);
		}

		public ParameterizedTask<AutoUpdatingStatistic> getUpdateTask() {
			return updateTask;
		}

		public boolean setPrivateTimeout(final long privateTimeout) {
			if (privateTimeout < 1)
				return false;
			else
				this.privateTimeout = privateTimeout;
			return true;
		}

		public void setUpdateTask(final ParameterizedTask<AutoUpdatingStatistic> updateTask) {
			this.updateTask = updateTask;
		}

		/**
		 * @return <code>null</code> incase this {@link AutoUpdatingStatistic} was not
		 *         started. Such should only occur when the {@link #privateTimeout} is
		 *         below 1.
		 */
		public AutoUpdatingStatistic start() {
			assert privateTimeout > 1;
			if (!(privateTimeout > 1))
				return null;
			update = true;
			privateUpdater.start();

			return this;
		}

		public void stop() {
			update = false;
		}

	}

	public static class ListObject {
		private ListCell<Object> cell;

		private StringProperty val;

		private Paint statColor = Color.WHITE;

		public ListObject() {
		}

		public ListObject(final Paint statColor) {
			this.statColor = statColor == null ? Color.WHITE : statColor;
		}

		public ListObject(final String val) {
			this(val, null);
		}

		public ListObject(final String val, final Paint statColor) {
			this.val = new SimpleStringProperty(val);
			this.statColor = statColor == null ? Color.WHITE : statColor;
		}

		/**
		 * @return the cell
		 */
		public final ListCell<Object> getCell() {
			return cell;
		}

		public String getDisplayText() {
			return val.get();
		}

		public Paint getStatColor() {
			return statColor;
		}

		/**
		 * @return the val
		 */
		public final String getVal() {
			return val.get();
		}

		public StringProperty getValProperty() {
			return val;
		}

		public boolean isAssignedToCell() {
			return cell != null;
		}

		/**
		 * @param cell
		 *            the cell to set
		 */
		private final void setCell(final ListCell<Object> cell) {
			this.cell = cell;
		}

		public void setStatColor(final Paint statColor) {
			this.statColor = statColor;
		}

		public void setVal(final Number val) {
			this.val.set(val.toString());
		}

		/**
		 * @param val
		 *            the val to set
		 */
		public void setVal(final String val) {
			this.val.set(val);
		}

		public final void unlinkCell() {
			cell = null;
		}

	}

	public static class Statistic extends ListObject {

		private StringProperty stat;

		public Statistic() {
		}

		public Statistic(final String stat, final Number val) {
			this(stat, "" + val);
		}

		public Statistic(final String stat, final String val) {
			this(stat, val, null);
		}

		public Statistic(final String stat, final String val, final Paint statColor) {
			super(val, statColor);
			this.stat = new SimpleStringProperty(stat);
		}

		@Override
		public String getDisplayText() {
			return stat.get() + ": " + super.getDisplayText();
		}

		/**
		 * @return the stat
		 */
		public final String getStat() {
			return stat.get();
		}

		public StringProperty getStatProperty() {
			return stat;
		}

		/**
		 * @param stat
		 *            the stat to set
		 */
		public final void setStat(final String stat) {
			this.stat.set(stat);
		}

	}

	private static ChangeListener<Object> windowMovedListener;

	static {
		Kröw.addReflectionClass(Statistics.class);
	}

	private static boolean closing;

	private static final ObservableList<Object> searchItems = FXCollections.observableArrayList();

	private static final double SEARCH_BAR_WIDTH = 1241, SEARCH_BAR_HEIGHT = 44;

	private static final double SEARCH_BAR_LAYOUT_X = 340, SEARCH_BAR_LAYOUT_Y = 24;

	private static final double SEARCH_BAR_FONT_SIZE = 18;
	private static final double SEARCH_LIST_WIDTH = 1875, SEARCH_LIST_HEIGHT = 900;
	private static final double SEARCH_LIST_LAYOUT_X = 22, SEARCH_LIST_LAYOUT_Y = 118;

	@FXML
	private AnchorPane pane;

	@FXML
	private TextField searchBar;

	@FXML
	private ListView<Object> searchList;

	@FXML
	private RadioButton contains, startsWith, endsWith;

	private final ToggleGroup searchTypesGroup = new ToggleGroup();

	private void addDefaultStats() {
		final NumberFormat formatter = new DecimalFormat();
		final Statistic dpi = new Statistic("Screen DPI",
				formatter.format(Kröw.getSystemProperties().getScreenDotsPerInch()));
		final Statistic screenWidth = new Statistic("Screen Width",
				formatter.format(Kröw.getSystemProperties().getScreenWidth()));
		final Statistic screenHeight = new Statistic("Screen Height",
				formatter.format(Kröw.getSystemProperties().getScreenHeight()));

		final Statistic username = new Statistic("System Username", System.getProperty("user.name", "???"));
		final Statistic countryCode = new Statistic("Country Code", System.getProperty("user.country", "???"));
		final Statistic osName = new Statistic("Operating System", System.getProperty("os.name", "???"));
		final Statistic homeDir = new Statistic("Home Directory", System.getProperty("user.home", "???"));
		final Statistic timezone = new Statistic("Timezone",
				System.getProperty("user.timezone", "???").isEmpty() ? "???"
						: System.getProperty("user.timezone", "???"));
		final Statistic osVer = new Statistic("Operating System Version", System.getProperty("os.version", "???"));

		final Statistic processorsAvailable = new Statistic("Available Processors",
				Runtime.getRuntime().availableProcessors());
		final Statistic totalAvailableMemory = new AutoUpdatingStatistic("Total Available Memory (in bytes)",
				Runtime.getRuntime().totalMemory(),
				(ParameterizedTask<AutoUpdatingStatistic>) param -> param.setVal(Runtime.getRuntime().totalMemory()));
		final Statistic freeAvailableMemory = new AutoUpdatingStatistic("Free Available Memory (in bytes)",
				Runtime.getRuntime().freeMemory(),
				(ParameterizedTask<AutoUpdatingStatistic>) param -> param.setVal(Runtime.getRuntime().freeMemory()));

		final Statistic usedMemory = new AutoUpdatingStatistic("Used Memory (in bytes; by Krow)",
				Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory(),
				(ParameterizedTask<AutoUpdatingStatistic>) param -> param
						.setVal(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));

		final Statistic fileEncoding = new Statistic("File Encoding", System.getProperty("file.encoding", "???"));
		final Statistic executionaryCommand = new Statistic("Used Execute-Command",
				System.getProperty("sun.java.command", "???"));
		final Statistic pathSeparator = new Statistic("System Path Separator",
				System.getProperty("path.separator", "???"));
		final Statistic vendorURL = new Statistic("Java Vendor URL", System.getProperty("java.vendor.url", "???"));
		final Statistic javaRuntimeName = new Statistic("Java Runtime Name",
				System.getProperty("java.runtime.name", "???"));

		if (windowMovedListener == null)
			windowMovedListener = (observable, oldValue, newValue) -> {
				dpi.setVal(Kröw.getSystemProperties().getScreenDotsPerInch() < 0 ? "???"
						: "" + Kröw.getSystemProperties().getScreenDotsPerInch());
				screenHeight.setVal(Kröw.getSystemProperties().getScreenHeight());
				screenWidth.setVal(Kröw.getSystemProperties().getScreenWidth());
			};

		addListObjects(dpi, screenWidth, screenHeight, username, countryCode, osName, osVer, timezone,
				processorsAvailable, totalAvailableMemory, freeAvailableMemory, usedMemory, homeDir, pathSeparator,
				fileEncoding, executionaryCommand, vendorURL, javaRuntimeName);

	}

	public void addListObjects(final ListObject... listObjects) {
		for (final ListObject lo : listObjects)
			searchItems.add(lo);
	}

	@Override
	public boolean canSwitchPage(final Class<? extends App> newSceneClass) {
		return true;
	}

	@Override
	public String getWindowFile() {
		return "Statistics.fxml";
	}

	@Override
	public void initialize() {

		searchBar.setPrefWidth(Kröw.scaleWidth(SEARCH_BAR_WIDTH));
		searchBar.setPrefHeight(Kröw.scaleHeight(SEARCH_BAR_HEIGHT));
		searchBar.setLayoutX(Kröw.scaleWidth(SEARCH_BAR_LAYOUT_X));
		searchBar.setLayoutY(Kröw.scaleHeight(SEARCH_BAR_LAYOUT_Y));

		searchList.setPrefWidth(Kröw.scaleWidth(SEARCH_LIST_WIDTH));
		searchList.setPrefHeight(Kröw.scaleHeight(SEARCH_LIST_HEIGHT));
		searchList.setLayoutX(Kröw.scaleWidth(SEARCH_LIST_LAYOUT_X));
		searchList.setLayoutY(Kröw.scaleHeight(SEARCH_LIST_LAYOUT_Y));

		/***/

		searchList.setItems(searchItems);

		searchList.setCellFactory(new Callback<ListView<Object>, ListCell<Object>>() {

			private static final double BACKGROUND_OPACITY = 0.2;

			@Override
			public ListCell<Object> call(final ListView<Object> param) {

				/***/

				final ListCell<Object> cell = new ListCell<Object>() {

					private final ChangeListener<Object> changeTextListener = (observable, oldValue,
							newValue) -> Platform.runLater(() -> {
								if (getItem() instanceof ListObject)
									setText(((ListObject) getItem()).getDisplayText());
							});

					@Override
					protected void updateItem(final Object item, final boolean empty) {
						// Get previous item.
						final Object previousItem = getItem();
						// Call super method.
						super.updateItem(item, empty);

						if (previousItem instanceof Statistic && previousItem != item) {
							final Statistic prevStatistic = (Statistic) previousItem;
							prevStatistic.unlinkCell();
							prevStatistic.getStatProperty().removeListener(changeTextListener);
							prevStatistic.getValProperty().removeListener(changeTextListener);
						}

						final BackgroundFill[] backgrounds = {
								new BackgroundFill(new Color(1, 0, 0, BACKGROUND_OPACITY), CornerRadii.EMPTY,
										Insets.EMPTY),
								new BackgroundFill(new Color(0, 1, 0, BACKGROUND_OPACITY), CornerRadii.EMPTY,
										Insets.EMPTY),
								new BackgroundFill(new Color(0, 0, 1, BACKGROUND_OPACITY), CornerRadii.EMPTY,
										Insets.EMPTY),
								new BackgroundFill(new Color(1, (double) 43 / 51, 0, BACKGROUND_OPACITY),
										CornerRadii.EMPTY, Insets.EMPTY) };

						String result;

						// Check if we're an empty cell.
						// If we are, don't run the code below.
						if (empty) {
							setText("");
							setBackground(new Background(
									new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
							return;// Don't run below code.
						}

						result = item.toString();

						setBackground(new Background(backgrounds[param.getItems().indexOf(item) % backgrounds.length]));

						if (item instanceof ListObject) {
							final ListObject listObject = (ListObject) item;
							setTextFill(listObject.getStatColor());
							listObject.setCell(this);
							result = listObject.getDisplayText();

							listObject.getValProperty().addListener(changeTextListener);

							/***/

							if (listObject instanceof Statistic)
								((Statistic) listObject).getStatProperty().addListener(changeTextListener);
						}

						setText(result);
					}
				};

				return cell;

				/***/

			}
		});

		searchBar.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.EXTRA_BOLD, SEARCH_BAR_FONT_SIZE));

		searchBar.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> updateList());

		searchItems.addListener((ListChangeListener<Object>) c -> updateList());

		final Reflection reflection = new Reflection(0, (double) 1 / 2, 0.05, 0);
		searchList.setEffect(reflection);

		/***/

		addDefaultStats();

		/***/

		startsWith.setToggleGroup(searchTypesGroup);
		contains.setToggleGroup(searchTypesGroup);
		endsWith.setToggleGroup(searchTypesGroup);

		startsWith.setLayoutX(Kröw.scaleWidth(1593));
		startsWith.setLayoutY(Kröw.scaleHeight(44));
		contains.setLayoutX(startsWith.getLayoutX());
		contains.setLayoutY(startsWith.getLayoutY() + startsWith.getPrefHeight() + Kröw.scaleHeight(25));
		endsWith.setLayoutX(contains.getLayoutX());
		endsWith.setLayoutY(contains.getLayoutY() + contains.getPrefHeight() + Kröw.scaleHeight(25));

		startsWith.setSelected(true);

		final EventHandler<ActionEvent> onSearchTypeChanged = event -> updateList();

		startsWith.setOnAction(onSearchTypeChanged);
		contains.setOnAction(onSearchTypeChanged);
		endsWith.setOnAction(onSearchTypeChanged);

		/***/

		AutoUpdatingStatistic.startChecker();

		applyDefaultBackground(pane);
		GUIHelper.addDefaultSettings(GUIHelper.buildMenu(pane));

	}

	@AutoLoad(value = LoadTime.PROGRAM_EXIT)
	private void onClose() {
		closing = true;
	}

	@Override
	protected void onPageSwitched() {
		closing = true;
	}

	private void updateList() {
		if (searchBar.getText().isEmpty())
			searchList.setItems(searchItems);
		else {
			final ObservableList<Object> newList = FXCollections.observableArrayList();
			for (final Object o : searchItems) {
				String stat;
				if (o instanceof Statistic)
					stat = ((Statistic) o).getStat();
				else if (o instanceof ListObject)
					stat = ((ListObject) o).getVal();
				else
					stat = o.toString();
				if (startsWith.isSelected() && stat.startsWith(searchBar.getText())
						|| contains.isSelected() && stat.contains(searchBar.getText())
						|| endsWith.isSelected() && stat.endsWith(searchBar.getText()))
					newList.add(o);
			}
			searchList.setItems(newList);
		}

	}
}
