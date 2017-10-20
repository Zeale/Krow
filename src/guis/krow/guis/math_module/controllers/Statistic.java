package krow.guis.math_module.controllers;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

public abstract class Statistic {
	public static Callback<ListView<Statistic>, ListCell<Statistic>> getStatisticListCellFactory() {
		return param -> {
			final ListCell<Statistic> cell = new ListCell<Statistic>() {
				@Override
				protected void updateItem(final Statistic item, final boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setText("");
						setBackground(
								new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
						return;// Don't run below code.
					} else
						setText(item.toString());

					setTextAlignment(TextAlignment.CENTER);
					setAlignment(Pos.CENTER);
					setTextFill(Color.WHITE);
					setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, Font.getDefault().getSize()));

					final BackgroundFill[] backgrounds = {
							new BackgroundFill(Color.gray(0.2, 0.3), CornerRadii.EMPTY, Insets.EMPTY),
							new BackgroundFill(Color.gray(0.4, 0.2), CornerRadii.EMPTY, Insets.EMPTY) };

					setBackground(new Background(backgrounds[param.getItems().indexOf(item) % backgrounds.length]));
				}
			};
			return cell;
		};
	}

	public final String name;

	public Statistic(final String name) {
		this.name = name;
	}

	protected abstract String getValue();

	@Override
	public String toString() {
		return name + ": " + getValue();
	}

}
