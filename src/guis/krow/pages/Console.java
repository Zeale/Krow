package krow.pages;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import kröw.core.managers.WindowManager.Page;

public abstract class Console extends Page {

	public Console() {
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
								t.setFont(Font.font(16));
							}

			}
		});
	}

	protected static void setFont(Text text, Font font) {
		text.getProperties().put(DataKeys.FONT, font);
	}

	private enum DataKeys {
		FONT
	}

}
