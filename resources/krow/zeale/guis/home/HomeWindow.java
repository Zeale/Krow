package krow.zeale.guis.home;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.List;

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
import javafx.stage.FileChooser;
import javafx.util.Duration;
import krow.zeale.guis.create.construct.CreateConstructWindow;
import krow.zeale.guis.create.law.CreateLawWindow;
import krow.zeale.guis.create.system.CreateSystemWindow;
import krow.zeale.guis.management.constructs.ConstructManagerWindow;
import krow.zeale.guis.management.laws.LawManagerWindow;
import krow.zeale.pages.Pages;
import kröw.zeale.v1.program.core.Kröw;
import wolf.mindset.Construct;
import wolf.mindset.Law;
import wolf.zeale.Wolf;
import wolf.zeale.guis.Window;

/**
 * <p>
 * The Home {@link Window} of the entire application.
 * <p>
 * This {@link Window} is opened automatically when the application is started.
 *
 * @author Zeale
 *
 * @Internal This is an internal class used for controlling the
 *           <code>Home.fxml</code> file.
 *
 */
public class HomeWindow extends Window {

	/**
	 * This is animated, color changing text that displays the amount of
	 * {@link Construct}s that the user has.
	 */
	@FXML
	private Text constructCount;
	/**
	 * A {@link Text} object with unchanging text that forever says
	 * <i>constructs</i>. It is positioned right underneath
	 * {@link #constructCount} and changes colors just like
	 * {@link #constructCount} does.
	 */
	@FXML
	private Text constructText;

	/**
	 * The central {@link AnchorPane}.
	 */
	@FXML
	private AnchorPane centerPane;

	/**
	 * The lower {@link ScrollPane}.
	 */
	@FXML
	private ScrollPane bottomPane;

	/**
	 * The {@link MenuBar} at the top of the {@link Window}.
	 */
	@FXML
	private MenuBar menuBar;

	/**
	 * The {@link TableView} that renders {@link Construct}s and their data.
	 */
	@FXML
	private TableView<Construct> constructs;

	/**
	 * The {@link TableView} that renders {@link Law}s and their data.
	 */
	@FXML
	private TableView<Law> laws;
	/**
	 * <p>
	 * {@link #constName} - The {@link TableColumn} that shows the names of all
	 * the {@link Construct}s.
	 * <p>
	 * {@link #constDesc} - The {@link TableColumn} that shows a description for
	 * all of the {@link Construct}s.
	 */
	@FXML
	private TableColumn<Construct, String> constName, constDesc;

	/**
	 * <p>
	 * {@link #lawName} - The {@link TableColumn} that shows the names of all
	 * the {@link Law}s.
	 * <p>
	 * {@link #lawDesc} - The {@link TableColumn} that shows a description for
	 * each {@link Law}.
	 */
	@FXML
	private TableColumn<Law, String> lawName, lawDesc;
	/**
	 * The picture of the crow in the middle of the {@link Window}.
	 */
	@FXML
	private ImageView krow;

	/**
	 * {@link FadeTransition}s used for the {@link #krow} image when the mouse
	 * hovers over or off of it.
	 */
	private FadeTransition krowFadeInTransition, krowFadeOutTransition;

	/**
	 * This method is called when the user attempts to change the currently
	 * displayed program icon. They can do this by selecting the <i>Options</i>
	 * tab in the {@link MenuBar} and then selecting <i>Change Icon</i>.
	 */
	@FXML
	private void onChangeIconRequested() {
		if (Kröw.DARK_CROW == null || Kröw.LIGHT_CROW == null)
			return;
		else {
			final ObservableList<Image> icons = Window.getStage().getIcons();
			if (icons.contains(Kröw.DARK_CROW)) {
				icons.remove(Kröw.DARK_CROW);
				icons.add(Kröw.LIGHT_CROW);
			} else {
				icons.remove(Kröw.LIGHT_CROW);
				icons.add(Kröw.DARK_CROW);
			}
		}
	}

	/**
	 * Called when the user attempts to close the program.
	 */
	@FXML
	private void onCloseRequested() {
		Platform.exit();
	}

