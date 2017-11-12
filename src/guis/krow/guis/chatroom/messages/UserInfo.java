package krow.guis.chatroom.messages;

import java.io.Serializable;

public class UserInfo implements Serializable {
	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;
	private final String username, password;

	public UserInfo(String username, String password) {
		this.username = username;
		this.password = password;
	}

	/**
	 * @return the username
	 */
	public final String getUsername() {
		return username;
	}

	/**
	 * @return the password
	 */
	public final String getPassword() {
		return password;
	}

}
