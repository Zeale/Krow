package zeale.guis.math_module.controllers;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import krow.guis.math_module.TabGroup;
import krow.guis.math_module.controllers.Distribution;
import krow.guis.math_module.controllers.NumericalStatistic;
import krow.guis.math_module.controllers.Statistic;
import kröw.core.managers.WindowManager;

public class StatisticsController {

	private TabPane pane;
	private boolean loaded;
	private Calculator calculator;

	StatisticsController(Calculator calcInst) {
		calculator = calcInst;
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
	CheckBox sampleCheckBox;

	@FXML
	private Tab dataSet, properties, output;

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

		ArrayList<Double> numbs = new ArrayList<>();
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
					numbs.add(NumberFormat.getNumberInstance(Locale.getDefault()).parse(curr.toString()).doubleValue());
					curr = new StringBuilder();
				} catch (ParseException e) {
					statsDataErrorText.setText("Could not parse the number at " + pos);
				}
		}

		try {
			numbs.add(NumberFormat.getNumberInstance(Locale.getDefault()).parse(curr.toString()).doubleValue());
		} catch (ParseException e) {
			statsDataErrorText.setText("Could not parse the number at character " + pos);
		}

		statsOutputListView.getItems().clear();

		pane.getSelectionModel().select(output);

		numbs.sort(null);

		Distribution distribution = new Distribution(numbs);
		
		NumericalStatistic count = new NumericalStatistic("Count", distribution.getCount()),
				sum = new NumericalStatistic("Sum", distribution.getSum()),
				min = new NumericalStatistic("Min", distribution.getMin()), mean = new NumericalStatistic("Mean", 0),
				max = new NumericalStatistic("Max", distribution.getMax()),
				mode = new NumericalStatistic("Mode", distribution.getMode()),
				median = new NumericalStatistic("Median", distribution.getMedian()),
				variance = new NumericalStatistic("Variance", distribution.getVariance(sampleCheckBox.isSelected())),
				standardDeviation = new NumericalStatistic("Standard Deviation",
						distribution.getStandardDeviation(sampleCheckBox.isSelected())),
				z_index = new NumericalStatistic("z_index", 1);

		statsOutputListView.getItems().addAll(count, sum, min, mean, max, mode, median, variance, standardDeviation,
				z_index);

	}

	public void show(TabPane pane) {
		show(pane, Mode.values()[0]);
	}

	public void show(TabPane pane, Mode mode) {
		TabGroup tg = loadMode(mode);
		this.pane = pane;
		tg.show(pane);
		currentMode = mode;
	}

	private Mode currentMode;

	public Mode getCurrentMode() {
		return currentMode;
	}

	private TabGroup loadMode(Mode mode) {
		if (mode.exists(this))
			return mode.tabs;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("_statistics/" + mode.location + ".fxml"));
		loader.setController(this);

		try {
			mode.controller = this;
			return mode.tabs = new TabGroup(loader.<TabPane>load().getTabs());
		} catch (IOException e) {
			WindowManager.spawnLabelAtMousePos("An error has occurred.", Color.FIREBRICK);
			e.printStackTrace();
			mode.controller = null;
			return null;
		}
	}

	enum Mode {
		DATA_SET("DataSet"), Z_SCORES("ZScores");

		private Mode(String location) {
			this.location = location;
		}

		private Mode() {
			StringBuilder sb = new StringBuilder(name().toLowerCase());
			String f = String.valueOf(sb.charAt(0)).toUpperCase();
			sb.deleteCharAt(0);
			sb.insert(0, f);

			for (int i = 0; i < sb.length(); i++)
				if (sb.charAt(i) == '_') {
					String s = String.valueOf(sb.charAt(i + 1)).toUpperCase();
					sb.delete(i, i + 2);
					sb.insert(i, s);
				}
			location = sb.toString();

		}

		private final String location;
		private TabGroup tabs;
		private StatisticsController controller;

		public TabGroup getTabs(StatisticsController controller) {
			if (tabs == null || controller != this.controller)
				tabs = controller.loadMode(this);
			return tabs;
		}

		public boolean exists(StatisticsController controller) {
			return tabs != null && controller == this.controller;
		}

	}

	@FXML
	private void _event_goHome() {
		calculator.show();
	}

	@FXML
	private void _event_importDataToZScores() {

	}

	@FXML
	private void _event_evalZScores() {
	}
}
