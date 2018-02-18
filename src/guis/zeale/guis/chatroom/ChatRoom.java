package zeale.guis.chatroom;

import java.io.IOException;
import java.util.Stack;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import krow.guis.GUIHelper;
import krow.guis.chatroom.messages.CommandMessage;
import krow.guis.chatroom.messages.ImageMessage;
import kröw.callables.VarArgsTask;
import kröw.core.Kröw;
import kröw.gui.Application;
import kröw.gui.ApplicationManager;

public class ChatRoom extends ConsoleWindow {

	private static final Color NOTIFICATION_COLOR = Color.LIGHTBLUE, ERROR_COLOR = Color.FIREBRICK,
			SUCCESS_COLOR = Color.GREEN, WARNING_COLOR = Color.GOLD;

	static {
		Kröw.addReflectionClass(ChatRoom.class);
	}

	private final static double CHAT_PANE_PREF_WIDTH = 1277, CHAT_PANE_PREF_HEIGHT = 827, CHAT_PANE_LAYOUTX = 305,
			CHAT_PANE_LAYOUTY = 14;
	private final static double CHAT_BOX_PREF_WIDTH = 1920, CHAT_BOX_PREF_HEIGHT = 220, CHAT_BOX_LAYOUTX = 0,
			CHAT_BOX_LAYOUTY = 860;
	private final static double SEND_BUTTON_PREF_WIDTH = 55, SEND_BUTTON_PREF_HEIGHT = 24, SEND_BUTTON_LAYOUTX = 1851,
			SEND_BUTTON_LAYOUTY = 958;

	private final Stack<String> history = new Stack<>();

	@Override
	public boolean canSwitchPage(final Class<? extends Application> newSceneClass) {
		return true;
	}

	private void emptyMessageWarning() {
		ApplicationManager.spawnLabelAtMousePos("Empty message...", ERROR_COLOR);
	}

