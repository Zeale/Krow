package krow.pages;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import kröw.core.managers.WindowManager.Page;

public abstract class TextPanel extends Page {

	public TextPanel() {
	}

	private Font defaultFont = Font.font(16);
	private Color defaultColor = Color.WHITE;

	/**
	 * @return the defaultFont
	 */
	protected Font getDefaultFont() {
		return defaultFont;
	}

	/**
	 * @param defaultFont
	 *            the defaultFont to set
	 */
	protected void setDefaultFont(Font defaultFont) {
		this.defaultFont = defaultFont;
	}

	/**
	 * @return the defaultColor
	 */
	protected Color getDefaultColor() {
		return defaultColor;
	}

	/**
	 * @param defaultColor
	 *            the defaultColor to set
	 */
	protected void setDefaultColor(Color defaultColor) {
		this.defaultColor = defaultColor;
	}

	@FXML
	private TextFlow console;

	@Override
	public void initialize() {
		if (console == null)
			console = new TextFlow();
		console.getChildren().addListener(new ListChangeListener<Node>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Node> c) {
				while (c.next())
					if (c.wasAdded())
						for (Node n : c.getAddedSubList())
							if (n instanceof Text) {
								Text t = (Text) n;
								if (t.getProperties().containsKey(DataKeys.FONT))
									t.setFont((Font) t.getProperties().get(DataKeys.FONT));
								else
									t.setFont(defaultFont);
								if (t.getProperties().containsKey(DataKeys.COLOR))
									t.setFill((Paint) t.getProperties().get(DataKeys.COLOR));
								else
									t.setFill(defaultColor);
							}

			}
		});
	}

	public final void print(String text) {
		print(new Text(text));
	}

	public final void print(Text text) {
		formatText(text);
		console.getChildren().add(text);
	}

	protected abstract void formatText(Text text);

	public final void println(String text) {
		print(text + "\n");
	}

	public final void println(Text text) {
		text.setText(text.getText() + "\n");
		print(text);
	}

	public void printerr(Text error) {
		setColor(error, Color.FIREBRICK);
		print(error);
	}

	public void printerr(String error) {
		printerr(new Text(error));
	}

	public void printerrln(String error) {
		printerr(error + "\n");
	}

	public void printerrln(Text text) {
		text.setText(text.getText() + "\n");
		printerr(text);
	}

	protected static final void setFont(Text text, Font font) {
		text.getProperties().put(DataKeys.FONT, font);
	}

	protected static final void setColor(Text text, Color color) {
		text.getProperties().put(DataKeys.COLOR, color);
	}

	private enum DataKeys {
		FONT, COLOR
	}

}
