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
	protected TextFlow console;
	@FXML
	protected TextArea input;
	@FXML
	protected AnchorPane root;
	@FXML
	protected Button send;

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
		printRawText(text);
	}

	public void clear() {
		console.getChildren().clear();
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

	public void printRawText(Text text) {
		console.getChildren().add(text);
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
