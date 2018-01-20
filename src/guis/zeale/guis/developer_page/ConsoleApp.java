package zeale.guis.developer_page;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import krow.guis.GUIHelper;
import krow.pages.TextPanel;
import kröw.core.Kröw;

public class ConsoleApp extends TextPanel {

	public static class ConsoleStream extends PrintStream {

		private static class ConsoleOutputStream extends OutputStream {

			private StringBuffer buffer = new StringBuffer();
			private ConsoleStream stream;

			@Override
			public void flush() throws IOException {
				try {
					final Text text = new Text(buffer.toString());
					stream.formatText(text);
					texts.add(text);
					buffer = new StringBuffer();
				} catch (final Exception e) {
					e.printStackTrace(Kröw.deferr);
				}
			}

			// A little workaround for not being able to access instance methods
			// while calling constructor.
			private void setConsoleStream(final ConsoleStream cs) {
				stream = cs;
			};

			@Override
			public void write(final int b) throws IOException {
				try {
					buffer.appendCodePoint(b);
				} catch (final Exception e) {
					e.printStackTrace(Kröw.deferr);
				}
			}

		}

		private ConsoleStream() {
			super(new ConsoleOutputStream());
			((ConsoleOutputStream) out).setConsoleStream(this);
		}

		protected void formatText(final Text text) {
		}
	}

	private enum DataKeys {
		TYPE;
	}

	private enum TextType {
		ERROR, SUCCESS, WARNING, STANDARD;
	}

	private static List<Text> texts;

	private static ConsoleApp currentApp;

	static {
		final ObservableList<Text> texts = FXCollections.observableArrayList();
		texts.addListener((ListChangeListener<Text>) c -> {
			if (currentApp != null)
				while (c.next())
					if (c.wasAdded())
						for (final Text t : c.getAddedSubList())
							if (isErrorText(t))
								currentApp.printerr(t);
							else if (getTextType(t) == TextType.SUCCESS) {
								t.setFill(Color.GREEN);
								currentApp.printRawText(t);
							} else if (getTextType(t) == TextType.WARNING) {
								t.setFill(Color.GOLD);
								currentApp.printRawText(t);
							} else
								currentApp.print(t);

		});

		ConsoleApp.texts = texts;

	}

	public static final PrintStream out = new ConsoleStream(), err = new ConsoleStream() {
		@Override
		protected void formatText(final Text text) {
			setTextType(text, TextType.ERROR);
		}
	}, scs = new ConsoleStream() {
		@Override
		protected void formatText(final Text text) {
			setTextType(text, TextType.SUCCESS);
		}
	}, wrn = new ConsoleStream() {
		@Override
		protected void formatText(final Text text) {
			setTextType(text, TextType.WARNING);
		}
	};

	private static TextType getTextType(final Text text) {
		return text.getProperties().containsKey(DataKeys.TYPE) ? (TextType) text.getProperties().get(DataKeys.TYPE)
				: null;
	}

	private static boolean isErrorText(final Text text) {
		return text.getProperties().containsKey(DataKeys.TYPE)
				&& (TextType) text.getProperties().get(DataKeys.TYPE) == TextType.ERROR;
	}

	private static void setTextType(final Text text, final TextType type) {
		text.getProperties().put(DataKeys.TYPE, type);
	}

	public void dispose() {
		if (currentApp == this)
			currentApp = null;
	}

	@Override
	protected void formatText(final Text text) {
	}

	@Override
	public String getWindowFile() {
		return "ConsoleApp.fxml";
	}

	@Override
	public void initialize() {
		super.initialize();
		setCurrentApp();
		GUIHelper.addDefaultSettings(GUIHelper.buildMenu(root));
	}

	@Override
	protected void onPageSwitched() {
		dispose();
	}

	private void setCurrentApp() throws RuntimeException {
		if (currentApp != null)
			throw new RuntimeException("Undisposed ConsoleApp already in use.");
		else
			currentApp = this;
		for (final Text t : texts)
			if (isErrorText(t))
				printerr(t);
			else
				print(t);
	}

}
