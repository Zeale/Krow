package zeale.guis.developer_module;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import krow.pages.TextPanel;
import kröw.core.Kröw;

public class ConsoleModule extends TextPanel {

	private static List<Text> texts;
	static {
		ObservableList<Text> texts = FXCollections.observableArrayList();
		texts.addListener(new ListChangeListener<Text>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Text> c) {
				if (currentModule != null)
					while (c.next())
						if (c.wasAdded())
							for (Text t : c.getAddedSubList())
								if (isErrorText(t))
									currentModule.printerr(t);
								else if (getTextType(t) == TextType.SUCCESS) {
									t.setFill(Color.GREEN);
									currentModule.printRawText(t);
								} else if (getTextType(t) == TextType.WARNING) {
									t.setFill(Color.GOLD);
									currentModule.printRawText(t);
								} else
									currentModule.print(t);

			}

		});

		ConsoleModule.texts = texts;

	}

	private static ConsoleModule currentModule;

	@Override
	protected void onPageSwitched() {
		dispose();
	}

	public static class ConsoleStream extends PrintStream {

		private static class ConsoleOutputStream extends OutputStream {

			private StringBuffer buffer = new StringBuffer();
			private ConsoleStream stream;

			@Override
			public void write(int b) throws IOException {
				try {
					buffer.appendCodePoint(b);
				} catch (Exception e) {
					e.printStackTrace(Kröw.deferr);
				}
			}

			@Override
			public void flush() throws IOException {
				try {
					Text text = new Text(buffer.toString());
					stream.formatText(text);
					texts.add(text);
					buffer = new StringBuffer();
				} catch (Exception e) {
					e.printStackTrace(Kröw.deferr);
				}
			};

			// A little workaround for not being able to access instance methods
			// while calling constructor.
			private void setConsoleStream(ConsoleStream cs) {
				stream = cs;
			}

		}

		private ConsoleStream() {
			super(new ConsoleOutputStream());
			((ConsoleOutputStream) out).setConsoleStream(this);
		}

		protected void formatText(Text text) {
		}
	}

	public static final PrintStream out = new ConsoleStream(), err = new ConsoleStream() {
		@Override
		protected void formatText(Text text) {
			setTextType(text, TextType.ERROR);
		}
	}, scs = new ConsoleStream() {
		@Override
		protected void formatText(Text text) {
			setTextType(text, TextType.SUCCESS);
		}
	}, wrn = new ConsoleStream() {
		@Override
		protected void formatText(Text text) {
			setTextType(text, TextType.WARNING);
		}
	};

	private static void setTextType(Text text, TextType type) {
		text.getProperties().put(DataKeys.TYPE, type);
	}

	private static TextType getTextType(Text text) {
		return text.getProperties().containsKey(DataKeys.TYPE) ? (TextType) text.getProperties().get(DataKeys.TYPE)
				: null;
	}

	private static boolean isErrorText(Text text) {
		return text.getProperties().containsKey(DataKeys.TYPE)
				&& (TextType) text.getProperties().get(DataKeys.TYPE) == TextType.ERROR;
	}

	private enum DataKeys {
		TYPE;
	}

	private enum TextType {
		ERROR, SUCCESS, WARNING, STANDARD;
	}

	@Override
	public void initialize() {
		super.initialize();
		setCurrentModule();
	}

	@Override
	public String getWindowFile() {
		return "ConsoleModule.fxml";
	}

	@Override
	protected void formatText(Text text) {
	}

	public void dispose() {
		if (currentModule == this)
			currentModule = null;
	}

	private void setCurrentModule() throws RuntimeException {
		if (currentModule != null)
			throw new RuntimeException("Undisposed ConsoleModule already in use.");
		else
			currentModule = this;
		for (Text t : texts)
			if (isErrorText(t))
				printerr(t);
			else
				print(t);
	}

}
