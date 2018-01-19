package zeale.guis.developer_page;

import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import kröw.data.protection.Protection;
import kröw.gui.WindowManager.App;

public class TestingApp extends App {

	private @FXML Accordion accordion;
	private @FXML BorderPane root;
	private @FXML VBox toolbox;

	private void addTool(Button button) {
		toolbox.getChildren().add(button);
	}

	public TestingApp() {
	}

	@Override
	public void initialize() {
		root.setLeft(null);
		root.setCenter(accordion);
		toolbox.setSpacing(20);

		// Add testing tools

		// Protection API
		Button button = new Button("Protection API");
		button.setOnAction(event -> testProtectionAPI());
		addTool(button);
	}

	@Override
	public String getWindowFile() {
		return "TestingApp.fxml";
	}

	private void show(Node n) {
		root.setCenter(n);
		root.setLeft(accordion);
	}

	private @FXML void testProtectionAPI() {
		// We don't want to clutter up the TestingApp class with fields or methods,
		// so here we store those inside this class.
		VBox pane = new VBox() {

			private TextArea output = new TextArea();
			private TextField field = new TextField();
			private Exception exception;
			private StringWriter strngWrtr = new StringWriter();
			private PrintWriter prntWrtr = new PrintWriter(strngWrtr);

			private void resolve() {
				try {
					output.setText(Protection.getDomain(field.getText()).getPath());
				} catch (Exception e) {
					output.setText(
							"An error occurred... Press the button again to see the stacktrace. (Before typing anything in the text field. :)  )");
					exception = e;
				}
			}

			/**
			 * @return <code>true</code> if there was an exception in {@link #exception} and
			 *         it was printed.
			 */
			private boolean printException() {
				// If there is an exception...
				if (exception != null) {
					// ...then print the exception's stacktrace to the printwriter, which will print
					// it to the stringwriter.
					exception.printStackTrace(prntWrtr);

					// Get the text from the stringwriter and set that text to the output TextArea.
					output.setText(strngWrtr.toString());

					// Empty the stringwriter and trim its buffer's size.
					strngWrtr.getBuffer().setLength(0);
					strngWrtr.getBuffer().trimToSize();
					return true;
				}
				return false;
			}

			{

				field.setTooltip(new Tooltip("Type in the full name of a Domain and push the button below."));
				field.setOnKeyPressed(event -> {
					if (event.getCode() == KeyCode.ENTER) {
						resolve();
						event.consume();
						return;
					}

					// If they did not push enter and there is a cached exception, show it.
					printException();
				});

				Button done = new Button("Resolve field");
				done.setOnAction(event -> {
					if (!printException())
						resolve();
				});

				output.setEditable(false);

				getChildren().addAll(field, done, output);
			}
		};

		pane.setAlignment(Pos.CENTER);
		pane.setSpacing(40);

		show(pane);

	}

}
