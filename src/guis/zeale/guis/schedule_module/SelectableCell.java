package zeale.guis.schedule_module;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;

public class SelectableCell<T> extends TableCell<T, Boolean> {

	public final CheckBox checkbox = new CheckBox();
	private Callback<Integer, BooleanProperty> propertyRetriever;

	public SelectableCell(Callback<Integer, BooleanProperty> propertyRetriever) {
		this.propertyRetriever = propertyRetriever;
	}

	{
		setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY)
				checkbox.setSelected(!checkbox.isSelected());
			else if (event.getButton() == MouseButton.MIDDLE)
				checkbox.setSelected(false);
		});
	}

	@Override
	protected void updateItem(Boolean item, boolean empty) {
		if (item == getItem())
			return;

		if (getItem() != null) {
			checkbox.selectedProperty().unbindBidirectional(propertyRetriever.call(getIndex()));
		}

		super.updateItem(item, empty);

		if (item == null || empty) {
			setGraphic(null);
			return;
		}

		setGraphic(checkbox);
		checkbox.selectedProperty().bindBidirectional(propertyRetriever.call(getIndex()));

	}
}