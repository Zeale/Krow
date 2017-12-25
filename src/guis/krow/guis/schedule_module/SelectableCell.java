package krow.guis.schedule_module;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;

public class SelectableCell<T> extends TableCell<T, T> {

	private final Callback<T, BooleanProperty> propertyRetriever;
	public final CheckBox checkbox = new CheckBox();
	private BooleanProperty boundProperty;
	/**
	 * The {@link Callback} we use to retrieve a cell's observable boolean
	 * property. The cell will bind its selection state to the property.
	 */

	// Make the entire cell's hitbox respond to mouse clicks. This listener does
	// not respond to the actual check box, just the surrounding cell margin.
	{
		setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY)
				checkbox.setSelected(!checkbox.isSelected());
			else if (event.getButton() == MouseButton.MIDDLE)
				checkbox.setSelected(false);
		});
	}

	public SelectableCell(Callback<T, BooleanProperty> propertyRetriever) {
		this.propertyRetriever = propertyRetriever;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	protected void updateItem(final T item, final boolean empty) {
		// If the item was not changed, there's no need to do anything...
		if (item == getItem())
			return;

		if (getItem() != null)
			checkbox.selectedProperty().unbindBidirectional(boundProperty);

		// Update the item in this cell.
		super.updateItem(item, empty);

		// If the new item is null or this cell is empty, remove the graphic; we
		// don't want empty cells to be checkable.
		if (item == null || empty) {
			boundProperty = null;
			setGraphic(null);
			return;
		}

		// Since the cell is not empty, (otherwise the above if would've made
		// this method return), we make sure that this cell's graphic is showing
		// and we bind the checkbox's "selected" property to the value returned
		// by the propertyRetriever.
		boundProperty = propertyRetriever.call(item);
		checkbox.selectedProperty().bindBidirectional(boundProperty);
		setGraphic(checkbox);

	}

}