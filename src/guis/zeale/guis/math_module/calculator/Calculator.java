package zeale.guis.math_module.calculator;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import krow.guis.math_module.TabGroup;
import kröw.core.Kröw;
import kröw.core.managers.WindowManager;
import kröw.core.managers.WindowManager.Page;
import kröw.libs.math.EquationParser;
import kröw.libs.math.exceptions.EmptyEquationException;
import kröw.libs.math.exceptions.IrregularCharacterException;
import kröw.libs.math.exceptions.UnmatchedParenthesisException;

public class Calculator extends Page {

	/**
	 * This text controls what is inside this {@link Page}'s {@link #calcIO}
	 * field.
	 */
	private static String cachedText;

	@Override
	public String getWindowFile() {
		return "Calculator.fxml";
	}

	@Override
	protected void onPageSwitched() {
		cachedText = calcIO.getText();
	}

	/*******************************************************************************************
	 *********************************** DEFINING INJECTED NODES *******************************
	 *******************************************************************************************/

	/* Basic Nodes */
	@FXML
	private TextField calcIO;
	@FXML
	private Button solve;
	@FXML
	private AnchorPane pane;
	@FXML
	private TabPane buttonTabPane;
	@FXML
	private Tab arithmeticTab, functionsTab;
	@FXML
	private Accordion menu;

	@FXML
	private TextField statsMinProperty, statsQ1Property, statsMedianProperty, statsQ3Property, statsMaxProperty,
			statsUBoundProperty, statsLBoundProperty, statsIQRProperty;
	@FXML
	private TextArea statsData;
	@FXML
	private Text statsDataErrorText;

	private TabGroup statistics, calculus, chemistry, dflt;

	/*****************************************************************************************
	 *********************************** INITIALIZATION METHOD *******************************
	 *****************************************************************************************/

