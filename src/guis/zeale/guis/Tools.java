package zeale.guis;

import java.io.File;
import java.io.FileNotFoundException;

import javafx.collections.ListChangeListener;
import javafx.embed.swing.SwingFXUtils;
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
import kröw.core.managers.WindowManager.Page;
import sun.awt.shell.ShellFolder;

public class Tools extends Page {

	private static final double PANE_VGAP = (double) 85 / 1080 * Kröw.SCREEN_HEIGHT,
			PANE_HGAP = (double) 70 / 1920 * Kröw.SCREEN_WIDTH;
	private static final double ICON_HEIGHT = (double) 100 / 1080 * Kröw.SCREEN_HEIGHT,
			ICON_WIDTH = (double) 100 / 1920 * Kröw.SCREEN_WIDTH;

	private static final Insets FLOW_PANE_PADDING = new Insets((double) 120 / 1080 * Kröw.SCREEN_HEIGHT,
			(double) 120 / 1920 * Kröw.SCREEN_WIDTH, 0, (double) 280 / 1080 * Kröw.SCREEN_HEIGHT);

	@FXML
	private Pane pane;

	@FXML
	private FlowPane subPane;

	@Override
	public String getWindowFile() {
		return "Tools.fxml";
	}

	@Override
	public void initialize() {
		GUIHelper.addDefaultSettings(GUIHelper.buildCloseButton(pane));
		if (Kröw.OVERSIZED_DPI) {
			Label label = new Label("Your screen's DPI is too large. Program icons may appear to be cut off.");
			label.setTextFill(Color.FIREBRICK);
			label.setFont(Font.font(Font.getDefault().getName(), FontWeight.EXTRA_BOLD,
					(double) 20 / 1920 * Kröw.SCREEN_WIDTH));
			pane.getChildren().add(label);
			label.setLayoutX(627.5146484375 / 1920 * Kröw.SCREEN_WIDTH);
			label.setOnMouseClicked(event -> label.setLayoutX(Kröw.SCREEN_WIDTH / 2 - label.prefWidth(-1) / 2));
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

		try {
			addExecutable(new File("C:/Users/BHF-DUDEGUY/Appdata/Roaming/Krow/Krow.ico"));
			addExecutable(new File("C:/Windows/regedit.exe"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public ToolButton addButton(String text, Image image) {
		ImageView icon = new ImageView(image);
		Button button = new Button(text, icon);
		button.setFocusTraversable(false);
		button.setContentDisplay(ContentDisplay.TOP);
		button.setBackground(Background.EMPTY);
		subPane.getChildren().add(button);
		button.setTextFill(Color.WHITE);
		button.setFont(Font.font(Font.getDefault().getName(), FontWeight.BOLD, 18));
		return new ToolButton(button);
	}

	public ToolButton addExecutable(File executable) throws FileNotFoundException {

		return addButton(executable.getName(),
				SwingFXUtils.toFXImage(Kröw.toBufferedImage(ShellFolder.getShellFolder(executable).getIcon(true),
						(int) ICON_WIDTH, (int) ICON_HEIGHT), null));
	}

	public static final class ToolButton {
		private Button button;

		private ToolButton(Button button) {
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

}