	/**
	 * Called when the user goes to create a {@link Construct}.
	 */
	@FXML
	private void onGoToCreateConstructWindow() {
		try {
			Window.setScene(CreateConstructWindow.class, "CreateConstructWindow.fxml");
		} catch (final IOException e) {
			System.err.println("Could not open up the Construct Creation Window.");

			if (Wolf.DEBUG_MODE) {
				System.out.println("\n\n");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Called when the user goes to create a {@link Law}.
	 */
	@FXML
	private void onGoToCreateLawWindow() {
		try {
			Window.setScene(CreateLawWindow.class, "CreateLawWindow.fxml");
		} catch (final Exception e) {
			System.err.println("Could not open up the Law Creation Window.");

			if (Wolf.DEBUG_MODE) {
				System.out.println("\n\n");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Will be implemented later... Do not use.....
	 */
	@FXML
	private void onGoToCreatePolicyWindow() {
		try {
			Window.setScene(CreateSystemWindow.class, "CreatePolicyWindow.fxml");
		} catch (final IOException e) {
			System.err.println("Could not open up the Policy Creation Window.");

			if (Wolf.DEBUG_MODE) {
				System.out.println("\n\n");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Called when the user goes to create a {@link System}.
	 */
	@FXML
	private void onGoToCreateSystemWindow() {
		try {
			Window.setScene(CreateSystemWindow.class, "CreateSystemWindow.fxml");
		} catch (final IOException e) {
			System.err.println("Could not open up the System Creation Window.");

			if (Wolf.DEBUG_MODE) {
				System.out.println("\n\n");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Called when the user goes to the {@link Construct} management
	 * {@link Window}.
	 */
	@FXML
	private void onGoToManageConstructWindow() {
		try {
			Window.setScene(ConstructManagerWindow.class, "ConstructManager.fxml");
		} catch (final IOException e) {
			System.err.println("Could not open up the Construct Manager Window.");
			if (Wolf.DEBUG_MODE) {
				System.out.println("\n\n");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Called when the user goes to the {@link Law} management {@link Window}.
	 */
	@FXML
	private void onGoToManageLawWindow() {
		try {
			Window.setScene(LawManagerWindow.class, "LawManager.fxml");
		} catch (final IOException e) {
			System.err.println("Could not open up the Law Manager Window.");
			if (Wolf.DEBUG_MODE) {
				System.out.println("\n\n");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Called when the user tries to go to the {@link Pages} window. This only
	 * works in debug mode, since the {@link Pages} API and feature is not yet
	 * ready. At all.
	 */
	@FXML
	private void onGoToPages() {
		if (Wolf.DEBUG_MODE)
			Pages.openPage(Kröw.constructs.getObservableList()
					.get((int) (Math.random() * Kröw.constructs.getObservableList().size())));
	}

	/**
	 * Called when the user's pointer hovers over the {@link #krow} image.
	 */
	@FXML
	private void onMouseEnteredKrowImage() {
		krowFadeInTransition.setFromValue(krow.getOpacity());
		krowFadeInTransition.play();
	}

	/**
	 * Called when the user's pointer exits the {@link #krow} image.
	 */
	@FXML
	private void onMouseExitedKrowImage() {
		krowFadeOutTransition.setFromValue(krow.getOpacity());
		krowFadeOutTransition.play();
	}

	@FXML
	private void openImportFileWindow() {
		final FileChooser chooser = new FileChooser();
		chooser.setTitle("Import a Construct, Law, or any other object...");
		final List<File> files = chooser.showOpenMultipleDialog(Window.getStage());
		for (final File f : files)
			try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(f)) {
				@Override
				protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
					ObjectStreamClass resultClassDescriptor = super.readClassDescriptor();

					if (resultClassDescriptor.getName().equals("kröw.libs.Construct"))
						resultClassDescriptor = ObjectStreamClass.lookup(wolf.mindset.Construct.class);
					else if (resultClassDescriptor.getName().equals("kröw.libs.System"))
						resultClassDescriptor = ObjectStreamClass.lookup(wolf.mindset.System.class);
					else if (resultClassDescriptor.getName().equals("kröw.libs.Law"))
						resultClassDescriptor = ObjectStreamClass.lookup(wolf.mindset.Law.class);
					return resultClassDescriptor;
				}
			};) {

			} catch (final FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see kröw.zeale.v1.program.guis.Window#getWindowFile()
	 */
	@Override
	public String getWindowFile() {
		return "Home.fxml";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see kröw.zeale.v1.program.guis.Window#initialize()
	 */
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

		constructs.setItems(Kröw.constructs.getObservableList());
		laws.setItems(Kröw.laws.getObservableList());
		constDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
		constName.setCellValueFactory(new PropertyValueFactory<>("name"));
		lawDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
		lawName.setCellValueFactory(new PropertyValueFactory<>("name"));

		constructCount.setTextAlignment(TextAlignment.CENTER);

		// Now lets set up some timelines for animations...

		constructCount.setText(String.valueOf(Kröw.constructs.getObservableList().size()));

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

}
