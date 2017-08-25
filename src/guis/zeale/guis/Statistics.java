package zeale.guis;

import java.util.ArrayList;
import java.util.Collection;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import krow.guis.GUIHelper;
import kröw.app.api.callables.Task;
import kröw.core.Kröw;
import kröw.core.managers.WindowManager;

public class Statistics extends WindowManager.Page {

	@FXML
	private AnchorPane pane;

	@FXML
	private TextField searchBar;

	@FXML
	private ListView<Object> searchList;

	private static final ObservableList<Object> searchItems = FXCollections.observableArrayList();

	private static final double SEARCH_BAR_WIDTH = 1241, SEARCH_BAR_HEIGHT = 44;
	private static final double SEARCH_BAR_LAYOUT_X = 340, SEARCH_BAR_LAYOUT_Y = 24;

	private static final double SEARCH_LIST_WIDTH = 1875, SEARCH_LIST_HEIGHT = 350;
	private static final double SEARCH_LIST_LAYOUT_X = 22, SEARCH_LIST_LAYOUT_Y = 118;

	@Override
	public void initialize() {
		GUIHelper.addDefaultSettings(GUIHelper.buildMenu(pane));

		searchBar.setPrefWidth(Kröw.scaleWidth(SEARCH_BAR_WIDTH));
		searchBar.setPrefHeight(Kröw.scaleHeight(SEARCH_BAR_HEIGHT));
		searchBar.setLayoutX(Kröw.scaleWidth(SEARCH_BAR_LAYOUT_X));
		searchBar.setLayoutY(Kröw.scaleHeight(SEARCH_BAR_LAYOUT_Y));

		searchList.setPrefWidth(Kröw.scaleWidth(SEARCH_LIST_WIDTH));
		searchList.setPrefHeight(Kröw.scaleHeight(SEARCH_LIST_HEIGHT));
		searchList.setLayoutX(Kröw.scaleWidth(SEARCH_LIST_LAYOUT_X));
		searchList.setLayoutY(Kröw.scaleHeight(SEARCH_LIST_LAYOUT_Y));

		searchList.setItems(searchItems);

		searchList.setCellFactory(new Callback<ListView<Object>, ListCell<Object>>() {

			private static final double BACKGROUND_OPACITY = 0.2;

			int color = 0;

			@Override
			public ListCell<Object> call(ListView<Object> param) {
				ListCell<Object> cell = new ListCell<Object>() {
					@Override
					protected void updateItem(Object item, boolean empty) {
						// Get previous item.
						Object previousItem = getItem();
						// Call super method.
						super.updateItem(item, empty);

						if (previousItem instanceof Statistic)
							((Statistic) previousItem).unlinkCell();

						BackgroundFill[] backgrounds = {
								new BackgroundFill(new Color(1, 0, 0, BACKGROUND_OPACITY), CornerRadii.EMPTY,
										Insets.EMPTY),
								new BackgroundFill(new Color(0, 1, 0, BACKGROUND_OPACITY), CornerRadii.EMPTY,
										Insets.EMPTY),
								new BackgroundFill(new Color(0, 0, 1, BACKGROUND_OPACITY), CornerRadii.EMPTY,
										Insets.EMPTY),
								new BackgroundFill(new Color(1, (double) 43 / 51, 0, BACKGROUND_OPACITY),
										CornerRadii.EMPTY, Insets.EMPTY) };

						setBackground(new Background(backgrounds[color]));

						if (++color > 3)
							color = 0;

						String result;

						// Check if we're an empty cell.
						// If we are, the below code is useless.
						if (empty) {
							setText("");
							return;// Code below is useless.
						} else
							result = item.toString();

						if (item instanceof ListObject) {
							ListObject listObject = (ListObject) item;
							setTextFill(listObject.getStatColor());
							listObject.setCell(this);
							result = listObject.getVal();
						}
						// Check if the item is a statistic.
						if (item instanceof Statistic) {
							Statistic statistic = (Statistic) item;
							result = statistic.getStat() + ": " + statistic.getVal();
						}

						setText(result);
					}
				};

				return cell;
			}
		});

		searchItems.add(new Statistic("Test", "Val"));
		GUIHelper.applyShapeBackground(pane, searchBar, searchList);
	}

	public static class ListObject {
		private ListCell<Object> cell;

