package krow.guis.schedule_module;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;

public class SelectableCell<T> extends TableCell<T, Boolean> {

	public final CheckBox checkbox = new CheckBox();
	private BooleanProperty boundProperty;
	/**
	 * The {@link Callback} we use to retrieve a cell's observable boolean
	 * property. The cell will bind its selection state to the property.
	 */
	private final Callback<T, BooleanProperty> propertyRetriever;

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

	public SelectableCell(final Callback<T, BooleanProperty> propertyRetriever) {
		this.propertyRetriever = propertyRetriever;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void updateItem(final Boolean item, final boolean empty) {
		// If the item was not changed, there's no need to do anything...
		if (item == getItem())
			return;

		// If the old item actually existed, remove its listener.
		if (getItem() != null) {
			checkbox.selectedProperty().unbindBidirectional(boundProperty);
		}

		// Update the item in this cell.
		super.updateItem(item, empty);

		// If the new item is null or this cell is empty, remove the graphic; we
		// don't want empty cells to be checkable.
		if (item == null || empty) {
			setGraphic(null);
			return;
		}

		// Since the cell is not empty, (otherwise the above if would've made
		// this method return), we make sure that this cell's graphic is showing
		// and we bind the checkbox's "selected" property to the value returned
		// by the propertyRetriever.
		setGraphic(checkbox);
		// Some weird error occurs when deleting an item. Apparently, this
		// method is called with getTableRow().getItem() being null, then having
		// a value... Anywyas, this suppresses the error and I don't think it
		// causes issues.
		T rowItem = (T) getTableRow().getItem();
		if (rowItem == null)
			return;
		boundProperty = propertyRetriever.call(rowItem);
		checkbox.selectedProperty().bindBidirectional(boundProperty);

	}

	@Override
	public void updateIndex(int i) {
		super.updateIndex(i);
	}
}