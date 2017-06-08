package krow.zeale.guis.home;

import java.io.IOException;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
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
import wolf.zeale.collections.ObservableListWrapper;
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

	static {
		HomeWindow.fileManager = new FileManager();
	}
	/**
	 * <p>
	 * The {@link FileManager} which is opened when the user selects any of the
	 * {@link Button}'s under the <code>File</code> tab in the {@link #menuBar}.
	 * <p>
	 * Each {@link Button} under the <code>File</code> tab will call its
	 * corresponding method when clicked. These methods will then do what they
	 * need and then call this {@link FileManager}'s {@link FileManager#show()
	 * show()} or {@link FileManager#show(javafx.scene.control.Tab)
	 * show(javafx.scene.control.Tab)} method.
	 * <p>
	 * The methods that will open this {@link FileManager}:
	 * <ul>
	 * <li>{@link #backup()}</li>
	 * <li>{@link #exportFile()}</li>
	 * <li>{@link #importFile()}</li>
	 * <li>{@link #restore()}</li>
	 * </ul>
	 *
	 */
	private static FileManager fileManager;

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
	 * <p>
	 * Called when the user presses the <code>Backup</code> button in the
	 * {@link #menuBar} under the <code>File</code> tab.
	 * <p>
	 * If the {@link #fileManager} is not showing, this method will show it and
	 * set its currently selected tab to {@link FileManager#BACKUP}. If the
	 * {@link #fileManager} is showing, this method will simply change its
	 * selected tab to {@link FileManager#BACKUP}.
	 * <p>
	 * This method will then pull the {@link #fileManager} in front of other
	 * applications and center it on the screen.
	 */
	@FXML
	private void backup() {
		if (!HomeWindow.fileManager.show(HomeWindow.fileManager.BACKUP))
			HomeWindow.fileManager.setTab(HomeWindow.fileManager.BACKUP);
		HomeWindow.fileManager.toFront();
		HomeWindow.fileManager.centerOnScreen();
	}

	/**
	 * <p>
	 * Called when the user presses the <code>Export</code> button in the
	 * {@link #menuBar} under the <code>File</code> tab.
	 * <p>
	 * If the {@link #fileManager} is not showing, this method will show it and
	 * set its currently selected tab to {@link FileManager#EXPORT}. If the
	 * {@link #fileManager} is showing, this method will simply change its
	 * selected tab to {@link FileManager#EXPORT}.
	 * <p>
	 * This method will then pull the {@link #fileManager} in front of other
	 * applications and center it on the screen.
	 */
	@FXML
	private void exportFile() {
		if (!HomeWindow.fileManager.show(HomeWindow.fileManager.EXPORT))
			HomeWindow.fileManager.setTab(HomeWindow.fileManager.EXPORT);
		HomeWindow.fileManager.toFront();
		HomeWindow.fileManager.centerOnScreen();
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

	/**
	 * <p>
	 * Called when the user presses the <code>Import</code> button in the
	 * {@link #menuBar} under the <code>File</code> tab.
	 * <p>
	 * If the {@link #fileManager} is not showing, this method will show it and
	 * set its currently selected tab to {@link FileManager#IMPORT}. If the
	 * {@link #fileManager} is showing, this method will simply change its
	 * selected tab to {@link FileManager#IMPORT}.
	 * <p>
	 * This method will then pull the {@link #fileManager} in front of other
	 * applications and center it on the screen.
	 */
	@FXML
	private void importFile() {
		if (!HomeWindow.fileManager.show(HomeWindow.fileManager.IMPORT))
			HomeWindow.fileManager.setTab(HomeWindow.fileManager.IMPORT);
		HomeWindow.fileManager.toFront();
		HomeWindow.fileManager.centerOnScreen();
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

		constructs.setItems(new ObservableListWrapper<>(Kröw.CONSTRUCT_MINDSET.getConstructs()));
		laws.setItems(new ObservableListWrapper<>(Kröw.CONSTRUCT_MINDSET.getLaws()));
		constDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
		constName.setCellValueFactory(new PropertyValueFactory<>("name"));
		lawDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
		lawName.setCellValueFactory(new PropertyValueFactory<>("name"));

		constructCount.setTextAlignment(TextAlignment.CENTER);

		// Now lets set up some timelines for animations...

		constructCount.setText(String.valueOf(Kröw.CONSTRUCT_MINDSET.getConstructsUnmodifiable().size()));

		final Duration animationDuration = Duration.seconds(0.8);
		final FillTransition constCountRed = new FillTransition(animationDuration, constructCount, Color.RED,
				Color.GOLD),
				constCountYellow = new FillTransition(animationDuration, constructCount, Color.GOLD, Color.GREEN),
				constCountGreen = new FillTransition(animationDuration, constructCount, Color.GREEN, Color.BLUE),
				constCountBlue = new FillTransition(animationDuration, constructCount, Color.BLUE, Color.RED);
		final SequentialTransition constructCountSequence = new SequentialTransition(constCountRed, constCountYellow,
				constCountGreen, constCountBlue);
		constructCountSequence.setCycleCount(Animation.INDEFINITE);
		constructCountSequence.play();

		final FillTransition constTextRed = new FillTransition(animationDuration, constructText, Color.RED, Color.GOLD),
				constTextYellow = new FillTransition(animationDuration, constructText, Color.GOLD, Color.GREEN),
				constTextGreen = new FillTransition(animationDuration, constructText, Color.GREEN, Color.BLUE),
				constTextBlue = new FillTransition(animationDuration, constructText, Color.BLUE, Color.RED);
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
			Pages.openPage(Kröw.CONSTRUCT_MINDSET.getConstructsUnmodifiable()
					.get((int) (Math.random() * Kröw.CONSTRUCT_MINDSET.getConstructsUnmodifiable().size())));
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

	/*
	 * (non-Javadoc)
	 *
	 * @see wolf.zeale.guis.Window#onRevertToThisWindow()
	 */
	@Override
	public void onRevertToThisWindow() {
		constructs.refresh();
		laws.refresh();
	}

	/**
	 * <p>
	 * Called when the user presses the <code>Restore</code> button in the
	 * {@link #menuBar} under the <code>File</code> tab.
	 * <p>
	 * If the {@link #fileManager} is not showing, this method will show it and
	 * set its currently selected tab to {@link FileManager#RESTORE}. If the
	 * {@link #fileManager} is showing, this method will simply change its
	 * selected tab to {@link FileManager#RESTORE}.
	 * <p>
	 * This method will then pull the {@link #fileManager} in front of other
	 * applications and center it on the screen.
	 */
	@FXML
	private void restore() {
		if (!HomeWindow.fileManager.show(HomeWindow.fileManager.RESTORE))
			HomeWindow.fileManager.setTab(HomeWindow.fileManager.RESTORE);
		HomeWindow.fileManager.toFront();
		HomeWindow.fileManager.centerOnScreen();
	}

}
