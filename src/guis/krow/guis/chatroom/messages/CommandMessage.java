package krow.guis.chatroom.messages;

import kr�w.app.api.connections.Message;

public class CommandMessage extends Message {

	private final String[] args;

	public CommandMessage(String command, String... args) {
		super(command);
		this.args = args;
	}

	public CommandMessage(String command) {
		super(command);
		args = null;
	}

	public String[] getArgs() {
		return args;
	}

	public String getCommand() {
		return (String) getText();
	}

}
