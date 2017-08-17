package zeale.guis;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import kröw.core.managers.WindowManager;
import kröw.core.managers.WindowManager.Page;

public class SoundSynthesizer extends WindowManager.Page {
	private static final Color GRID_EMPTY_NOTE_COLOR = new Color(1, 0, 0, 0.2);

	private static Synthesizer synthesizer;

	@FXML
	private AnchorPane pane;
	@FXML
	private GridPane noteGrid;

	private boolean snapToGrid;

	private static final int NOTE_GRID_PITCH_RANGE = 10, NOTE_GRID_DEFAULT_LENGTH = 20;

	private static final int NOTE_ICON_HEIGHT = 25, NOTE_ICON_WIDTH = 35;

	private static final double NOTE_GRIDCELL_BORDER_WIDTH = 0.5;

	@Override
	public String getWindowFile() {
		return "SoundSynthesizer.fxml";
	}

	@Override
	public void initialize() {
		try {
			synthesizer = MidiSystem.getSynthesizer();
			setAvailable();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
			setUnavailable();
			return;
		}

		noteGrid.setSnapToPixel(true);
		noteGrid.setGridLinesVisible(false);

		// Set our grid to its default size.
		while (getRowSize() < NOTE_GRID_PITCH_RANGE)
			addRow(0);
		while (getColumnSize() < NOTE_GRID_DEFAULT_LENGTH)
			addColumn(0);

		GUIHelper.addDefaultSettings(GUIHelper.buildMenu(pane));

		int originalSize = noteGrid.getRowConstraints().size();
		noteGrid.getRowConstraints().clear();
		for (; originalSize > 0; originalSize--)
			noteGrid.getRowConstraints().add(new RowConstraints(NOTE_ICON_HEIGHT));

		originalSize = noteGrid.getColumnConstraints().size();
		noteGrid.getColumnConstraints().clear();
		for (; originalSize > 0; originalSize--)
			noteGrid.getColumnConstraints().add(new ColumnConstraints(NOTE_ICON_WIDTH));

		for (int i = 0; i < getRowSize(); i++)
			for (int j = 0; j < getColumnSize(); j++) {
				Pane p = new Pane();
				p.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, null,
						new BorderWidths(NOTE_GRIDCELL_BORDER_WIDTH))));
				p.setBackground(new Background(new BackgroundFill(GRID_EMPTY_NOTE_COLOR, null, null)));
				noteGrid.add(p, j, i);
			}
	}

	private void addRow(int index, Node... children) {
		noteGrid.addRow(index, children);
		noteGrid.getRowConstraints().add(index, new RowConstraints(NOTE_ICON_HEIGHT));
	}

	private int getRowSize() {
		return noteGrid.getRowConstraints().size();
	}

	private int getColumnSize() {
		return noteGrid.getColumnConstraints().size();
	}

	private void addColumn(int index, Node... children) {
		noteGrid.addColumn(index, children);
		noteGrid.getColumnConstraints().add(index, new ColumnConstraints(NOTE_ICON_WIDTH));
	}

	private void setUnavailable() {
		pane.getChildren().add(new Label("Synthesizer Unavailable"));
	}

	private void setAvailable() throws MidiUnavailableException {
		synthesizer.open();
	}

	@Override
	public boolean canSwitchScenes(Class<? extends Page> newSceneClass) {
		synthesizer.close();
		return super.canSwitchScenes(newSceneClass);
	}

}
