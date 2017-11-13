package krow.guis;

import java.io.IOException;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Random;

import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import krow.guis.BackgroundBuilder.ShapeBackgroundManager;
import kröw.core.Kröw;
import kröw.core.managers.WindowManager;
import zeale.guis.Home;

public final class GUIHelper {

	public static class MenuBox extends VBox {
		private AnchorPane parentWrapper;

		public MenuBox() {
			// TODO Auto-generated constructor stub
		}

		public MenuBox(final double spacing) {
			super(spacing);
			// TODO Auto-generated constructor stub
		}

		public MenuBox(final double spacing, final Node... children) {
			super(spacing, children);
			// TODO Auto-generated constructor stub
		}

		public MenuBox(final Node... children) {
			super(children);
			// TODO Auto-generated constructor stub
		}

		public AnchorPane getParentWrapper() {
			return parentWrapper;
		}

		private void setParentWrapper(final AnchorPane parentWrapper) {
			this.parentWrapper = parentWrapper;
		}

	}

	public static final class MenuOption extends Text {
		private Color fadeColor, startColor;

		public MenuOption(final Color fadeColor, final Color startColor, final String text) {
			super(text);
			this.fadeColor = fadeColor;
			this.startColor = startColor;
		}

		public MenuOption(final Color fadeColor, final String text) {
			this(fadeColor, MENU_CHILD_NODE_START_COLOR, text);
		}

		public MenuOption(final String text) {
			this(MENU_CHILD_NODE_HOVER_ANIMATION_END_COLOR, text);
		}

		/**
		 * @return the fadeColor
		 */
		public final Color getFadeColor() {
			return fadeColor;
		}

		/**
		 * @return the startColor
		 */
		public final Color getStartColor() {
			return startColor;
		}

		/**
		 * @param fadeColor
		 *            the fadeColor to set
		 */
		public final void setFadeColor(final Color fadeColor) {
			this.fadeColor = fadeColor;
		}

		/**
		 * @param startColor
		 *            the startColor to set
		 */
		public final void setStartColor(final Color startColor) {
			this.startColor = startColor;
		}

	}

	private static final Random RANDOM = new Random();
	private static final Color MENU_BAR_SHADOW_COLOR = Color.BLACK;
	private static final int MENU_BAR_SHADOW_RADIUS = 7;
	private static final Color MENU_BACKGROUND_COLOR = new Color(0, 0, 0, 0.3);
	private static final double MENU_WIDTH_FRACTION = 4.5;
	private static final double MENU_ITEM_SPACING = 25 / 1080 * Kröw.getSystemProperties().getScreenHeight();
	private static final Interpolator MENU_BUTTON_TRANSITION_INTERPOLATOR = Interpolator.EASE_OUT,
			MENU_CHILD_TRANSITION_INTERPOLATOR = MENU_BUTTON_TRANSITION_INTERPOLATOR;
	private static final Duration MENU_BUTTON_ANIMATION_DURATION = Duration.seconds(0.8);
	private static final double MENU_BUTTON_RECTANGLE_WIDTH = 25, MENU_BUTTON_RECTANGLE_HEIGHT = 5,
			MENU_BUTTON_RECTANGLE_X = Kröw.getSystemProperties().getScreenWidth()
					- (double) 50 / 1920 * Kröw.getSystemProperties().getScreenWidth(),
			MENU_BUTTON_Y = (double) 33 / 1080 * Kröw.getSystemProperties().getScreenHeight(),
			MENU_BUTTON_RECTANGLE_SPACING = (double) 13 / 1080 * Kröw.getSystemProperties().getScreenHeight();
	private static final int SIDE_MENU_PADDING_TOP = (int) ((double) 80 / 1080
			* Kröw.getSystemProperties().getScreenHeight());
	private static final byte MENU_BAR_ANIMATION_ROTATION_COUNT = 1;
	private static final Color MENU_BUTTON_START_COLOR = MENU_BAR_SHADOW_COLOR, MENU_BUTTON_END_COLOR = Color.WHITE;
	private static final Duration DEFAULT_MENU_CHILD_NODE_HOVER_ANIMATION_DURATION = Duration.seconds(0.6);
	private static final Duration MENU_CHILD_NODE_HOVER_ANIMATION_ENTER_DURATION = Duration.seconds(0.35);
	private static final Duration MENU_CHILD_NODE_HOVER_ANIMATION_EXIT_DURATION = Duration.seconds(1);
	private static final Color MENU_CHILD_NODE_START_COLOR = Color.BLACK,
			MENU_CHILD_NODE_HOVER_ANIMATION_END_COLOR = Color.WHITE;

