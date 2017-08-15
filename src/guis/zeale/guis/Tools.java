package zeale.guis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javafx.collections.ListChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.SwipeEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import kröw.core.Kröw;
import kröw.core.managers.WindowManager;
import kröw.core.managers.WindowManager.Page;
import sun.awt.shell.ShellFolder;

public class Tools extends Page {

	private static final double PANE_VGAP = (double) 85 / 1080 * Kröw.getSystemProperties().getScreenHeight(),
			PANE_HGAP = (double) 70 / 1920 * Kröw.getSystemProperties().getScreenWidth();
	private static final double ICON_HEIGHT = (double) 100 / 1080 * Kröw.getSystemProperties().getScreenHeight(),
			ICON_WIDTH = (double) 100 / 1920 * Kröw.getSystemProperties().getScreenWidth();

	private static final Insets FLOW_PANE_PADDING = new Insets(
			(double) 120 / 1080 * Kröw.getSystemProperties().getScreenHeight(),
			(double) 120 / 1920 * Kröw.getSystemProperties().getScreenWidth(), 0,
			(double) 280 / 1080 * Kröw.getSystemProperties().getScreenHeight());

	@FXML
	private Pane pane;

	@FXML
	private FlowPane subPane;

	@Override
	public String getWindowFile() {
		return "Tools.fxml";
	}

	private void addWindowsTools() {
		try {
			addCommandTool("regedit", "Regedit",
					Kröw.getImageFromFile(new File("C:/Windows/regedit.exe"), (int) ICON_WIDTH, (int) ICON_HEIGHT));
			addCommandTool("services.msc", "Services", Kröw.getImageFromFile(
					new File("C:/Windows/System32/services.msc"), (int) ICON_WIDTH, (int) ICON_HEIGHT));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initialize() {
		GUIHelper.addDefaultSettings(GUIHelper.buildCloseButton(pane));
		if (Kröw.getSystemProperties().isDPIOversized()) {
			Label label = new Label("Your screen's DPI is too large. Program icons may appear to be cut off.");
			label.setTextFill(Color.FIREBRICK);
			label.setFont(Font.font(Font.getDefault().getName(), FontWeight.EXTRA_BOLD,
					(double) 20 / 1920 * Kröw.getSystemProperties().getScreenWidth()));
			pane.getChildren().add(label);
			label.setLayoutX(627.5146484375 / 1920 * Kröw.getSystemProperties().getScreenWidth());
			label.setOnMouseClicked(event -> label
					.setLayoutX(Kröw.getSystemProperties().getScreenWidth() / 2 - label.prefWidth(-1) / 2));
		}

		subPane.setVgap(PANE_VGAP);
		subPane.setHgap(PANE_HGAP);

		subPane.getChildren().addListener(new ListChangeListener<Node>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends Node> c) {
				while (c.next())
					if (c.wasAdded())
						for (Node n : c.getAddedSubList())
							if (!(n instanceof Button))
								throw new RuntimeException("Only Buttons are allowed in the Tools page.");
			}
		});

		subPane.setPadding(FLOW_PANE_PADDING);
		if (Kröw.getSystemProperties().isWindows())
			addWindowsTools();
	}

	public Tool addButton(String text, Image image) {
		ImageView icon = new ImageView(image);
		Button button = buildButton();
		button.setGraphic(icon);
		button.setText(text);
		return new Tool(button);
	}

	private Button buildButton() {
		Button button = new Button();
		button.setFocusTraversable(false);
		button.setContentDisplay(ContentDisplay.TOP);
		button.setBackground(Background.EMPTY);
		subPane.getChildren().add(button);
		button.setTextFill(Color.WHITE);
		button.setFont(Font.font(Font.getDefault().getName(), FontWeight.BOLD, 18));
		return button;
	}

	public ExecutableTool addExecutable(File executable) throws FileNotFoundException {
		Button button = buildButton();
		button.setText(executable.getName());
		button.setGraphic(new ImageView(SwingFXUtils.toFXImage(Kröw.toBufferedImage(
				ShellFolder.getShellFolder(executable).getIcon(true), (int) ICON_WIDTH, (int) ICON_HEIGHT), null)));
		return new ExecutableTool(button, executable);
	}

	public CommandTool addCommandTool(String command, String name, Image graphic) {
		Button button = buildButton();
		button.setText(name);
		button.setGraphic(new ImageView(graphic));
		return new CommandTool(button, command);
	}

	public static class Tool {
		private final Button button;

		private Tool(Button button) {
			this.button = button;
		}

