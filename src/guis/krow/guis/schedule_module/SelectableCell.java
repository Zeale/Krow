package krow.guis.schedule_module;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;

public class SelectableCell<T> extends TableCell<T, Boolean> {

	public final CheckBox checkbox = new CheckBox();
	/**
	 * The {@link Callback} we use to retrieve a cell's observable boolean
	 * property. The cell will bind its selection state to the property.
	 */
	private Callback<Integer, BooleanProperty> propertyRetriever;

	public SelectableCell(Callback<Integer, BooleanProperty> propertyRetriever) {
		this.propertyRetriever = propertyRetriever;
	}

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	protected void updateItem(Boolean item, boolean empty) {
		// If the item was not changed, there's no need to do anything...
		if (item == getItem())
			return;

		// If the old item actually existed, remove its listener.
		if (getItem() != null) {
			checkbox.selectedProperty().unbindBidirectional(propertyRetriever.call(getIndex()));
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
		checkbox.selectedProperty().bindBidirectional(propertyRetriever.call(getIndex()));

	}
}