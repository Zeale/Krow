package krow.zeale.guis.home;

import java.io.IOException;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import krow.zeale.guis.create.construct.CreateConstructWindow;
import krow.zeale.guis.management.ConstructManagerWindow;
import kröw.libs.Construct;
import kröw.libs.Law;
import kröw.zeale.v1.program.core.Kröw;
import kröw.zeale.v1.program.guis.Window;

public class HomeWindow extends Window {

	@FXML
	private Text constructCount;
	@FXML
	private Text constructText;

	@FXML
	private AnchorPane centerPane;

	@FXML
	private ScrollPane bottomPane;

	@FXML
	private MenuBar menuBar;

	@FXML
	private TableView<Construct> constructs;
	@FXML
	private TableView<Law> laws;

	@FXML
	private TableColumn<Construct, String> constName, constDesc;
	@FXML
	private TableColumn<Law, String> lawName, lawDesc;

	@FXML
	private ImageView krow;

	private FadeTransition krowFadeInTransition, krowFadeOutTransition;

	@Override
	@SuppressWarnings("deprecation")
	@FXML
	public void initialize() {

		// The window can now be dragged around the screen by the Menu Bar.
		Window.setPaneDraggableByNode(menuBar);
		Window.setPaneDraggableByNode(krow);

		/*
		 * "If it's not intended for use then add a workaround..."
		 *
		 * ~ Zeale
		 *
		 * Here I'm calling a deprecated method because the JavaFX APIs didn't
		 * have a more simple way to make table columns non-reorderable.
		 * <strike>These may or may not work, but it's worth a shot.</strike>
		 * These do work... :)
		 */
		try {
			constDesc.impl_setReorderable(false);
			constName.impl_setReorderable(false);
			lawDesc.impl_setReorderable(false);
			lawName.impl_setReorderable(false);
		} catch (final NoSuchMethodError e) {
			System.err.println(
					"The tables used in the home screen have headers that can be moved around. This is not supported. Because of the version of Java you are running, some availability to stop those headers from being moved is no longer here. This is not a bad thing but be warned that reordering and dragging around table headers MAY cause visual issues or other effects.");
		}

		constructs.setItems(Kröw.INSTANCE.getConstructs());
		laws.setItems(Kröw.INSTANCE.getLaws());
		constDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
		constName.setCellValueFactory(new PropertyValueFactory<>("name"));
		lawDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
		lawName.setCellValueFactory(new PropertyValueFactory<>("name"));

		constructCount.setTextAlignment(TextAlignment.CENTER);

		// Now lets set up some timelines for animations...

		constructCount.setText(String.valueOf(Kröw.INSTANCE.getConstructs().size()));

		final FillTransition constCountRed = new FillTransition(Duration.seconds(0.8), constructCount, Color.RED,
				Color.GOLD),
				constCountYellow = new FillTransition(Duration.seconds(0.8), constructCount, Color.GOLD, Color.GREEN),
				constCountGreen = new FillTransition(Duration.seconds(0.8), constructCount, Color.GREEN, Color.BLUE),
				constCountBlue = new FillTransition(Duration.seconds(0.8), constructCount, Color.BLUE, Color.RED);
		final SequentialTransition constructCountSequence = new SequentialTransition(constCountRed, constCountYellow,
				constCountGreen, constCountBlue);
		constructCountSequence.setCycleCount(Animation.INDEFINITE);
		constructCountSequence.play();

		final FillTransition constTextRed = new FillTransition(Duration.seconds(0.8), constructText, Color.RED,
				Color.GOLD),
				constTextYellow = new FillTransition(Duration.seconds(0.8), constructText, Color.GOLD, Color.GREEN),
				constTextGreen = new FillTransition(Duration.seconds(0.8), constructText, Color.GREEN, Color.BLUE),
				constTextBlue = new FillTransition(Duration.seconds(0.8), constructText, Color.BLUE, Color.RED);
		final SequentialTransition constructTextSequence = new SequentialTransition(constTextRed, constTextYellow,
				constTextGreen, constTextBlue);
		constructTextSequence.setCycleCount(Animation.INDEFINITE);
		constructTextSequence.play();

		// Krow image
		krowFadeInTransition = new FadeTransition(Duration.seconds(0.4), krow);
		krowFadeInTransition.setInterpolator(Interpolator.EASE_OUT);
		krowFadeInTransition.setToValue(1);

		krowFadeOutTransition = new FadeTransition(Duration.seconds(0.4), krow);
		krowFadeOutTransition.setInterpolator(Interpolator.EASE_OUT);
		krowFadeOutTransition.setToValue(0.1);
	}

	@FXML
	private void onChangeIconRequested() {
		if (Window.DARK_CROW == null || Window.LIGHT_CROW == null)
			return;
		else {
			final ObservableList<Image> icons = Window.getStage().getIcons();
			if (icons.contains(Window.DARK_CROW)) {
				icons.remove(Window.DARK_CROW);
				icons.add(Window.LIGHT_CROW);
			} else {
				icons.remove(Window.LIGHT_CROW);
				icons.add(Window.DARK_CROW);
			}
		}
	}

	@FXML
	private void onCloseRequested() {
		Platform.exit();
	}

	@FXML
	private void onGoToCreateConstructWindow() {
		try {
			Window.setScene(CreateConstructWindow.class, "CreateConstructWindow.fxml");
		} catch (final IOException e) {
			System.err.println("Could not open up the Create Construct Window.");

			if (Kröw.DEBUG_MODE) {
				System.out.println("\n\n");
				e.printStackTrace();
			}
		}
	}

	@FXML
	private void onGoToManageConstructWindow() {
		try {
			Window.setScene(ConstructManagerWindow.class, "ConstructManager.fxml");
		} catch (final IOException e) {
			System.err.println("Could not open up the Construct Manager Window.");
			if (Kröw.DEBUG_MODE) {
				System.out.println("\n\n");
				e.printStackTrace();
			}
		}
	}

	@FXML
	private void onMouseEnteredKrowImage() {
		krowFadeInTransition.setFromValue(krow.getOpacity());
		krowFadeInTransition.play();
	}

	@FXML
	private void onMouseExitedKrowImage() {
		krowFadeOutTransition.setFromValue(krow.getOpacity());
		krowFadeOutTransition.play();
	}

}