	@Override
	public void initialize() {

		chatPane.setPrefSize(Kröw.scaleWidth(CHAT_PANE_PREF_WIDTH), Kröw.scaleHeight(CHAT_PANE_PREF_HEIGHT));
		chatPane.setLayoutX(Kröw.scaleWidth(CHAT_PANE_LAYOUTX));
		chatPane.setLayoutY(Kröw.scaleHeight(CHAT_PANE_LAYOUTY));

		chatBox.setPrefSize(Kröw.scaleWidth(CHAT_BOX_PREF_WIDTH), Kröw.scaleHeight(CHAT_BOX_PREF_HEIGHT));
		chatBox.setLayoutX(Kröw.scaleWidth(CHAT_BOX_LAYOUTX));
		chatBox.setLayoutY(Kröw.scaleHeight(CHAT_BOX_LAYOUTY));

		sendButton.setPrefSize(Kröw.scaleWidth(SEND_BUTTON_PREF_WIDTH), Kröw.scaleHeight(SEND_BUTTON_PREF_HEIGHT));
		sendButton.setLayoutX(Kröw.scaleWidth(SEND_BUTTON_LAYOUTX));
		sendButton.setLayoutY(Kröw.scaleHeight(SEND_BUTTON_LAYOUTY));

		sendButton.setOnAction(event -> {
			if (!chatBox.getText().isEmpty())
				handleUserInput(chatBox.getText());
			else
				emptyMessageWarning();
		});

		chatPane.setOnDragOver(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				if (event.getDragboard().hasImage() && event.getSource() != chatPane)
					event.acceptTransferModes(TransferMode.COPY);
			}
		});

		chatPane.setOnDragDropped(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				Dragboard board = event.getDragboard();
				if (board.hasImage()) {
					try {
						rawSend(new ImageMessage(board.getImage()));
						event.setDropCompleted(true);
					} catch (Exception e) {
						printError("There was an error sending the image...");
						event.setDropCompleted(false);
						return;
					} finally {
						// Called after the return in the catch statement if the catch statement is
						// called.
						event.consume();
					}
					// We get to here if everything succeeded. Now we need to show the image in the
					// chat pane
					chatPane.getChildren().add(new ImageView(board.getImage()));
				}
			}
		});

		chatBox.setOnKeyPressed(new EventHandler<KeyEvent>() {

			private final SimpleIntegerProperty historyPos = new SimpleIntegerProperty();
			private final ChangeListener<Number> listener = new ChangeListener<Number>() {

				@Override
				public void changed(final ObservableValue<? extends Number> observable, final Number oldValue,
						final Number newValue) {

					if (oldValue.intValue() == newValue.intValue() || history.isEmpty())
						return;

					historyPos.removeListener(this);

					// When subtracting 0 from array's size, you get the
					// array's size.
					// Getting a value at arr.get(arr.size()) throws an
					// exception.
					if (historyPos.get() <= 0)
						historyPos.set(1);

					if (historyPos.get() > history.size())
						historyPos.set(history.size());

					if (historyPos.get() == 1 && !(oldValue.intValue() > 1) && !chatBox.getText().isEmpty()
							&& !chatBox.getText().equals(history.peek()))
						history.push(chatBox.getText());
					chatBox.setText(history.get(history.size() - historyPos.get()));
					chatBox.positionCaret(chatBox.getText().length());

					historyPos.addListener(this);
				}
			};
			{
				historyPos.addListener(listener);
			}

			@Override
			public void handle(final KeyEvent event) {
				if (event.getCode().equals(KeyCode.UP) && event.isShiftDown()) {

					event.consume();

					historyPos.set(historyPos.get() + 1);

					return;
				} else if (event.getCode().equals(KeyCode.DOWN) && event.isShiftDown()) {
					event.consume();
					if (historyPos.get() <= 1) {
						chatBox.setText("");
						return;
					}
					historyPos.set(historyPos.get() - 1);
					return;
				} else if (event.getCode().equals(KeyCode.ENTER)) {
					if (event.isShiftDown() ^ event.isControlDown())
						chatBox.appendText("\n");
					if (!event.isShiftDown()) {
						event.consume();
						if (!chatBox.getText().isEmpty()) {
							// TODO Move this up to superclass.

							if (history.isEmpty() || !chatBox.getText().equals(history.peek().trim()))
								history.add(chatBox.getText());
							handleUserInput(chatBox.getText());
						} else
							emptyMessageWarning();
					}
				} else if (event.getCode().equals(KeyCode.LEFT)) {
					if (event.isShiftDown()) {
						if (event.isControlDown())
							chatBox.selectPositionCaret(0);
						else if (event.isAltDown())
							chatBox.selectPreviousWord();
						else
							chatBox.positionCaret(0);
						event.consume();
					}
				} else if (event.getCode().equals(KeyCode.RIGHT)) {
					if (event.isShiftDown()) {
						if (event.isControlDown())
							chatBox.selectPositionCaret(chatBox.getText().length());
						else if (event.isAltDown())
							chatBox.selectEndOfNextWord();
						else
							chatBox.positionCaret(chatBox.getText().length());
						event.consume();
					}

				} else if (event.getCode().equals(KeyCode.D))
					if (event.isAltDown() && event.isControlDown() && event.isShiftDown())
						chatPane.getChildren().clear();
					else if (event.isAltDown() && event.isControlDown())
						chatBox.setText("");
					else if (event.isControlDown()) {
						if (chatBox.getText().isEmpty())
							return;

						while (chatBox.deleteNextChar())
							;
						event.consume();

					} else if (event.isAltDown()) {
						if (chatBox.getText().isEmpty())
							return;
						while (chatBox.deletePreviousChar())
							;
						event.consume();
					}
				if (event.getText().length() == 1) {
					historyPos.removeListener(listener);
					historyPos.set(0);
					historyPos.addListener(listener);
				}

			}
		});

		try {
			if (!hostingServer() && Kröw.getProgramSettings().isChatRoomHostServer()) {
				println("Starting a server...", NOTIFICATION_COLOR);
				startServer();
				println("Created a server successfully!", SUCCESS_COLOR);
			} else {
				print("Please use ", ERROR_COLOR);
				print("/connect ", Color.RED);
				println("to connect to a server.", ERROR_COLOR);
			}

		} catch (final IOException e) {
			e.printStackTrace();
		}

		GUIHelper.addDefaultSettings(GUIHelper.buildMenu(pane));
		GUIHelper.applyShapeBackground(pane, chatPane, chatBox);
	}

	@Override
	protected void onPageSwitched() {
		disconnect();
		try {
			stopServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static final String ERROR_LOG_NAME = "[ERR]";

	private void printError(String errorText) {
		println(ERROR_LOG_NAME + ": " + errorText, Color.FIREBRICK);
	}

	private void sendingMessageNotification() {
		ApplicationManager.spawnLabelAtMousePos("Sending...", SUCCESS_COLOR);
	}

	@Override
	protected void commandInput(String cmd, String... args) {

		final CommandMessage msg = new CommandMessage(cmd, args);

		if (cmd.startsWith("/")) {
			if (args != null)
				for (final String s : args)
					cmd += " " + s;
			if (connected()) {
				send(cmd);
				sendingMessageNotification();
			} else {
				printError("You can't send messages without being connected to a server.");
			}
			return;
		}

		if (cmd.equalsIgnoreCase("setname") || cmd.equalsIgnoreCase("set-name"))
			if (args.length == 0 || args[0].trim().isEmpty())
				println("Command usage: /setname (name)", Color.RED);
			else {
				username = args[0];
				println("Your name has been changed to: " + username, Color.AQUA);
				rawSend(msg);
			}
		else if (cmd.equalsIgnoreCase("start-server") || cmd.equalsIgnoreCase("startserver")) {
			if (hostingServer()) {
				println("The server already exists!", ERROR_COLOR);
				print("Use ", ERROR_COLOR);
				print("/stop-server ", Color.RED);
				println("to stop it.", ERROR_COLOR);
			} else
				try {
					if (args != null && args.length > 0)
						startServer(Short.parseShort(args[0]));
					else
						startServer();
					println("The server was created successfully!", SUCCESS_COLOR);
				} catch (final IOException e) {
					println("The server could not be created! (Perhaps the port number is taken :(  )", ERROR_COLOR);
					e.printStackTrace();
				} catch (final NumberFormatException e) {
					println("The port number must not exceed 65535 or precede 1. It also must be a valid port number.",
							ERROR_COLOR);
				}

		} else if (cmd.equalsIgnoreCase("stopserver") || cmd.equalsIgnoreCase("stop-server")) {
			STOP_SERVER: if (hostingServer()) {
				try {
					stopServer();
					println("The server is being closed...", SUCCESS_COLOR);
				} catch (IOException e) {
					printError("Failed to close the server...");
					e.printStackTrace();
					break STOP_SERVER;
				}
				println("The server was closed successfully.", SUCCESS_COLOR);
			} else {
				println("The server wasn't open...", ERROR_COLOR);
				print("Use ", ERROR_COLOR);
				print("/start-server ", Color.RED);
				println("to start it.", ERROR_COLOR);
			}

		} else if (cmd.equalsIgnoreCase("help")) {

			// This removes redundant calls to multiple print commands.
			// (Especially printing " - " repeatedly.)
			//
			// Instead we can call this Task's execute method with the name of
			// the command to show in the help menu, and the description of the
			// command.
			final VarArgsTask<String> showHelp = params -> {
				if (params.length < 2)
					try {
						throw new Exception("Invalid args");
					} catch (final Exception e) {
						e.printStackTrace();
						return;
					}
				print(params[0], Color.CRIMSON);
				print(" - ");
				println(params[1], Color.DEEPSKYBLUE);
			};

			println("Showing pg. 1 for help.", Color.BISQUE);
			println("{Necessary Information} - Whatever is inside braces must be given by the user when entering the command.",
					Color.MEDIUMPURPLE);
			println("[Unnecessary Information] - Whatever is inside brackets does not need to be given by the user when entering the command.",
					Color.MEDIUMPURPLE);
			println("(Data Type) - Whatever follows parentheses must be of the type specified inside the parentheses.",
					Color.MEDIUMPURPLE);

			println();
			println();
			println();

			showHelp.execute("set-name {name}", "Set's your name. This method takes effect across chats.");
			showHelp.execute("start-server [(Integer) port]", "Starts a server if one has not already been started.");
			showHelp.execute("stop-server", "Stops the running server... If it's running, that is.");
			showHelp.execute("help [(Integer) page number]", "Shows command help [at the specified page.]");
			showHelp.execute("connect {(Text) server address} [(Integer) port]",
					"Connects to the specified server if you're not already connected to one. The port is optional, and defaults to 25000.");
			showHelp.execute("disconnect", "Disconnects from a server, if you are connected to one.");
			showHelp.execute("clear-screen", "Clears the screen; removes all the messages being displayed.");
			showHelp.execute("debug {(Text) option}", "Shows some debug information based on the given option.");

			rawSend(msg);
			return;
		} else if (cmd.equalsIgnoreCase("connect")) {
			if (args.length == 0 || args.length > 2) {
				println("Usage: /connect (address) [port]", ERROR_COLOR);
				return;
			}
			if (connected()) {
				println("You are already connected to a server...", ERROR_COLOR);
				return;
			}
			if (args.length == 1) {
				final String[] strings = args[0].split(":");
				if (strings.length < 2)
					try {
						println("Attempting to connect using the default port...", NOTIFICATION_COLOR);
						connect(args[0]);

						print("Connected to server ", SUCCESS_COLOR);
						print(args[0] + ":" + 25000, Color.DARKGREEN);
						println(" successfully.", SUCCESS_COLOR);
					} catch (final IOException e) {
						print("Couldn't connect to ", ERROR_COLOR);
						print(args[0], Color.BLUEVIOLET);
						println(".", ERROR_COLOR);
					}
				else
					try {
						println("Attempting to connect...", NOTIFICATION_COLOR);
						connect(strings[0], Short.parseShort(strings[1]));
						print("Connected to server ", SUCCESS_COLOR);
						print(strings[0] + ":" + strings[1], Color.DARKGREEN);
						println(" successfully.", SUCCESS_COLOR);
					} catch (final IOException e) {
						println("An error ocurred while connecting to the server.", ERROR_COLOR);
					} catch (final NumberFormatException e) {
						print("Could not parse the numerical value of ", ERROR_COLOR);
						print(strings[1], Color.BLUEVIOLET);
						println(".", ERROR_COLOR);
					}
			} else

				try {
					println("Attempting to connect...", NOTIFICATION_COLOR);
					connect(args[0], Short.parseShort(args[1]));
					print("Connected to server ", SUCCESS_COLOR);
					print(args[0] + ":" + args[1], Color.DARKGREEN);
					println(" successfully.", SUCCESS_COLOR);
				} catch (final IOException e) {
					println("An error ocurred while connecting to the server.", ERROR_COLOR);
				} catch (final NumberFormatException e) {
					print("Could not parse the numerical value of ", ERROR_COLOR);
					print(args[1], Color.BLUEVIOLET);
					println(".", ERROR_COLOR);
				}

		} else if (cmd.equalsIgnoreCase("disconnect")) {
			if (!connected()) {
				println("You are not connected to a server.", WARNING_COLOR);
				if (hostingServer())
					println("\tYou are hosting one though...", WARNING_COLOR);
			} else {
				println("Attempting to disconnect...", NOTIFICATION_COLOR);
				if (hostingServer())
					println("\tYou're still hosting a server though...", WARNING_COLOR);
				try {
					rawSend(msg);
					disconnect();
				} catch (final Exception e) {
					println("Failed to close the connection...", ERROR_COLOR);
					return;
				}
				println("The connection was closed successfully...", SUCCESS_COLOR);
			}
		} else if (cmd.equalsIgnoreCase("cls") || cmd.equalsIgnoreCase("clear-screen")) {
			chatPane.getChildren().clear();
		} else if (cmd.equalsIgnoreCase("debug")) {
			if (args.length == 0)
				printError("Too few arguments. Usage: /debug {(Text) option}");
			else if (args.length > 1)
				printError("Too many arguments. Usage: /debug {(Text) option}");
			else {
				if (args[0].equalsIgnoreCase("textObjectsReferenced"))
					println("" + getReferencedTexts(), Color.PEACHPUFF);
				else
					printError("Argument unrecognized: '" + args[0] + "'");
			}
		} else if (cmd.equalsIgnoreCase("clstream") || cmd.equalsIgnoreCase("clear-stream")) {
			if (connected()) {
				getClient().reset();
				println("Stream cleared");
			} else {
				printError("You're not connected to a server.");
			}
		} else
			ApplicationManager.spawnLabelAtMousePos("Unknown Command", ERROR_COLOR);

	}

	@Override
	protected void textInput(String message) {
		if (connected())
			send(message);
		else
			printError("You can't send messages without being connected to a server.");
	}

}