		/**
		 * @param value
		 * @see javafx.scene.control.ButtonBase#setOnAction(javafx.event.EventHandler)
		 */
		public final void setOnAction(EventHandler<javafx.event.ActionEvent> value) {
			button.setOnAction(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.control.Labeled#setText(java.lang.String)
		 */
		public final void setText(String value) {
			button.setText(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.control.Labeled#setGraphic(javafx.scene.Node)
		 */
		public final void setGraphic(Image image) {
			ImageView icon = new ImageView(image);
			icon.setFitHeight(ICON_HEIGHT);
			icon.setFitWidth(ICON_WIDTH);
			button.setGraphic(icon);
		}

		/**
		 * @param value
		 * @see javafx.scene.control.Control#setTooltip(javafx.scene.control.Tooltip)
		 */
		public final void setTooltip(Tooltip value) {
			button.setTooltip(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.layout.Region#setBackground(javafx.scene.layout.Background)
		 */
		public final void setBackground(Background value) {
			button.setBackground(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setVisible(boolean)
		 */
		public final void setVisible(boolean value) {
			button.setVisible(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOpacity(double)
		 */
		public final void setOpacity(double value) {
			button.setOpacity(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setEffect(javafx.scene.effect.Effect)
		 */
		public final void setEffect(Effect value) {
			button.setEffect(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setDisable(boolean)
		 */
		public final void setDisable(boolean value) {
			button.setDisable(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnDragEntered(javafx.event.EventHandler)
		 */
		public final void setOnDragEntered(EventHandler<? super DragEvent> value) {
			button.setOnDragEntered(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnDragExited(javafx.event.EventHandler)
		 */
		public final void setOnDragExited(EventHandler<? super DragEvent> value) {
			button.setOnDragExited(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnDragOver(javafx.event.EventHandler)
		 */
		public final void setOnDragOver(EventHandler<? super DragEvent> value) {
			button.setOnDragOver(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnDragDropped(javafx.event.EventHandler)
		 */
		public final void setOnDragDropped(EventHandler<? super DragEvent> value) {
			button.setOnDragDropped(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnDragDone(javafx.event.EventHandler)
		 */
		public final void setOnDragDone(EventHandler<? super DragEvent> value) {
			button.setOnDragDone(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setRotate(double)
		 */
		public final void setRotate(double value) {
			button.setRotate(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnContextMenuRequested(javafx.event.EventHandler)
		 */
		public final void setOnContextMenuRequested(EventHandler<? super ContextMenuEvent> value) {
			button.setOnContextMenuRequested(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnMouseClicked(javafx.event.EventHandler)
		 */
		public final void setOnMouseClicked(EventHandler<? super MouseEvent> value) {
			button.setOnMouseClicked(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnMouseDragged(javafx.event.EventHandler)
		 */
		public final void setOnMouseDragged(EventHandler<? super MouseEvent> value) {
			button.setOnMouseDragged(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnMouseEntered(javafx.event.EventHandler)
		 */
		public final void setOnMouseEntered(EventHandler<? super MouseEvent> value) {
			button.setOnMouseEntered(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnMouseExited(javafx.event.EventHandler)
		 */
		public final void setOnMouseExited(EventHandler<? super MouseEvent> value) {
			button.setOnMouseExited(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnMouseMoved(javafx.event.EventHandler)
		 */
		public final void setOnMouseMoved(EventHandler<? super MouseEvent> value) {
			button.setOnMouseMoved(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnMousePressed(javafx.event.EventHandler)
		 */
		public final void setOnMousePressed(EventHandler<? super MouseEvent> value) {
			button.setOnMousePressed(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnMouseReleased(javafx.event.EventHandler)
		 */
		public final void setOnMouseReleased(EventHandler<? super MouseEvent> value) {
			button.setOnMouseReleased(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnDragDetected(javafx.event.EventHandler)
		 */
		public final void setOnDragDetected(EventHandler<? super MouseEvent> value) {
			button.setOnDragDetected(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnMouseDragOver(javafx.event.EventHandler)
		 */
		public final void setOnMouseDragOver(EventHandler<? super MouseDragEvent> value) {
			button.setOnMouseDragOver(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnMouseDragReleased(javafx.event.EventHandler)
		 */
		public final void setOnMouseDragReleased(EventHandler<? super MouseDragEvent> value) {
			button.setOnMouseDragReleased(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnMouseDragEntered(javafx.event.EventHandler)
		 */
		public final void setOnMouseDragEntered(EventHandler<? super MouseDragEvent> value) {
			button.setOnMouseDragEntered(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnMouseDragExited(javafx.event.EventHandler)
		 */
		public final void setOnMouseDragExited(EventHandler<? super MouseDragEvent> value) {
			button.setOnMouseDragExited(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnScrollStarted(javafx.event.EventHandler)
		 */
		public final void setOnScrollStarted(EventHandler<? super ScrollEvent> value) {
			button.setOnScrollStarted(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnScroll(javafx.event.EventHandler)
		 */
		public final void setOnScroll(EventHandler<? super ScrollEvent> value) {
			button.setOnScroll(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnScrollFinished(javafx.event.EventHandler)
		 */
		public final void setOnScrollFinished(EventHandler<? super ScrollEvent> value) {
			button.setOnScrollFinished(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnRotationStarted(javafx.event.EventHandler)
		 */
		public final void setOnRotationStarted(EventHandler<? super RotateEvent> value) {
			button.setOnRotationStarted(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnRotate(javafx.event.EventHandler)
		 */
		public final void setOnRotate(EventHandler<? super RotateEvent> value) {
			button.setOnRotate(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnRotationFinished(javafx.event.EventHandler)
		 */
		public final void setOnRotationFinished(EventHandler<? super RotateEvent> value) {
			button.setOnRotationFinished(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnZoomStarted(javafx.event.EventHandler)
		 */
		public final void setOnZoomStarted(EventHandler<? super ZoomEvent> value) {
			button.setOnZoomStarted(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnZoomFinished(javafx.event.EventHandler)
		 */
		public final void setOnZoomFinished(EventHandler<? super ZoomEvent> value) {
			button.setOnZoomFinished(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnSwipeUp(javafx.event.EventHandler)
		 */
		public final void setOnSwipeUp(EventHandler<? super SwipeEvent> value) {
			button.setOnSwipeUp(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnSwipeDown(javafx.event.EventHandler)
		 */
		public final void setOnSwipeDown(EventHandler<? super SwipeEvent> value) {
			button.setOnSwipeDown(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnSwipeLeft(javafx.event.EventHandler)
		 */
		public final void setOnSwipeLeft(EventHandler<? super SwipeEvent> value) {
			button.setOnSwipeLeft(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnSwipeRight(javafx.event.EventHandler)
		 */
		public final void setOnSwipeRight(EventHandler<? super SwipeEvent> value) {
			button.setOnSwipeRight(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnTouchPressed(javafx.event.EventHandler)
		 */
		public final void setOnTouchPressed(EventHandler<? super TouchEvent> value) {
			button.setOnTouchPressed(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnTouchMoved(javafx.event.EventHandler)
		 */
		public final void setOnTouchMoved(EventHandler<? super TouchEvent> value) {
			button.setOnTouchMoved(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnKeyPressed(javafx.event.EventHandler)
		 */
		public final void setOnKeyPressed(EventHandler<? super KeyEvent> value) {
			button.setOnKeyPressed(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnKeyReleased(javafx.event.EventHandler)
		 */
		public final void setOnKeyReleased(EventHandler<? super KeyEvent> value) {
			button.setOnKeyReleased(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnKeyTyped(javafx.event.EventHandler)
		 */
		public final void setOnKeyTyped(EventHandler<? super KeyEvent> value) {
			button.setOnKeyTyped(value);
		}

		/**
		 * @param value
		 * @see javafx.scene.Node#setOnInputMethodTextChanged(javafx.event.EventHandler)
		 */
		public final void setOnInputMethodTextChanged(EventHandler<? super InputMethodEvent> value) {
			button.setOnInputMethodTextChanged(value);
		}

	}

	public static abstract class LaunchableTool extends Tool {

		private LaunchableTool(Button button) {
			super(button);
			button.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					if (!launchProcess())
						WindowManager.spawnLabelAtMousePos("The process could not be started...", Color.FIREBRICK);
				}
			});
		}

		public abstract boolean launchProcess();
	}

	public static class ExecutableTool extends LaunchableTool {

		private Process process;
		private File executable;

		private ExecutableTool(Button button, File executable) {
			super(button);
			this.executable = executable;
			button.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					if (!launchProcess())
						WindowManager.spawnLabelAtMousePos("Already running...", Color.FIREBRICK);
				}
			});
		}

		public boolean launchProcess() {
			if (!(process == null) && process.isAlive())
				return false;
			try {
				process = Runtime.getRuntime().exec(executable.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}

	}

	public static class CommandTool extends LaunchableTool {
		private String command;
		private Process process;

		private CommandTool(Button b, String command) {
			super(b);
			this.command = command;
		}

		@Override
		public boolean launchProcess() {
			if (process == null || !process.isAlive())
				try {
					process = Runtime.getRuntime().exec("powershell");
					PrintWriter pw = new PrintWriter(process.getOutputStream());
					pw.println(command);
					pw.close();

					String s;
					try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));) {
						while ((s = br.readLine()) != null)
							System.out.println(s);
					}

					try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));) {
						while ((s = br.readLine()) != null)
							System.out.println(s);
					}

					process.destroy();

					return true;
				} catch (IOException e) {
					e.printStackTrace();
				}
			return false;
		}

	}
}
