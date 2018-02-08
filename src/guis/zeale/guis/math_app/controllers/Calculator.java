package zeale.guis.math_app.controllers;

import java.util.List;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import krow.guis.GUIHelper;
import krow.guis.GUIHelper.MenuOption;
import krow.guis.math_app.TabGroup;
import kröw.core.Kröw;
import kröw.gui.Application;
import kröw.math.lexer.EquationParser;
import zeale.guis.math_app.controllers.StatisticsController.Mode;

public class Calculator extends Application {

	private static enum PropertyKeys {
		DISCRETE_TAB;
	}

	/**
	 * This text controls what is inside this {@link Application}'s {@link #calcIO}
	 * field.
	 */
	private static String cachedText;
	private static boolean parsingDebugEnabled = false;

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
	private TabGroup dflt;

	private @FXML TextField searchBar;
	private @FXML Accordion searchResultAccordion;
	private @FXML Group searchTools;

	private @FXML ImageView evaluatorHelpImgView;

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

	@FXML
	private void _event_evaluate() {
		calcIO.setText("" + (parsingDebugEnabled ? EquationParser.getDebuggingParser() : new EquationParser())
				.evaluate(calcIO.getText()));
	}

	@FXML
	private void _event_showSlideMenu() {
		showMenuButton.setVisible(false);
		menu.setVisible(false);
		slideMenu.getParentWrapper().setVisible(true);
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

	private void appendText(final String text) {
		calcIO.appendText(text);
		calcIO.positionCaret(calcIO.getLength());
	}

	public void enableStatsMode() {
		_event_enableStatsMode();
	}

	@Override
	public void initialize() {
		if (hasLoaded)
			return;
		hasLoaded = true;

		dflt = new TabGroup(buttonTabPane.getTabs());

		searchTools.setVisible(false);

		statisticsMenuBox.setSpacing(Kröw.scaleHeight(10));

		evaluatorHelpImgView.setImage(new Image("/krow/resources/graphics/math-app/question-mark.png"));
		evaluatorHelpImgView.setEffect(new DropShadow());
		evaluatorHelpImgView.setOpacity(0.17);
		FadeTransition ehFadeTransition = new FadeTransition();
		ehFadeTransition.setNode(evaluatorHelpImgView);
		evaluatorHelpImgView.setOnMouseEntered(event -> {
			ehFadeTransition.stop();
			ehFadeTransition.setDuration(Duration.seconds(0.3));
			ehFadeTransition.setFromValue(evaluatorHelpImgView.getOpacity());
			ehFadeTransition.setToValue(1);
			ehFadeTransition.play();
		});

		evaluatorHelpImgView.setOnMouseExited(event -> {
			ehFadeTransition.stop();
			ehFadeTransition.setDuration(Duration.seconds(1.2));
			ehFadeTransition.setFromValue(evaluatorHelpImgView.getOpacity());
			ehFadeTransition.setToValue(0.17);
			ehFadeTransition.play();
		});

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
					n.setLayoutX(Kröw.scaleWidth(n.getLayoutX()));
					n.setLayoutY(Kröw.scaleWidth(n.getLayoutY()));
					if (n instanceof Region) {
						final Region r = (Region) n;
						r.setPrefSize(Kröw.scaleWidth(r.getPrefWidth()), Kröw.scaleHeight(r.getPrefHeight()));
						if (n instanceof Button) {
							final Button b = (Button) n;
							if (b.getOnAction() == null)
								b.setOnAction(event -> appendText(b.getText()));
						}
					}
				}

		if (Kröw.getProgramSettings().calculatorUseOuterZoomAnimation)
			addHoverAnimation(1.02, pane.getChildren());
		addHoverAnimation(1.1, ((AnchorPane) arithmeticTab.getContent()).getChildren());

		slideMenu = GUIHelper.buildMenu(pane);
		GUIHelper.addDefaultSettings(slideMenu);
		slideMenu.getParentWrapper().setVisible(false);

		final Node showMathMenuItem = new MenuOption(Color.ORANGE, "Show Math Menu");
		showMathMenuItem.setOnMouseClicked(event -> {
			slideMenu.getParentWrapper().setVisible(false);
			menu.setVisible(true);
			showMenuButton.setVisible(true);
		});
		slideMenu.getChildren().add(showMathMenuItem);

		MenuOption parsingDebugEnabledMenuItem = new MenuOption(
				parsingDebugEnabled ? "Disable Parser Debugging (" + (char) 10003 + ")" : ("Enable Parser Debugging"));
		parsingDebugEnabledMenuItem.setOnMouseClicked(event -> {
			parsingDebugEnabled ^= true;
			parsingDebugEnabledMenuItem.setStartColor(parsingDebugEnabled ? Color.GREEN : Color.BLACK);
			parsingDebugEnabledMenuItem.setText(parsingDebugEnabled ? "Disable Parser Debugging (" + (char) 10003 + ")"
					: "Enable Parser Debugging");
		});
		parsingDebugEnabledMenuItem.setStartColor(parsingDebugEnabled ? Color.GREEN : Color.BLACK);
		slideMenu.getChildren().add(parsingDebugEnabledMenuItem);

		MenuOption showSearchBar = new MenuOption("Show Search Bar");
		showSearchBar.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY)
				if (toggleSearchBarVisibility())
					showSearchBar.setText("Hide Search Bar");
				else
					showSearchBar.setText("Show Search Bar");
		});
		slideMenu.getChildren().add(showSearchBar);

	}

	private boolean isTabDiscrete(final Tab tab) {
		return tab.getProperties().containsKey(PropertyKeys.DISCRETE_TAB)
				&& tab.getProperties().get(PropertyKeys.DISCRETE_TAB).equals(true);
	}

	private boolean toggleSearchBarVisibility() {
		if (searchTools.isVisible()) {
			searchTools.setVisible(false);
			return false;
		} else {
			searchTools.setVisible(true);
			return true;
		}
	}

	@Override
	protected void onPageSwitched() {
		cachedText = calcIO.getText();
	}

	public void show() {
		dflt.show(buttonTabPane);
	}
	
	public class CalculatorTool extends Button {

		private String output;
		
		public CalculatorTool() {
		}

		public CalculatorTool(final String text) {
			super(text);
		}

		public CalculatorTool(final String text, final Node graphic) {
			super(text, graphic);
		}

		public CalculatorTool(final String text, final Node graphic, final String output) {
			super(text, graphic);
			this.output = output;
		}

		public CalculatorTool(final String text, final String output) {
			super(text);
			this.output = output;
		}

		/**
		 * @return the output
		 */
		public final String getOutput() {
			return output;
		}

		/**
		 * @param output
		 *            the output to set
		 */
		public final void setOutput(final String output) {
			this.output = output;
		}

	}


}