	@Override
	public void initialize() {

		dflt = new TabGroup(buttonTabPane.getTabs());

		/******************************
		 **** INJECTED NODE SIZING ****
		 ******************************/
		pane.setPrefSize(Kröw.scaleWidth(1920), Kröw.scaleHeight(1080));

		calcIO.setPrefSize(Kröw.scaleWidth(954), Kröw.scaleHeight(42));
		calcIO.setLayoutX(Kröw.scaleWidth(485));
		calcIO.setLayoutY(Kröw.scaleHeight(175));

		buttonTabPane.setPrefSize(Kröw.scaleWidth(954), Kröw.scaleHeight(478));
		buttonTabPane.setLayoutX(Kröw.scaleWidth(485));
		buttonTabPane.setLayoutY(Kröw.scaleHeight(248));

		/**********************************
		 **** INJECTED NODE PROPERTIES ****
		 **********************************/
		calcIO.setText(cachedText);

		// This gives accordion buttons animations.
		for (TitledPane tp : menu.getPanes())
			if (tp.getContent() instanceof Pane)
				for (Node n : ((Pane) tp.getContent()).getChildren())
					if (n instanceof Button) {
						new Transition() {

							private Color toColor, fromColor;

							{
								setCycleDuration(Duration.seconds(0.4));
								setInterpolator(Interpolator.EASE_OUT);

								((Button) n).setTextFill(Color.GOLD);

								n.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<Event>() {

									@Override
									public void handle(Event event) {
										stop();
										fromColor = (Color) ((Button) n).getTextFill();
										toColor = Color.RED;
										play();
									}
								});
								n.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<Event>() {

									@Override
									public void handle(Event event) {
										stop();
										fromColor = (Color) ((Button) n).getTextFill();
										toColor = Color.GOLD;
										play();
									}
								});
							}

							@Override
							protected void interpolate(double frac) {
								((Button) n).setTextFill((fromColor).interpolate(toColor, frac));
							}
						};

					}

		// This loop assures that regular buttons will automatically append
		// their
		// visual content to the TEXT content.
		//
		// That will then copy its contents to text
		for (Tab t : buttonTabPane.getTabs())
			if (!(isTabDiscrete(t)))
				for (Node n : ((Pane) t.getContent()).getChildren()) {
					n.setLayoutX(Kröw.scaleWidth(n.getLayoutX()));
					n.setLayoutY(Kröw.scaleWidth(n.getLayoutY()));
					if (n instanceof Region) {
						Region r = (Region) n;
						r.setPrefSize(Kröw.scaleWidth(r.getPrefWidth()), Kröw.scaleHeight(r.getPrefHeight()));
						if (n instanceof Button) {
							Button b = (Button) n;
							if (b.getOnAction() == null)
								b.setOnAction(event -> appendText(b.getText()));
						}
					}
				}

		if (Kröw.getProgramSettings().calculatorUseOuterZoomAnimation)
			addHoverAnimation(1.02, pane.getChildren());
		addHoverAnimation(1.1, ((AnchorPane) arithmeticTab.getContent()).getChildren());

	}

	/*************************************************************************************
	 *********************************** EVENT METHODS ***********************************
	 *************************************************************************************/

	@FXML
	private void _event_evaluate() {
		try {
			calcIO.setText("" + new EquationParser().evaluate(calcIO.getText()));
		} catch (EmptyEquationException | UnmatchedParenthesisException | IrregularCharacterException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void _event_cube() {
		appendText("^3");
	}

	@FXML
	private void _event_square() {
		appendText("^2");
	}

	@FXML
	private void _event_sqrt() {
		appendText((char) 8730 + "(");
	}

	@FXML
	private void _event_clear() {
		calcIO.clear();
	}

	@FXML
	private void _event_enableStatsMode() {
		if (statistics == null)
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("StatisticsTabs.fxml"));
				loader.setController(this);
				statistics = new TabGroup(loader.<TabPane>load().getTabs());
			} catch (IOException e) {
				WindowManager.spawnLabelAtMousePos("An error has occurred.", Color.FIREBRICK);
				e.printStackTrace();
				return;
			}
		statistics.show(buttonTabPane);

	}

	@FXML
	private void _event_enableCalculusMode() {
		// TODO Implement
	}

	@FXML
	private void _event_enableChemistryMode() {
		// TODO Implement
	}

	@FXML
	private void _event_enableDefaultMode() {
		// TODO Implement
	}

	@FXML
	private void _event_evaluateStatsFromProperties() {
		statsIQRProperty.setText(
				"" + (Double.parseDouble(statsQ3Property.getText()) - Double.parseDouble(statsQ1Property.getText())));
		statsLBoundProperty.setText("" + (Double.parseDouble(statsQ1Property.getText())
				- Double.parseDouble(statsIQRProperty.getText()) * 1.5));
		statsUBoundProperty.setText("" + (Double.parseDouble(statsQ3Property.getText())
				+ Double.parseDouble(statsIQRProperty.getText()) * 1.5));
	}

	@FXML
	private void _event_evaluateStats() {
		ArrayList<Number> numbs = new ArrayList<>();
		StringBuilder curr = new StringBuilder();
		int pos = -1;
		while (++pos < statsData.getText().length()) {
			char c = statsData.getText().charAt(pos);
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
			statsDataErrorText.setText("Could not parse the number at " + pos);
		}

	}

	/**************************************************************************************
	 *********************************** HELPER METHODS ***********************************
	 **************************************************************************************/

	private void addHoverAnimation(double size, Node... nodes) {
		for (Node n : nodes) {

			ScaleTransition buttonTabPaneScaleTransition = new ScaleTransition(Duration.seconds(0.1));
			buttonTabPaneScaleTransition.setNode(n);

			n.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<Event>() {

				@Override
				public void handle(Event event) {
					buttonTabPaneScaleTransition.stop();
					buttonTabPaneScaleTransition.setToX(size);
					buttonTabPaneScaleTransition.setToY(size);
					buttonTabPaneScaleTransition.play();
				}
			});
			n.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<Event>() {

				@Override
				public void handle(Event event) {
					buttonTabPaneScaleTransition.stop();
					buttonTabPaneScaleTransition.setToX(1);
					buttonTabPaneScaleTransition.setToY(1);
					buttonTabPaneScaleTransition.play();
				}
			});

		}
	}

	private void addHoverAnimation(double size, List<Node> nodes) {
		for (Node n : nodes) {

			ScaleTransition buttonTabPaneScaleTransition = new ScaleTransition(Duration.seconds(0.1));
			buttonTabPaneScaleTransition.setNode(n);

			n.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<Event>() {

				@Override
				public void handle(Event event) {
					buttonTabPaneScaleTransition.stop();
					buttonTabPaneScaleTransition.setToX(size);
					buttonTabPaneScaleTransition.setToY(size);
					buttonTabPaneScaleTransition.play();
				}
			});
			n.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<Event>() {

				@Override
				public void handle(Event event) {
					buttonTabPaneScaleTransition.stop();
					buttonTabPaneScaleTransition.setToX(1);
					buttonTabPaneScaleTransition.setToY(1);
					buttonTabPaneScaleTransition.play();
				}
			});

		}
	}

	private void makeTabDiscrete(Tab tab) {
		tab.getProperties().put(PropertyKeys.DISCRETE_TAB, true);
	}

	private boolean isTabDiscrete(Tab tab) {
		return tab.getProperties().containsKey(PropertyKeys.DISCRETE_TAB)
				&& tab.getProperties().get(PropertyKeys.DISCRETE_TAB).equals(true);
	}

	private void makeTabNormal(Tab tab) {
		tab.getProperties().remove(PropertyKeys.DISCRETE_TAB);
	}

	private static enum PropertyKeys {
		DISCRETE_TAB;
	}

	/***************************************************************************************
	 *********************************** INSTANCE METHODS **********************************
	 ***************************************************************************************/

	private void appendText(String text) {
		calcIO.appendText(text);
		calcIO.positionCaret(calcIO.getLength());
	}

}
