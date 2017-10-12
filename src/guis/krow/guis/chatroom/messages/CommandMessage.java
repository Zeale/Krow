package krow.guis.chatroom.messages;

import kröw.connections.TextMessage;

public class CommandMessage extends TextMessage {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;
	private final String[] args;

	public CommandMessage(final String command) {
		super(command);
		args = null;
	}

	public CommandMessage(final String command, final String... args) {
		super(command);
		this.args = args;
	}

	public String[] getArgs() {
		return args;
	}

	public String getCommand() {
		return (String) getText();
	}

}