	private static final int MENU_CHILD_NODE_FONT_SIZE;

	static {
		double size = Kröw.getSystemProperties().isDPIOversized() ? 16 : 18;
		size *= 1920 / Kröw.getSystemProperties().getScreenWidth();
		MENU_CHILD_NODE_FONT_SIZE = (int) Math.round(size);
	}

	private static ShapeBackgroundManager backgroundmngr;

	public static void addDefaultSettings(final VBox vbox) {
		final List<Node> children = vbox.getChildren();

		final Node close = new MenuOption(Color.RED, "Close"), goHome = new Text("Go Home"),
				goBack = new Text("Go Back"), hideProgram = new Text("Hide Program"),
				sendProgramToBack = new Text("Send to back"), background = new Text("Background...");

		final Text systemTray = new Text(
				"Tray Icon: " + (Kröw.getSystemTrayManager().isIconShowing() ? "Hide" : "Show"));
		close.setOnMouseClicked(Kröw.CLOSE_PROGRAM_EVENT_HANDLER);

		goHome.setOnMouseClicked(event -> {
			try {
				WindowManager.setScene(Home.class);
			} catch (InstantiationException | IllegalAccessException | IOException e1) {
				e1.printStackTrace();
			} catch (final WindowManager.NotSwitchableException e2) {
				if (e2.getCurrentWindow().getController().getClass() == e2.getControllerClass())
					WindowManager.spawnLabelAtMousePos("You're already here.", Color.FIREBRICK);
				else
					WindowManager.spawnLabelAtMousePos("You can't go there right now...", Color.FIREBRICK);
			}
		});

		goBack.setOnMouseClicked(event -> {

			try {
				WindowManager.goBack();
			} catch (final WindowManager.NotSwitchableException e1) {
				WindowManager.spawnLabelAtMousePos("You can't go there right now...", Color.FIREBRICK);
			} catch (final EmptyStackException e2) {
				WindowManager.spawnLabelAtMousePos("Back to where???...", Color.FIREBRICK);
			}
		});

		systemTray.setOnMouseClicked(event -> {
			if (Kröw.getSystemTrayManager().isIconShowing())
				if (Kröw.getSystemTrayManager().hideIcon())
					systemTray.setText("Tray Icon: Show");
				else
					WindowManager.spawnLabelAtMousePos("Something went wrong...", Color.FIREBRICK);
			else if (Kröw.getSystemTrayManager().showIcon())
				systemTray.setText("Tray Icon: Hide");
			else
				WindowManager.spawnLabelAtMousePos("Something went wrong...", Color.FIREBRICK);
		});

		hideProgram.setOnMouseClicked(event -> {
			WindowManager.getStage().hide();
			if (!(Kröw.getSystemTrayManager().isIconShowing() || Kröw.getSystemTrayManager().showIcon())) {
				WindowManager.getStage().show();
				WindowManager.spawnLabelAtMousePos("Failed to show icon...", Color.FIREBRICK);
			}
		});

		sendProgramToBack.setOnMouseClicked(event -> WindowManager.getStage().toBack());
		background.setOnMouseClicked(event -> {

			if (backgroundmngr.isDisabled())
				backgroundmngr.fadeIn();
			else
				backgroundmngr.fadeOut();

			/*
			 * backgroundmngr.setColorAnimations(false,
			 * BackgroundBuilder.ShapeBackgroundManager.ColorAnimation.
			 * generateRandomColorAnimation(50));
			 *
			 * backgroundmngr.setRepeatColorAnimations(true);
			 * backgroundmngr.setRotatable(true);
			 *
			 * backgroundmngr.playColorAnimations();
			 */
		});

		children.add(close);
		children.add(goHome);
		children.add(goBack);
		children.add(systemTray);
		children.add(hideProgram);
		children.add(sendProgramToBack);
		children.add(background);
	}

