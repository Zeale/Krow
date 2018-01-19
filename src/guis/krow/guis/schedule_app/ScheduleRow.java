package krow.guis.schedule_app;

import java.io.IOException;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import krow.guis.PopupHelper;
import krow.guis.PopupHelper.PopupWrapper;
import kröw.core.managers.WindowManager;
import kröw.core.managers.WindowManager.NotSwitchableException;
import zeale.guis.schedule_app.NewEvent;
import zeale.guis.schedule_app.ScheduleApp;

public class ScheduleRow extends TableRow<ScheduleEvent> {

	/**
	 * 20 days in milliseconds.
	 */
	private static final double MILLIS_UNTIL_IMPORTANT = 1728e6;
	private static final Color EMPTY_CELL_COLOR = Color.TRANSPARENT, DEFAULT_START_COLOR = new Color(0, 0, 0, 0.3),
			DEFAULT_END_COLOR = Color.GOLD, COMPLETE_COLOR = Color.GREEN, URGENT_END_COLOR = Color.HOTPINK,
			PAST_DUE_COLOR = Color.RED, URGENT_PAST_DUE_COLOR = new Color(0.294117647, 0, 0.50980392156, 1);

	private static final Background buildBackground(final Color color) {
		return new Background(new BackgroundFill(color, null, null));
	}

	private static final Color getColorFromDueDate(final long time) {
		return getColorFromDueDate(time, DEFAULT_START_COLOR, DEFAULT_END_COLOR.interpolate(Color.ORANGE, 0.6));
	}

	private static final Color getColorFromDueDate(final long time, final Color upcomingColor,
			final Color nearDueColor) {
		return getColorFromDueDate(time, upcomingColor, nearDueColor, PAST_DUE_COLOR);
	}

	private static final Color getColorFromDueDate(final long time, final Color upcomingColor, final Color nearDueColor,
			final Color dueColor) {

		// Get the time until the date is due.
		final long timeUntilDue = time - System.currentTimeMillis();

		// If the due date has passed, make the event red.
		if (timeUntilDue < 0)
			return dueColor;

		// Interpolate the color between dueColor and our background color; the
		// color will approach dueColor as the timeUntilDue approaches 0.
		return nearDueColor.interpolate(upcomingColor, timeUntilDue / MILLIS_UNTIL_IMPORTANT);

	}

	private ScheduleApp app;

	{

		// Set the default background (so it isn't white)
		setBackground(buildBackground(EMPTY_CELL_COLOR));

		final Label delete = new Label("Delete");
		final PopupWrapper<VBox> wrapper = PopupHelper.buildPopup(delete);
		PopupHelper.applyClickPopup(getThis(), wrapper.popup, MouseButton.SECONDARY);
		delete.setOnMouseClicked(event -> {

			if (!isEmpty() && getItem() != null && event.getButton() == MouseButton.PRIMARY) {
				getItem().delete();
				app.removeEvent(getItem());
				wrapper.popup.hide();
			}
		});

		setTextFill(Color.WHITE);

		setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY && !isEmpty()
					&& !(event.getPickResult().getIntersectedNode() instanceof SelectableCell))
				try {
					WindowManager.setScene(new NewEvent(app, getItem()));
					event.consume();
				} catch (IOException | NotSwitchableException e) {
					e.printStackTrace();
				}
		});

		setOnMouseEntered(event -> {
			if (!isEmpty()) {
				setBackground(buildBackground(Color.WHITE));
				setTextFill(Color.BLACK);
			}
		});

		setOnMouseExited(event -> {
			resetBackground();
		});
	}

	public ScheduleRow(final ScheduleApp app) {
		this.app = app;
	}

	private TableRow<ScheduleEvent> getThis() {
		return this;
	}

	private void resetBackground() {
		if (isEmpty() || getItem() == null) {
			setBackground(buildBackground(Color.TRANSPARENT));
			return;
		}

		setBackground(
				buildBackground(getItem().complete.get() ? COMPLETE_COLOR
						: getItem().urgent.get() ? getColorFromDueDate(getItem().dueDate.get(), DEFAULT_START_COLOR,
								URGENT_END_COLOR, URGENT_PAST_DUE_COLOR)
								: getColorFromDueDate(getItem().dueDate.get())));
		setTextFill(Color.WHITE);
	}

	@Override
	protected void updateItem(final ScheduleEvent item, final boolean empty) {
		if (item == getItem())
			return;
		super.updateItem(item, empty);

		if (item == null || empty)
			setBackground(buildBackground(EMPTY_CELL_COLOR));
		else
			resetBackground();
	}

}