		public final void unlinkCell() {
			cell = null;
		}

		/**
		 * @return the cell
		 */
		public final ListCell<Object> getCell() {
			return cell;
		}

		/**
		 * @param cell
		 *            the cell to set
		 */
		private final void setCell(ListCell<Object> cell) {
			this.cell = cell;
		}

		protected void updateCell() {
			if (isAssignedToCell())
				cell.setItem(this);
		}

		public boolean isAssignedToCell() {
			return cell != null;
		}

		private String val;

		public ListObject() {
		}

		public ListObject(Paint statColor) {
			this.statColor = statColor;
		}

		public ListObject(String val) {
			this.val = val;
		}

		public ListObject(String val, Paint statColor) {
			this.val = val;
			this.statColor = statColor;
		}

		/**
		 * @return the val
		 */
		public final String getVal() {
			return val;
		}

		/**
		 * @param val
		 *            the val to set
		 */
		public void setVal(String val) {
			this.val = val;
		}

		private Paint statColor = Color.WHITE;

		public Paint getStatColor() {
			return statColor;
		}

		public void setStatColor(Paint statColor) {
			this.statColor = statColor;
		}
	}

	public static class ListParent extends ListObject {
		private ObservableList<ListObject> children = FXCollections.observableArrayList();
		{
			children.addListener(new ListChangeListener<ListObject>() {

				@Override
				public void onChanged(ListChangeListener.Change<? extends ListObject> c) {
					getCell().setItem(ListParent.this);
				}
			});
		}

		public ListParent(Paint statColor, ListObject... children) {
			this(null, statColor, children);
		}

		public ListParent(String val, Paint statColor, ListObject... children) {
			super(val, statColor);
			for (ListObject lo : children)
				this.children.add(lo);
		}

		public ListParent(String val, ListObject... children) {
			this(val, null, children);
		}

		public ObservableList<ListObject> getChildrenUnmodifiable() {
			return FXCollections.unmodifiableObservableList(children);
		}

		public ObservableList<ListObject> getChildren() {
			return children;
		}

	}

	public static class Statistic extends ListObject {

		private String stat;

		public Statistic() {
		}

		public Statistic(String stat, String val, Paint statColor) {
			super(val, statColor);
			this.stat = stat;
		}

		public Statistic(String stat, String val) {
			super(val);
			this.stat = stat;
		}

		/**
		 * @return the stat
		 */
		public final String getStat() {
			return stat;
		}

		/**
		 * @param stat
		 *            the stat to set
		 */
		public final void setStat(String stat) {
			updateCell();
			this.stat = stat;
		}

	}

	public static class AutoUpdatingStatistic extends Statistic {
		private static Thread updater = new Thread(new Runnable() {

			@Override
			public void run() {
				while (statistics.size() > 0) {
					for (AutoUpdatingStatistic s : statistics)
						try {
							s.updateTask.execute();
						} catch (Exception e) {
							e.printStackTrace();
						}
					try {
						Thread.sleep(timeout);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				Thread.currentThread().interrupt();
				updater = new Thread(this);
			}
		});

		public AutoUpdatingStatistic(String stat, String val, Task updater) {
			super(stat, val);
			updateTask = updater;
		}

		private static Collection<AutoUpdatingStatistic> statistics = new ArrayList<>();
		private static long timeout;

		private Task updateTask;

		public Task getUpdateTask() {
			return updateTask;
		}

		public void setUpdateTask(Task updateTask) {
			this.updateTask = updateTask;
		}

		public AutoUpdatingStatistic(Task updater) {
			updateTask = updater;
			if (!AutoUpdatingStatistic.updater.isInterrupted())
				AutoUpdatingStatistic.updater.start();
		}

		public AutoUpdatingStatistic(String stat, String val, Paint statColor, Task updater) {
			super(stat, val, statColor);
			updateTask = updater;
		}

		/**
		 * @return the timeout
		 */
		public static final long getTimeout() {
			return timeout;
		}

		/**
		 * @param timeout
		 *            the timeout to set
		 */
		public static final void setTimeout(long timeout) {
			AutoUpdatingStatistic.timeout = timeout;
		}

		public void dispose() {
			statistics.remove(this);
		}

	}

	@Override
	public String getWindowFile() {
		return "Statistics.fxml";
	}

}
