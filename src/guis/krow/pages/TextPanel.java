package krow.pages;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import kröw.core.Kröw;
import kröw.core.managers.WindowManager.App;

public abstract class TextPanel extends App {

	private enum DataKeys {
		FONT, COLOR
	}

	protected static final void setColor(final Text text, final Color color) {
		text.getProperties().put(DataKeys.COLOR, color);
	}

	protected static final void setFont(final Text text, final Font font) {
		text.getProperties().put(DataKeys.FONT, font);
	}

	private Font defaultFont = Font.font(16);

	private Color defaultColor = Color.WHITE;

	@FXML
	protected TextFlow console;

	@FXML
	protected TextArea input;

	@FXML
	protected AnchorPane root;
	@FXML
	protected Button send;

	public TextPanel() {
	}

	public void clear() {
		console.getChildren().clear();
	}

	protected abstract void formatText(Text text);

	/**
	 * @return the defaultColor
	 */
	protected Color getDefaultColor() {
		return defaultColor;
	}

	/**
	 * @return the defaultFont
	 */
	protected Font getDefaultFont() {
		return defaultFont;
	}

	@Override
	public void initialize() {
		if (console == null) {
			console = new TextFlow();
			console.setPrefHeight(Kröw.scaleHeight(550));
			console.setPrefWidth(Kröw.scaleWidth(1920 - 400));
			console.setLayoutX(Kröw.scaleWidth(200));
			console.setLayoutY(Kröw.scaleHeight(0));
		}
		if (input == null) {
			input = new TextArea();
			input.setPrefWidth(Kröw.scaleWidth(1920));
			input.setPrefHeight(Kröw.scaleHeight(325));
			input.setLayoutX(Kröw.scaleWidth(0));
			input.setLayoutY(Kröw.scaleHeight(755));
		}

		if (root == null)
			throw new RuntimeException(getClass().getName() + "'s scene root is null");

		if (!root.getChildren().contains(console))
			root.getChildren().add(console);
		if (!root.getChildren().contains(input))
			root.getChildren().add(input);

		console.getChildren().addListener((ListChangeListener<Node>) c -> {
			while (c.next())
				if (c.wasAdded())
					for (final Node n : c.getAddedSubList())
						if (n instanceof Text) {
							final Text t = (Text) n;
							if (t.getProperties().containsKey(DataKeys.FONT))
								t.setFont((Font) t.getProperties().get(DataKeys.FONT));
							else
								t.setFont(defaultFont);
							if (t.getProperties().containsKey(DataKeys.COLOR))
								t.setFill((Paint) t.getProperties().get(DataKeys.COLOR));
							else
								t.setFill(defaultColor);
						}

		});

	}

	public final void print(final String text) {
		print(new Text(text));
	}

	public final void print(final Text text) {
		formatText(text);
		printRawText(text);
	}

	public void printerr(final String error) {
		printerr(new Text(error));
	}

	public void printerr(final Text error) {
		setColor(error, Color.FIREBRICK);
		print(error);
	}

	public void printerrln(final String error) {
		printerr(error + "\n");
	}

	public void printerrln(final Text text) {
		text.setText(text.getText() + "\n");
		printerr(text);
	}

	public final void println(final String text) {
		print(text + "\n");
	}

	public final void println(final Text text) {
		text.setText(text.getText() + "\n");
		print(text);
	}

	public void printRawText(final Text text) {
		console.getChildren().add(text);
	}

	/**
	 * @param defaultColor
	 *            the defaultColor to set
	 */
	protected void setDefaultColor(final Color defaultColor) {
		this.defaultColor = defaultColor;
	}

	/**
	 * @param defaultFont
	 *            the defaultFont to set
	 */
	protected void setDefaultFont(final Font defaultFont) {
		this.defaultFont = defaultFont;
	}

}
