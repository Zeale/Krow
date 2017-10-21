package zeale.guis.developer_module;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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
								else
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

	public static final PrintStream out = new PrintStream(new OutputStream() {

		private StringBuffer buffer = new StringBuffer();

		@Override
		public void write(int b) throws IOException {
			try {
				buffer.appendCodePoint(b);
			} catch (Exception e) {
				e.printStackTrace(Kröw.getDefaultErr());
			}
		}

		@Override
		public void flush() throws IOException {
			try {
				texts.add(new Text(buffer.toString()));
				buffer = new StringBuffer();
			} catch (Exception e) {
				e.printStackTrace(Kröw.getDefaultErr());
			}
		};
	}, true), err = new PrintStream(new OutputStream() {

		private StringBuffer buffer = new StringBuffer();

		@Override
		public void write(int b) throws IOException {
			try {
				buffer.appendCodePoint(b);
			} catch (Exception e) {
				e.printStackTrace(Kröw.getDefaultErr());
			}
		}

		@Override
		public void flush() throws IOException {
			try {
				Text text = new Text(buffer.toString());
				setErrorText(text);
				texts.add(text);
				buffer = new StringBuffer();
			} catch (Exception e) {
				e.printStackTrace(Kröw.getDefaultErr());
			}
		};
	}, true);

	private static void setErrorText(Text text) {
		text.getProperties().put(DataKeys.ERROR, true);
	}

	private static boolean isErrorText(Text text) {
		return text.getProperties().containsKey(DataKeys.ERROR)
				&& (boolean) text.getProperties().get(DataKeys.ERROR) == true;
	}

	private enum DataKeys {
		ERROR;
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
