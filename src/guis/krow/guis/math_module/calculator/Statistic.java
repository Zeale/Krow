package krow.guis.math_module.calculator;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public final class Statistic {
	public final String name;
	public final String value;

	public Statistic(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public static Callback<ListView<Statistic>, ListCell<Statistic>> getStatisticListCellFactory() {
		return new Callback<ListView<Statistic>, ListCell<Statistic>>() {

			@Override
			public ListCell<Statistic> call(ListView<Statistic> param) {
				ListCell<Statistic> cell = new ListCell<Statistic>() {
					@Override
					protected void updateItem(Statistic item, boolean empty) {
						super.updateItem(item, empty);
						if (empty)
							setText("");
						else
							setText(item.name + ": " + item.value);
					}
				};
				return cell;
			}
		};
	}

}
