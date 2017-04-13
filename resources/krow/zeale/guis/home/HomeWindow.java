package krow.zeale.guis.home;

import java.io.IOException;
import java.util.ArrayList;

import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import krow.zeale.guis.create.construct.CreateConstructWindow;
import kröw.libs.Construct;
import kröw.libs.Law;
import kröw.zeale.v1.program.core.Kröw;
import kröw.zeale.v1.program.guis.Window;

public class HomeWindow extends Window {

	// The following two suppressed fields will be used in the next commit.
	@SuppressWarnings("unused")
	private final Text[][] textSlides = new Text[Kröw.INSTANCE.getConstructs().size()][];
	@SuppressWarnings("unused")
	private final ArrayList<Timeline> timeline = new ArrayList<>();

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
	private void onCloseRequested() {
		Platform.exit();
	}

	@FXML
	private void onGoToCreateConstructWindow() {
		try {
			Window.setScene(CreateConstructWindow.class, "CreateConstructWindow.fxml");
		} catch (final IOException e) {
			System.err.println("Could not open up the Create Construct Window.");
		}
	}

	@Override
	@SuppressWarnings("deprecation")
	@FXML
	public void initialize() {

		// The window can now be dragged around the screen by the Menu Bar.
		Window.setPaneDraggableByNode(menuBar);

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
		constDesc.impl_setReorderable(false);
		constName.impl_setReorderable(false);
		lawDesc.impl_setReorderable(false);
		lawName.impl_setReorderable(false);

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
				Color.YELLOW),
				constCountYellow = new FillTransition(Duration.seconds(0.8), constructCount, Color.YELLOW, Color.GREEN),
				constCountGreen = new FillTransition(Duration.seconds(0.8), constructCount, Color.GREEN, Color.BLUE),
				constCountBlue = new FillTransition(Duration.seconds(0.8), constructCount, Color.BLUE, Color.RED);
		final SequentialTransition constructCountSequence = new SequentialTransition(constCountRed, constCountYellow,
				constCountGreen, constCountBlue);
		constructCountSequence.setCycleCount(Animation.INDEFINITE);
		constructCountSequence.play();

		final FillTransition constructTextBlue = new FillTransition(Duration.seconds(1.5), constructText, Color.BLUE,
				Color.GREEN),
				constructTextGreen = new FillTransition(Duration.seconds(1.5), constructText, Color.GREEN,
						Color.YELLOW),
				constructTextYellow = new FillTransition(Duration.seconds(1.5), constructText, Color.YELLOW, Color.RED),
				constructTextRed = new FillTransition(Duration.seconds(1.5), constructText, Color.RED, Color.BLUE);
		final SequentialTransition constructTextSequence = new SequentialTransition(constructTextBlue,
				constructTextGreen, constructTextYellow, constructTextRed);
		constructTextSequence.setCycleCount(Animation.INDEFINITE);
		constructTextSequence.play();

	}

}
