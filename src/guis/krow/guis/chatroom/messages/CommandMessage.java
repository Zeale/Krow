package krow.guis.chatroom.messages;

import kröw.app.api.connections.Message;

public class CommandMessage extends Message {

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
