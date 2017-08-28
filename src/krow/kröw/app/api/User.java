package kröw.app.api;

public class User {
	private final String username;
	private final String sessionId;

	private User(final String username, final String sessionId) {
		this.username = username;
		this.sessionId = sessionId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public String getUsername() {
		return username;
	}

}
