package kr�w.app.api.connections;

public interface FullClientListener extends ClientListener {

	void connectionClosed();

	void connectionEstablished();

	void connectionLost();

}
