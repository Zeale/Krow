package zeale.guis.math_module.controllers;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import krow.guis.math_module.TabGroup;
import krow.guis.math_module.controllers.Statistic;
import kröw.core.managers.WindowManager;

public class StatisticsController {

	private TabGroup tabs;
	private boolean loaded;

	StatisticsController() {
		// TODO Auto-generated constructor stub
	}

	@FXML
	TextField statsMinProperty;
	@FXML
	TextField statsQ1Property;
	@FXML
	TextField statsMedianProperty;
	@FXML
	TextField statsQ3Property;
	@FXML
	TextField statsMaxProperty;
	@FXML
	TextField statsUBoundProperty;
	@FXML
	TextField statsLBoundProperty;
	@FXML
	TextField statsIQRProperty;
	@FXML
	TextArea statsData;
	@FXML
	Text statsDataErrorText;
	@FXML
	ListView<Statistic> statsOutputListView;

	@FXML
	private void initialize() {
		if (loaded)
			return;
		loaded = false;

		statsOutputListView.setCellFactory(Statistic.getStatisticListCellFactory());
	}

	@FXML
	void _event_evaluateStatsFromProperties() {
		statsIQRProperty.setText(
				"" + (Double.parseDouble(statsQ3Property.getText()) - Double.parseDouble(statsQ1Property.getText())));
		statsLBoundProperty.setText("" + (Double.parseDouble(statsQ1Property.getText())
				- Double.parseDouble(statsIQRProperty.getText()) * 1.5));
		statsUBoundProperty.setText("" + (Double.parseDouble(statsQ3Property.getText())
				+ Double.parseDouble(statsIQRProperty.getText()) * 1.5));
	}

	@FXML
	void _event_evaluateStats() {

		if (statsData.getText().isEmpty()) {
			statsDataErrorText.setText("Input some values...");
			return;
		}

		statsDataErrorText.setText("");

		ArrayList<Number> numbs = new ArrayList<>();
		StringBuilder curr = new StringBuilder();
		int pos = -1;
		StringBuilder text = new StringBuilder(statsData.getText());
		while (!(Character.isDigit(text.charAt(text.length() - 1)) || (text.charAt(text.length() - 1) == '.')
				|| (text.charAt(text.length() - 1) == ',')))
			text.deleteCharAt(text.length() - 1);

		while (++pos < text.toString().length()) {
			char c = text.toString().charAt(pos);
			if ((Character.isDigit(c) || c == '.' || c == ','))
				curr.append(c);
			else if (!curr.toString().isEmpty())
				try {
					numbs.add(NumberFormat.getNumberInstance(Locale.getDefault()).parse(curr.toString()));
					curr = new StringBuilder();
				} catch (ParseException e) {
					statsDataErrorText.setText("Could not parse the number at " + pos);
				}
		}

		try {
			numbs.add(NumberFormat.getNumberInstance(Locale.getDefault()).parse(curr.toString()));
		} catch (ParseException e) {
			statsDataErrorText.setText("Could not parse the number at character " + pos);
		}

		statsOutputListView.getItems().clear();

		double sum = 0;

		for (Number n : numbs)
			sum += n.doubleValue();

		statsOutputListView.getItems().add(new Statistic("Sum", sum));

	}

	public TabGroup show(TabPane pane) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("StatisticsTabs.fxml"));
			loader.setController(this);
			tabs = new TabGroup(loader.<TabPane>load().getTabs());
		} catch (IOException e) {
			WindowManager.spawnLabelAtMousePos("An error has occurred.", Color.FIREBRICK);
			e.printStackTrace();
			return null;
		}

		return tabs.show(pane);
	}

}
