package zeale.guis.math_app.controllers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
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

	private TabGroup dflt;

	private @FXML ImageView evaluatorHelpImgView;

	private @FXML TextField searchBar;

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
		searchBar.setVisible(false);
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

		for (TitledPane ct : queue)
			menu.getPanes().add(ct);
		queue.clear();

		setupTools();

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

		searchBar.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				List<CalculatorTool> toCache = new LinkedList<>(), removeCache = new LinkedList<>();
				// For each tool...
				for (CalculatorTool ct : registeredTools)
					// ...if the tool contains the text...
					if (!ct.getSearchKeyWord().contains(newValue)) {
						// remove it from it's parent,
						((Pane) ct.getTab().getContent()).getChildren().remove(ct);
						// add it to the cache,
						toCache.add(ct);
					}
				// then remove it from the registry.
				registeredTools.removeAll(toCache);
				unregisteredTools.addAll(toCache);

				for (CalculatorTool ct : unregisteredTools)
					if (ct.getSearchKeyWord().contains(newValue)) {
						removeCache.add(ct);
						if (ct.getTab() != null) {
							((Pane) ct.getTab().getContent()).getChildren().add(ct);
						}
					}
				unregisteredTools.removeAll(removeCache);
				registeredTools.addAll(removeCache);
			}
		});

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
			searchBar.setVisible(false);
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

		showSearchBarMenuOption = new MenuOption("Show Search Bar");
		showSearchBarMenuOption.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY)
				toggleSearchBarVisibility();
		});
		slideMenu.getChildren().add(showSearchBarMenuOption);

	}

	private MenuOption showSearchBarMenuOption;

	public boolean toggleSearchBarVisibility() {
		if (searchBar.isVisible()) {
			// Search bar will be set to invisible, so false is returned.
			searchBar.setVisible(false);
			showSearchBarMenuOption.setText("Show Search Bar");
			return false;
		}
		// Search bar will be set to visible, so true is returned.
		searchBar.setVisible(true);
		showSearchBarMenuOption.setText("Hide Search Bar");
		slideMenu.getParentWrapper().setVisible(false);
		menu.setVisible(true);
		showMenuButton.setVisible(true);
		return true;
	}

	private void setupTools() {
		new CalculatorTool("Data Set", STATISTICS).setOnMouseClicked(event -> _event_enableStatsMode());
		new CalculatorTool("Z Scores", STATISTICS).setOnMouseClicked(event -> _event_enableStatsZScores());
	}

	private boolean isTabDiscrete(final Tab tab) {
		return tab.getProperties().containsKey(PropertyKeys.DISCRETE_TAB)
				&& tab.getProperties().get(PropertyKeys.DISCRETE_TAB).equals(true);
	}

	@Override
	protected void onPageSwitched() {
		cachedText = calcIO.getText();
	}

	public void show() {
		dflt.show(buttonTabPane);
	}

	private List<CalculatorTool> registeredTools = new ArrayList<>(), unregisteredTools = new ArrayList<>();

	public class CalculatorTool extends Button {

		private StringProperty searchKeyWord = new SimpleStringProperty();

		{

			registeredTools.add(this);
			setStyle("-fx-background-color: #00000020;" + "	-fx-font-size: 12.0px;" + " -fx-text-fill: gold;"
					+ " -fx-font-weight: bold;");
			setPrefWidth(200);

		}

		/**
		 * Sets this tool's text and search key word to the param specified.
		 * 
		 * @param name
		 */
		public CalculatorTool(String name) {
			this.searchKeyWord.set(name);
			setText(name);
		}

		public CalculatorTool() {
			super();
		}

		public CalculatorTool(String text, Node graphic) {
			super(text, graphic);
		}

		public CalculatorTool(String name, CalculatorTab tab) {
			this(name);
			setTab(tab);
		}

		public final StringProperty searchKeyWordProperty() {
			return this.searchKeyWord;
		}

		public final String getSearchKeyWord() {
			return this.searchKeyWordProperty().get();
		}

		public final void setSearchKeyWord(final String searchKeyWord) {
			this.searchKeyWordProperty().set(searchKeyWord);
		}

		public CalculatorTool(String text, String searchKeyWord) {
			super(text);
			this.searchKeyWord.set(searchKeyWord);
		}

		public CalculatorTool(StringProperty searchKeyWord, CalculatorTab tab) {
			this.searchKeyWord = searchKeyWord;
			setTab(tab);
		}

		public CalculatorTool(String text, StringProperty searchKeyWord, CalculatorTab tab) {
			super(text);
			this.searchKeyWord = searchKeyWord;
			setTab(tab);
		}

		private CalculatorTab tab;

		public void setTab(CalculatorTab tab) {
			if (tab == this.tab)
				return;
			if (this.tab != null)
				this.tab.removeTool(this);
			if (tab != null)
				(this.tab = tab).addTool(this);
		}

		public CalculatorTab getTab() {
			return tab;
		}

	}

	public final CalculatorTab STATISTICS = new CalculatorTab("Statistics"), CALCULUS = new CalculatorTab("Calculus"),
			CHEMISTRY = new CalculatorTab("Chemistry"), PHYSICS = new CalculatorTab("Physics");

	private static final List<TitledPane> queue = new LinkedList<>();

	public class CalculatorTab extends TitledPane {

		private VBox box = new VBox(10);

		{
			box.getStyleClass().add("calculator-accordion-vbox");
			setContent(box);
			setStyle("-fx-text-fill: gold;");
			// If queue is a List of CalculatorTabs, rather than TitledPanes, then this will
			// be an error, and require casting which is, apparently, unsafe.
			//
			// The ternary expression below returns its value casted to a type that
			// encompasses both of its possible values. Since CalculatorTab is a subclass of
			// TitledPane, in our case, this casted type turns out to be a List<? extends
			// TitledPane> if queue is of the type List<CalculatorTab>. Since the ternary
			// operation is evaluated independently of the following statement, the add(...)
			// statement, the add(...) statement gets performed on what it sees as a List<?
			// extends TitledPane>. This could mean many things. CalculatorTabs do indeed
			// "extend" TitledPanes, but a subclass of CalculatorTab would to. Our call to
			// add(...) wants to add a CalculatorTab to whatever is returned from the
			// ternary, but, for all the call to add(...) knows, the ternary could be
			// returning a List of items that are the 8th subclass of CalculatorTab, so the
			// call to add(...) will refuse to add a CalculatorTab to the result of the
			// ternary, even though any result the ternary could possibly give will be
			// applicable for our add(...) call.
			//
			//
			//
			// ArrayList<Object> upperList = new ArrayList<>();
			// LinkedList<String> lowerList = new LinkedList<>();
			//
			// lowerList.add("I am a String");
			// upperList.add("I am ALSO an Object");
			//
			// (new Random().nextBoolean() ? upperList : lowerList).add("I am both a String
			// and an Object");
			//
			//
			(menu != null ? menu.getPanes() : queue).add(this);
		}

		public CalculatorTab(String name) {
			setText(name);
		}

		public void addTool(CalculatorTool tool) {
			box.getChildren().add(tool);
		}

		public void removeTool(CalculatorTool calculatorTool) {
			box.getChildren().remove(calculatorTool);
		}
	}

}
