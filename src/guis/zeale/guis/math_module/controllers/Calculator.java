package zeale.guis.math_module.controllers;

import java.util.List;

import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import krow.guis.GUIHelper;
import krow.guis.math_module.TabGroup;
import kr�w.core.Kr�w;
import kr�w.core.managers.WindowManager.Page;
import kr�w.lexers.equation.EquationParser;
import kr�w.lexers.equation.exceptions.EmptyEquationException;
import kr�w.lexers.equation.exceptions.IrregularCharacterException;
import kr�w.lexers.equation.exceptions.UnmatchedParenthesisException;
import zeale.guis.math_module.controllers.StatisticsController.Mode;

public class Calculator extends Page {

	private static enum PropertyKeys {
		DISCRETE_TAB;
	}

	/**
	 * This text controls what is inside this {@link Page}'s {@link #calcIO}
	 * field.
	 */
	private static String cachedText;

	private boolean hasLoaded;

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
	private VBox statisticsMenuBox;
	private TabGroup calculus, chemistry, dflt;

	@FXML
	private Button showMenuButton;

	private final StatisticsController statistics = new StatisticsController(this);

	private GUIHelper.MenuBox slideMenu;

	@FXML
	private void _event_clear() {
		calcIO.clear();
	}

	@FXML
	private void _event_cube() {
		appendText("^3");
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
	private void _event_showSlideMenu() {
		showMenuButton.setVisible(false);
		menu.setVisible(false);
		slideMenu.getParentWrapper().setVisible(true);
	}

	@FXML
	private void _event_enableStatsMode() {
		if (statistics.getCurrentMode() == Mode.DATA_SET)
			return;
		statistics.show(buttonTabPane);
	}

	@FXML
	private void _event_enableStatsZScores() {
		if (statistics.getCurrentMode() == Mode.Z_SCORES)
			return;
		statistics.show(buttonTabPane, Mode.Z_SCORES);
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
	private void _event_sqrt() {
		appendText((char) 8730 + "(");
	}

	@FXML
	private void _event_square() {
		appendText("^2");
	}

	private void addHoverAnimation(final double size, final List<Node> nodes) {
		for (final Node n : nodes) {

			final ScaleTransition buttonTabPaneScaleTransition = new ScaleTransition(Duration.seconds(0.1));
			buttonTabPaneScaleTransition.setNode(n);

			n.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
				buttonTabPaneScaleTransition.stop();
				buttonTabPaneScaleTransition.setToX(size);
				buttonTabPaneScaleTransition.setToY(size);
				buttonTabPaneScaleTransition.play();
			});
			n.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
				buttonTabPaneScaleTransition.stop();
				buttonTabPaneScaleTransition.setToX(1);
				buttonTabPaneScaleTransition.setToY(1);
				buttonTabPaneScaleTransition.play();
			});

		}
	}

	/**************************************************************************************
	 *********************************** HELPER METHODS ***********************************
	 **************************************************************************************/

	private void addHoverAnimation(final double size, final Node... nodes) {
		for (final Node n : nodes) {

			final ScaleTransition buttonTabPaneScaleTransition = new ScaleTransition(Duration.seconds(0.1));
			buttonTabPaneScaleTransition.setNode(n);

			n.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
				buttonTabPaneScaleTransition.stop();
				buttonTabPaneScaleTransition.setToX(size);
				buttonTabPaneScaleTransition.setToY(size);
				buttonTabPaneScaleTransition.play();
			});
			n.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
				buttonTabPaneScaleTransition.stop();
				buttonTabPaneScaleTransition.setToX(1);
				buttonTabPaneScaleTransition.setToY(1);
				buttonTabPaneScaleTransition.play();
			});

		}
	}

	/***************************************************************************************
	 *********************************** INSTANCE METHODS **********************************
	 ***************************************************************************************/

	private void appendText(final String text) {
		calcIO.appendText(text);
		calcIO.positionCaret(calcIO.getLength());
	}

	@Override
	public String getWindowFile() {
		return "Calculator.fxml";
	}

	@Override
	public void initialize() {
		if (hasLoaded)
			return;
		hasLoaded = true;

		dflt = new TabGroup(buttonTabPane.getTabs());

		statisticsMenuBox.setSpacing(Kr�w.scaleHeight(10));

		/******************************
		 **** INJECTED NODE SIZING ****
		 ******************************/
		pane.setPrefSize(Kr�w.scaleWidth(1920), Kr�w.scaleHeight(1080));

		calcIO.setPrefSize(Kr�w.scaleWidth(954), Kr�w.scaleHeight(42));
		calcIO.setLayoutX(Kr�w.scaleWidth(485));
		calcIO.setLayoutY(Kr�w.scaleHeight(175));

		buttonTabPane.setPrefSize(Kr�w.scaleWidth(954), Kr�w.scaleHeight(478));
		buttonTabPane.setLayoutX(Kr�w.scaleWidth(485));
		buttonTabPane.setLayoutY(Kr�w.scaleHeight(248));

		/**********************************
		 **** INJECTED NODE PROPERTIES ****
		 **********************************/
		calcIO.setText(cachedText);

		// This gives accordion buttons animations.
		for (final TitledPane tp : menu.getPanes())
			if (tp.getContent() instanceof Pane)
				for (final Node n : ((Pane) tp.getContent()).getChildren())
					if (n instanceof Button)
						new Transition() {

							private Color toColor, fromColor;

							{
								setCycleDuration(Duration.seconds(0.4));
								setInterpolator(Interpolator.EASE_OUT);

								((Button) n).setTextFill(Color.GOLD);

								n.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
									stop();
									fromColor = (Color) ((Button) n).getTextFill();
									toColor = Color.RED;
									play();
								});
								n.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
									stop();
									fromColor = (Color) ((Button) n).getTextFill();
									toColor = Color.GOLD;
									play();
								});
							}

							@Override
							protected void interpolate(final double frac) {
								((Button) n).setTextFill(fromColor.interpolate(toColor, frac));
							}
						};

		// This loop assures that regular buttons will automatically append
		// their
		// visual content to the TEXT content.
		//
		// That will then copy its contents to text
		for (final Tab t : buttonTabPane.getTabs())
			if (!isTabDiscrete(t))
				for (final Node n : ((Pane) t.getContent()).getChildren()) {
					n.setLayoutX(Kr�w.scaleWidth(n.getLayoutX()));
					n.setLayoutY(Kr�w.scaleWidth(n.getLayoutY()));
					if (n instanceof Region) {
						final Region r = (Region) n;
						r.setPrefSize(Kr�w.scaleWidth(r.getPrefWidth()), Kr�w.scaleHeight(r.getPrefHeight()));
						if (n instanceof Button) {
							final Button b = (Button) n;
							if (b.getOnAction() == null)
								b.setOnAction(event -> appendText(b.getText()));
						}
					}
				}

		if (Kr�w.getProgramSettings().calculatorUseOuterZoomAnimation)
			addHoverAnimation(1.02, pane.getChildren());
		addHoverAnimation(1.1, ((AnchorPane) arithmeticTab.getContent()).getChildren());

		slideMenu = GUIHelper.buildMenu(pane);
		GUIHelper.addDefaultSettings(slideMenu);
		slideMenu.getParentWrapper().setVisible(false);

		Node showMathMenuItem = new GUIHelper.MenuOption(Color.ORANGE, "Show Math Menu");
		showMathMenuItem.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				slideMenu.getParentWrapper().setVisible(false);
				menu.setVisible(true);
				showMenuButton.setVisible(true);
			}
		});
		slideMenu.getChildren().add(showMathMenuItem);

	}

	private boolean isTabDiscrete(final Tab tab) {
		return tab.getProperties().containsKey(PropertyKeys.DISCRETE_TAB)
				&& tab.getProperties().get(PropertyKeys.DISCRETE_TAB).equals(true);
	}

	private void makeTabDiscrete(final Tab tab) {
		tab.getProperties().put(PropertyKeys.DISCRETE_TAB, true);
	}

	private void makeTabNormal(final Tab tab) {
		tab.getProperties().remove(PropertyKeys.DISCRETE_TAB);
	}

	@Override
	protected void onPageSwitched() {
		cachedText = calcIO.getText();
	}

	/*****************************************************************************************
	 *********************************** INITIALIZATION METHOD *******************************
	 *****************************************************************************************/

	public void show() {
		dflt.show(buttonTabPane);
	}

}