	public static void applyShapeBackground(final Pane pane, final Node... mouseDetectionNodes) {
		if (backgroundmngr == null) {
			backgroundmngr = BackgroundBuilder.shapeBackground(pane, mouseDetectionNodes);
			if (RANDOM.nextBoolean()) {
				final Shape octicon = ShapeFactory.buildOcticon(4);
				octicon.setOnMouseClicked(event -> {
					if (event.getButton() == MouseButton.PRIMARY) {
						final Color color = Color.rgb(RANDOM.nextInt(256), RANDOM.nextInt(256), RANDOM.nextInt(256));
						octicon.setStroke(color);

						if (event.getClickCount() == 2) {
							Kröw.openLink("https://github.com/Zeale/Krow");
							((DropShadow) octicon.getEffect()).setColor(color);
						} else
							((DropShadow) octicon.getEffect()).setColor(Color.BLACK);
					}
				});
				octicon.setPickOnBounds(true);
				backgroundmngr.addShape(octicon);
			}
			backgroundmngr.animateShapes();
		} else {
			backgroundmngr.setCurrentPane(pane);
			backgroundmngr.addMouseDetectionNodes(mouseDetectionNodes);
		}
	}

	public static MenuBox buildMenu(final Pane pane) {

		final Shape menubarTop = new Rectangle(MENU_BUTTON_RECTANGLE_WIDTH, MENU_BUTTON_RECTANGLE_HEIGHT),
				menubarBottom = new Rectangle(MENU_BUTTON_RECTANGLE_WIDTH, MENU_BUTTON_RECTANGLE_HEIGHT);
		menubarTop.setLayoutX(MENU_BUTTON_RECTANGLE_X);
		menubarBottom.setLayoutX(MENU_BUTTON_RECTANGLE_X);
		menubarTop.setLayoutY(MENU_BUTTON_Y);
		menubarBottom.setLayoutY(MENU_BUTTON_RECTANGLE_SPACING + MENU_BUTTON_Y);

		menubarTop.setStroke(MENU_BUTTON_START_COLOR);
		menubarTop.setFill(MENU_BUTTON_START_COLOR);
		menubarBottom.setStroke(MENU_BUTTON_START_COLOR);
		menubarBottom.setFill(MENU_BUTTON_START_COLOR);

		menubarTop.setEffect(new DropShadow(MENU_BAR_SHADOW_RADIUS, MENU_BAR_SHADOW_COLOR));
		menubarBottom.setEffect(new DropShadow(MENU_BAR_SHADOW_RADIUS, MENU_BAR_SHADOW_COLOR));

		final FillTransition topFill = new FillTransition(MENU_BUTTON_ANIMATION_DURATION, menubarTop),
				bottomFill = new FillTransition(MENU_BUTTON_ANIMATION_DURATION, menubarBottom);
		topFill.setInterpolator(MENU_BUTTON_TRANSITION_INTERPOLATOR);
		bottomFill.setInterpolator(MENU_BUTTON_TRANSITION_INTERPOLATOR);

		final RotateTransition topRot = new RotateTransition(MENU_BUTTON_ANIMATION_DURATION, menubarTop),
				bottomRot = new RotateTransition(MENU_BUTTON_ANIMATION_DURATION, menubarBottom);
		topRot.setInterpolator(MENU_BUTTON_TRANSITION_INTERPOLATOR);
		bottomRot.setInterpolator(MENU_BUTTON_TRANSITION_INTERPOLATOR);

		final TranslateTransition topTrans = new TranslateTransition(MENU_BUTTON_ANIMATION_DURATION, menubarTop),
				bottomTrans = new TranslateTransition(MENU_BUTTON_ANIMATION_DURATION, menubarBottom);

		final MenuBox menu = new MenuBox(MENU_ITEM_SPACING);
		menu.setPrefSize(Kröw.getSystemProperties().getScreenWidth() / MENU_WIDTH_FRACTION,
				Kröw.getSystemProperties().getScreenHeight());
		menu.setLayoutX(Kröw.getSystemProperties().getScreenWidth());
		menu.setLayoutY(0);
		menu.setBackground(new Background(new BackgroundFill(MENU_BACKGROUND_COLOR, null, null)));
		menu.setId("GUIH-SideMenu");
		menu.setStyle("-fx-padding: " + SIDE_MENU_PADDING_TOP + "px 0px 0px 0px; -fx-alignment: top-center;");
		menu.getChildren().addListener((ListChangeListener<Node>) c -> {
			while (c.next())
				if (c.wasAdded())
					for (final Node n : c.getAddedSubList())
						try {
							assert n instanceof Text;// This is caught in
														// actual runtime
														// below if it's
														// thrown.
							final Text t = (Text) n;

							t.setTextAlignment(TextAlignment.CENTER);

							final Color startColor = t instanceof MenuOption ? ((MenuOption) t).getStartColor()
									: MENU_CHILD_NODE_START_COLOR,
									endColor = t instanceof MenuOption ? ((MenuOption) t).getFadeColor()
											: MENU_CHILD_NODE_HOVER_ANIMATION_END_COLOR;
							t.setFill(startColor);
							t.setFont(
									Font.font(Font.getDefault().getName(), FontWeight.BOLD, MENU_CHILD_NODE_FONT_SIZE));

							final FillTransition ft = new FillTransition(
									DEFAULT_MENU_CHILD_NODE_HOVER_ANIMATION_DURATION, t);
							ft.setInterpolator(MENU_CHILD_TRANSITION_INTERPOLATOR);
							t.setOnMouseEntered(event -> {
								Kröw.getSoundManager().playSound(Kröw.getSoundManager().TICK);
								ft.stop();
								ft.setDuration(Kröw.getProgramSettings().getCurrentAnimationMode() == 0
										? MENU_BUTTON_ANIMATION_DURATION
										: MENU_CHILD_NODE_HOVER_ANIMATION_ENTER_DURATION);
								ft.setFromValue((Color) t.getFill());
								ft.setToValue(endColor);
								ft.play();
							});
							t.setOnMouseExited(event -> {
								ft.stop();
								ft.setDuration(Kröw.getProgramSettings().getCurrentAnimationMode() == 0
										? MENU_BUTTON_ANIMATION_DURATION
										: MENU_CHILD_NODE_HOVER_ANIMATION_EXIT_DURATION);
								ft.setFromValue((Color) t.getFill());
								ft.setToValue(startColor);
								ft.play();
							});
						} catch (final ClassCastException e) {
						}
		});

		final TranslateTransition menuSlide = new TranslateTransition(MENU_BUTTON_ANIMATION_DURATION, menu);
		menuSlide.setInterpolator(MENU_BUTTON_TRANSITION_INTERPOLATOR);

		final AnchorPane cover = new AnchorPane();

		cover.setPrefSize(MENU_BUTTON_RECTANGLE_WIDTH * 2 + MENU_BUTTON_RECTANGLE_SPACING,
				MENU_BUTTON_RECTANGLE_WIDTH * 2 + MENU_BUTTON_RECTANGLE_SPACING);
		cover.setLayoutX(Kröw.getSystemProperties().getScreenWidth() - cover.getPrefWidth());
		cover.setLayoutY(0);

		new Object() {
			private boolean closing, opening;

			{

				final EventHandler<? super MouseEvent> enterHandler = event -> {
					if (event.getPickResult().getIntersectedNode() == cover && opening)
						return;
					Kröw.getSoundManager().playSound(Kröw.getSoundManager().TICK);
					open();
				};

				final EventHandler<MouseEvent> exitHandler = event -> {
					if (event.getPickResult().getIntersectedNode() == menu
							|| event.getPickResult().getIntersectedNode() == cover
							|| event.getPickResult().getIntersectedNode().getParent() == menu || closing
							|| isDeepChildOf(event.getPickResult().getIntersectedNode(), menu))
						return;
					close();

				};

				final EventHandler<MouseEvent> coverClickHandler = event -> {
					if (opening) {
						close();
						Kröw.getSoundManager().playSound(Kröw.getSoundManager().TICK);
						return;
					}
					if (closing) {
						open();
						Kröw.getSoundManager().playSound(Kröw.getSoundManager().TICK);
					}

				};

				cover.setOnMouseEntered(enterHandler);

				cover.setOnMouseExited(exitHandler);
				menu.setOnMouseExited(exitHandler);

				cover.setOnMousePressed(coverClickHandler);

			}

			private void close() {
				closing = true;
				opening = false;
				topFill.stop();
				bottomFill.stop();
				topRot.stop();
				bottomRot.stop();
				topTrans.stop();
				bottomTrans.stop();
				menuSlide.stop();

				topFill.setFromValue((Color) menubarTop.getFill());
				bottomFill.setFromValue((Color) menubarBottom.getFill());
				topFill.setToValue(MENU_BUTTON_START_COLOR);
				bottomFill.setToValue(MENU_BUTTON_START_COLOR);

				topRot.setFromAngle(menubarTop.getRotate());
				bottomRot.setFromAngle(menubarBottom.getRotate());
				topRot.setToAngle(0);
				bottomRot.setToAngle(0);

				topTrans.setFromY(menubarTop.getTranslateY());
				bottomTrans.setFromY(menubarBottom.getTranslateY());
				topTrans.setToY(0);
				bottomTrans.setToY(0);

				menuSlide.setFromX(menu.getTranslateX());
				menuSlide.setToX(0);

				topFill.play();
				bottomFill.play();
				topRot.play();
				bottomRot.play();
				topTrans.play();
				bottomTrans.play();
				menuSlide.play();
			}

			private void open() {
				closing = false;
				opening = true;
				topFill.stop();
				bottomFill.stop();
				topRot.stop();
				bottomRot.stop();
				topTrans.stop();
				bottomTrans.stop();
				menuSlide.stop();

				topFill.setFromValue((Color) menubarTop.getFill());
				bottomFill.setFromValue((Color) menubarBottom.getFill());
				topFill.setToValue(MENU_BUTTON_END_COLOR);
				bottomFill.setToValue(MENU_BUTTON_END_COLOR);

				topRot.setFromAngle(menubarTop.getRotate());
				bottomRot.setFromAngle(menubarBottom.getRotate());
				final double rotationCount = 45 + MENU_BAR_ANIMATION_ROTATION_COUNT * 180;
				topRot.setToAngle(rotationCount);
				bottomRot.setToAngle(-rotationCount);

				topTrans.setFromY(menubarTop.getTranslateY());
				bottomTrans.setFromY(menubarBottom.getTranslateY());
				topTrans.setToY(MENU_BUTTON_RECTANGLE_SPACING / 2);
				bottomTrans.setToY(-MENU_BUTTON_RECTANGLE_SPACING / 2);

				menuSlide.setFromX(menu.getTranslateX());
				menuSlide.setToX(-menu.getPrefWidth());

				topFill.play();
				bottomFill.play();
				topRot.play();
				bottomRot.play();
				topTrans.play();
				bottomTrans.play();
				menuSlide.play();
			}
		};

		// Children will be added at the end.

		final AnchorPane wrapper = new AnchorPane();

		wrapper.setPickOnBounds(false);
		wrapper.setEventDispatcher(null);

		wrapper.setBackground(null);

		wrapper.getChildren().add(menu);
		wrapper.getChildren().add(menubarTop);
		wrapper.getChildren().add(menubarBottom);
		wrapper.getChildren().add(cover);

		menu.setParentWrapper(wrapper);

		pane.getChildren().add(wrapper);

		final ImageView github = new ImageView("/krow/resources/graphics/github120px.png");
		github.setFitHeight(Kröw.scaleHeight(40));
		github.setFitWidth(Kröw.scaleWidth(40));
		github.setPickOnBounds(true);
		github.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton().equals(MouseButton.PRIMARY))
				Kröw.openLink("https://github.com/Zeale/Krow");
		});

		PopupHelper.buildHoverPopup(github, GUIHelper.makeBoldLabel("GitHub", 18));
		final Label authorAccount = new Label("Author"), programPage = new Label("Program");
		authorAccount.setOnMouseClicked(event -> {
			if (event.getButton().equals(MouseButton.PRIMARY))
				Kröw.openLink("https://github.com/Zeale");
		});
		programPage.setOnMouseClicked(event -> {
			if (event.getButton().equals(MouseButton.PRIMARY))
				Kröw.openLink("https://github.com/Zeale/Krow");

		});
		PopupHelper.buildRightClickPopup(github, authorAccount, programPage);

		final ImageView cookie = new ImageView("/krow/resources/graphics/cookie256px.png");
		cookie.setFitHeight(Kröw.scaleHeight(40));
		cookie.setFitWidth(Kröw.scaleWidth(40));
		cookie.setPickOnBounds(true);
		cookie.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton().equals(MouseButton.PRIMARY))
				Kröw.openLink("https://github.com/Zeale/Cookie/releases");
		});

		PopupHelper.buildHoverPopup(cookie, GUIHelper.makeBoldLabel("Cookie", 18));

		final ImageView cookiep = new ImageView("/krow/resources/graphics/cookie+256px.png");
		cookiep.setFitHeight(Kröw.scaleHeight(40));
		cookiep.setFitWidth(Kröw.scaleWidth(40));
		cookiep.setPickOnBounds(true);
		cookiep.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton().equals(MouseButton.PRIMARY))
				Kröw.openLink("https://github.com/Zeale/Cookie-plus/releases");
		});

		PopupHelper.buildHoverPopup(cookiep, Color.BLUE, "Cookie+");

		final ImageView krow = new ImageView("/krow/resources/Kröw_hd.png");
		krow.setFitHeight(Kröw.scaleHeight(40));
		krow.setFitWidth(Kröw.scaleWidth(40));
		krow.setPickOnBounds(true);
		krow.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton().equals(MouseButton.PRIMARY))
				Kröw.openLink("https://github.com/Zeale/Krow/releases");
		});

		PopupHelper.buildHoverPopup(krow, Color.RED, "Updates");

		final HBox iconsBox = new HBox(Kröw.scaleWidth(5), github, krow, cookie, cookiep);
		iconsBox.setAlignment(Pos.CENTER);
		iconsBox.setTranslateY(Kröw.scaleHeight(800));
		menu.getChildren().add(iconsBox);

		return menu;

	}

	public static boolean isDeepChildOf(final Node child, final Parent parent) {
		for (final Node n : parent.getChildrenUnmodifiable())
			if (n == child)
				return true;
			else if (n instanceof Parent)
				if (isDeepChildOf(child, (Parent) n))
					return true;
		return false;
	}

	public static Label makeBoldLabel(final String text, final double fontSize) {
		final Label lbl = new Label(text);
		lbl.setFont(Font.font(lbl.getFont().getFamily(), FontWeight.BOLD, fontSize));
		return lbl;
	}

	public static Label makeLabel(final String text, final double fontSize) {
		final Label lbl = new Label(text);
		lbl.setFont(Font.font(lbl.getFont().getFamily(), fontSize));
		return lbl;
	}

}
