package kröw.app.api;

public class User {
	private final String username;
	private final String sessionId;
	private User(String username, String sessionId) {
		this.username = username;
		this.sessionId = sessionId;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getSessionId() {
		return sessionId;
	}
	
}
